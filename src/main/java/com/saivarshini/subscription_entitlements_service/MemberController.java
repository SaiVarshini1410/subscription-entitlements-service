package com.saivarshini.subscription_entitlements_service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class MemberController {

  private final MemberRepository memberRepo;
  private final SubscriptionRepository subRepo;
  private final PlanRepository planRepo;
  private final AuditService audit;

  public MemberController(MemberRepository memberRepo,
                          SubscriptionRepository subRepo,
                          PlanRepository planRepo,
                          AuditService audit) {
    this.memberRepo = memberRepo;
    this.subRepo = subRepo;
    this.planRepo = planRepo;
    this.audit = audit;
  }

  @PostMapping("/workspaces/{workspaceId}/members")
  public Member addMember(@RequestHeader(value = "X-Actor-Email", required = false) String actorEmail,
                          @PathVariable String workspaceId,
                          @RequestBody MemberCreateRequest req) {

    if (req == null || req.getEmail() == null || req.getEmail().trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email is required");
    }

    Subscription sub = subRepo.findByWorkspaceId(workspaceId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "No subscription for workspaceId: " + workspaceId));

    if (!"ACTIVE".equalsIgnoreCase(sub.getStatus())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          "Subscription not ACTIVE (status=" + sub.getStatus() + ")");
    }

    Plan plan = planRepo.findByCode(sub.getPlanCode())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
            "Plan not found for planCode: " + sub.getPlanCode()));

    long currentSeats = memberRepo.countByWorkspaceId(workspaceId);
    if (currentSeats >= plan.getMaxSeats()) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          "Seat limit reached: " + currentSeats + "/" + plan.getMaxSeats());
    }

    Member m = new Member();
    m.setWorkspaceId(workspaceId);
    m.setEmail(req.getEmail().trim().toLowerCase());
    Member saved = memberRepo.save(m);

    audit.log(workspaceId, actorEmail, "MEMBER_ADDED",
        "memberId=" + saved.getId() + ", email=" + saved.getEmail());

    return saved;
  }

  @GetMapping("/workspaces/{workspaceId}/members")
  public List<Member> listMembers(@PathVariable String workspaceId) {
    return memberRepo.findByWorkspaceId(workspaceId);
  }
}
