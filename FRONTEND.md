# EduPedu API Documentation

> **Base URL:** `http://localhost:8080`
> **Content-Type:** `application/json`
> **Authentication:** Bearer JWT Token (RSA256 signed)

---

## Table of Contents

1. [Authentication Flow](#1-authentication)
2. [Universities](#2-universities)
3. [Users](#3-users)
4. [Faculties](#4-faculties)
5. [Subjects](#5-subjects)
6. [Teachers](#6-teachers)
7. [Students](#7-students)
8. [Student Groups](#8-student-groups)
9. [Courses & LMS](#9-courses--lms)
10. [Enrollments](#10-enrollments)
11. [Grades](#11-grades)
12. [Schedule](#12-schedule)
13. [Attendance](#13-attendance)
14. [Announcements](#14-announcements)
15. [Messages](#15-messages)
16. [Tests & Quizzes](#16-tests--quizzes)
17. [Progress Tracking](#17-progress-tracking)
18. [Enums Reference](#18-enums-reference)
19. [Role-Based Access Matrix](#19-role-based-access-matrix)
20. [Error Handling](#20-error-handling)

---

## Authentication Headers

All authenticated endpoints require:

```
Authorization: Bearer <access_token>
```

---

## 1. Authentication

> **No authentication required** for these endpoints.

### 1.1 Register

Creates a new user account.

```
POST /api/v1/register
```

**Request Body:**

```json
{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe",
  "role": "ROLE_STUDENT"
}
```

| Field      | Type   | Required | Description                                                               |
|------------|--------|----------|---------------------------------------------------------------------------|
| email      | string | ✅       | Must be unique, valid email                                               |
| password   | string | ✅       | User password                                                             |
| firstName  | string | ✅       | First name                                                                |
| lastName   | string | ✅       | Last name                                                                 |
| role       | string | ❌       | One of the [Role](#roles) enum values. Defaults to `ROLE_STUDENT` if null |

**Response:** `200 OK`

```json
{
  "success": true,
  "message": "User created successfully",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "ROLE_STUDENT",
    "enabled": true,
    "emailVerified": false
  },
  "userType": null
}
```

---

### 1.2 Login

Authenticates a user and returns JWT tokens.

```
POST /api/v1/auth/login
```

**Request Body:**

```json
{
  "email": "user@example.com",
  "password": "SecurePass123!"
}
```

**Response:** `200 OK`

```json
{
  "access_token": "eyJhbGciOiJSUzI1NiJ9...",
  "refresh_token": "eyJhbGciOiJSUzI1NiJ9...",
  "token_type": "Bearer"
}
```

> **⚠️ Important:** Store both tokens. The `access_token` expires in **24 hours** (configurable), the `refresh_token` expires in **7 days**.

**Frontend Integration:**

```javascript
// Store tokens after login
const response = await fetch('/api/v1/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email, password })
});
const { access_token, refresh_token } = await response.json();

localStorage.setItem('access_token', access_token);
localStorage.setItem('refresh_token', refresh_token);
```

---

### 1.3 Refresh Token

Exchanges a valid refresh token for new access + refresh tokens.

```
POST /api/v1/auth/refresh
```

**Request Body:**

```json
{
  "refresh_token": "eyJhbGciOiJSUzI1NiJ9..."
}
```

> **Note:** Field name is `refresh_token` (snake_case), matching the login response.

**Response:** `200 OK`

```json
{
  "access_token": "eyJhbGciOiJSUzI1NiJ9...(new)",
  "refresh_token": "eyJhbGciOiJSUzI1NiJ9...(new)",
  "token_type": "Bearer"
}
```

**Frontend Integration — Auto-refresh interceptor:**

```javascript
async function fetchWithAuth(url, options = {}) {
  let token = localStorage.getItem('access_token');
  options.headers = {
    ...options.headers,
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  };

  let response = await fetch(url, options);

  if (response.status === 403 || response.status === 401) {
    // Try refreshing the token
    const refreshResponse = await fetch('/api/v1/auth/refresh', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        refresh_token: localStorage.getItem('refresh_token')
      })
    });

    if (refreshResponse.ok) {
      const tokens = await refreshResponse.json();
      localStorage.setItem('access_token', tokens.access_token);
      localStorage.setItem('refresh_token', tokens.refresh_token);

      // Retry with new token
      options.headers['Authorization'] = `Bearer ${tokens.access_token}`;
      response = await fetch(url, options);
    } else {
      // Refresh failed — redirect to login
      window.location.href = '/login';
    }
  }

  return response;
}
```

---

### 1.4 Admin Registration

Creates a user with full profile (student/teacher) in one step. **Requires ADMIN role.**

```
POST /api/v1/admin/registerNewUser
```

**Request Body:**

```json
{
  "email": "teacher@uni.edu",
  "password": "SecurePass123!",
  "firstName": "Jane",
  "lastName": "Smith",
  "phoneNumber": "+1234567890",
  "role": "ROLE_TEACHER",
  "university": { "id": 1 },
  "studentGroupId": null,
  "subjects": [{ "id": 1 }, { "id": 2 }]
}
```

---

### 1.5 Forgot Password

```
POST /api/v1/forgot-password?email=user@example.com
```

**Response:** `200 OK` — Sends a password reset email.

---

### 1.6 Reset Password

```
POST /api/v1/reset-password?token=<reset_token>&password=NewPass123!&confirmPassword=NewPass123!
```

---

## 2. Universities

> **Requires:** `ROLE_ADMIN`

### Endpoints

| Method | Endpoint                    | Description          | Status |
|--------|-----------------------------|----------------------|--------|
| GET    | `/api/v1/universities`      | List all             | 200    |
| GET    | `/api/v1/universities/{id}` | Get by ID            | 200    |
| POST   | `/api/v1/universities`      | Create new           | 201    |
| PUT    | `/api/v1/universities/{id}` | Update               | 200    |
| DELETE | `/api/v1/universities/{id}` | Delete               | 204    |

### Create/Update University

```json
{
  "name": "MIT",
  "address": "77 Massachusetts Ave, Cambridge, MA",
  "phone": "617-253-1000",
  "email": "admissions@mit.edu"
}
```

---

## 3. Users

> **Requires:** `ROLE_ADMIN`

| Method | Endpoint                       | Description            | Status |
|--------|--------------------------------|------------------------|--------|
| GET    | `/api/v1/users`                | List all users         | 200    |
| GET    | `/api/v1/users/{id}`           | Get user by ID         | 200    |
| GET    | `/api/v1/users/email/{email}`  | Get user by email      | 200    |
| GET    | `/api/v1/users/university?universityId={id}` | Users by university | 200 |
| POST   | `/api/v1/users`                | Create user            | 201    |
| DELETE | `/api/v1/users/{id}`           | Delete user            | 204    |

**Response format:**

```json
{
  "id": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "ROLE_STUDENT",
  "universityId": 1,
  "universityName": "MIT"
}
```

---

## 4. Faculties

> **Requires:** `ROLE_ADMIN` or `ROLE_UNIVERSITY_ADMIN`

| Method | Endpoint                                         | Description               | Status |
|--------|--------------------------------------------------|---------------------------|--------|
| GET    | `/api/v1/faculties`                              | List all faculties        | 200    |
| GET    | `/api/v1/faculties/{id}`                         | Get by ID                 | 200    |
| GET    | `/api/v1/faculties/university?universityId={id}` | Faculties by university   | 200    |
| POST   | `/api/v1/faculties`                              | Create faculty            | 201    |
| PUT    | `/api/v1/faculties/{id}`                         | Update faculty            | 200    |
| DELETE | `/api/v1/faculties/{id}`                         | Delete faculty            | 204    |

---

## 5. Subjects

> **Requires:** `ROLE_ADMIN` or `ROLE_UNIVERSITY_ADMIN`

| Method | Endpoint                                     | Description              | Status |
|--------|----------------------------------------------|--------------------------|--------|
| GET    | `/api/v1/subjects`                           | List all subjects        | 200    |
| GET    | `/api/v1/subjects/{id}`                      | Get by ID                | 200    |
| GET    | `/api/v1/subjects/university/{universityId}` | Subjects by university   | 200    |
| POST   | `/api/v1/subjects`                           | Create subject           | 201    |
| PUT    | `/api/v1/subjects/{id}`                      | Update subject           | 200    |
| DELETE | `/api/v1/subjects/{id}`                      | Delete subject           | 204    |

### Create/Update Subject

```json
{
  "name": "Linear Algebra",
  "description": "Introductory course on linear algebra",
  "credits": 3
}
```

---

## 6. Teachers

> **Requires:** `ROLE_ADMIN` or `ROLE_UNIVERSITY_ADMIN`

| Method | Endpoint                                          | Description                  | Status |
|--------|---------------------------------------------------|------------------------------|--------|
| GET    | `/api/v1/teachers`                                | List all teachers            | 200    |
| GET    | `/api/v1/teachers/{id}`                           | Get by ID                    | 200    |
| GET    | `/api/v1/teachers/user/{userId}`                  | Get by user ID               | 200    |
| GET    | `/api/v1/teachers/university?universityId={id}`   | Teachers by university       | 200    |
| POST   | `/api/v1/teachers`                                | Create teacher profile       | 201    |
| PUT    | `/api/v1/teachers/{id}`                           | Update teacher               | 200    |
| DELETE | `/api/v1/teachers/{id}`                           | Delete teacher               | 204    |
| PUT    | `/api/v1/teachers/{teacherId}/subjects`           | Assign subjects to teacher   | 200    |
| GET    | `/api/v1/teachers/getWorkload?teacherId={id}&year={y}&month={m}` | Get teacher workload | 200 |

### Create Teacher

```json
{
  "userId": 5,
  "employeeNumber": "EMP-001",
  "subjectIds": [1, 2, 3]
}
```

### Assign Subjects

```
PUT /api/v1/teachers/{teacherId}/subjects
Body: [1, 2, 3]  // Array of subject IDs
```

---

## 7. Students

> **Requires:** `ROLE_ADMIN` or `ROLE_UNIVERSITY_ADMIN`

| Method | Endpoint                                          | Description                     | Status |
|--------|---------------------------------------------------|---------------------------------|--------|
| GET    | `/api/v1/students`                                | List all students               | 200    |
| GET    | `/api/v1/students/{id}`                           | Get by ID                       | 200    |
| GET    | `/api/v1/students/user/{userId}`                  | Get by user ID                  | 200    |
| GET    | `/api/v1/students/group/{groupId}`                | Students by group               | 200    |
| GET    | `/api/v1/students/university?universityId={id}`   | Students by university          | 200    |
| GET    | `/api/v1/students/unassigned?universityId={id}`   | Unassigned students             | 200    |
| POST   | `/api/v1/students`                                | Create student profile          | 201    |
| PUT    | `/api/v1/students/{id}`                           | Update student                  | 200    |
| DELETE | `/api/v1/students/{id}`                           | Delete student                  | 204    |
| PUT    | `/api/v1/students/{studentId}/group`              | Assign student to group         | 200    |
| PUT    | `/api/v1/students/bulk-assign`                    | Bulk assign students to group   | 200    |

### Create Student

```json
{
  "userId": 10,
  "studentNumber": "STU-2024-001",
  "accountNumber": "ACC-001",
  "parentPhone": "+1234567890",
  "studentGroupId": 1
}
```

### Assign Student to Group

```
PUT /api/v1/students/{studentId}/group
Body: 3  // studentGroupId as Long
```

### Bulk Assign

```json
{
  "studentIds": [1, 2, 3, 4],
  "classGroupId": 2
}
```

---

## 8. Student Groups

> **Requires:** `ROLE_ADMIN` or `ROLE_UNIVERSITY_ADMIN`

| Method | Endpoint                       | Description          | Status |
|--------|--------------------------------|----------------------|--------|
| GET    | `/api/v1/student-groups`       | List all groups      | 200    |
| GET    | `/api/v1/student-groups/{id}`  | Get by ID            | 200    |
| POST   | `/api/v1/student-groups`       | Create group         | 201    |
| PUT    | `/api/v1/student-groups/{id}`  | Update group         | 200    |
| DELETE | `/api/v1/student-groups/{id}`  | Delete group         | 204    |

---

## 9. Courses & LMS

> **Requires:** Any authenticated role (`ADMIN`, `UNIVERSITY_ADMIN`, `TEACHER`, `STUDENT`)

| Method | Endpoint                                              | Description              | Status |
|--------|-------------------------------------------------------|--------------------------|--------|
| GET    | `/api/v1/courses/catalog`                             | Browse course catalog    | 200    |
| GET    | `/api/v1/courses/my`                                  | My created courses       | 200    |
| GET    | `/api/v1/courses/{id}`                                | Get course with modules  | 200    |
| POST   | `/api/v1/courses`                                     | Create course            | 200    |
| PUT    | `/api/v1/courses/{id}`                                | Update course            | 200    |
| DELETE | `/api/v1/courses/{id}`                                | Delete course            | 204    |
| POST   | `/api/v1/courses/{courseId}/modules`                   | Add module to course     | 200    |
| POST   | `/api/v1/courses/{courseId}/modules/{moduleId}/lessons`| Add lesson to module     | 200    |

### Create Course

```json
{
  "title": "Introduction to Programming",
  "description": "Learn the basics of programming with Python",
  "enrollmentPassword": "optional-password",
  "isPublic": true
}
```

### Create Module

```json
{
  "title": "Module 1: Variables & Types"
}
```

### Create Lesson

```json
{
  "title": "Variables in Python",
  "content": "A variable is a named storage location...",
  "contentType": "TEXT",
  "fileUrl": null
}
```

| contentType | Description       |
|-------------|-------------------|
| `TEXT`      | Rich text content |
| `PDF`       | PDF document      |
| `VIDEO`     | Video content     |

### Course Detail Response

```json
{
  "course": {
    "id": 1,
    "title": "Introduction to Programming",
    "description": "...",
    "isPublic": true,
    "hasPassword": false,
    "instructorName": "Jane Smith",
    "instructorId": 1,
    "universityName": "MIT",
    "universityId": 1
  },
  "modules": [
    {
      "id": 1,
      "title": "Module 1: Variables",
      "orderIndex": 0,
      "lessons": [
        {
          "id": 1,
          "title": "Variables in Python",
          "content": "...",
          "contentType": "TEXT",
          "fileUrl": null,
          "orderIndex": 0
        }
      ],
      "tests": [
        { "id": 1, "title": "Quiz 1" }
      ]
    }
  ]
}
```

---

## 10. Enrollments

> **Requires:** Any authenticated role

| Method | Endpoint                              | Description                 | Status |
|--------|---------------------------------------|-----------------------------|--------|
| POST   | `/api/v1/enrollments`                 | Enroll in a course          | 200    |
| GET    | `/api/v1/enrollments/my`              | My enrollments              | 200    |
| GET    | `/api/v1/enrollments/course/{courseId}`| Course enrollments (admin)  | 200    |

### Enroll in Course

```json
{
  "courseId": 1,
  "password": "enrollment-password-if-required"
}
```

### Enrollment Response

```json
{
  "id": 1,
  "studentId": 5,
  "studentName": "John Doe",
  "courseId": 1,
  "courseTitle": "Introduction to Programming",
  "enrolledAt": "2026-04-14T13:00:00"
}
```

---

## 11. Grades

> **Requires:** Any authenticated role

| Method | Endpoint                              | Description                          | Status |
|--------|---------------------------------------|--------------------------------------|--------|
| GET    | `/api/v1/grades`                      | My grades (or all for admins)        | 200    |
| GET    | `/api/v1/grades/subject/{subjectId}`  | My grades by subject                 | 200    |
| GET    | `/api/v1/grades/averages`             | My grade averages per subject        | 200    |
| GET    | `/api/v1/grades/student/{studentId}`  | Specific student's grades (teachers) | 200    |
| POST   | `/api/v1/grades`                      | Create/assign a grade (teachers)     | 200    |
| DELETE | `/api/v1/grades/{id}`                 | Delete a grade                       | 204    |

> **Note:** Admin and University Admin users see all grades. Students see only their own.

### Create Grade

```json
{
  "studentId": 5,
  "takenClassId": 1,
  "value": 92.5,
  "maxValue": 100.0,
  "gradeType": "MIDTERM",
  "description": "Midterm Exam",
  "date": "2026-04-14"
}
```

### Grade Averages Response

```json
{
  "Linear Algebra": 87.5,
  "Physics": 92.0,
  "Programming": 95.3
}
```

---

## 12. Schedule

> **Requires:** Any authenticated role

| Method | Endpoint                                    | Description                        | Status |
|--------|---------------------------------------------|------------------------------------|--------|
| GET    | `/api/v1/schedule/week`                     | My weekly schedule (role-based)    | 200    |
| GET    | `/api/v1/schedule/class/{classGroupId}`     | Schedule for a specific class      | 200    |
| GET    | `/api/v1/schedule/teacher/{teacherId}`      | Schedule for a specific teacher    | 200    |
| POST   | `/api/v1/schedule`                          | Create a single schedule entry     | 200    |
| POST   | `/api/v1/schedule/generate`                 | Auto-generate schedule             | 200    |
| DELETE | `/api/v1/schedule/{id}`                     | Delete a schedule entry            | 204    |

> **`GET /schedule/week` behavior by role:**
> - **ROLE_STUDENT** → returns schedule for the student's class group
> - **ROLE_TEACHER** → returns the teacher's schedule
> - **ROLE_ADMIN / ROLE_UNIVERSITY_ADMIN** → returns all schedules

### Create Schedule Entry

```json
{
  "studentGroupId": 1,
  "classId": 3,
  "dayOfWeek": "MONDAY",
  "startTime": "09:00",
  "endTime": "10:30",
  "room": "Building A, Room 101",
  "lessonNumber": 1
}
```

### Auto-Generate Schedule

```json
{
  "dayStartTime": "08:00",
  "dayEndTime": "17:00",
  "lessonDurationMinutes": 90,
  "breakDurationMinutes": 15,
  "classMappings": [
    {
      "classId": 1,
      "studentGroupIds": [1, 2]
    },
    {
      "classId": 2,
      "studentGroupIds": [3]
    }
  ]
}
```

---

## 13. Attendance

> **Requires:** Any authenticated role

| Method | Endpoint                                                     | Description                        | Status |
|--------|--------------------------------------------------------------|------------------------------------|--------|
| GET    | `/api/v1/attendance`                                         | My attendance (role-based)         | 200    |
| GET    | `/api/v1/attendance/stats`                                   | Attendance statistics              | 200    |
| GET    | `/api/v1/attendance/range?startDate={}&endDate={}`           | Attendance by date range           | 200    |
| GET    | `/api/v1/attendance/student/{studentId}`                     | Specific student's attendance      | 200    |
| GET    | `/api/v1/attendance/schedule/{scheduleId}?date={}`           | Attendance for a class on date     | 200    |
| GET    | `/api/v1/attendance/reports/attendance?startDate={}&endDate={}` | Attendance report              | 200    |
| POST   | `/api/v1/attendance`                                         | Mark attendance (teachers)         | 200    |

> **Date format:** `YYYY-MM-DD` (ISO 8601)

### Mark Attendance (Batch)

```json
{
  "scheduleId": 1,
  "date": "2026-04-14",
  "attendanceRecords": [
    { "studentId": 1, "status": "PRESENT" },
    { "studentId": 2, "status": "ABSENT" },
    { "studentId": 3, "status": "LATE" },
    { "studentId": 4, "status": "EXCUSED" }
  ]
}
```

### Attendance Stats Response

```json
{
  "PRESENT": 45,
  "ABSENT": 3,
  "LATE": 5,
  "EXCUSED": 2
}
```

---

## 14. Announcements

> **Requires:** Any authenticated role

| Method | Endpoint                         | Description                        | Status |
|--------|----------------------------------|------------------------------------|--------|
| GET    | `/api/v1/announcements`          | My announcements (role-based)      | 200    |
| GET    | `/api/v1/announcements/all`      | All announcements                  | 200    |
| POST   | `/api/v1/announcements`          | Create announcement (teachers+)   | 200    |
| DELETE | `/api/v1/announcements/{id}`     | Delete announcement                | 204    |

### Create Announcement

```json
{
  "title": "Midterm Exam Schedule",
  "content": "Midterm exams will be held from April 20-25...",
  "targetRole": "ROLE_STUDENT",
  "targetStudentGroupId": null,
  "important": true,
  "expiresAt": "2026-04-25T23:59:59"
}
```

| Field                | Type     | Description                                       |
|----------------------|----------|---------------------------------------------------|
| targetRole           | string?  | Filter by role. `null` = all roles                |
| targetStudentGroupId | Long?    | Filter by student group. `null` = all groups      |
| important            | boolean? | Mark as important                                 |
| expiresAt            | string?  | ISO 8601 datetime. `null` = never expires         |

---

## 15. Messages

> **Requires:** Any authenticated role

| Method | Endpoint                                        | Description                       | Status |
|--------|------------------------------------------------|-----------------------------------|--------|
| POST   | `/api/v1/messages`                              | Send a message                    | 201    |
| GET    | `/api/v1/messages`                              | Get all my messages               | 200    |
| GET    | `/api/v1/messages/conversations`                | List conversation summaries       | 200    |
| GET    | `/api/v1/messages/conversations/{otherUserId}`  | Get conversation with a user      | 200    |

> **WebSocket chat** is also available at `ws://localhost:8080/ws-chat`

### Send Message

```json
{
  "recipientId": 5,
  "content": "Hello! When is the next assignment due?"
}
```

### Conversation Summary Response

```json
[
  {
    "otherUserId": 5,
    "otherUserName": "Jane Smith",
    "lastMessage": "The assignment is due Friday",
    "lastMessageAt": "2026-04-14T12:30:00",
    "unreadCount": 2
  }
]
```

---

## 16. Tests & Quizzes

> **Requires:** Any authenticated role

| Method | Endpoint                                        | Description                    | Status |
|--------|-------------------------------------------------|--------------------------------|--------|
| POST   | `/api/v1/tests`                                 | Create a test (teachers)       | 200    |
| GET    | `/api/v1/tests/{testId}`                        | Get test details               | 200    |
| GET    | `/api/v1/tests/{testId}/questions`              | Get questions (student view)   | 200    |
| POST   | `/api/v1/tests/{testId}/questions`              | Add question to test           | 200    |
| POST   | `/api/v1/tests/{testId}/attempts`               | Start a test attempt           | 200    |
| POST   | `/api/v1/tests/attempts/{attemptId}/submit`     | Submit test answers            | 200    |

### Create Test

```json
{
  "moduleId": 1,
  "title": "Quiz 1: Variables",
  "timeLimitMinutes": 30,
  "randomizeQuestions": true,
  "randomizeChoices": true,
  "passingScore": 70.0
}
```

### Add Question

```json
{
  "text": "What is a variable in Python?",
  "questionType": "SINGLE_CHOICE",
  "choices": [
    { "text": "A named storage location", "correct": true },
    { "text": "A function", "correct": false },
    { "text": "A loop construct", "correct": false },
    { "text": "A comment", "correct": false }
  ]
}
```

### Submit Answers

```json
{
  "answers": [
    { "questionId": 1, "choiceIds": [3] },
    { "questionId": 2, "choiceIds": [5, 7] }
  ]
}
```

### Attempt Response

```json
{
  "id": 1,
  "startedAt": "2026-04-14T13:00:00",
  "submittedAt": "2026-04-14T13:25:00",
  "score": 85.0,
  "maxScore": 100.0
}
```

---

## 17. Progress Tracking

> **Requires:** Any authenticated role

| Method | Endpoint                                         | Description                    | Status |
|--------|--------------------------------------------------|--------------------------------|--------|
| POST   | `/api/v1/progress/lessons/{lessonId}/complete`   | Mark a lesson as completed     | 200    |
| GET    | `/api/v1/progress/courses/{courseId}`             | My progress in a course        | 200    |
| GET    | `/api/v1/progress/courses/{courseId}/students`    | All students' progress (admin) | 200    |

### Course Progress Response

```json
{
  "courseId": 1,
  "courseTitle": "Introduction to Programming",
  "totalLessons": 15,
  "completedLessons": 8,
  "progressPercent": 53.3
}
```

---

## 18. Enums Reference

### Roles

| Value                  | Description                  |
|------------------------|------------------------------|
| `ROLE_ADMIN`           | Platform super admin         |
| `ROLE_UNIVERSITY_ADMIN`| University administrator     |
| `ROLE_TEACHER`         | Teacher / Instructor         |
| `ROLE_STUDENT`         | Student                      |
| `ROLE_ACCOUNTANT`      | Accountant                   |
| `ROLE_GUEST`           | Guest / Limited access       |

### Content Type

| Value   | Description     |
|---------|-----------------|
| `TEXT`  | Rich text       |
| `PDF`   | PDF document    |
| `VIDEO` | Video content  |

### Attendance Status

| Value     | Description          |
|-----------|----------------------|
| `PRESENT` | Student was present  |
| `ABSENT`  | Student was absent   |
| `LATE`    | Student was late     |
| `EXCUSED` | Excused absence      |

### Question Type

| Value             | Description                    |
|-------------------|--------------------------------|
| `SINGLE_CHOICE`   | Only one correct answer       |
| `MULTIPLE_CHOICE`  | Multiple correct answers     |

### Day of Week

Standard Java `DayOfWeek`: `MONDAY`, `TUESDAY`, `WEDNESDAY`, `THURSDAY`, `FRIDAY`, `SATURDAY`, `SUNDAY`

---

## 19. Role-Based Access Matrix

| API Category     | ADMIN | UNIVERSITY_ADMIN | TEACHER | STUDENT |
|------------------|:-----:|:-----------------:|:-------:|:-------:|
| Universities     | ✅    | ❌                | ❌      | ❌      |
| Users            | ✅    | ❌                | ❌      | ❌      |
| Admin Register   | ✅    | ❌                | ❌      | ❌      |
| Faculties        | ✅    | ✅                | ❌      | ❌      |
| Subjects         | ✅    | ✅                | ❌      | ❌      |
| Teachers         | ✅    | ✅                | ❌      | ❌      |
| Students         | ✅    | ✅                | ❌      | ❌      |
| Student Groups   | ✅    | ✅                | ❌      | ❌      |
| Courses          | ✅    | ✅                | ✅      | ✅      |
| Enrollments      | ✅    | ✅                | ✅      | ✅      |
| Grades           | ✅    | ✅                | ✅      | ✅      |
| Schedule         | ✅    | ✅                | ✅      | ✅      |
| Attendance       | ✅    | ✅                | ✅      | ✅      |
| Announcements    | ✅    | ✅                | ✅      | ✅      |
| Messages         | ✅    | ✅                | ✅      | ✅      |
| Tests            | ✅    | ✅                | ✅      | ✅      |
| Progress         | ✅    | ✅                | ✅      | ✅      |

---

## 20. Error Handling

### HTTP Status Codes

| Code | Meaning                                           |
|------|---------------------------------------------------|
| 200  | Success                                           |
| 201  | Created                                           |
| 204  | No Content (successful delete)                    |
| 400  | Bad Request (validation error)                    |
| 401  | Unauthorized (missing/invalid token)              |
| 403  | Forbidden (insufficient role or expired token)    |
| 404  | Not Found                                         |
| 500  | Internal Server Error                             |

### Common Error Patterns

**Authentication failed:**
```
HTTP 403 (empty body)
```
→ Token is missing, expired, or user doesn't have the required role.

**Resource not found:**
```
HTTP 500 with ResourceNotFoundException
```
→ The requested entity (ID) doesn't exist in the database.

### Frontend Error Handling Pattern

```javascript
async function apiCall(url, options) {
  try {
    const response = await fetchWithAuth(url, options);

    if (response.status === 204) return null; // Delete success

    if (!response.ok) {
      if (response.status === 403) {
        throw new Error('Access denied. Check your permissions.');
      }
      const error = await response.text();
      throw new Error(error || `HTTP ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error(`API Error [${url}]:`, error.message);
    throw error;
  }
}
```

---

## Complete Authentication Flow Diagram

```
┌─────────┐     POST /register      ┌──────────┐
│ Frontend │ ──────────────────────→ │  Backend │
│          │ ←────────────────────── │          │
│          │     { success: true }   │          │
│          │                         │          │
│          │    POST /auth/login     │          │
│          │ ──────────────────────→ │          │
│          │ ←────────────────────── │          │
│          │  { access_token,        │          │
│          │    refresh_token }      │          │
│          │                         │          │
│          │   GET /api/v1/users     │          │
│          │   Authorization:        │          │
│          │   Bearer <access_token> │          │
│          │ ──────────────────────→ │          │
│          │ ←────────────────────── │          │
│          │     [user data]         │          │
│          │                         │          │
│          │  ── Token Expired ──    │          │
│          │   403 Forbidden         │          │
│          │ ←────────────────────── │          │
│          │                         │          │
│          │  POST /auth/refresh     │          │
│          │  { refresh_token }      │          │
│          │ ──────────────────────→ │          │
│          │ ←────────────────────── │          │
│          │  { new access_token,    │          │
│          │    new refresh_token }  │          │
└─────────┘                         └──────────┘
```
