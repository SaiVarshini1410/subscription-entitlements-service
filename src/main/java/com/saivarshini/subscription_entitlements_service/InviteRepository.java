package com.saivarshini.subscription_entitlements_service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InviteRepository extends JpaRepository<Invite, Long> {
  Optional<Invite> findByToken(String token);
  List<Invite> findByWorkspaceId(String workspaceId);
}
