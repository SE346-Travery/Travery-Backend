# Travery Backend Architecture — Grafana Alloy Edition
## Complete System Description for Code Generation

---

## 1. System Overview

Travery is a mobile travel booking and social platform with a two-tier observability architecture. The backend runs on **Server 1 (GCP)**, which sends telemetry data to **Server 2 (GCP)** for centralized monitoring and visualization.

**Design Principle:** All observability is **push-based** (Server 1 pushes to Server 2, never the reverse). This ensures Server 1 is stateless, easily replaceable, and secure (no inbound observability ports).

---

## 2. Server 1: The Application Server (GCP VM)

Server 1 is a single-responsibility server dedicated to serving the Travery mobile app and generating observability telemetry. It runs **6 services** in Docker containers on a dedicated bridge network (`travery-network`).

### 2.1 Core Services

#### **Service 1: Nginx (Reverse Proxy)**
- **Role:** The only gateway to external traffic. Acts as SSL termination point and load balancer.
- **Image:** `nginx:1.27-alpine`
- **Host Port:** `80` (HTTP) — exposed to internet
- **Container Port:** `80` (internal)
- **Volume Mounts:**
  - `./nginx/nginx.conf` → `/etc/nginx/nginx.conf:ro` (read-only)
- **Upstream Target:** Routes all traffic to `travery-app:8080` (Spring Boot via Docker DNS)
- **Functionality:**
  - Listens for incoming HTTP requests from the Travery mobile app
  - Proxies requests to the Spring Boot backend with proper headers (`X-Real-IP`, `X-Forwarded-For`, `X-Forwarded-Proto`)
  - Exposes `/actuator/health` and `/actuator/prometheus` endpoints (read by Alloy and Grafana)
  - Handles request/response buffering and timeout management (60s)
- **Network:** `travery-network` (internal Docker bridge)
- **Health Check:** Pulls `/actuator/health` every 30 seconds
- **Restart Policy:** `unless-stopped`

#### **Service 2: Spring Boot (Core Application)**
- **Role:** The business logic engine. Processes API requests, manages database connections, and exposes metrics.
- **Image:** `ghcr.io/${GITHUB_REPOSITORY_OWNER}/travery:latest` (pulled from GitHub Container Registry)
- **Container Port:** `8080` (internal, NOT exposed to host)
- **Expose Only:** Port `8080` (visible to Docker network only)
- **Profile:** `prod` (enabled via `SPRING_PROFILES_ACTIVE=prod`)
- **Configuration File:** `application-prod.yml`
- **Key Dependencies:**
  - `spring-boot-starter-actuator` (exposes `/actuator/*` endpoints)
  - `micrometer-registry-prometheus` (exports metrics in Prometheus format)
- **Functionality:**
  - Processes requests proxied by Nginx (`/api/users`, `/api/tours`, `/api/bookings`, etc.)
  - Executes business logic (user authentication, tour booking, payments, etc.)
  - Connects to PostgreSQL for persistent data storage
  - Connects to Redis for session/cache management
  - Logs to Docker's JSON logging driver (read by Alloy)
  - Exposes `/actuator/health` (JVM health, database connectivity)
  - Exposes `/actuator/prometheus` (Micrometer metrics: JVM memory, request latency, custom app metrics)
- **Dependencies:**
  - PostgreSQL (database, hostname: `postgres`)
  - Redis (cache, hostname: `redis`)
- **Health Check:** Hits `/actuator/health` every 30 seconds
- **Logging:** JSON format to Docker stdout (Alloy collects these)
- **Restart Policy:** `unless-stopped`
- **Environment Variables:**
  - `SPRING_PROFILES_ACTIVE=prod` (use production configuration)
  - Database: `POSTGRES_HOST`, `POSTGRES_PORT`, `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`
  - Cache: `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`
  - Security: `JWT_SECRET`, `JWT_ACCESS_EXPIRATION`, `JWT_REFRESH_EXPIRATION`
  - File Upload: `MAX_FILE_SIZE`, `MAX_REQUEST_SIZE`
  - Email: `PROD_MAIL_HOST`, `PROD_MAIL_PORT`, `EMAIL_USERNAME`, `EMAIL_PASSWORD`
  - External APIs: `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`
  - OTP: `OTP_LENGTH`, `OTP_TTL_MINUTES`, `MAX_ATTEMPTS`, `RESEND_COOLDOWN_SECONDS`, `RESET_MAX_PER_HOUR`

#### **Service 3: PostgreSQL (Primary Database)**
- **Role:** Persistent data store for users, tours, bookings, transactions, etc.
- **Image:** `postgres:17-alpine`
- **Container Port:** `5432` (internal, NOT exposed to host)
- **Expose Only:** Port `5432` (visible to Docker network only)
- **Volume Mounts:**
  - `postgres_data` (named volume) → `/var/lib/postgresql/data` (persistent storage on host)
