package com.saivarshini.subscription_entitlements_service;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_event")
public class AuditEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String workspaceId;

  @Column(nullable = false)
  private String actorEmail;

  @Column(nullable = false)
  private String action;

  @Column(nullable = false, length = 1000)
  private String details;

  @Column(nullable = false)
  private Instant createdAt;

  @PrePersist
  void onCreate() {
    this.createdAt = Instant.now();
  }

  public Long getId() { return id; }
  public String getWorkspaceId() { return workspaceId; }
  public String getActorEmail() { return actorEmail; }
  public String getAction() { return action; }
  public String getDetails() { return details; }
  public Instant getCreatedAt() { return createdAt; }

  public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }
  public void setActorEmail(String actorEmail) { this.actorEmail = actorEmail; }
  public void setAction(String action) { this.action = action; }
  public void setDetails(String details) { this.details = details; }
}
