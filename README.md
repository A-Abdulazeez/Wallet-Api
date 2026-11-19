# Wallet API

A RESTful wallet management API built with Java and Spring Boot. The application provides customer authentication, wallet management, financial transactions and administrative controls.

The project focuses on backend development, secure authentication, transaction integrity, role-based access control and database management.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 4.1.1 | Backend framework |
| Spring Security | Authentication and authorization |
| JWT | Stateless authentication |
| BCrypt | Password hashing |
| Spring Data JPA | Database operations |
| Hibernate | ORM |
| PostgreSQL 17 | Relational database |
| Maven | Dependency management and build |
| JUnit 5 | Unit and integration testing |
| Mockito | Mocking and service testing |
| Docker Compose | Database containerization |
| Springdoc OpenAPI | API documentation |

## Features

### Authentication and Authorization

- Customer registration and login.
- JWT-based authentication.
- Password hashing with BCrypt.
- Role-based access control using CUSTOMER and ADMIN roles.
- Stateless authentication with Spring Security.
- Automatic administrator account initialization.

### Customer Management

- Register and authenticate customers.
- Retrieve customer profiles.
- Update customer information.
- Update passwords.
- Create and retrieve personal wallets.
- View personal transaction history.

### Wallet Management

- Create customer wallets.
- Generate unique wallet account numbers.
- Retrieve wallet information.
- Manage wallet balances.
- Freeze and unfreeze wallets through administrative controls.
- Prevent financial operations on frozen wallets.

### Financial Transactions

- Fund wallets.
- Transfer funds between wallets.
- Withdraw funds.
- Validate transaction amounts.
- Prevent transfers to the same wallet.
- Prevent transactions when the available balance is insufficient.
- Prevent funding, transfers and withdrawals involving frozen wallets.
- Maintain transaction records with unique references.
- Use `BigDecimal` for financial calculations.

### Admin Management

Administrators have access to dedicated endpoints for monitoring and managing customer accounts, wallets and transactions.

**Customer management**
- View all customers.
- Retrieve a specific customer by email.

**Wallet management**
- View all wallets.
- Retrieve a wallet by account number.
- Freeze a wallet.
- Unfreeze a wallet.

**Transaction management**
- View all transactions.
- Find a transaction by reference.
- View transactions associated with a specific wallet.

**Administrative restrictions**
- Administrators cannot transfer or withdraw customer funds.
- Administrators cannot modify transaction history.
- Administrators cannot register through public customer registration.

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a customer |
| POST | `/api/auth/login` | Authenticate a customer |

### Customer

All customer endpoints require a valid customer JWT.

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/customer/profile` | Retrieve customer profile |
| PATCH | `/api/customer/update-profile` | Update customer profile |
| PATCH | `/api/customer/update-password` | Update password |
| POST | `/api/customer/create-wallet` | Create a wallet |
| GET | `/api/customer/get-wallet` | Retrieve wallet |
| PATCH | `/api/customer/fund-wallet` | Fund wallet |
| POST | `/api/customer/transfer` | Transfer funds |
| PATCH | `/api/customer/withdraw` | Withdraw funds |
| GET | `/api/customer/transactions` | Retrieve transaction history |

### Admin

All admin endpoints require a valid administrator JWT.

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/customers` | View all customers |
| GET | `/api/admin/customers/{email}` | Find customer by email |
| GET | `/api/admin/wallets` | View all wallets |
| GET | `/api/admin/wallets/{accountNumber}` | Find wallet by account number |
| GET | `/api/admin/transactions` | View all transactions |
| GET | `/api/admin/transactions/{reference}` | Find transaction by reference |
| GET | `/api/admin/wallets/{accountNumber}/transactions` | View wallet transactions |
| PATCH | `/api/admin/wallets/{accountNumber}/freeze` | Freeze wallet |
| PATCH | `/api/admin/wallets/{accountNumber}/unfreeze` | Unfreeze wallet |

## Architecture

The application follows a layered architecture to separate HTTP handling, business logic and database operations.

```text
Controller Layer
       |
       v
Service Layer
       |
       v
Repository Layer
       |
       v
PostgreSQL Database
```

**Controller Layer**

Handles incoming HTTP requests and returns API responses.

**Service Layer**

Contains application logic, business rules, wallet operations and transaction processing.

**Repository Layer**

Handles database access using Spring Data JPA.

**DTOs**

Separates API request and response objects from database entities.

**Mapper**

Handles conversion between entities and response DTOs.

**Security Layer**

Handles JWT authentication, password security and role-based authorization.

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── az/project/walletapi/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── data/
│   │       │   ├── model/
│   │       │   └── repository/
│   │       ├── dtos/
│   │       │   ├── request/
│   │       │   └── response/
│   │       ├── exception/
│   │       ├── service/
│   │       └── utils/
│   └── resources/
│       └── application.properties/
│
└── test/
    └── java/
        └── az/project/walletapi/
            ├── data/
            │   └── repository/
            ├── integration/
            └── service/
```

## Getting Started

### Prerequisites

- Java 21
- Docker Desktop
- Git

The project includes Maven Wrapper, so a separate Maven installation is not required.

### 1. Clone the repository

```bash
git clone https://github.com/A-Abdulazeez/Wallet-Api.git

cd Wallet-Api
```

### 2. Start PostgreSQL

Start the PostgreSQL container using Docker Compose:

```bash
docker compose up -d
```

The database configuration is:

| Setting | Value |
|---|---|
| Database | wallet_db |
| Username | wallet_user |
| Password | wallet_password |
| Host | localhost |
| Port | 1001 |
| Container port | 5432 |

### 3. Configure environment variables

The application supports the following environment variables:

| Variable | Purpose |
|---|---|
| JWT_SECRET | JWT signing secret |
| DB_USERNAME | Database username |
| DB_PASSWORD | Database password |

The application currently provides local development defaults in `application.properties`.

For production, replace default credentials with secure environment variables.

The administrator account is initialized automatically by `AdminSeeder` using the configured admin email and password.

Configure these properties before starting the application:

```properties
admin.email=your-admin-email
admin.password=your-secure-admin-password
```

### 4. Run the application

On macOS or Linux:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The application runs on the default Spring Boot port:

```text
http://localhost:8080
```

## Running Tests

Run the unit and integration test suite:

```bash
./mvnw test
```

Run a clean build and verification:

```bash
./mvnw clean verify
```

The project uses JUnit 5, Mockito and Spring Boot Test to verify service logic, repository operations and application integration.

## API Documentation

The API documentation is available through Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI specification:

```text
http://localhost:8080/v3/api-docs
```

## Future Improvements

- Pagination for administrative listings.
- Improved concurrency control for simultaneous wallet transactions.
- Additional transaction failure and recovery handling.
- Production-ready configuration and deployment.

## Author

**Abdullahi Abdulazeez**

GitHub: [A-Abdulazeez](https://github.com/A-Abdulazeez)

Repository: [Wallet API](https://github.com/A-Abdulazeez/Wallet-Api)
