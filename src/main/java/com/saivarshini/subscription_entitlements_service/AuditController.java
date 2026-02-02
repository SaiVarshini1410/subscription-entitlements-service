package com.saivarshini.subscription_entitlements_service;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class AuditController {

  private final AuditEventRepository repo;

  public AuditController(AuditEventRepository repo) {
    this.repo = repo;
  }

  @GetMapping("/workspaces/{workspaceId}/audit")
  public List<AuditEvent> list(@PathVariable String workspaceId) {
    return repo.findTop50ByWorkspaceIdOrderByCreatedAtDesc(workspaceId);
  }
}
