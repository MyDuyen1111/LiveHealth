# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

LiveHealth is a clean-food e-commerce site with an AI health advisor. It is a polyglot monorepo of three services plus MySQL + Redis:

- **`backend/`** — Quarkus 3.12 / Java 21 REST API (reactive RESTEasy + Hibernate ORM/Panache + Narayana JTA). The system of record.
- **`frontend/`** — React 19 + Vite SPA (React Router 7, axios, lucide-react).
- **`ai-service/`** — Python FastAPI service that talks to an OpenAI-compatible LLM and reads the MySQL DB directly.

The README (in Vietnamese) is the authoritative source for env vars, test accounts, and troubleshooting — read it for operational details.

## Commands

All services run on non-standard ports: frontend `62173`, backend `62080`, AI `62000`, MySQL `62307`, Redis `62380`.

**Whole system (from repo root):**
```bash
bash scripts/setup.sh      # generate .env files + install deps (run once)
bash scripts/run_all.sh    # starts MySQL+Redis via docker compose, builds & runs all services, seeds 90 products if DB empty
bash scripts/stop_all.sh
```
`docker compose up` (root `docker-compose.yml`) builds & runs everything in containers. Note there is a **second** `backend/docker-compose.yml` used by `run_all.sh` to bring up only MySQL+Redis for local dev.

**Backend (Quarkus, run from `backend/`):**
```bash
mvn quarkus:dev     # dev mode with hot reload, port 62080
mvn compile         # quick compile check
mvn test            # run tests
mvn test -Dtest=ApplicationTests        # single test class
mvn test -Dtest=ApplicationTests#method # single method
```
Swagger UI: http://localhost:62080/swagger-ui

**Frontend (run from `frontend/`):**
```bash
npm run dev -- --port 62173 --strictPort
npm run build
npm run lint        # eslint
npm test            # vitest (jsdom); npm test -- src/__tests__/foo.test.jsx for one file
```

**AI service (run from `ai-service/`):**
```bash
pip install -r requirements.txt
python main.py      # port 62000; docs at /docs
curl http://localhost:62000/api/ai/health
```

**Seed / E2E:**
```bash
python scripts/gen_100_products.py   # idempotent (SKU-checked) product seed
# Selenium E2E lives in selenium_tests/ (see its README); requires services running
```

## Backend architecture — Spring idioms on Quarkus

This is the single most important thing to understand. The backend is Quarkus/Jakarta EE but deliberately wears a **Spring-style skin**, so don't expect idiomatic Quarkus/Panache:

- **`shared/base/BaseRepository<T, ID>`** is a hand-rolled Spring-Data-like repository over `EntityManager` (`save`/`findById`/`findAll(Pageable)`/`deleteById`/`count`). Every `*Repository` extends it instead of using `PanacheRepository`. Pagination uses custom `shared/base/pagination/` classes (`Page`, `Pageable`, `PageRequest`, `Sort`) that mirror Spring Data's API.
- Password hashing uses **Spring Security** `BCryptPasswordEncoder`, produced as a CDI bean in `shared/config/SecurityProducer.java`.

**Per-feature module layout** (see `product/` for the canonical example). Each domain concept is a flat set of sibling classes in one package:
```
Foo.java              # JPA @Entity
FooRepository.java    # extends BaseRepository<Foo, ID>
FooService.java       # interface
FooServiceImpl.java   # business logic, @Transactional, calls SecurityUtils for current user
FooController.java    # JAX-RS resource
FooMapper.java        # entity <-> DTO (MapStruct-style)
dto/request/ dto/response/
```
Domain packages: `auth`, `user`, `cart`, `order`, `product`, `news`, `payment`, `health`, and `shared/web` (dashboard, contact, testimonials, web info).

**Controllers & responses:**
- A `@RestApiV1` meta-annotation exists in `shared/base/` (base path `/api/v1` + JSON produce/consume), but in practice controllers declare `@Path("/api/v1")` directly and pull per-route paths from `shared/constant/UrlConstant.java`. Route strings live in `UrlConstant`, not inline — add new routes there.
- Return `Response` built via `shared/base/VsResponseUtil` + `RestData`/`RestStatus` for the standard envelope. Messages come from `shared/constant/{Success,Error}Message.java`.

**Security flow:**
- `shared/security/JwtAuthenticationFilter` (JAX-RS `ContainerRequestFilter`, Nimbus JOSE) parses the `Bearer` token, checks `InvalidatedTokenRepository` (logout/blacklist by JWT ID), and populates a request-scoped `CurrentRequestUser` + a custom `SecurityContext`.
- Inside services, get the caller via `shared/security/SecurityUtils.getCurrentUserId()` / `getCurrentEmail()` (resolves `CurrentRequestUser` through `Arc.container()`).
- The `security.*-endpoints` keys in `application.properties` declare public/user/admin route groups, but authorization is enforced in code (filter + service-layer checks), not by those properties alone — verify the actual check when touching auth.

**Config & profiles:** `application.properties` uses the `%prod.` profile prefix for values injected from env at runtime (DB URL, Redis, mailer, ports); dev mode falls back to inline defaults. Hibernate runs `database.generation=update` (schema auto-migrates from entities — no migration tool). `Application.java`'s `@Observes StartupEvent` seeds/refreshes test accounts (admin@livehealth.com / Admin@123, user@livehealth.vn / User123@), payment methods, and shipping methods on every boot.

Integrations: VNPay (sandbox) payment in `payment/` + `shared/config/VNPayConfig.java`; Cloudinary image upload in `shared/config/CloudinaryConfig.java`; Quarkus Mailer for email/OTP in `auth/`.

## Frontend architecture

- `src/api/*.js` — one module per backend resource, all built on `apiClient.js` (axios instance; attaches JWT, handles refresh/errors). Add a new endpoint by extending the matching `*Api.js`.
- `src/context/` — global state via React Context: `AuthContext`, `CartContext`, `WishlistContext`, `LanguageContext`. Wrap consumers here rather than prop-drilling auth/cart state.
- `src/pages/` — route components (admin pages under `pages/admin/`); `src/components/` — shared UI.
- Vite proxy (`vite.config.js`) forwards `/api/ai` → `:62000` and `/api` → `:62080`, so frontend code calls relative `/api/...` paths in all environments.

## AI service architecture

`main.py` (FastAPI) exposes three endpoints — `/api/ai/health`, `/api/ai/analyze` (BMI/BMR/TDEE via `health_calculator.py`), `/api/ai/chat` (conversational advisor). Logic is split across `chat_assistant.py`, `openai_chat_advisor.py` (LLM calls), and `product_matcher.py` (recommends products by querying MySQL directly with the shared DB credentials). It depends on the same `livehealth` database as the backend.
