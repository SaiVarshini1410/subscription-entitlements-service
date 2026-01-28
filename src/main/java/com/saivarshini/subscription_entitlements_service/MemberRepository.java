package com.saivarshini.subscription_entitlements_service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
  long countByWorkspaceId(String workspaceId);
  List<Member> findByWorkspaceId(String workspaceId);
}
