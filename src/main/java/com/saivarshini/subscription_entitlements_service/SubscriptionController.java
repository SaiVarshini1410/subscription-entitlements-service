package com.saivarshini.subscription_entitlements_service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

  private final SubscriptionRepository subRepo;
  private final PlanRepository planRepo;
  private final MemberRepository memberRepo;
  private final NotificationRepository notificationRepo;
  private final AuditService audit;

  public SubscriptionController(SubscriptionRepository subRepo,
                                PlanRepository planRepo,
                                MemberRepository memberRepo,
                                NotificationRepository notificationRepo,
                                AuditService audit) {
    this.subRepo = subRepo;
    this.planRepo = planRepo;
    this.memberRepo = memberRepo;
    this.notificationRepo = notificationRepo;
    this.audit = audit;
  }

  @PostMapping
  public Subscription create(@RequestHeader(value = "X-Actor-Email", required = false) String actorEmail,
                             @RequestBody Subscription req) {
    if (req.getWorkspaceId() == null || req.getWorkspaceId().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "workspaceId is required");
    }
    if (req.getPlanCode() == null || req.getPlanCode().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "planCode is required");
    }

    String workspaceId = req.getWorkspaceId().trim();
    String planCode = req.getPlanCode().trim().toUpperCase();

    planRepo.findByCode(planCode)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown planCode: " + planCode));

    Subscription sub = subRepo.findByWorkspaceId(workspaceId).orElse(new Subscription());
    sub.setWorkspaceId(workspaceId);
    sub.setPlanCode(planCode);
    sub.setStatus("ACTIVE");

    Subscription saved = subRepo.save(sub);

    audit.log(workspaceId, actorEmail, "SUBSCRIPTION_CREATED",
        "workspaceId=" + workspaceId + ", planCode=" + planCode);

    return saved;
  }

  @GetMapping("/{workspaceId}")
  public Subscription getByWorkspace(@PathVariable String workspaceId) {
    return subRepo.findByWorkspaceId(workspaceId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "No subscription for workspaceId: " + workspaceId));
  }

  @PostMapping("/{workspaceId}/cancel")
  public Subscription cancel(@RequestHeader(value = "X-Actor-Email", required = false) String actorEmail,
                             @PathVariable String workspaceId) {
    Subscription sub = subRepo.findByWorkspaceId(workspaceId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No subscription for workspaceId: " + workspaceId));

    String oldStatus = sub.getStatus();
    sub.setStatus("CANCELED");

    Subscription saved = subRepo.save(sub);

    audit.log(workspaceId, actorEmail, "SUBSCRIPTION_CANCELED",
        "status " + oldStatus + " -> CANCELED");

    return saved;
  }

  @PostMapping("/{workspaceId}/change-plan")
  public Subscription changePlan(@RequestHeader(value = "X-Actor-Email", required = false) String actorEmail,
                                 @PathVariable String workspaceId,
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

    String oldPlanCode = sub.getPlanCode();
    sub.setPlanCode(newPlanCode);

    Subscription saved = subRepo.save(sub);

    audit.log(workspaceId, actorEmail, "PLAN_CHANGED",
        "planCode " + oldPlanCode + " -> " + newPlanCode);

    String msg = "Workspace plan changed from " + oldPlanCode + " to " + newPlanCode;

    for (Member m : memberRepo.findByWorkspaceId(workspaceId)) {
      Notification n = new Notification();
      n.setWorkspaceId(workspaceId);
      n.setEmail(m.getEmail());
      n.setType("SUBSCRIPTION_CHANGED");
      n.setMessage(msg);
      notificationRepo.save(n);
    }

    return saved;
  }
}
