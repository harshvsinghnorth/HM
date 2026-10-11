# Hospital Management System — Backend

A REST API backend for a hospital: patient sign-up, doctor onboarding, appointment booking, and role-based access for admins, doctors and patients. Built with Spring Boot, Spring Security and PostgreSQL, with stateless JWT authentication and OAuth2 social login.

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4, Spring Web MVC |
| Security | Spring Security, JWT (jjwt 0.12), OAuth2 Client (Google, GitHub) |
| Persistence | Spring Data JPA (Hibernate), PostgreSQL |
| Other | ModelMapper (entity ↔ DTO), Lombok, Maven |

## Features

- **Authentication**: username/password sign-up and login returning a signed JWT. Passwords are hashed with BCrypt. Every new account is created as a `PATIENT`; roles are never accepted from the client.
- **Social login**: OAuth2 login with Google or GitHub. First-time users are registered automatically as patients. An email that is already registered through another provider is rejected, so one account can't be claimed twice.
- **Role- and permission-based access control**: three roles (`ADMIN`, `DOCTOR`, `PATIENT`), each mapped to fine-grained permissions such as `appointment:write` and `user:manage`. Access is enforced both per route (`SecurityFilterChain`) and per method (`@PreAuthorize`, `@Secured`). For example, a doctor can only list their own appointments.
- **Appointments**: patients book appointments with a doctor. The patient is always taken from the JWT, never from the request body, so nobody can book on someone else's behalf. Admins and doctors with the right permission can reassign an appointment to another doctor.
- **Doctor onboarding**: admins promote an existing user to a doctor, which adds the `DOCTOR` role.
- **Admin views**: paginated patient listing.
- **Consistent errors**: a global exception handler returns structured JSON errors with the right HTTP status (401 for a bad or expired JWT, 403 for missing permissions, 404 for an unknown user).

## Architecture

```mermaid
flowchart LR
    C[Client] -->|Bearer JWT| F[JwtAuthFilter]
    C -->|OAuth2| O[Google / GitHub]
    O --> S[OAuth2SuccessHandler]
    F --> SC[SecurityFilterChain<br/>route rules]
    S --> A[AuthService]
    SC --> CT[Controllers<br/>auth · public · patients · doctors · admin]
    CT --> SV[Services<br/>@PreAuthorize / @Secured]
    SV --> R[JPA Repositories]
    R --> DB[(PostgreSQL)]
```

Requests pass through `JwtAuthFilter`, which validates the token and loads the user's roles and permissions into the security context. Route rules then gate each path, and service methods apply finer checks.

### Data model

- **User**: login identity (username, BCrypt password, auth provider, roles)
- **Patient** and **Doctor**: share their primary key with `User` (`@MapsId`), so each profile is tied to exactly one login
- **Appointment**: many-to-one with both Patient and Doctor
- **Department**: many-to-many with Doctor, plus an optional head doctor
- **Insurance**: one-to-one with Patient, cascaded and removed along with it

## API

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/auth/signup` | Public | Register a user |
| POST | `/auth/login` | Public | Log in and receive a JWT |
| GET | `/oauth2/authorization/{google\|github}` | Public | Start OAuth2 login |
| GET | `/public/doctors` | Public | List all doctors |
| POST | `/patients/appointments` | Patient | Book an appointment |
| GET | `/patients/profile` | Patient | Get your own patient profile |
| GET | `/doctors/appointments` | Doctor, Admin | List the logged-in doctor's appointments |
| GET | `/admin/patients?page=0&size=10` | Admin | Paginated patient list |
| POST | `/admin/OnBoardNewDoctor` | Admin | Promote a user to doctor |

Send the token on protected routes as `Authorization: Bearer <jwt>`. Tokens expire after 10 minutes.

## Running locally

**Requirements:** Java 21 and PostgreSQL.

1. Create a database:
   ```sql
   CREATE DATABASE hospital_db;
   ```
2. Create `src/main/resources/application.properties`. It is git-ignored so credentials never get committed.
   ```properties
   spring.application.name=HM
   spring.datasource.url=jdbc:postgresql://localhost:5432/hospital_db
   spring.datasource.username=YOUR_DB_USER
   spring.datasource.password=YOUR_DB_PASSWORD
   spring.jpa.hibernate.ddl-auto=update

   # At least 32 characters (HMAC-SHA256)
   jwt.secretKey=REPLACE_WITH_A_LONG_RANDOM_SECRET

   # Optional: only needed for social login
   spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
   spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
   spring.security.oauth2.client.registration.github.client-id=YOUR_GITHUB_CLIENT_ID
   spring.security.oauth2.client.registration.github.client-secret=YOUR_GITHUB_CLIENT_SECRET
   ```
3. Run the app:
   ```bash
   ./mvnw spring-boot:run
   ```
   The API starts on `http://localhost:8080`.

### Quick test

```bash
# Sign up
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"asha@example.com","password":"secret123","name":"Asha"}'

# Log in
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"asha@example.com","password":"secret123"}'

# Public route (no token needed)
curl http://localhost:8080/public/doctors

# Protected route (use the JWT from the login response)
curl -X POST http://localhost:8080/patients/appointments \
  -H "Authorization: Bearer <jwt>" -H "Content-Type: application/json" \
  -d '{"doctorId":1,"appointmentTime":"2026-11-02T10:30:00","reason":"Fever"}'
```

### Creating the first admin

Sign-up always creates a patient, so the first admin is promoted directly in the database. Sign up normally, then run:

```sql
INSERT INTO user_roles (user_id, roles) VALUES (<your user id>, 'ADMIN');
```

Log in again to get a token with the new role. From then on, admins can onboard doctors through `/admin/OnBoardNewDoctor`.

## Project structure

```
src/main/java/com/example/HM
├── config/       ModelMapper, BCrypt encoder, AuthenticationManager beans
├── controllers/  auth, public, patient, doctor, admin endpoints
├── dto/          request/response objects (entities are never exposed directly)
├── entity/       JPA entities and enums (roles, permissions, blood groups)
├── error/        global exception handler and ApiError
├── repository/   Spring Data JPA repositories
├── security/     JWT filter, OAuth2 handler, role → permission mapping, security config
└── service/      business logic with method-level authorization
```

## Roadmap

- Swagger/OpenAPI documentation
- Refresh tokens
- Department and insurance endpoints (the entities and service logic already exist)
- Integration tests with Testcontainers
