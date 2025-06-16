# Finance Tracker API

A secure, production-ready RESTful API for personal finance management, built with **Spring Boot**.  
Features JWT authentication, DTO-based design, validation, robust error handling, and full Swagger/OpenAPI documentation.

---

## Features

- **User Management:** Registration, update, and deletion with secure password hashing.
- **JWT Authentication:** Secure login and stateless authorization for all endpoints.
- **Transaction Management:** CRUD for financial transactions, with filtering and pagination.
- **Category Management:** User-specific categories with ownership checks.
- **Validation:** Input validation on all DTOs.
- **Global Exception Handling:** Consistent, structured error responses.
- **API Documentation:** Interactive Swagger UI (`/swagger-ui.html`).
- **Integration Tests:** Full CRUD and security test coverage.

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- MySQL (or another supported RDBMS)

### Setup

1. **Clone the repository:**
    ```bash
    git clone https://github.com/yourusername/finance-tracker.git
    cd finance-tracker
    ```

2. **Configure the database:**
    - Edit `src/main/resources/application.properties`:
        ```bash
        spring.datasource.url=jdbc:mysql://localhost:3306/finance_tracker
        spring.datasource.username=your_db_user
        spring.datasource.password=your_db_password
        spring.jpa.hibernate.ddl-auto=update
        ```

3. **Build and run:**
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

4. **Access Swagger UI:**
    - Visit [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) for interactive API docs.

---

## API Documentation

- **Swagger/OpenAPI:** All endpoints are documented and testable via Swagger UI.
- **Authentication:**
    - Register with `/api/users/register`
    - Login with `/api/auth/login` to receive a JWT
    - Use the JWT as a `Bearer` token for all secured endpoints

---

## Example API Usage

### Register a User

```bash
curl -X POST http://localhost:8080/api/users/register
-H "Content-Type: application/json"
-d '{"name":"Test User","email":"user@example.com","password":"password"}'
```

### Authenticate and Get JWT

```bash
curl -X POST http://localhost:8080/api/auth/login
-H "Content-Type: application/json"
-d '{"email":"user@example.com","password":"password"}'
```

### Use JWT for Authenticated Requests

```bash
curl -X GET http://localhost:8080/api/transactions
-H "Authorization: Bearer <your-jwt-token>"
```

---

## Running Tests

```bash
mvn test
```

- Integration tests cover all CRUD endpoints, authentication, and error scenarios.

---

## Technologies Used

- Spring Boot
- Spring Security (JWT)
- Spring Data JPA (MySQL)
- Lombok
- Swagger/OpenAPI (`springdoc-openapi`)
- JUnit, MockMvc (for testing)

---

## Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

---

## License

This project is licensed under the MIT License.

---

## Author

- [Yogendra Singh Baghel](https://github.com/yogendra2126)
