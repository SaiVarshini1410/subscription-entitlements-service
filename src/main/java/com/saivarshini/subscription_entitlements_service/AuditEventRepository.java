package com.saivarshini.subscription_entitlements_service;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
  List<AuditEvent> findTop50ByWorkspaceIdOrderByCreatedAtDesc(String workspaceId);
}
