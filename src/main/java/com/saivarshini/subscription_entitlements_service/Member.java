package com.saivarshini.subscription_entitlements_service;

import jakarta.persistence.*;

@Entity
@Table(
    name = "member",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_member_workspace_email", columnNames = {"workspaceId", "email"})
    }
)
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String workspaceId;

  @Column(nullable = false)
  private String email;

  @PrePersist
  @PreUpdate
  void normalize() {
    if (workspaceId != null) workspaceId = workspaceId.trim();
    if (email != null) email = email.trim().toLowerCase();
  }

  public Long getId() { return id; }
  public String getWorkspaceId() { return workspaceId; }
  public String getEmail() { return email; }

  public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }
  public void setEmail(String email) { this.email = email; }
}
