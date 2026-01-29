package com.saivarshini.subscription_entitlements_service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
  List<Notification> findByWorkspaceIdOrderByCreatedAtDesc(String workspaceId);
}
