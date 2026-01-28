package com.saivarshini.subscription_entitlements_service;

import jakarta.persistence.*;

@Entity
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String workspaceId;

  @Column(nullable = false)
  private String email;

  public Long getId() { return id; }
  public String getWorkspaceId() { return workspaceId; }
  public String getEmail() { return email; }

  public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }
  public void setEmail(String email) { this.email = email; }
}
