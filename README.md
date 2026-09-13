# File Transfer Protocol

[svg](https://github.com/ajaydeshmukh29/File_Transfer_Protocol#file-transfer-protocol)

A full-stack file transfer and management system built using React, Spring Boot, MySQL, JWT Authentication, and Socket-based FTP.

## 🚀 Features

[svg](https://github.com/ajaydeshmukh29/File_Transfer_Protocol#-features)

* User Registration and Login
* JWT-based Authentication
* Upload Files
* Download Files
* Delete Files
* Rename Files
* Check File Information and Size
* User-specific File Management
* Socket-based FTP Server

## 🛠️ Technologies

[svg](https://github.com/ajaydeshmukh29/File_Transfer_Protocol#%EF%B8%8F-technologies)

### Frontend

[svg](https://github.com/ajaydeshmukh29/File_Transfer_Protocol#frontend)

* React.js
* Vite
* CSS

### Backend

[svg](https://github.com/ajaydeshmukh29/File_Transfer_Protocol#backend)

* Java
* Spring Boot
* Spring Security
* JWT
* Maven
* Socket Programming

### Database

[svg](https://github.com/ajaydeshmukh29/File_Transfer_Protocol#database)

* MySQL
* JPA / Hibernate

## 📁 Project Structure

[svg](https://github.com/ajaydeshmukh29/File_Transfer_Protocol#-project-structure)

```text
File_Transfer_Protocol/
├── fileflow-backend/
└── fileflow-react/
```

---

## 🐛 Development Mistakes & How I Fixed Them

During the development of this project, I used ChatGPT as a development and debugging assistant. Some suggestions provided by ChatGPT were incorrect, incomplete, or based on assumptions about the actual project configuration.

Instead of blindly applying the suggestions, I verified the errors, checked the actual project configuration, and fixed the issues step by step.

### 1. Vite Was Not Recognized

**Mistake:**
The frontend was started using `npm run dev` before verifying that all required Node.js dependencies were installed.

**Error:**

```text
'vite' is not recognized as an internal or external command,
operable program or batch file.
```

**Fix:**

```bash
npm install
npm run dev
```

After installing the dependencies, Vite started successfully.

**Lesson Learned:**
Always install and verify frontend dependencies before running a Vite project.

---

### 2. Backend Connection Refused

**Mistake:**
The React frontend was tested against the login API before confirming that the Spring Boot backend was running.

**Error:**

```text
POST http://localhost:8080/api/auth/login
ERR_CONNECTION_REFUSED
```

**Fix:**

I started the Spring Boot backend and verified that it was running on port `8080`.

```text
React Frontend
localhost:5173
       ↓
Spring Boot Backend
localhost:8080
```

**Lesson Learned:**
When `ERR_CONNECTION_REFUSED` occurs, first verify that the backend is running and that the frontend is using the correct port and API URL.

---

### 3. Database Schema Mismatch

**Mistake:**
The Java `User` entity expected a `created_at` column, but the existing MySQL `users` table did not contain that column.

**Error:**

```text
Unknown column 'u1_0.created_at'
```

**Investigation:**

I checked the actual database structure using:

```sql
DESCRIBE users;
```

The `created_at` column was missing.

**Fix:**

I added the required `created_at` column to the `users` table.

**Lesson Learned:**
The JPA entity and database table must remain synchronized. When Hibernate reports a missing column, always check the actual database schema.

---

### 4. Login Still Failed After Fixing the Database Error

**Mistake:**
After fixing the `created_at` database error, it was initially assumed that the login problem had also been solved.

However, login was still failing.

**Fix:**

Instead of assuming the problem was solved, I traced the complete authentication flow:

```text
Login.jsx
    ↓
POST /api/auth/login
    ↓
AuthController
    ↓
UserRepository
    ↓
MySQL
    ↓
Password Verification
    ↓
JWT Generation
    ↓
Response
    ↓
React Frontend
```

I continued debugging each layer separately.

**Lesson Learned:**
Fixing one error does not necessarily fix the complete feature. The entire request-response flow must be tested.

---

### 5. Multiple Changes Before Testing

**Mistake:**
During development, multiple files were sometimes modified before testing the previous change.

This made it difficult to identify which change introduced a new error.

**Fix:**

I changed the development approach to:

```text
Change ONE file
      ↓
Run the application
      ↓
Test the change
      ↓
Check logs/errors
      ↓
Confirm the fix
      ↓
Move to the next file
```

**Lesson Learned:**
Making one change at a time makes debugging easier and helps identify the exact source of an error.

---

### 6. Adding Complexity Too Early

**Mistake:**
Spring Security, JWT authentication, and other components were introduced while some basic frontend-backend functionality was still being stabilized.

This increased the number of components that needed to be debugged simultaneously.

**Fix:**

I learned to verify the basic application flow first:

```text
React
  ↓
Spring Boot REST API
  ↓
Service Layer
  ↓
JPA / Hibernate
  ↓
MySQL
```

Then authentication and JWT were tested separately.

**Lesson Learned:**
Build and verify the basic functionality first, then introduce additional complexity step by step.

---

### 7. Assuming Project Structure Instead of Verifying It

**Mistake:**
Some suggestions were based on assumptions about existing classes, files, dependencies, or project structure.

**Fix:**

I checked the actual project structure and existing source code before making changes.

**Lesson Learned:**
Never assume that a class, dependency, API endpoint, or configuration exists. Always verify the actual project first.

---

## 💡 Overall Lesson

The biggest lesson I learned while developing this project was:

> **Do not blindly trust generated code. Understand, verify, implement, and test every change.**

ChatGPT was useful for generating code, explaining concepts, and suggesting possible solutions. However, the final responsibility remained with me to verify the code against the actual project, understand the errors, inspect logs, check the database, and test the application.

This debugging process helped me understand how the **React frontend, Spring Boot backend, REST APIs, JPA/Hibernate, MySQL, Spring Security, JWT, and Socket-based FTP server** work together.
