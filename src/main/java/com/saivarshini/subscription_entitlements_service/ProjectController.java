package com.saivarshini.subscription_entitlements_service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class ProjectController {

  private final ProjectRepository projectRepo;
  private final SubscriptionRepository subRepo;
  private final PlanRepository planRepo;
  private final AuditService audit;

  public ProjectController(ProjectRepository projectRepo,
                           SubscriptionRepository subRepo,
                           PlanRepository planRepo,
                           AuditService audit) {
    this.projectRepo = projectRepo;
    this.subRepo = subRepo;
    this.planRepo = planRepo;
    this.audit = audit;
  }

  @PostMapping("/workspaces/{workspaceId}/projects")
  public Project createProject(@RequestHeader(value = "X-Actor-Email", required = false) String actorEmail,
                               @PathVariable String workspaceId,
                               @RequestBody ProjectCreateRequest req) {

    if (req == null || req.getName() == null || req.getName().trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project name is required");
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

    long currentCount = projectRepo.countByWorkspaceId(workspaceId);
    if (currentCount >= plan.getMaxProjects()) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          "Project limit reached: " + currentCount + "/" + plan.getMaxProjects());
    }

    Project p = new Project();
    p.setWorkspaceId(workspaceId);
    p.setName(req.getName().trim());
    Project saved = projectRepo.save(p);

    audit.log(workspaceId, actorEmail, "PROJECT_CREATED",
        "projectId=" + saved.getId() + ", name=" + saved.getName());

    return saved;
  }

  @GetMapping("/workspaces/{workspaceId}/projects")
  public List<Project> listProjects(@PathVariable String workspaceId) {
    return projectRepo.findByWorkspaceId(workspaceId);
  }
}
