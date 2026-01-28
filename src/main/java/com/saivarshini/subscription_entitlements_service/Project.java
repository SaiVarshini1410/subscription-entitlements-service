package com.saivarshini.subscription_entitlements_service;

import jakarta.persistence.*;

@Entity
public class Project {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String workspaceId;

  @Column(nullable = false)
  private String name;

  public Long getId() { return id; }
  public String getWorkspaceId() { return workspaceId; }
  public String getName() { return name; }

  public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }
  public void setName(String name) { this.name = name; }
}
