package com.saivarshini.subscription_entitlements_service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

  private final SubscriptionRepository subRepo;
  private final PlanRepository planRepo;

  public SubscriptionController(SubscriptionRepository subRepo, PlanRepository planRepo) {
    this.subRepo = subRepo;
    this.planRepo = planRepo;
  }

  @PostMapping
  public Subscription create(@RequestBody Subscription req) {
    if (req.getWorkspaceId() == null || req.getWorkspaceId().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "workspaceId is required");
    }
    if (req.getPlanCode() == null || req.getPlanCode().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "planCode is required");
    }

    String planCode = req.getPlanCode().trim().toUpperCase();

    planRepo.findByCode(planCode)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown planCode: " + planCode));

    Subscription sub = subRepo.findByWorkspaceId(req.getWorkspaceId()).orElse(new Subscription());
    sub.setWorkspaceId(req.getWorkspaceId());
    sub.setPlanCode(planCode);
    sub.setStatus("ACTIVE");

    return subRepo.save(sub);
  }

  @GetMapping("/{workspaceId}")
  public Subscription getByWorkspace(@PathVariable String workspaceId) {
    return subRepo.findByWorkspaceId(workspaceId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No subscription for workspaceId: " + workspaceId));
  }

  @PostMapping("/{workspaceId}/cancel")
  public Subscription cancel(@PathVariable String workspaceId) {
    Subscription sub = subRepo.findByWorkspaceId(workspaceId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No subscription for workspaceId: " + workspaceId));

    sub.setStatus("CANCELED");
    return subRepo.save(sub);
  }

  @PostMapping("/{workspaceId}/change-plan")
  public Subscription changePlan(@PathVariable String workspaceId,
                                 @RequestBody ChangePlanRequest req) {

    if (req == null || req.getPlanCode() == null || req.getPlanCode().trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "planCode is required");
    }

    Subscription sub = subRepo.findByWorkspaceId(workspaceId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "No subscription for workspaceId: " + workspaceId));

    String newPlanCode = req.getPlanCode().trim().toUpperCase();

    planRepo.findByCode(newPlanCode)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Unknown planCode: " + newPlanCode));

    sub.setPlanCode(newPlanCode);
    return subRepo.save(sub);
  }
}
