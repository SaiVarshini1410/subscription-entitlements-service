package com.saivarshini.subscription_entitlements_service;

import jakarta.persistence.*;

@Entity
public class Plan {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String code;
  private String name;
  private int monthlyPriceCents;
  private int maxProjects;
  private boolean analyticsEnabled;

  public Long getId() { return id; }
  public String getCode() { return code; }
  public String getName() { return name; }
  public int getMonthlyPriceCents() { return monthlyPriceCents; }
  public int getMaxProjects() { return maxProjects; }
  public boolean isAnalyticsEnabled() { return analyticsEnabled; }

  public void setCode(String code) { this.code = code; }
  public void setName(String name) { this.name = name; }
  public void setMonthlyPriceCents(int monthlyPriceCents) { this.monthlyPriceCents = monthlyPriceCents; }
  public void setMaxProjects(int maxProjects) { this.maxProjects = maxProjects; }
  public void setAnalyticsEnabled(boolean analyticsEnabled) { this.analyticsEnabled = analyticsEnabled; }
}
