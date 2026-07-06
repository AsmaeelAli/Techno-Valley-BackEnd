## Overview

Techno-Valley is a backend system designed to support academic collaboration within IT faculties at Jordanian universities.

The system provides a secure and structured API that allows different user roles (visitors, students, and professors) to interact with academic content.

Students and verified professors can publish and manage educational resources, while visitors have read-only access.

The platform focuses on enabling secure content management, role-based access control, and efficient data retrieval through a hashtag-based search system.

It supports structured file uploads such as text and PDF, ensuring organized academic resource sharing.

The main goal of this backend system is to provide a reliable, secure, and scalable API that enables academic collaboration, improves content discoverability, and ensures that only verified users can contribute to the system. It also introduces an expert search system that allows users to discover individuals based on their skills and academic expertise, enhancing collaboration between students and professors.



## Features

### 1. User Authentication
- User login and registration system
- Email-based account verification
- Role-based access (USER, ADMIN)

---

### 2. Post Management System
- Create, view, and manage posts
- Support for file attachments
- Organized content sharing between users

---

### 3. Search System
- Hashtag-based content search
- Expert search system to find users by skills and expertise

---

### 4. User Roles
- Visitors can browse content
- Registered users can create and interact with content
- Admin users manage system access

---

### 5. File Handling System
- Upload and attach files to posts
- Supports academic file formats (e.g., PDF, text)



## Security

### 1. Authentication Security
- Stateless authentication using JWT tokens
- Secure password storage using BCrypt hashing
- Token-based identity management without server-side sessions

---

### 2. Access Control
- Role-based authorization using Spring Security (USER / ADMIN)
- Method-level security using `@PreAuthorize` annotations
- Fine-grained endpoint protection based on user roles

---

### 3. API Protection Layer
- Custom JWT filter intercepts all incoming requests
- Validates token signature and expiration before request processing
- Extracts and injects authenticated user into Spring Security context

---

### 4. Abuse Protection
- Login attempt tracking system to prevent brute-force attacks
- Temporary blocking after exceeding maximum failed login attempts
- Automatic reset of attempts after successful authentication

---

### 5. Email Security
- Email-based one-time password (OTP) verification for account activation
- Time-sensitive verification flow with controlled request limits
- Protection against repeated verification attempts (anti-spam mechanism)



## System Architecture

The system is built using a modular layered architecture combined with key software engineering principles to ensure maintainability, scalability, and clean separation of concerns.

---

### 1. High-Level Architecture

The project is organized into the following main layers:

- **Controller Layer**: Handles HTTP requests and responses
- **UseCase / Service Layer**: Contains business logic
- **Data Layer**: Manages database operations and persistence
- **Security Layer**: Handles authentication and authorization
- **Config Layer**: Manages application configuration, security setup, and Swagger documentation
- **Common Layer**: Shared utilities and global exception handling

This structure ensures clear responsibility separation and reduces coupling between components.

---

### 2. Feature-Based Architecture

The system follows a feature-oriented modular design, where each feature is self-contained and includes its own internal structure:

feature/
├── controller
├── usecase
├── model
├── data
└── config (if required)

Each feature is isolated to ensure high modularity, scalability, and easier maintenance.

Example: Post Module Structure

- Controller → Handles HTTP requests and responses  
- UseCase → Contains business logic  
- Model → Defines domain entities and DTOs  
- Data → Handles database operations

---

### 3. Security Architecture

- JWT-based authentication using custom security filter
- Public/private key mechanism for token signing and verification
- Security context populated after successful token validation
- Role-based access control using `@PreAuthorize` annotations
- Stateless authentication with no server-side sessions

---

### 4. Request Flow

Client → Controller → UseCase → Data Layer → Database
↑
JWT Security Filter

Every request is intercepted by the security filter before reaching business logic to ensure authentication and authorization.

---

### 5. Database Layer

- Database schema versioning managed using Flyway migrations (`db/migration`)
- Controlled evolution of database structure through versioned scripts

---

### 6. SOLID Principles

The system partially applies SOLID principles to improve code quality and maintainability:

- **Single Responsibility Principle (SRP)**: Each layer has a clear and independent responsibility
- **Dependency Inversion Principle (DIP)**: Implemented using Spring Dependency Injection
- **Open/Closed Principle (OCP)**: System is designed to allow extension without modifying existing logic
- **Interface Segregation & Liskov Substitution**: Applied partially depending on module design

---

### 7. Design Goals

- High modularity through feature-based architecture
- Clear separation of concerns across all layers
- Secure stateless authentication system
- Scalable backend structure for future expansion



