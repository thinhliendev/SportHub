# SportHub – Sports E-commerce and Court Booking Platform

> **Project Status:** In Development

SportHub is a personal web application independently designed and developed for purchasing sports equipment and booking sports courts.

The project combines two business domains in a single modular monolith:

- Sports equipment e-commerce
- Sports venue and court booking

The main engineering focus is building reliable business workflows with Spring Boot, including authentication, authorization, inventory consistency, transactional order processing, and conflict-free court reservations.

## Project Objectives

SportHub is being developed to demonstrate practical Java backend development skills, including:

- Designing a modular monolith with clear business boundaries
- Building web applications with Spring MVC and Thymeleaf
- Implementing form-based authentication and role-based authorization
- Designing relational data models with JPA and PostgreSQL
- Maintaining inventory consistency during checkout
- Preventing duplicate court reservations
- Handling validation, exceptions, and transactional failures
- Writing unit and integration tests for critical business logic

## Main Features

### Authentication and Authorization

- User registration, login, and logout
- Form-based authentication with Spring Security
- Session-based authentication
- Password hashing with BCrypt
- CSRF protection
- Role-based access control
- Conditional page elements based on user roles

The application supports three roles:

| Role | Responsibilities |
|---|---|
| `CUSTOMER` | Browse products, manage a cart, place orders, and book courts |
| `VENUE_OWNER` | Manage venues, courts, schedules, and booking requests |
| `ADMIN` | Manage users, products, orders, venues, and the overall system |

### Sports E-commerce

- Product and category browsing
- Product searching, filtering, and pagination
- Product and inventory management
- Shopping cart management
- Checkout and order creation
- Order history
- Order status management
- Transactional stock validation and deduction
- Prevention of negative inventory and overselling

### Court Booking

- Sports venue and court browsing
- Support for football, badminton, tennis, and pickleball courts
- Fixed time-slot management
- Available-slot searching by court and date
- Court reservation and cancellation
- Booking history
- Booking confirmation or rejection by venue owners
- Prevention of duplicate bookings

## Key Engineering Challenges

### Transactional Checkout

Order creation and inventory updates are performed within the same database transaction. If stock is insufficient or any step fails, the operation is rolled back so that an invalid order is not created and inventory remains consistent.

### Concurrent Inventory Updates

Inventory access is protected with an appropriate database-locking strategy. This prevents two customers from successfully purchasing the final available item at the same time and ensures that stock never becomes negative.

### Conflict-Free Court Reservations

Court availability is checked in the service layer, while the database provides the final guarantee through a unique constraint:

```text
court_id + booking_date + time_slot_id
```

If another customer reserves the selected slot first, the later request is rejected instead of creating a duplicate booking.

## Architecture

SportHub follows a modular monolith architecture. The application is deployed as one unit while business features remain separated into focused modules.

```text
vn.thinhliendev.sporthub
├── auth
├── user
├── catalog
├── inventory
├── cart
├── order
├── venue
├── booking
├── common
└── config
```

Each business module can contain its own controllers, services, repositories, entities, DTOs, and validation logic.

The request flow follows the Spring MVC pattern:

```text
Browser → Spring MVC Controller → Service → Repository → PostgreSQL
                ↓
        Thymeleaf Template
```

## Technology Stack

### Backend

- Java 21
- Spring Boot 4.0.7
- Spring MVC
- Spring Data JPA
- Spring Security
- Bean Validation
- PostgreSQL
- Maven
- JUnit 5
- Mockito

### User Interface

- Thymeleaf
- Thymeleaf Spring Security Extras
- HTML5
- CSS3
- Bootstrap 5
- JavaScript

## Project Structure

```text
src/main
├── java/vn/thinhliendev/sporthub
│   ├── auth
│   ├── user
│   ├── catalog
│   ├── inventory
│   ├── cart
│   ├── order
│   ├── venue
│   ├── booking
│   ├── common
│   └── config
└── resources
    ├── static
    │   ├── css
    │   ├── js
    │   └── images
    ├── templates
    │   ├── auth
    │   ├── products
    │   ├── cart
    │   ├── orders
    │   ├── venues
    │   ├── bookings
    │   ├── admin
    │   └── fragments
    ├── application.properties
    └── application-dev.properties
```

## Getting Started

### Prerequisites

- Java 21
- PostgreSQL
- Git

The Maven Wrapper included in the project can be used, so a separate Maven installation is optional.

### 1. Clone the Repository

```bash
git clone https://github.com/thinhliendev/SportHub.git
cd SportHub
```

### 2. Create the Database

Create a PostgreSQL database named `sporthub`:

```sql
CREATE DATABASE sporthub;
```

### 3. Configure Environment Variables

Database credentials should not be committed to GitHub. The development configuration can read them from environment variables:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sporthub
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

On Windows PowerShell, define the variables for the current terminal session:

```powershell
$env:DB_USERNAME="your_postgresql_username"
$env:DB_PASSWORD="your_postgresql_password"
```

### 4. Run the Application

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

On macOS or Linux:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Then open:

```text
http://localhost:8080
```

## Current Progress

- [ ] Project initialization
- [ ] Database design
- [ ] User registration and login
- [ ] Role-based authorization
- [ ] Thymeleaf layout and reusable fragments
- [ ] Product and category management
- [ ] Product searching and pagination
- [ ] Shopping cart
- [ ] Order and inventory management
- [ ] Transactional checkout
- [ ] Sports venue and court management
- [ ] Court availability
- [ ] Court reservation
- [ ] Concurrency control
- [ ] Admin dashboard
- [ ] Unit tests
- [ ] Integration tests
- [ ] Deployment

The checklist will be updated as development progresses. Completed items should only be checked after their implementation has been tested.

## Planned Database Entities

| Module | Main entities |
|---|---|
| Account | `User`, `Role` |
| Catalog | `Product`, `Category`, `Inventory` |
| Cart | `Cart`, `CartItem` |
| Order | `Order`, `OrderItem` |
| Venue | `SportsVenue`, `Court`, `SportType` |
| Schedule | `TimeSlot`, `CourtSchedule` |
| Booking | `Booking` |

## Scope Limitations

The first version focuses on core business workflows. It does not currently include real payment processing, shipping-provider integration, vouchers, chat, maps, or a multi-vendor marketplace.

## Author

Independently designed and developed by **Thịnh Liên Văn** as a personal Java backend development project.

