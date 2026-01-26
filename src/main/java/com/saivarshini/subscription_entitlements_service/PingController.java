package com.saivarshini.subscription_entitlements_service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

  @GetMapping("/ping")
  public String ping() {
    return "ok";
  }
}
