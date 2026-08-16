# SmartResolve AI: Service Request & Complaint Management System

SmartResolve AI is a production-quality, full-stack web application designed to solve maintenance, repair, and complaint tracking challenges faced by colleges, apartments, companies, offices, and institutions.

Traditionally, maintenance requests are sent through disjointed channels like WhatsApp, phone calls, or paper logs. This system replaces that manual workflow with a centralized, automated platform that uses AI to analyze, prioritize, and categorize tickets, instantly assigns them to staff, and provides robust oversight for administrators.

---

## 🚀 Key Features

### 👤 User Dashboards by Role
- **Resident / Student (USER)**:
  - Register & securely log in.
  - File new complaints with automatic AI classification.
  - View personal ticket lists, statuses, and history logs.
  - Leave comments on own complaints.
  - Close resolved complaints.
- **Technician / Staff (TECHNICIAN)**:
  - View a custom queue of assigned tickets.
  - Leave comments or progress updates.
  - Update complaint status (`ASSIGNED` -> `IN_PROGRESS` -> `RESOLVED`).
- **Platform Administrator (ADMIN)**:
  - Comprehensive analytics dashboard.
  - View all complaints, users, and technicians.
  - Assign tickets to staff.
  - Override ticket categories and priorities.
  - Manage database departments.

### 🧠 Gemini AI-Powered Routing
- Instantly analyzes complaint titles and descriptions upon submission.
- Auto-extracts category, severity priority, suggested department, and a brief summary.
- Drafts a recommended troubleshooting response for technicians.
- **Reliable Fallback**: Uses a deterministic Java rule engine if the Gemini API is offline, rate-limited, or unconfigured, preventing request failures.

### 📈 DSA Implementation
- **Java PriorityQueue**: Organizes active tickets dynamically using a custom comparator (`CRITICAL` > `HIGH` > `MEDIUM` > `LOW`, breaking ties by oldest submission date first).
- **In-Memory HashMaps**: Computes real-time statistics (totals, pending, resolved counts, and category/priority counts) in a single database scan, minimizing query overhead.

---

## 🛠️ Technology Stack

### Backend
- **Core**: Java 17, Spring Boot 3.2.5
- **Security**: Spring Security 6.x (Stateless session, custom JWT filter, BCrypt password hashing)
- **Database**: Spring Data JPA (Hibernate), MySQL 8.0
- **Validation**: Jakarta Bean Validation (`@NotBlank`, `@Email`, etc.)
- **Documentation**: Springdoc OpenAPI / Swagger UI

### Frontend
- **Core**: React 18, Vite, JavaScript
- **Routing**: React Router DOM 6
- **HTTP Client**: Axios (Centralized instance with JWT auth interceptors)
- **Styling**: Vanilla CSS (Space-dark theme, glassmorphism, responsive grid layouts)

---

## 🗄️ Database Design

The system runs on the `complaint_management_db` database with 6 normalized tables:

1. **`users`**: Manages accounts and roles (`ADMIN`, `TECHNICIAN`, `USER`).
2. **`departments`**: Contains administrative work groups (IT, Plumbing, Electrical, etc.).
3. **`complaints`**: Core ticket table storing title, description, statuses, AI diagnostics, and references.
4. **`complaint_comments`**: Stores discussion streams for each ticket.
5. **`complaint_history`**: Audit trail tracking status transitions.
6. **`notifications`**: Stores system alerts for stakeholders.

---

## 🔑 Authentication Flow

```text
Register User ➔ Hash Password (BCrypt) ➔ Save in MySQL
                                             │
                                             ▼
Login Request ➔ Spring Security validates ➔ Generate JWT token
                                             │
                                             ▼
Frontend stores JWT ➔ Axios interceptor appends Bearer Token
                                             │
                                             ▼
Request received ➔ JWT Authentication Filter validates ➔ Controller execution
```

---

## 📡 REST API List

### 🔓 Public / Authentication Endpoints
- `POST /api/auth/register` - Create user profile.
- `POST /api/auth/login` - Verify credentials and obtain JWT access token.
- `GET /api/auth/me` - Fetch profile of the active session.

### 📋 Complaint Endpoints
- `POST /api/complaints` - Create a complaint (Trigger AI analysis).
- `GET /api/complaints` - List complaints (Filtered based on role).
- `GET /api/complaints/{id}` - Get full complaint details.
- `PUT /api/complaints/{id}` - Update status (User closes ticket).
- `POST /api/complaints/{id}/comments` - Add comment.
- `GET /api/complaints/{id}/comments` - Fetch comment thread.
- `GET /api/complaints/{id}/history` - Fetch audit logs.

### ⚙️ Admin Endpoints
- `GET /api/admin/dashboard` - Get platform metrics (Uses HashMap).
- `GET /api/admin/complaints` - List all complaints.
- `GET /api/admin/complaints/prioritized` - Prioritized queue (Uses PriorityQueue).
- `PUT /api/admin/complaints/{id}/assign` - Dispatch technician to complaint.
- `PUT /api/admin/complaints/{id}/priority` - Override ticket priority.
- `PUT /api/admin/complaints/{id}/category` - Override ticket category.
- `GET /api/admin/users` - List all users.
- `GET /api/admin/technicians` - List all technicians.

### 🔧 Technician Endpoints
- `GET /api/technician/complaints` - View assigned complaints.
- `PUT /api/technician/complaints/{id}/status` - Update status (`IN_PROGRESS`/`RESOLVED`).

---

## ⚙️ Setup & Installation

### Prerequisites
- Java 17+ Installed.
- Node.js v18+ and npm installed.
- MySQL Server 8.0 running on port 3306.

### 1. Database Setup
Ensure your MySQL server is running and log in to execute the schema and seed scripts:
```bash
# From project root
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed.sql
```
*Note: If the password is `password`, you can run `mysql -u root -ppassword` directly.*

### 2. Backend Setup
1. Copy the `.env.example` in the root folder to `.env` (or configure environment variables locally).
2. Navigate to the `backend/` directory:
```bash
cd backend
# Build the project (runs tests automatically)
mvn clean install
# Run the Spring Boot application
mvn spring-boot:run
```
The backend server will launch at `http://localhost:8080`.
OpenAPI Swagger docs can be accessed at `http://localhost:8080/swagger-ui/index.html`.

### 3. Frontend Setup
1. Navigate to the `frontend/` directory:
```bash
cd frontend
# Install node packages
npm install
# Run Vite local dev server
npm run dev
```
The React frontend will boot at `http://localhost:5173`.

---

## 👥 Demo Accounts

For testing, use the following pre-seeded credentials (all passwords are `password`):

- **Platform Admin**:
  - Email: `admin@example.com`
  - Password: `password`
- **Technician Staff**:
  - Email: `technician@example.com`
  - Password: `password`
- **Resident User**:
  - Email: `user@example.com`
  - Password: `password`
