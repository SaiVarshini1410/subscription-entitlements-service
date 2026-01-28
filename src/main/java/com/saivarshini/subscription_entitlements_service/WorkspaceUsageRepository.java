package com.saivarshini.subscription_entitlements_service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkspaceUsageRepository extends JpaRepository<WorkspaceUsage, Long> {
  Optional<WorkspaceUsage> findByWorkspaceId(String workspaceId);
}
