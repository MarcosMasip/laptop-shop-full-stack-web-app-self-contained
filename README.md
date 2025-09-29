# Laptop Shop – Full‑Stack Web App (Spring Boot + Angular)

This repository is a self‑contained full‑stack web application for a laptop store. It includes:

- Backend: Spring Boot (Java) with Spring Security + JWT, MySQL (JPA/Hibernate)
- Frontend: Angular 18 with Bootstrap

You can run it with Docker (recommended for a quick start) or without Docker using a local MySQL installation. This README gives copy‑paste commands for macOS, Windows, and Linux, plus expected outcomes and cleanup instructions.

Key ports and endpoints:
- Backend: http://localhost:8080
- Frontend: http://localhost:4200
- API base URL: http://localhost:8080/api/v1 (see `frontend/src/app/util/constants.ts`)


## Tech Stack
- Spring Boot 3 (Java 17+), Spring Security, JWT (jjwt)
- MySQL 8, JPA/Hibernate, HikariCP
- Angular 18, Bootstrap 5, RxJS
- Maven (Wrapper included), npm/Angular CLI


## Prerequisites by OS

All platforms
- Git
- Node.js 18.19+ or 20+ (Angular 18 requirement)
- Java 17+ (JDK 17 or newer; 17/21 LTS recommended)

macOS
- Option A (Dockerized DB): Docker Desktop
- Option B (Local DB): Homebrew MySQL

Windows
- Option A (Dockerized DB): Docker Desktop (Windows)
- Option B (Local DB): MySQL installer
- Use PowerShell for the Windows command examples

Linux
- Option A (Dockerized DB): Docker Engine
- Option B (Local DB): MySQL server via your package manager


## Quick Start (Docker – fastest path)

These steps start MySQL in Docker, boot the backend, seed a user, and run the Angular frontend.

1) Start MySQL 8 in Docker

macOS/Linux (bash/zsh):
```bash
docker run -d --name mysql-hklapstore -p 3306:3306 \
    -e MYSQL_DATABASE=hklapstore \
    -e MYSQL_USER=hkl \
    -e MYSQL_PASSWORD=hkl \
    -e MYSQL_ROOT_PASSWORD=root \
    mysql:8
```

Windows (PowerShell):
```powershell
docker run -d --name mysql-hklapstore -p 3306:3306 `
    -e MYSQL_DATABASE=hklapstore `
    -e MYSQL_USER=hkl `
    -e MYSQL_PASSWORD=hkl `
    -e MYSQL_ROOT_PASSWORD=root `
    mysql:8
```

Expected outcome: A container ID is printed. `docker ps` shows a running container exposing 3306.


2) Run the backend (Spring Boot on 8080)

macOS/Linux (bash/zsh):
```bash
cd backend
chmod +x mvnw
SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/hklapstore?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false" \
SPRING_DATASOURCE_USERNAME=hkl \
SPRING_DATASOURCE_PASSWORD=hkl \
./mvnw spring-boot:run
```

Windows (PowerShell):
```powershell
cd backend
$env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/hklapstore?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false"
$env:SPRING_DATASOURCE_USERNAME = "hkl"
$env:SPRING_DATASOURCE_PASSWORD = "hkl"
./mvnw.cmd spring-boot:run
```

Expected outcome: Maven downloads dependencies (first run) and logs include:
- “Tomcat started on port 8080 (http)”
- “Started BackendApplication …”


3) Seed a user and verify auth (new terminal)

The backend auto‑seeds a default user on startup if missing: username `admin`, password `admin123`.

Register a user manually (optional / one‑time):
```bash
curl -X POST http://localhost:8080/api/v1/user/registerUser \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"admin123"}'
```

Expected: 201 JSON with message “User Saved Successfully”. If the user already exists, you may see a 5xx; proceed to login.

Login and copy the token:
```bash
curl -X POST http://localhost:8080/api/v1/user/login \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"admin123"}'
```

Expected: 200 JSON with `data.token` (a JWT). Keep it for testing protected endpoints.


4) Run the frontend (Angular on 4200) – new terminal

macOS/Linux:
```bash
cd frontend
npm ci
npm start
```

Windows (PowerShell):
```powershell
cd frontend
npm ci
npm start
```

Expected outcome:
- “✔ Compiled successfully” and “Local: http://localhost:4200/”
- Open http://localhost:4200 and log in with admin/admin123. You should be redirected to `/dashboard`.


## Quick Start (Non‑Docker – local MySQL)

Use this if you prefer installing MySQL directly on your machine.

1) Install and start MySQL

macOS (Homebrew):
```bash
brew install mysql
brew services start mysql
mysql -u root <<'SQL'
CREATE DATABASE IF NOT EXISTS hklapstore;
CREATE USER IF NOT EXISTS 'hkl'@'localhost' IDENTIFIED BY 'hkl';
GRANT ALL PRIVILEGES ON hklapstore.* TO 'hkl'@'localhost';
FLUSH PRIVILEGES;
SQL
```

Windows (MySQL Installer):
- Install MySQL Server and MySQL Shell/Workbench.
- Create database and user (Workbench or mysql.exe):
```sql
CREATE DATABASE IF NOT EXISTS hklapstore;
CREATE USER IF NOT EXISTS 'hkl'@'localhost' IDENTIFIED BY 'hkl';
GRANT ALL PRIVILEGES ON hklapstore.* TO 'hkl'@'localhost';
FLUSH PRIVILEGES;
```

Linux (Debian/Ubuntu example):
```bash
sudo apt-get update
sudo apt-get install -y mysql-server
sudo service mysql start
mysql -u root <<'SQL'
CREATE DATABASE IF NOT EXISTS hklapstore;
CREATE USER IF NOT EXISTS 'hkl'@'localhost' IDENTIFIED BY 'hkl';
GRANT ALL PRIVILEGES ON hklapstore.* TO 'hkl'@'localhost';
FLUSH PRIVILEGES;
SQL
```

Expected outcome: You can connect to `mysql://localhost:3306/hklapstore` with user `hkl`/`hkl`.


