package com.saivarshini.subscription_entitlements_service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/entitlements")
public class EntitlementsController {

  private final SubscriptionRepository subRepo;
  private final PlanRepository planRepo;

  public EntitlementsController(SubscriptionRepository subRepo, PlanRepository planRepo) {
    this.subRepo = subRepo;
    this.planRepo = planRepo;
  }

  @GetMapping("/{workspaceId}")
  public EntitlementsResponse get(@PathVariable String workspaceId) {
    Subscription sub = subRepo.findByWorkspaceId(workspaceId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No subscription for workspaceId: " + workspaceId));

    if (!"ACTIVE".equalsIgnoreCase(sub.getStatus())) {
      return new EntitlementsResponse(workspaceId, sub.getPlanCode(), 0, false, sub.getStatus());
    }

    Plan plan = planRepo.findByCode(sub.getPlanCode())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Plan not found for planCode: " + sub.getPlanCode()));

    return new EntitlementsResponse(
        workspaceId,
        plan.getCode(),
        plan.getMaxProjects(),
        plan.isAnalyticsEnabled(),
        sub.getStatus()
    );
  }

  public record EntitlementsResponse(
      String workspaceId,
      String planCode,
      int maxProjects,
      boolean analyticsEnabled,
      String subscriptionStatus
  ) {}
}
