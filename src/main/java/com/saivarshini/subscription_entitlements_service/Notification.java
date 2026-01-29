package com.saivarshini.subscription_entitlements_service;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notifications")
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String workspaceId;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String type; // SUBSCRIPTION_CHANGED

  @Column(nullable = false, length = 500)
  private String message;

  @Column(nullable = false)
  private Instant createdAt;

  @PrePersist
  void onCreate() { this.createdAt = Instant.now(); }

  public Long getId() { return id; }
  public String getWorkspaceId() { return workspaceId; }
  public String getEmail() { return email; }
  public String getType() { return type; }
  public String getMessage() { return message; }
  public Instant getCreatedAt() { return createdAt; }

  public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }
  public void setEmail(String email) { this.email = email; }
  public void setType(String type) { this.type = type; }
  public void setMessage(String message) { this.message = message; }
}
