package com.saivarshini.subscription_entitlements_service;

import org.springframework.stereotype.Service;

@Service
public class AuditService {

  private final AuditEventRepository repo;

  public AuditService(AuditEventRepository repo) {
    this.repo = repo;
  }

  public void log(String workspaceId, String actorEmail, String action, String details) {
    AuditEvent e = new AuditEvent();
    e.setWorkspaceId(workspaceId);
    e.setActorEmail(normalizeActor(actorEmail));
    e.setAction(action);
    e.setDetails(details == null ? "" : details);
    repo.save(e);
  }

  private String normalizeActor(String actorEmail) {
    if (actorEmail == null || actorEmail.trim().isEmpty()) return "system";
    return actorEmail.trim().toLowerCase();
  }
}
