package com.saivarshini.subscription_entitlements_service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@RestController
public class InviteController {

  private final InviteRepository inviteRepo;
  private final MemberRepository memberRepo;
  private final SubscriptionRepository subRepo;
  private final PlanRepository planRepo;

  public InviteController(InviteRepository inviteRepo,
                          MemberRepository memberRepo,
                          SubscriptionRepository subRepo,
                          PlanRepository planRepo) {
    this.inviteRepo = inviteRepo;
    this.memberRepo = memberRepo;
    this.subRepo = subRepo;
    this.planRepo = planRepo;
  }

  @PostMapping("/workspaces/{workspaceId}/invites")
  public InviteResponse createInvite(@PathVariable String workspaceId,
                                     @RequestBody InviteCreateRequest req) {
    if (req == null || req.getEmail() == null || req.getEmail().trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email is required");
    }

    String email = req.getEmail().trim().toLowerCase();

    Subscription sub = subRepo.findByWorkspaceId(workspaceId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "No subscription for workspaceId: " + workspaceId));
    if (!"ACTIVE".equalsIgnoreCase(sub.getStatus())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          "Subscription not ACTIVE (status=" + sub.getStatus() + ")");
    }

    if (memberRepo.existsByWorkspaceIdAndEmail(workspaceId, email)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Already a member: " + email);
    }

    Invite inv = new Invite();
    inv.setWorkspaceId(workspaceId);
    inv.setEmail(email);
    inv.setStatus("PENDING");
    inv.setToken(UUID.randomUUID().toString().replace("-", ""));
    inv.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));

    inv = inviteRepo.save(inv);

    String link = "http://localhost:8080/invites/" + inv.getToken() + "/accept";
    System.out.println("INVITE LINK (simulate email): " + link);

    return new InviteResponse(inv.getId(), inv.getWorkspaceId(), inv.getEmail(),
        inv.getStatus(), inv.getExpiresAt().toString(), link);
  }

  @PostMapping("/invites/{token}/accept")
  public Member accept(@PathVariable String token) {
    Invite inv = inviteRepo.findByToken(token)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid invite token"));

    if (!"PENDING".equalsIgnoreCase(inv.getStatus())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invite not pending (status=" + inv.getStatus() + ")");
    }

    if (Instant.now().isAfter(inv.getExpiresAt())) {
      inv.setStatus("EXPIRED");
      inviteRepo.save(inv);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invite expired");
    }

    String workspaceId = inv.getWorkspaceId();
    String email = inv.getEmail();

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

    if (memberRepo.existsByWorkspaceIdAndEmail(workspaceId, email)) {
      inv.setStatus("ACCEPTED");
      inviteRepo.save(inv);
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Already a member: " + email);
    }

    Member m = new Member();
    m.setWorkspaceId(workspaceId);
    m.setEmail(email);
    Member saved = memberRepo.save(m);

    inv.setStatus("ACCEPTED");
    inviteRepo.save(inv);

    return saved;
  }

  @GetMapping("/workspaces/{workspaceId}/invites")
  public List<Invite> list(@PathVariable String workspaceId) {
    return inviteRepo.findByWorkspaceId(workspaceId);
  }

  public record InviteResponse(
      Long inviteId,
      String workspaceId,
      String email,
      String status,
      String expiresAt,
      String inviteLink
  ) {}
}