- **Functionality:**
  - Stores all transactional data (users, tours, bookings, reviews, payments)
  - Manages ACID compliance for critical operations
  - Enforces data integrity via FK constraints and triggers
  - Supports Flyway migrations (managed by Spring Boot)
- **Environment Variables:**
  - `POSTGRES_DB` (database name, e.g., `travery`)
  - `POSTGRES_USER` (username, e.g., `travery_user`)
  - `POSTGRES_PASSWORD` (strong password)
- **Health Check:** `pg_isready` command every 10 seconds (5 retries)
- **Dependencies:** None (primary data store)
- **Restart Policy:** `unless-stopped`

#### **Service 4: Redis (Cache & Session Store)**
- **Role:** In-memory cache for sessions, real-time data, and frequently accessed objects.
- **Image:** `redis:8-alpine`
- **Container Port:** `6379` (internal, NOT exposed to host)
- **Expose Only:** Port `6379` (visible to Docker network only)
- **Volume Mounts:**
  - `redis_data` (named volume) → `/data` (persistent RDB/AOF backups)
- **Command:** `redis-server --requirepass ${REDIS_PASSWORD} --appendonly yes`
  - `--requirepass`: Requires password for AUTH
  - `--appendonly yes`: AOF persistence (logs every command to disk)
- **Functionality:**
  - Stores user sessions (JWT token blacklist, refresh token store)
  - Caches frequently queried data (user profiles, tour details, search results)
  - Implements rate limiting buckets (OTP resend limits, login attempt tracking)
  - Supports real-time notifications (pub/sub or streams)
  - Provides sub-millisecond latency for hot data
- **Health Check:** `redis-cli -a ${REDIS_PASSWORD} ping` every 10 seconds (5 retries)
- **Dependencies:** None (independent cache)
- **Restart Policy:** `unless-stopped`

#### **Service 5: Grafana Alloy (Unified Observability Agent) — NEW**
- **Role:** The invisible telemetry collector. Unifies log collection, metrics scraping, and host monitoring into a single agent.
- **Image:** `grafana/alloy:v1.3.0`
- **Container Name:** `travery-alloy`
- **Port Exposure:** None (no host ports; all communication is outbound to Server 2)
- **Volume Mounts:**
  - `./alloy/config.yml` → `/etc/alloy/config.yml:ro` (configuration, read-only)
  - `/var/lib/docker/containers` → `/var/lib/docker/containers:ro` (Docker logs, read-only)
  - `/var/run/docker.sock` → `/var/run/docker.sock:ro` (Docker API, read-only)
  - `/proc` → `/host/proc:ro` (host process info, read-only)
  - `/sys` → `/host/sys:ro` (host system info, read-only)
  - `/` → `/rootfs:ro` (host root filesystem, read-only)
  - `alloy_wal` (named volume) → `/alloy/wal` (Write-Ahead Log for resilience)
- **Configuration:** `/alloy/config.yml` (comprehensive YAML configuration)
- **Functionality:** (Alloy has three main responsibilities)

