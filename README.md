# Subscription & Entitlements Service (Spring Boot)

Backend service that models a simple SaaS system:
- Plans define limits (max projects, max seats, feature flags)
- Workspaces subscribe to a plan
- Entitlements are derived from the current subscription + plan
- Projects/Members are enforced by those limits
- Invites enable adding members via token-based acceptance (email simulated)
- Notifications are generated on plan changes
- Audit log records “who did what” for major write actions

## Tech Stack
- Java 17
- Spring Boot (REST)
- Spring Data JPA + Hibernate
- MySQL
- Maven

---

## Key Features
- **Plans**: create/list plans with limits and feature flags
- **Subscriptions**: subscribe workspace, cancel, change plan
- **Entitlements**: view effective entitlements for a workspace
- **Projects enforcement**: blocks creation when maxProjects reached
- **Members enforcement**: blocks adds/accepts when maxSeats reached
- **Invites**: create invite, accept invite using token (email simulated via response/log)
- **Notifications**: plan change generates a notification per member
- **Audit Log**: records actions like PLAN_CHANGED, PROJECT_CREATED, MEMBER_ADDED, INVITE_CREATED, INVITE_ACCEPTED

---

## How to Run
### 1) Start MySQL and create a database
Example:
```sql
CREATE DATABASE subentitlements;
```

### 2) Configure DB in `application.properties`
Make sure your MySQL URL/user/pass are correct.

### 3) Run the service
```bash
./mvnw spring-boot:run
```

Ping:
```bash
curl http://localhost:8080/ping
```

---

## API Walkthrough (End-to-End Demo)

### Header (used for audit logs)
For write endpoints, send:
`X-Actor-Email: admin@acme.com`

---

### 1) Create Plans
```bash
curl -X POST http://localhost:8080/plans \
  -H "Content-Type: application/json" \
  -H "X-Actor-Email: admin@acme.com" \
  -d '{"code":"FREE","name":"Free","monthlyPriceCents":0,"maxProjects":1,"maxSeats":1,"analyticsEnabled":false}'

curl -X POST http://localhost:8080/plans \
  -H "Content-Type: application/json" \
  -H "X-Actor-Email: admin@acme.com" \
  -d '{"code":"PRO","name":"Pro","monthlyPriceCents":999,"maxProjects":3,"maxSeats":3,"analyticsEnabled":true}'
```

### 2) Subscribe Workspace
```bash
curl -X POST http://localhost:8080/subscriptions \
  -H "Content-Type: application/json" \
  -H "X-Actor-Email: admin@acme.com" \
  -d '{"workspaceId":"acme","planCode":"PRO"}'
```

### 3) Add Members (seats enforced)
```bash
curl -X POST http://localhost:8080/workspaces/acme/members \
  -H "Content-Type: application/json" \
  -H "X-Actor-Email: admin@acme.com" \
  -d '{"email":"a@acme.com"}'
```

### 4) Create Projects (limits enforced)
```bash
curl -X POST http://localhost:8080/workspaces/acme/projects \
  -H "Content-Type: application/json" \
  -H "X-Actor-Email: admin@acme.com" \
  -d '{"name":"p1"}'
```

### 5) Invite + Accept (email simulated)
Create invite:
```bash
curl -X POST http://localhost:8080/workspaces/acme/invites \
  -H "Content-Type: application/json" \
  -H "X-Actor-Email: admin@acme.com" \
  -d '{"email":"new@acme.com"}'
```

Response includes inviteLink like:
`http://localhost:8080/invites/<token>/accept`

Accept invite:
```bash
curl -X POST http://localhost:8080/invites/<token>/accept
```

### 6) Change Plan + Notifications
```bash
curl -X POST http://localhost:8080/subscriptions/acme/change-plan \
  -H "Content-Type: application/json" \
  -H "X-Actor-Email: admin@acme.com" \
  -d '{"planCode":"FREE"}'
```

View notifications:
```bash
curl http://localhost:8080/workspaces/acme/notifications
```

### 7) Audit Log
```bash
curl http://localhost:8080/workspaces/acme/audit
```

---

## Error Format
All errors return consistent JSON:
```json
{
  "timestamp": "2026-02-03T12:34:56Z",
  "status": 403,
  "error": "Seat limit reached: 3/3",
  "path": "/invites/abc/accept"
}
```