## Search System

The system provides a unified search feature that allows users to discover both academic content and experts within the platform using a single query interface.

---

### 1. Overview

The search system combines two main data sources:
- Posts based on hashtags
- Users based on their experience and expertise

This allows users to perform both content discovery and expert discovery in one request.

---

### 2. Functionality

- Search posts using hashtag prefix matching (case-insensitive)
- Search experts based on experience text matching
- Return grouped results in a structured response
- Normalize user queries for consistent matching

---

### 3. Search Output Structure

Results are grouped into two categories:

- **Experts**: Users matched by experience or skills
- **Posts**: Posts matched by hashtags

Each result includes only essential information to keep responses lightweight and relevant.

---

### 4. Technical Approach

- Hashtag search uses prefix-based matching for fast filtering
- Expert search uses case-insensitive text matching on experience field
- Data is aggregated from multiple repositories into a unified response
- Results are returned in a grouped DTO format for frontend simplicity

---

### 5. Design Goal

The goal of the system is to provide a simple and efficient way to connect users with relevant academic content and people without introducing complex search overhead.



## Posts System

The Posts System is the core feature of the platform, allowing users to create and share academic content in a controlled and secure environment.

---

### 1. Overview

Users can create posts containing:
- Text content
- Optional file attachment (single file per post)
- Hashtag for categorization

Each post is linked to its creator and stored with metadata such as creation time and status.

---

### 2. Core Features

- Create academic posts with optional file upload
- Attach a single file per post
- Categorize posts using hashtags
- Retrieve all posts or user-related interactions
- Support for likes and favorites system

---

### 3. File Handling Process

File upload is handled through a secure multi-step pipeline:

1. File validation (extension and size limits)
2. Temporary storage before processing
3. Security scanning using ClamAV (virus detection)
4. MIME type validation
5. Final storage on server filesystem
6. Cleanup of temporary files after processing

Only files that pass all validation steps are stored permanently.

---

### 4. Security & Content Validation

Before saving a post, the system performs:

- Banned words filtering on both content and hashtags
- File integrity validation before storage
- Rejection of any suspicious or invalid upload attempts

---

### 5. Logging & Security Monitoring

The system records suspicious activities such as:

- Invalid file extensions
- Blocked MIME types
- Virus detection events
- File scanning failures

These logs include user information and timestamps, providing traceability for security monitoring.

---

### 6. Design Notes

- Each post supports only one attached file for simplicity
- Files are stored locally on the server with controlled access permissions
- Temporary files are used during scanning to ensure isolation
- UUIDs are used as unique identifiers for posts to ensure scalability



## Challenges and Engineering Decisions

During the development of the system, several key engineering challenges were encountered, mainly related to performance, search design, and system organization.

---

### 1. Posts System Complexity

The main challenge was designing a secure and reliable post creation system that supports file uploads while maintaining system stability.

The process was designed as a multi-stage pipeline:
- File validation (extension and size checks)
- MIME type verification
- Virus scanning using ClamAV
- Temporary file storage during processing
- Permanent storage after validation
- Logging of any suspicious or failed operations

Instead of increasing system complexity with advanced frameworks, a simple approach was used based on logs and flags to track issues and system behavior.

---

### 2. Hybrid Search Design

One of the more challenging parts of the system was designing a hybrid search feature capable of returning two different types of results within a single response:

- Posts based on hashtags
- Users based on experience

The challenge was not only in implementing the queries, but in designing a unified response structure that can combine heterogeneous data in a meaningful way.

This required balancing clarity in the API response while keeping database queries efficient and lightweight.

---

### 3. Database Efficiency and Query Optimization

A key goal during development was minimizing database load and avoiding unnecessary queries.

To achieve this:
- Simple indexing and structured queries were preferred over complex joins
- Full-text search support was used where applicable (`tsvector`)
- Data relationships were kept normalized but lightweight
- Only essential data is fetched per request

The focus was on reducing database pressure while maintaining acceptable response speed for search and retrieval operations.

---

### 4. Code Organization and Maintainability

As the system grew, maintaining clear structure became a challenge.

The solution was to enforce a strict separation of concerns:
- Controllers for request handling
- UseCases for business logic
- Data layer for persistence
- Dedicated services for security, scanning, and storage

This structure helped reduce confusion and improved long-term maintainability.

---

### 5. Frontend Data Handling (Supporting Layer)

Although the project is backend-focused, some client-side utilities were implemented to improve performance:
- JWT decoding and validation
- Token storage and lifecycle management
- Caching user experiences in session storage

This reduced unnecessary API calls and improved user experience.
