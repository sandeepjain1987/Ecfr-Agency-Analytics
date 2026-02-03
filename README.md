<img width="907" height="393" alt="image" src="https://github.com/user-attachments/assets/82404780-8611-470d-aef2-6a4bcb089d79" />

<img width="630" height="722" alt="image" src="https://github.com/user-attachments/assets/1bbbc403-8b5c-4c93-80a8-9b51dd471849" />



ECFR Agency Analytics
A full‑stack, cloud‑hosted analytics platform that ingests, stores, and visualizes U.S. Federal agency metadata from the Electronic Code of Federal Regulations (eCFR).
Built with Spring Boot, PostgreSQL, React + Vite, and deployed entirely on Render.

 Live Demo
Frontend:
https://ecfr-agency-analytics-ui.onrender.com (ecfr-agency-analytics-ui.onrender.com in Bing)
Backend API:
https://ecfr-agency-analytics.onrender.com/api/agencies (ecfr-agency-analytics.onrender.com in Bing)


Features
Backend (Spring Boot 3 + Java 17)
- Automated ingestion of agency data from the eCFR API
- PostgreSQL persistence with JPA/Hibernate
- Safe, idempotent startup ingestion logic
- REST API for agencies and analytics
- Production‑ready CORS configuration
- Dockerized and deployed on Render

Frontend (React + Vite)
- Clean UI for browsing agency data
- Environment‑driven API base URL
- Fully static build deployed on Render
- Responsive layout and modern component structure
Deployment
- Multi‑stage Dockerfile builds backend JAR inside Docker
- Render Static Site for frontend
- Managed PostgreSQL instance
- Environment‑based configuration for production

🧱 Architecture
frontend/        → React + Vite UI (Render Static Site)
backend/         → Spring Boot API (Render Web Service)
postgresql       → Managed DB on Render


Communication flow:
Frontend → Backend API → PostgreSQL



🔌 API Endpoints
|  |  |  | 
|  | /api/agencies |  | 
|  | /api/metrics |  | 



🗄️ Database
- PostgreSQL (Render)
- Hibernate auto‑manages schema (ddl-auto=update)
- Agency entity stored with ID, name, and metadata fields

⚙️ Backend Setup (Local)
1. Clone the repo
git clone https://github.com/<your-username>/<your-repo>.git
cd backend


2. Create application-dev.properties
Example (H2):
spring.datasource.url=jdbc:h2:file:./data/ecfrdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true


3. Run locally
mvn spring-boot:run -Dspring-boot.run.profiles=dev



🎨 Frontend Setup (Local)
cd frontend
npm install
npm run dev


Environment Variables
Create .env:
VITE_API_BASE_URL=http://localhost:8080



☁️ Render Deployment
Backend
- Dockerized Spring Boot app
- Environment variables:
- SPRING_PROFILES_ACTIVE=render
- SPRING_DATASOURCE_URL
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD
Frontend
- Static Site
- Root Directory: frontend
- Build Command: npm run build
- Publish Directory: dist
- .env.production:

Flow : Frontend → Backend API → PostgreSQL

Backend Setup (Local)

1. Clone the repo

git clone https://github.com/<your-username>/<your-repo>.git
cd backend

2. Create application-dev.properties

spring.datasource.url=jdbc:h2:file:./data/ecfrdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true

3. Run locally
   mvn spring-boot:run -Dspring-boot.run.profiles=dev

Front end setup (local)
cd frontend
npm install
npm run dev

Environment variables:
VITE_API_BASE_URL=http://localhost:8080

Ingestion Logic
On startup:
- If DB schema is not ready → skip ingestion
- If agencies already exist → skip ingestion
- If table exists and is empty → ingest fresh data
This ensures safe, repeatable deployments.






