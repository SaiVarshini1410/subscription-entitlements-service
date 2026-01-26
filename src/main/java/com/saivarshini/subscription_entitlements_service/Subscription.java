package com.saivarshini.subscription_entitlements_service;

import jakarta.persistence.*;

@Entity
public class Subscription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String workspaceId;

  @Column(nullable = false)
  private String planCode;

  @Column(nullable = false)
  private String status;

  public Long getId() { return id; }
  public String getWorkspaceId() { return workspaceId; }
  public String getPlanCode() { return planCode; }
  public String getStatus() { return status; }

  public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }
  public void setPlanCode(String planCode) { this.planCode = planCode; }
  public void setStatus(String status) { this.status = status; }
}
