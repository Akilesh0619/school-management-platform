# Enterprise School Management System (EduMaster)

A complete, production-ready enterprise School Management System built with a Spring Boot 3 + Java 21 REST API backend, MySQL database with Flyway migrations, Redis token/session management, and a React 19 + Vite + Tailwind CSS + Framer Motion glassmorphic frontend.

---

## System Architecture & Tech Stack

### Backend
- **Framework**: Spring Boot 3.3.1 (Java 21)
- **Security**: Spring Security + JWT Authentication with Refresh Token Rotation
- **Session & Caching**: Redis (Spring Data Redis)
- **Database**: MySQL 8.0 with Flyway Database Migrations (`V1__init_schema.sql`, `V2__seed_data.sql`)
- **API Documentation**: OpenAPI 3 with Swagger UI (`/swagger-ui/index.html`)
- **Logging**: SLF4J + Logback with rolling file logs (`./logs/school-system.log`)
- **Utility Features**: ZXing for QR Code & Barcode Generation, Spring Mail for HTML OTPs & Receipts, Global Exception Handling (`@ControllerAdvice`)

### Frontend
- **Core**: React 19, Vite, TypeScript
- **Styling**: Tailwind CSS with Glassmorphism, Dark/Light Mode
- **Animations**: Framer Motion
- **Icons & Charts**: Lucide React, Chart.js, React-ChartJS-2
- **State & HTTP**: React Context API, Axios with Automatic Token Refresh Interceptor

---

## Key Features & Modules

1. **Authentication & RBAC**:
   - Roles: `ROLE_SUPER_ADMIN`, `ROLE_ADMIN`, `ROLE_TEACHER`, `ROLE_STUDENT`, `ROLE_PARENT`
   - Permission-based authorization controls (`READ_STUDENTS`, `WRITE_MARKS`, etc.)
   - Login, OTP Verification, Password Reset, Refresh Token Rotation, Rate Limiting, Account Locking

2. **Core Modules**:
   - **Student Management**: Full CRUD, admission numbers, parents linking, QR Code ID cards.
   - **Teacher Management**: Qualifications, experience, department stats, salary tracking.
   - **Parent Module**: Linked children, fee statuses, student grades monitoring.
   - **Class & Subject Management**: Grade structures, credit mapping, teacher assignments.
   - **Timetable Generator**: Automatic conflict detection (room overlaps, teacher double-booking, class period collisions).
   - **Attendance System**: Daily student attendance marking, summary analytics.
   - **Marks & Exams**: Exam grade entry, automatic percentage & CGPA calculation.
   - **Fees & Finance**: Fee structures, payment collection, digital receipt generation, income/expense ledgers.
   - **Notice Board & Events**: Target-audience bulletins, calendar schedules.
   - **Inventory Assets**: School asset inventory tracking.
   - **Global Search**: Cross-module entity search drawer.

---

## Deployment & Running Locally

### Option 1: Running with Docker Compose (Recommended)
```bash
docker-compose up --build
```
- Frontend SPA: [http://localhost:5173](http://localhost:5173)
- Backend APIs: [http://localhost:8080](http://localhost:8080)
- Swagger Documentation: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Option 2: Cloud Deployment

#### Railway (Backend & MySQL)
1. Provision a MySQL database on Railway.
2. Deploy the `backend` folder to Railway as a Spring Boot service.
3. Configure Environment Variables on Railway:
   - `SPRING_DATASOURCE_URL`: `jdbc:mysql://<RAILWAY_HOST>:<PORT>/<DB_NAME>`
   - `SPRING_DATASOURCE_USERNAME`: `<RAILWAY_USER>`
   - `SPRING_DATASOURCE_PASSWORD`: `<RAILWAY_PASSWORD>`
   - `JWT_SECRET`: `<YOUR_RANDOM_SECRET_KEY>`

#### Vercel (Frontend SPA)
1. Import the `frontend` folder into Vercel.
2. Vercel automatically reads `vercel.json` for SPA routing fallback.
3. Set `VITE_API_BASE_URL` to your Railway backend URL.

---

## Demo Accounts (Pre-Seeded)

- **Super Admin**: `superadmin` / `password`
- **Admin**: `admin` / `password`
- **Teacher**: `teacher_smith` / `password`
- **Student**: `student_john` / `password`
- **Parent**: `parent_doe` / `password`
