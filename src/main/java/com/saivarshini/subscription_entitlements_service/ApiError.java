package com.saivarshini.subscription_entitlements_service;

import java.time.Instant;

public class ApiError {
  private Instant timestamp;
  private int status;
  private String error;
  private String path;

  public ApiError() {}

  public ApiError(Instant timestamp, int status, String error, String path) {
    this.timestamp = timestamp;
    this.status = status;
    this.error = error;
    this.path = path;
  }

  public Instant getTimestamp() { return timestamp; }
  public int getStatus() { return status; }
  public String getError() { return error; }
  public String getPath() { return path; }

  public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
  public void setStatus(int status) { this.status = status; }
  public void setError(String error) { this.error = error; }
  public void setPath(String path) { this.path = path; }
}
