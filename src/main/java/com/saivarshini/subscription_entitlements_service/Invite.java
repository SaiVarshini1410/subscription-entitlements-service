package com.saivarshini.subscription_entitlements_service;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "invites")
public class Invite {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String workspaceId;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false, unique = true, length = 64)
  private String token;

  @Column(nullable = false)
  private String status; // PENDING / ACCEPTED / EXPIRED

  @Column(nullable = false)
  private Instant expiresAt;

  @Column(nullable = false)
  private Instant createdAt;

  @PrePersist
  void onCreate() {
    this.createdAt = Instant.now();
  }

  public Long getId() { return id; }
  public String getWorkspaceId() { return workspaceId; }
  public String getEmail() { return email; }
  public String getToken() { return token; }
  public String getStatus() { return status; }
  public Instant getExpiresAt() { return expiresAt; }
  public Instant getCreatedAt() { return createdAt; }

  public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }
  public void setEmail(String email) { this.email = email; }
  public void setToken(String token) { this.token = token; }
  public void setStatus(String status) { this.status = status; }
  public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
