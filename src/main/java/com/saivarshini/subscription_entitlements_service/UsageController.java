package com.saivarshini.subscription_entitlements_service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UsageController {

  private final WorkspaceUsageRepository usageRepo;
  private final SubscriptionRepository subRepo;
  private final PlanRepository planRepo;

  public UsageController(WorkspaceUsageRepository usageRepo,
                         SubscriptionRepository subRepo,
                         PlanRepository planRepo) {
    this.usageRepo = usageRepo;
    this.subRepo = subRepo;
    this.planRepo = planRepo;
  }


  @PostMapping("/usage/{workspaceId}/projects/{count}")
  public WorkspaceUsage setProjects(@PathVariable String workspaceId, @PathVariable int count) {
    if (count < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "count must be >= 0");
    }

    WorkspaceUsage usage = usageRepo.findByWorkspaceId(workspaceId).orElse(new WorkspaceUsage());
    usage.setWorkspaceId(workspaceId);
    usage.setProjectCount(count);
    return usageRepo.save(usage);
  }

  @GetMapping("/workspaces/{workspaceId}/can-create-project")
  public CanCreateProjectResponse canCreateProject(@PathVariable String workspaceId) {
    Subscription sub = subRepo.findByWorkspaceId(workspaceId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No subscription for workspaceId: " + workspaceId));

    if (!"ACTIVE".equalsIgnoreCase(sub.getStatus())) {
      return new CanCreateProjectResponse(workspaceId, false, 0, 0, sub.getStatus(), sub.getPlanCode());
    }

    Plan plan = planRepo.findByCode(sub.getPlanCode())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Plan not found: " + sub.getPlanCode()));

    int max = plan.getMaxProjects();
    int current = usageRepo.findByWorkspaceId(workspaceId).map(WorkspaceUsage::getProjectCount).orElse(0);

    boolean allowed = current < max;

    return new CanCreateProjectResponse(workspaceId, allowed, current, max, sub.getStatus(), plan.getCode());
  }

  public record CanCreateProjectResponse(
      String workspaceId,
      boolean allowed,
      int currentProjects,
      int maxProjects,
      String subscriptionStatus,
      String planCode
  ) {}
}
