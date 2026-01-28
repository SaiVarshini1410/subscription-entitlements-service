package com.saivarshini.subscription_entitlements_service;

import jakarta.persistence.*;

@Entity
public class WorkspaceUsage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String workspaceId;

  @Column(nullable = false)
  private int projectCount;

  public Long getId() { return id; }
  public String getWorkspaceId() { return workspaceId; }
  public int getProjectCount() { return projectCount; }

  public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }
  public void setProjectCount(int projectCount) { this.projectCount = projectCount; }
}
