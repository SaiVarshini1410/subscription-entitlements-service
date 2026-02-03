package com.saivarshini.subscription_entitlements_service;

import jakarta.persistence.*;

@Entity
@Table(
    name = "subscription",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_subscription_workspace", columnNames = {"workspaceId"})
    }
)
public class Subscription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String workspaceId;

  @Column(nullable = false)
  private String planCode;

  @Column(nullable = false)
  private String status;

  @PrePersist
  @PreUpdate
  void normalize() {
    if (workspaceId != null) workspaceId = workspaceId.trim();
    if (planCode != null) planCode = planCode.trim().toUpperCase();
    if (status != null) status = status.trim().toUpperCase();
  }

  public Long getId() { return id; }
  public String getWorkspaceId() { return workspaceId; }
  public String getPlanCode() { return planCode; }
  public String getStatus() { return status; }

  public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }
  public void setPlanCode(String planCode) { this.planCode = planCode; }
  public void setStatus(String status) { this.status = status; }
}
