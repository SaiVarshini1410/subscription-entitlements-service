package com.saivarshini.subscription_entitlements_service;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plans")
public class PlanController {

  private final PlanRepository repo;

  public PlanController(PlanRepository repo) {
    this.repo = repo;
  }

  @PostMapping
  public Plan create(@RequestBody Plan plan) {
    return repo.save(plan);
  }

  @GetMapping
  public List<Plan> list() {
    return repo.findAll();
  }
}