**Responsibility 1: Log Collection & Shipping**
- Discovers all Docker containers using Docker API (`discovery.docker`)
- Reads container logs from `/var/lib/docker/containers/<container_id>/<container_id>-json.log`
- Extracts metadata: `container_name`, `service`, `container_id` as log labels
- Parses JSON log format (Docker's default)
- Processes logs through pipeline stages (JSON parsing, label extraction)
- Pushes logs to Loki on Server 2 via HTTP POST to `http://${LOKI_HOST}:3100/loki/api/v1/push`
- **Components used:**
  - `discovery.docker.docker_containers` (auto-discover containers)
  - `loki.source.docker.docker_logs` (read container logs)
  - `loki.process.docker_logs_pipeline` (parse and transform)
  - `loki.write.loki_push` (send to Loki)

**Responsibility 2: Metrics Scraping & Pushing**
- Scrapes Spring Boot's `/actuator/prometheus` endpoint every 30 seconds
- Endpoint: `http://travery-app:8080/actuator/prometheus` (internal Docker DNS)
- Collects all Micrometer metrics: JVM, HTTP requests, custom app metrics
- Adds labels to each metric: `server=server1`, `instance=travery-backend`, `job=spring-boot`
- Pushes metrics to Prometheus on Server 2 via remote_write API
- Endpoint: `http://${PROMETHEUS_HOST}:9090/api/v1/write` (push, not pull)
- **Components used:**
  - `prometheus.scrape.spring_boot` (scrape app metrics)
  - `prometheus.relabel.add_server_label` (add metadata labels)
  - `prometheus.remote_write.prometheus_push` (send to Prometheus)

**Responsibility 3: Host Metrics Collection**
- Collects CPU, memory, disk, network metrics from the host using `prometheus.exporter.unix`
- No external tools needed (replaces the old Node Exporter container)
- Exports in Prometheus format
- Metrics available at `localhost:9100` (for local debugging, not exposed)
- Pushed to Prometheus via same remote_write endpoint
- **Components used:**
  - `prometheus.exporter.unix.unix_metrics` (collect host metrics)

**Resilience & Reliability:**
- WAL (Write-Ahead Log) at `/alloy/wal`: If Alloy crashes, metrics are replayed to Prometheus on restart
- Max segment size: 128MB (disk space bounded)
- Logs and metrics are buffered in memory before sending
- Failed sends are retried with backoff

- **Environment Variables:**
  - `PROMETHEUS_HOST` (Server 2's IP where Prometheus runs, e.g., `10.0.0.5`)
  - `LOKI_HOST` (Server 2's IP where Loki runs, e.g., `10.0.0.5`)
- **Command:** `run /etc/alloy/config.yml` (starts Alloy with the config)
- **Dependencies:** Nginx, Spring Boot (needs to read their logs and scrape metrics)
- **Restart Policy:** `unless-stopped`
- **Logging:** JSON format to Docker stdout (Alloy logs its own operations)

---

### 2.2 Data Persistence & Networking

#### **Named Volumes**
- `postgres_data`: Persists PostgreSQL data across container restarts
- `redis_data`: Persists Redis RDB snapshots and AOF logs
- `alloy_wal`: Persists Alloy's Write-Ahead Log for metric resilience

#### **Bridge Network**
- **Network Name:** `travery-network`
- **Driver:** `bridge` (Docker's default, containers can reach each other via DNS)
- **Service Discovery:** All services reachable by hostname (e.g., `postgres`, `redis`, `travery-app`)
- **Purpose:** Isolated network for internal communication; external traffic only enters via Nginx port 80

#### **Host Port Mapping**
- Only `Nginx` maps a host port: `80:80` (HTTP traffic from the internet)
- All other services: NO host port mapping (internal only)
- Observability: NO inbound ports (all push-based, outbound to Server 2)

---

### 2.3 Logging Strategy

**Logging Driver:** `json-file` (Docker's JSON Structured Logging)

**Configuration for All Services:**
```
logging:
  driver: "json-file"
  options:
    max-size: "10m"      # Rotate log file at 10MB
    max-file: "3"        # Keep maximum 3 files (30MB total per container)
```

**Why JSON Logging?**
- Structured format (timestamp, message, stream)
- Alloy can parse and extract metadata
- Logs written to `/var/lib/docker/containers/<container_id>/<container_id>-json.log`
- Prevents unbounded disk growth

**Log Collection Flow:**
```
Spring Boot stdout/stderr 
  → Docker JSON driver 
  → /var/lib/docker/containers/.../json.log 
  → Alloy reads 
  → Alloy forwards to Loki on Server 2
```

---

### 2.4 Health Checks

**Purpose:** Docker uses health checks to determine if a container is healthy. Failed health checks trigger restarts.

**Health Check for Each Service:**

| Service | Command | Interval | Timeout | Retries |
|---------|---------|----------|---------|---------|
| Nginx | `wget --quiet --tries=1 --spider http://localhost/actuator/health` | 30s | 10s | 3 |
| Spring Boot | `wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health` | 30s | 10s | 5 |
| PostgreSQL | `pg_isready -U ${POSTGRES_USER} -d ${POSTGRES_DB}` | 10s | 5s | 5 |
| Redis | `redis-cli -a ${REDIS_PASSWORD} ping` | 10s | 5s | 5 |

**Health Check Dependencies:** Services wait for dependencies to be healthy before starting:
```yaml
depends_on:
  postgres:
    condition: service_healthy
  redis:
    condition: service_healthy
```

---

### 2.5 Environment Configuration

**File:** `.env` (created on Server 1 at `~/travery-deployment/.env`)

**Categories of Variables:**

| Category | Examples | Source |
|----------|----------|--------|
| GitHub Registry | `GITHUB_REPOSITORY_OWNER` | For pulling Docker image from GHCR |
| Database | `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` | PostgreSQL container setup |
| Cache | `REDIS_PASSWORD` | Redis container setup |
| Security | `JWT_SECRET`, `JWT_ACCESS_EXPIRATION`, `JWT_REFRESH_EXPIRATION` | Spring Boot JWT configuration |
| File Upload | `MAX_FILE_SIZE`, `MAX_REQUEST_SIZE` | Spring Boot file upload limits |
| Email | `PROD_MAIL_HOST`, `PROD_MAIL_PORT`, `EMAIL_USERNAME`, `EMAIL_PASSWORD` | SMTP server credentials |
| External APIs | `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, etc. | Cloudinary image/video storage |
| OTP | `OTP_LENGTH`, `OTP_TTL_MINUTES`, `MAX_ATTEMPTS`, `RESEND_COOLDOWN_SECONDS` | OTP authentication |
| Observability | `PROMETHEUS_HOST`, `LOKI_HOST` | Server 2's IP (for Alloy to push data) |

---

## 3. Server 2: The Observability Server (GCP VM)

Server 2 is a dedicated remote server for **receiving and storing telemetry data** sent by Server 1 (via Alloy). It never initiates connections to Server 1—all communication is push-based from Server 1.

### 3.1 Core Services

#### **Service 1: Prometheus (Time-Series Database for Metrics)**
- **Role:** Stores metrics sent by Alloy (push model via remote_write API, not pull).
- **Image:** `prom/prometheus:v2.56.1`
- **Host Port:** `9090` (exposed to your monitoring subnet)
- **Container Port:** `9090` (internal)
- **Volume Mounts:**
  - `./prometheus/prometheus.yml` → `/etc/prometheus/prometheus.yml:ro` (configuration)
  - `prometheus_data` → `/prometheus` (persistent storage for metrics)
- **Configuration:** `prometheus.yml` (minimal, no scrape_configs needed because metrics are pushed, not pulled)
- **Functionality:**
  - Receives metrics pushed by Alloy on Server 1 via HTTP POST to `/api/v1/write`
  - Stores metrics in TSDB (time-series database) format
  - Indexes metrics by labels (job, instance, container_name, server, etc.)
  - Retains metrics for 30 days (retention: `30d`)
  - Provides time-range query API (used by Grafana)
  - Supports instant queries (current value) and range queries (time series)
- **Network:** `monitoring-network` (internal Docker bridge on Server 2)
- **Restart Policy:** `unless-stopped`

#### **Service 2: Loki (Log Aggregation & Storage)**
- **Role:** Stores logs sent by Alloy (push model via HTTP POST to `/loki/api/v1/push`).
- **Image:** `grafana/loki:3.5.0`
- **Host Port:** `3100` (exposed to Server 1 for log pushing)
- **Container Port:** `3100` (internal)
- **Volume Mounts:**
  - `./loki/loki-config.yml` → `/etc/loki/local-config.yml:ro` (configuration)
  - `loki_data` → `/loki` (persistent storage for logs and indices)
- **Configuration:** `loki-config.yml` (minimal, just listens for incoming logs)
- **Functionality:**
  - Receives logs pushed by Alloy on Server 1 via HTTP POST
  - Parses log entries and extracts labels (container_name, service, stream, etc.)
  - Stores logs in chunks on disk (compressed)
  - Builds inverted index for fast label-based queries
  - Provides LogQL query API (used by Grafana for log search)
  - Supports label-based filtering: `{container_name="travery-app"}`, `{service="spring-boot"}`
- **Storage:** BoltDB (local embedded database) + filesystem storage (no external dependencies)
- **Network:** `monitoring-network` (internal Docker bridge on Server 2)
- **Restart Policy:** `unless-stopped`

#### **Service 3: Grafana (Visualization & Dashboarding)**
- **Role:** The control panel for engineers. Provides web UI for querying and visualizing metrics and logs.
- **Image:** `grafana/grafana:11.3.0`
- **Host Port:** `3000` (exposed to your monitoring subnet, e.g., your IP only)
- **Container Port:** `3000` (internal)
- **Volume Mounts:**
  - `grafana_data` → `/var/lib/grafana` (persistent storage for dashboards, datasources, users)
- **Functionality:**
  - Provides web UI at `http://<SERVER2_IP>:3000`
  - Authenticates users (default admin/admin, change on first login)
  - Allows users to add data sources (Prometheus, Loki)
  - Provides dashboarding interface (drag-drop visualization panels)
  - Supports metric queries (PromQL) and log queries (LogQL)
  - Auto-generates dashboards from metric names
  - Supports alerting (trigger on metric thresholds, send to Slack/email, etc.)
  - Stores all dashboards and configuration in `grafana_data` volume
- **Data Sources:**
  - Prometheus: `http://prometheus:9090` (for metric queries)
  - Loki: `http://loki:3100` (for log queries)
- **Environment Variables:**
  - `GF_SECURITY_ADMIN_USER=admin` (default admin username)
  - `GF_SECURITY_ADMIN_PASSWORD=${GRAFANA_PASSWORD}` (admin password, set in `.env`)
- **Network:** `monitoring-network` (internal Docker bridge on Server 2)
- **Restart Policy:** `unless-stopped`
- **Dependencies:** Prometheus and Loki must be running before Grafana starts (for health checks)

---

### 3.2 Networking on Server 2

#### **Bridge Network**
- **Network Name:** `monitoring-network`
- **Driver:** `bridge` (isolated network for observability services)
- **Service Discovery:** Services reachable by hostname within the network
  - Grafana connects to Prometheus at `http://prometheus:9090`
  - Grafana connects to Loki at `http://loki:3100`

#### **Host Port Mapping**
- **Prometheus:** `9090:9090` (exposed for external queries from your machine, if needed)
- **Loki:** `3100:3100` (exposed for Alloy on Server 1 to push logs)
- **Grafana:** `3000:3000` (exposed for you to access the web UI)

---

### 3.3 Data Persistence

#### **Named Volumes**
- `prometheus_data`: Persists Prometheus TSDB (metrics)
- `loki_data`: Persists Loki chunks and indices (logs)
- `grafana_data`: Persists Grafana dashboards, data sources, users

---

## 4. Network Communication & Data Flow

### 4.1 Data Flow: Logs

```
Spring Boot Container (logs to stdout)
  ↓ (Docker's JSON logging driver captures)
/var/lib/docker/containers/<id>-json.log (on Server 1 host)
  ↓ (Alloy mounts this via volume)
Grafana Alloy (loki.source.docker reads)
  ↓ (Parses JSON, extracts labels: container_name, service, stream)
Grafana Alloy (loki.process pipeline transforms)
  ↓ (Sends HTTP POST)
Server 2: Loki :3100/loki/api/v1/push
  ↓ (Stores with labels)
Loki storage (/loki/chunks, /loki/boltdb-shipper-active)
  ↓ (Grafana queries via LogQL)
Grafana Dashboard: Logs Panel (displays log entries)
```

**Example Log Query in Grafana:**
```
{container_name="travery-app"} | logfmt
```
This finds all logs from the Spring Boot container, parses them as key=value pairs, and displays them.

---

### 4.2 Data Flow: Metrics

```
Spring Boot (generates metrics internally via Micrometer)
  ↓ (Exposes via)
/actuator/prometheus endpoint :8080
  ↓ (Alloy scrapes every 30 seconds)
Grafana Alloy (prometheus.scrape.spring_boot)
  ↓ (Adds labels: server=server1, instance=travery-backend, job=spring-boot)
Grafana Alloy (prometheus.relabel)
  ↓ (Sends HTTP POST)
Server 2: Prometheus :9090/api/v1/write (remote_write API)
  ↓ (Stores in TSDB)
Prometheus storage (/prometheus)
  ↓ (Grafana queries via PromQL)
Grafana Dashboard: Metric Panel (displays graphs, gauges, etc.)
```

**Example Metric Queries in Grafana:**
```
jvm_memory_used_bytes{instance="travery-backend"}  # JVM memory usage
http_requests_total{job="spring-boot"}              # Total HTTP requests
node_cpu_seconds_total{server="server1"}             # CPU time
```

---

### 4.3 Data Flow: Host Metrics

```
Host OS (CPU, memory, disk, network)
  ↓ (Alloy reads via /proc, /sys, /rootfs)
Grafana Alloy (prometheus.exporter.unix collects)
  ↓ (Formats as Prometheus metrics)
Grafana Alloy (prometheus.relabel adds labels)
  ↓ (Sends HTTP POST)
Server 2: Prometheus :9090/api/v1/write (same remote_write endpoint)
  ↓ (Stores alongside app metrics)
Prometheus storage
  ↓ (Grafana queries)
Grafana Dashboard: Host Metrics Panel
```

---

## 5. CI/CD Pipeline (GitHub Actions)

### 5.1 Workflow Trigger

**Event:** Push to `main` branch (or pull request to `main`)

**Workflow File:** `.github/workflows/ci.yml`

---

### 5.2 Job 1: Format Check

- **Runs:** `spotless:check` (Java code formatting)
- **Purpose:** Ensure code follows style guide
- **Fails Build:** If formatting is incorrect

---

### 5.3 Job 2: Build & Test

- **Runs:** `mvn clean verify`
- **Purpose:** Compile code and run unit/integration tests
- **Fails Build:** If tests fail
- **Artifact:** `target/*.jar` (uploaded for next job)

---

### 5.4 Job 3: Build & Push Docker Image

- **Trigger:** Only on push to `main` (not on PRs)
- **Steps:**
  1. Download JAR artifact from Job 2
  2. Build Docker image from `Dockerfile`
  3. Log in to GitHub Container Registry (GHCR) using `GHCR_PAT` secret
  4. Push image to `ghcr.io/<username>/travery:latest` and `ghcr.io/<username>/travery:<commit-sha>`
- **Purpose:** Make latest code available for deployment

---

### 5.5 Job 4: Deploy to Server 1 (NEW)

- **Trigger:** Only on push to `main` (not on PRs)
- **Prerequisite:** Job 3 (build-and-push) must succeed
- **SSH Action:** `appleboy/ssh-action` v1.2.2
- **Steps:**
  1. SSH into Server 1 using private key (`SERVER1_SSH_KEY` secret)
  2. Authenticate with GHCR using `GHCR_PAT` secret
  3. Navigate to `~/travery-deployment` directory
  4. Run `docker compose -f docker-compose-prod.yml pull app` (pull latest image)
  5. Run `docker compose -f docker-compose-prod.yml up -d app` (restart only app container)
  6. Clean up old images: `docker image prune -f`
  7. Verify: `docker ps | grep travery-app`
- **Purpose:** Automatically deploy new code to production after merge to main
- **Zero-Downtime:** Only the app container is restarted; Nginx, PostgreSQL, Redis, Alloy stay running

---

### 5.6 GitHub Secrets Required

| Secret | Value | Source |
|--------|-------|--------|
| `SERVER1_HOST` | Server 1's public IP or hostname | GCP Compute Engine console |
| `SERVER1_USERNAME` | SSH username (e.g., `deploy`) | Created when setting up Server 1 |
| `SERVER1_SSH_KEY` | Private SSH key (Ed25519) | Generated locally, public key on Server 1 |
| `GHCR_PAT` | GitHub Personal Access Token | Created in GitHub settings (read:packages scope) |
| `GHCR_USERNAME` | Your GitHub username | For `docker login ghcr.io` |

---

## 6. Key Technologies & Their Roles

| Technology | Role | On Server |
|------------|------|-----------|
| **Spring Boot** | Application logic, business rules | Server 1 |
| **Micrometer** | Metrics library for Spring Boot | Server 1 (app) |
| **PostgreSQL** | Relational database for transactional data | Server 1 |
| **Redis** | In-memory cache and session store | Server 1 |
| **Nginx** | Reverse proxy, SSL termination, API gateway | Server 1 |
| **Grafana Alloy** | Unified observability agent (logs, metrics, host data) | Server 1 |
| **Docker** | Container runtime for all services | Both servers |
| **Docker Compose** | Orchestration for multi-container setup | Both servers |
| **GitHub Actions** | CI/CD automation | External (GitHub.com) |
| **Prometheus** | Time-series metrics database (push model) | Server 2 |
| **Loki** | Log aggregation and storage | Server 2 |
| **Grafana** | Visualization and dashboarding | Server 2 |
| **GCP** | Cloud infrastructure hosting | Both servers |

---

## 7. Security Model

### 7.1 Network Isolation

- **Server 1 & Server 2:** Separated by GCP VPC firewall
- **Docker Networks:** Each server has isolated bridge network
  - `travery-network` on Server 1 (app services)
  - `monitoring-network` on Server 2 (observability services)
- **No Cross-Network Communication:** Containers on different servers can only communicate via mapped host ports

---

### 7.2 Firewall Rules

**Inbound to Server 1:**
- Port `80` (HTTP): Open to internet (for mobile app traffic)
- Port `22` (SSH): Open to GitHub Actions IPs + your IP (for deployments)
- **No inbound observability ports:** Alloy pushes outbound only

**Inbound to Server 2:**
- Port `3100` (Loki): Open to Server 1's IP only (for Alloy to push logs)
- Port `3000` (Grafana): Open to your IP only (for you to view dashboards)
- Port `9090` (Prometheus): Open to your IP only (optional, for debugging)

**Outbound from Server 1:**
- Port `5432` (PostgreSQL): To `postgres` container (internal Docker network)
- Port `6379` (Redis): To `redis` container (internal Docker network)
- Port `3100` (Loki): To Server 2 (Alloy pushes logs)
- Port `9090` (Prometheus remote_write): To Server 2 (Alloy pushes metrics)

---

### 7.3 Secrets Management

**In GitHub Secrets:**
- SSH private key (for deployment)
- GHCR Personal Access Token (for Docker registry authentication)

**In Server 1 `.env` file:**
- Database password
- Redis password
- JWT secret
- Email credentials
- External API keys (Cloudinary, etc.)
- **Note:** `.env` is NOT in git (listed in `.gitignore`)

---

## 8. Resilience & High Availability

### 8.1 Auto-Restart Policy

All containers have `restart: unless-stopped`:
- If a container crashes, Docker automatically restarts it
- If the host reboots, `docker-compose up -d` is run automatically (via systemd or cron)

### 8.2 Health Checks

Each service has health checks with retries:
- If a service fails health check, Docker marks it unhealthy
- After N consecutive failures, Docker can take action (restart, alert)

### 8.3 Data Persistence

- **PostgreSQL:** Data persisted to `postgres_data` volume (survives container restarts)
- **Redis:** Data persisted via AOF (append-only file) to `redis_data` volume
- **Prometheus:** Metrics persisted to `prometheus_data` volume
- **Loki:** Logs persisted to `loki_data` volume
- **Grafana:** Dashboards persisted to `grafana_data` volume

### 8.4 Grafana Alloy WAL (Write-Ahead Log)

- Alloy writes metrics to local WAL before sending to Prometheus
- If Alloy crashes, metrics are replayed on restart
- Ensures no metrics are lost during brief interruptions

---

## 9. Monitoring & Observability of the Observability Stack

**The Problem:** If Alloy fails to send metrics, how do we know?

**Solution:** Alloy logs its own operations to Docker stdout, which can be viewed:
```bash
docker logs travery-alloy
```

Look for:
- `ERROR` messages indicating connection failures
- `INFO` messages showing successful metric/log sends
- `WARN` messages for retries or backpressure

**Manual Verification:**
```bash
# On Server 1, check if metrics are being scraped:
curl http://localhost:8080/actuator/prometheus | head -10

# On Server 2, check if Prometheus received metrics:
curl http://localhost:9090/api/v1/query?query=up

# On Server 2, check if Loki received logs:
curl http://localhost:3100/loki/api/v1/label
```

---

## 10. Deployment Process

### 10.1 Code Change → Deployment Timeline

1. **Developer** commits and pushes to `main`
2. **GitHub Actions** triggered automatically
3. **Format check** runs (1 min)
4. **Build & test** runs (5 min)
5. **Docker build & push** runs (3 min)
6. **SSH deploy** runs (2 min):
   - Pulls latest image from GHCR
   - Restarts app container
   - Health check passes
7. **Total:** ~15 minutes from push to live in production

### 10.2 Rollback Procedure

If deployment causes issues:
1. Identify the issue via Grafana dashboards (CPU spike, error rate up, etc.)
2. Revert the code commit:
   ```bash
   git revert <commit-sha>
   git push origin main
   ```
3. GitHub Actions re-runs, deploys the reverted code
4. Monitoring shows recovery within minutes

---

## 11. Scalability Considerations

### 11.1 Current Setup

- **Single Server 1:** Can handle ~1000 concurrent mobile app users
- **Single Server 2:** Can handle metrics from 100s of servers + logs from 1000s of containers

### 11.2 Scaling Paths

If traffic grows:

**Option A: Vertical Scaling (bigger VMs)**
- Increase Server 1 CPU/RAM (handles more requests)
- Increase Server 2 CPU/RAM (stores more metrics/logs)

**Option B: Horizontal Scaling (multiple app servers)**
- Create Server 1B, 1C, etc. (clones of Server 1)
- Load balancer in front (routes traffic round-robin)
- All instances send observability to same Server 2
- **Note:** This architecture is designed to support this (all push-based)

**Option C: Observability Sharding**
- Multiple Server 2s, each receiving observability from a subset of app servers
- Grafana federation (queries across multiple Prometheus instances)
- **Advanced:** Not needed until very large scale

---

## 12. Configuration Files Summary

| File | Location | Purpose |
|------|----------|---------|
| `application-prod.yml` | Spring Boot classpath | Production config (actuator endpoints, logging) |
| `nginx/nginx.conf` | Server 1 volume | Nginx reverse proxy configuration |
| `alloy/config.yml` | Server 1 volume | Grafana Alloy observability configuration |
| `docker-compose-prod.yml` | Server 1 `~/travery-deployment/` | Compose file defining all services |
| `.env` | Server 1 `~/travery-deployment/` | Environment variables (secrets, credentials) |
| `prometheus/prometheus.yml` | Server 2 `~/observability-stack/` | Prometheus configuration |
| `loki/loki-config.yml` | Server 2 `~/observability-stack/` | Loki configuration |
| `docker-compose-observability.yml` | Server 2 `~/observability-stack/` | Compose file for observability stack |
| `.github/workflows/ci.yml` | GitHub repository | CI/CD pipeline definition |

---

## 13. Common Operations

### 13.1 View App Logs (Real-Time)

**On Server 1:**
```bash
docker logs -f travery-app
```

### 13.2 View Alloy Logs (Debug Observability)

**On Server 1:**
```bash
docker logs -f travery-alloy | grep -i error
```

### 13.3 Restart a Service

**On Server 1:**
```bash
cd ~/travery-deployment
docker compose -f docker-compose-prod.yml restart <service_name>
# Example: docker compose -f docker-compose-prod.yml restart app
```

### 13.4 Check Database Connectivity

**On Server 1:**
```bash
docker exec travery-postgres psql -U travery_user -d travery -c "SELECT version();"
```

### 13.5 Check Redis Connectivity

**On Server 1:**
```bash
docker exec travery-redis redis-cli -a <REDIS_PASSWORD> ping
```

### 13.6 View Metrics in Grafana

1. Open `http://<SERVER2_IP>:3000`
2. Go to Explore (left sidebar)
3. Select Prometheus as data source
4. Query: `jvm_memory_used_bytes{instance="travery-backend"}`
5. View real-time metric

### 13.7 View Logs in Grafana

1. Open `http://<SERVER2_IP>:3000`
2. Go to Explore
3. Select Loki as data source
4. Query: `{container_name="travery-app"}`
5. View logs with labels

---

## 14. Troubleshooting Guide

### Issue: App Container Won't Start

**Symptoms:** `docker ps` doesn't show `travery-app`

**Diagnosis:**
```bash
docker ps -a  # See all containers, including stopped ones
docker logs travery-app  # View error messages
```

**Common Causes:**
- Database not ready: Check PostgreSQL is running
- Redis not ready: Check Redis is running
- Missing environment variable: Check `.env` file

**Fix:**
```bash
docker compose -f docker-compose-prod.yml up -d postgres redis
sleep 10  # Wait for databases to start
docker compose -f docker-compose-prod.yml up -d app
```

### Issue: No Metrics in Prometheus

**Symptoms:** Grafana queries return "No data"

**Diagnosis:**
1. Check Alloy is running: `docker ps | grep alloy`
2. Check Alloy logs: `docker logs travery-alloy`
3. Check if metrics endpoint is working: `curl http://localhost:8080/actuator/prometheus`

**Common Causes:**
- Spring Boot `/actuator/prometheus` endpoint not enabled
- `PROMETHEUS_HOST` environment variable wrong
- Network firewall blocking Server 1 → Server 2 communication

**Fix:**
1. Verify `application-prod.yml` has `include: health,prometheus`
2. Verify `.env` has correct `PROMETHEUS_HOST` (Server 2's private IP)
3. Check GCP VPC firewall allows outbound from Server 1 port 9090

### Issue: No Logs in Loki

**Symptoms:** Grafana Explore → Loki returns no results

**Diagnosis:**
1. Check Alloy logs: `docker logs travery-alloy | grep loki`
2. Verify Loki is running on Server 2: `docker logs monitoring-loki`

**Common Causes:**
- `LOKI_HOST` environment variable wrong
- Network firewall blocking Server 1 → Server 2 communication
- Loki not listening on port 3100

**Fix:**
1. Verify `.env` has correct `LOKI_HOST`
2. On Server 2, verify Loki is running: `docker ps | grep loki`
3. Test connectivity: `docker exec travery-alloy curl http://${LOKI_HOST}:3100/loki/api/v1/status/buildinfo`

### Issue: Deploy Job Fails in GitHub Actions

**Symptoms:** GitHub Actions shows deploy job as failed (red X)

**Diagnosis:** Click the failed job to view logs

**Common Causes:**
- `SERVER1_SSH_KEY` format incorrect (missing newlines)
- `GHCR_PAT` expired or wrong scope
- Server 1's `~/travery-deployment/` directory doesn't exist
- Network firewall blocking GitHub Actions → Server 1 SSH

**Fix:**
1. Recreate SSH keys with correct format
2. Create new GHCR_PAT with `read:packages` scope
3. Ensure `~/travery-deployment/` exists on Server 1 with docker-compose files
4. Check GCP VPC firewall allows inbound SSH from GitHub Actions IP ranges

---

## 15. Design Principles & Decisions

### 15.1 Why Push Model (Alloy → Server 2)?

**Advantages:**
- Server 1 is stateless (no stored scrape configs)
- Server 1 doesn't need inbound observability ports
- Easier to scale horizontally (multiple app servers all push to same Server 2)
- Single point of observability (all data flows to Server 2)

**Disadvantages:**
- Requires an agent on Server 1 (Alloy)
- Slightly higher latency (agent processes before sending)

---

### 15.2 Why Grafana Alloy (Not Promtail + Node Exporter)?

**Unified Agent Benefits:**
- Single configuration file (not separate Promtail + Node Exporter configs)
- Reduced operational complexity
- Built by Grafana (tight integration with Loki, Prometheus, Grafana)
- Future-proof (supports traces, profiles, additional telemetry)

---

### 15.3 Why Docker Compose (Not Kubernetes)?

**Simpler for Small Team:**
- Easier to understand and debug
- Lower operational overhead
- Suitable for single-server or small multi-server deployments
- Easy local development (`docker-compose-dev.yml`)

**Kubernetes Would Be Better When:**
- Running 10+ app servers
- Need automatic scaling, rolling updates, complex networking
- Team is large enough to manage Kubernetes complexity

---

## 16. Summary

**Server 1 (Application Server):**
- Runs the Travery backend, databases, and observability agent
- Alloy pushes logs and metrics to Server 2
- All inbound traffic through Nginx
- Stateless (can be replaced/restarted)

**Server 2 (Observability Server):**
- Receives and stores metrics and logs from Server 1
- Provides visualization (Grafana)
- Remote from Server 1 (separate VM)

**CI/CD:**
- Push to main → GitHub Actions → Build, Test, Push Docker → Deploy to Server 1 (SSH)
- Zero-downtime deployments (only app container restarts)
- Rollback is simple (revert commit, push, GitHub Actions redeploys)

**Observability:**
- Unified with Grafana Alloy
- All push-based (no inbound ports on Server 1)
- Real-time visibility in Grafana dashboards
- Historical data stored in Prometheus and Loki

This architecture is **simple, secure, scalable, and operationally straightforward** for a team of 1-2 engineers.

---

**End of Architecture Description**