2) Run the backend (same as Docker path, just different DB host if needed)

macOS/Linux:
```bash
cd backend
chmod +x mvnw
SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/hklapstore?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false" \
SPRING_DATASOURCE_USERNAME=hkl \
SPRING_DATASOURCE_PASSWORD=hkl \
./mvnw spring-boot:run
```

Windows (PowerShell):
```powershell
cd backend
$env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/hklapstore?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false"
$env:SPRING_DATASOURCE_USERNAME = "hkl"
$env:SPRING_DATASOURCE_PASSWORD = "hkl"
./mvnw.cmd spring-boot:run
```

Expected outcome: Backend starts on port 8080. Proceed to seed/login and run the frontend as in the Docker path.


## Verify the app end‑to‑end

1) Login at http://localhost:4200 with admin/admin123
2) Browse dashboard, products, clients, orders, cart.
3) API base consumed by the frontend is `http://localhost:8080/api/v1`. The Angular app automatically attaches the JWT via an HTTP interceptor.


## Cleanup / Shutdown

Backend (in the backend terminal):
```bash
Ctrl+C
```
Expected: Spring Boot logs graceful shutdown and returns to your shell.

Frontend (in the frontend terminal):
```bash
Ctrl+C
```
Expected: Angular dev server stops.

Dockerized MySQL:
```bash
docker stop mysql-hklapstore
docker rm mysql-hklapstore
```
Expected: Container stops and is removed. Note: Data is lost unless you mounted a volume; recreating the container gives you a fresh DB. The backend will re‑seed the default admin if missing at next start.

Local MySQL:
- macOS (Homebrew): `brew services stop mysql`
- Linux (Debian/Ubuntu): `sudo service mysql stop`
- Windows: stop the MySQL service from Services or MySQL Notifier.


## Troubleshooting

- Port already in use (8080 or 4200):
    - Backend: run with `SERVER_PORT=8081 ./mvnw spring-boot:run` (macOS/Linux) or `$env:SERVER_PORT=8081; ./mvnw.cmd spring-boot:run` (Windows).
    - Frontend: `npm start -- --port 4300`.
- CORS errors: CORS allows `http://localhost:4200` by default (see `backend/src/main/java/com/hklapstore/backend/config/CORSConfig.java`). If you change the frontend port, update that allowed origin.
- Login 401/403: Ensure you registered the user and that the backend startup logs didn’t show DB connection errors. The login endpoint is `/api/v1/user/login`.
- Database connection errors: Confirm MySQL is running and the `SPRING_DATASOURCE_*` values match your setup.
 - After pulling code updates: Restart both backend and frontend to pick up changes. If the browser still fails to authenticate, clear `localStorage` to remove any stale JWT: open DevTools Console and run `localStorage.clear()`.


## What this repo is for

This repository demonstrates a complete JWT‑secured CRUD app for a laptop shop, suitable as a reference or starter for:
- Admin dashboards managing products, clients, and orders
- Token‑based authentication flows (Angular + Spring Security + JWT)
- Typical Spring Boot + Angular monorepo layouts

If you plan to deploy, add production‑grade configs (externalized DB credentials, stable JWT secret, HTTPS, etc.).


## License

MIT License – see `LICENSE`.
