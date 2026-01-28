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

  public MemberController(MemberRepository memberRepo,
                          SubscriptionRepository subRepo,
                          PlanRepository planRepo) {
    this.memberRepo = memberRepo;
    this.subRepo = subRepo;
    this.planRepo = planRepo;
  }

  @PostMapping("/workspaces/{workspaceId}/members")
  public Member addMember(@PathVariable String workspaceId,
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
    m.setEmail(req.getEmail().trim());
    return memberRepo.save(m);
  }

  @GetMapping("/workspaces/{workspaceId}/members")
  public List<Member> listMembers(@PathVariable String workspaceId) {
    return memberRepo.findByWorkspaceId(workspaceId);
  }
}
