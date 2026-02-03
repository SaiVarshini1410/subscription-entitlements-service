package com.saivarshini.subscription_entitlements_service;

import jakarta.persistence.*;

@Entity
@Table(
    name = "project",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_project_workspace_name", columnNames = {"workspaceId", "name"})
    }
)
public class Project {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String workspaceId;

  @Column(nullable = false)
  private String name;

  @PrePersist
  @PreUpdate
  void normalize() {
    if (workspaceId != null) workspaceId = workspaceId.trim();
    if (name != null) name = name.trim();
  }

  public Long getId() { return id; }
  public String getWorkspaceId() { return workspaceId; }
  public String getName() { return name; }

  public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }
  public void setName(String name) { this.name = name; }
}
