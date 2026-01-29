package com.saivarshini.subscription_entitlements_service;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NotificationController {

  private final NotificationRepository repo;

  public NotificationController(NotificationRepository repo) {
    this.repo = repo;
  }

  @GetMapping("/workspaces/{workspaceId}/notifications")
  public List<Notification> list(@PathVariable String workspaceId) {
    return repo.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId);
  }
}
