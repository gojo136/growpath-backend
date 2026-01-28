# SelfGrowth Backend – Run & Verify

## Is the backend set up properly?

**Yes.** When the app starts successfully you should see:

- `Started SelfGrowthBackendApplication in ... seconds`
- `Tomcat started on port 8082 (http) with context path ''`
- `HikariPool-1 - Start completed.`
- `JWT Token Provider initialized successfully with valid secret.`

If you see those lines, the backend is running correctly.

---

## How to run the backend

### Option 1: Maven (recommended)

```bash
cd D:\Selfwork\SelfGrowth_Backend
mvn spring-boot:run
```

### Option 2: IDE

- Run `org.example.SelfGrowthBackendApplication` (main class).

### Option 3: JAR

```bash
mvn package -DskipTests
java -jar target/SelfGrowth_Backend-1.0-SNAPSHOT.jar
```

**Base URL:** `http://localhost:8082`

---

## How to verify it’s running

1. **Health check (no auth):**
   ```bash
   curl http://localhost:8082/api/auth/health
   ```
   Expected: JSON with `"status": "UP"` and `"service": "SelfGrowth Backend"`.

2. **Batch script (Windows):**
   ```bash
   test_backend.bat
   ```
   This calls the health endpoint, then signup and login.

3. **Postman:**  
   `GET http://localhost:8082/api/auth/health` – no headers required.

---

## Root causes when the backend does **not** run

| Symptom | Likely cause | What to do |
|--------|----------------|------------|
| **Port 8082 already in use** | Another app (or another instance) using 8082 | 1) Stop the other process. 2) Or set `server.port=8083` in `application.properties` and use 8083 instead. |
| **Unable to determine Dialect** | Missing `hibernate.dialect` with Supabase pooler | Keep `spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect` in `application.properties`. |
| **Database connection failed** | Wrong Supabase URL, user, password, or network | Check `spring.datasource.url`, `username`, `password` in `application.properties`. Ensure Supabase project is running and reachable (no firewall blocking). |
| **Failed to clean project / delete target** | `target` locked by IDE, app, or antivirus | Stop the app, close the project in the IDE, then run `mvn clean install`. |
| **JWT / security config errors** | Invalid or missing `jwt.secret` | Use a long, random `jwt.secret` in `application.properties` (e.g. 64+ chars). |
| **Bean creation / startup exception** | Missing config, incompatible deps, or bad custom code | Read the **full stack trace** in the console. The last “Caused by” line usually points to the real problem (e.g. missing bean, invalid property). |

---

## Quick checklist before running

- [ ] Java 17 or 21 installed (`java -version`)
- [ ] Maven installed (`mvn -v`)
- [ ] `application.properties`: correct DB URL, user, password; `jwt.secret` set
- [ ] No other process using port 8082 (or you changed `server.port`)
- [ ] Supabase project is up and you can connect (e.g. via Supabase dashboard)

---

## Android app connection

- **Emulator:** use `http://10.0.2.2:8082` as base URL (localhost of host machine).
- **Physical device (same Wi‑Fi):** use `http://<your-PC-IP>:8082` (e.g. `192.168.1.x`).

Ensure the Android app’s base URL matches where the backend is actually running (port 8082 or whatever you set).
