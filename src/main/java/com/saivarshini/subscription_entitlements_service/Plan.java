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

  public Long getId() { return id; }
  public String getCode() { return code; }
  public String getName() { return name; }
  public int getMonthlyPriceCents() { return monthlyPriceCents; }

  public void setCode(String code) { this.code = code; }
  public void setName(String name) { this.name = name; }
  public void setMonthlyPriceCents(int monthlyPriceCents) { this.monthlyPriceCents = monthlyPriceCents; }
}
