# Parcel Tracker

A robust parcel tracking system built with Spring Boot that allows users to manage and track parcels efficiently.

## 📊 Feature Status

| Feature Category | Feature | Status | Priority | Notes |
|-----------------|---------|---------|-----------|--------|
| **Core Features** | User Authentication | ✅ Complete | High | Basic authentication implemented |
| | Parcel Tracking | ✅ Complete | High | Real-time tracking with status updates |
| | RESTful API | ✅ Complete | High | RESTful endpoints with proper documentation |
| | Database Migration | ✅ Complete | High | Using Flyway for version control |
| **Security** | Basic Authentication | ✅ Complete | High | Basic auth with admin credentials |
| | Role-based Access | ❌ Pending | High | Need to implement proper roles |
| | CORS Configuration | ❌ Pending | Medium | Need to implement CORS policy |
| | Rate Limiting | ❌ Pending | High | Required for DDoS protection |
| | Security Headers | ❌ Pending | High | Need to add security headers |
| **Monitoring & Observability** | Health Checks | ✅ Complete | High | Basic health endpoints implemented |
| | Metrics Collection | ✅ Complete | High | Prometheus metrics implemented |
| | Logging | ✅ Complete | High | Configured with proper levels |
| | Distributed Tracing | ❌ Pending | Medium | Need to implement tracing |
| **Performance** | Caching | ✅ Complete | High | Caffeine cache implementation |
| | Connection Pooling | ✅ Complete | High | HikariCP configured |
| | Load Testing | ❌ Pending | High | JMeter tests implemented |
| **DevOps** | Docker Support | ✅ Complete | High | Dockerfile and docker-compose available |
| | CI/CD Pipeline | ❌ Pending | High | Need automated deployment |
| | Kubernetes Support | ❌ Pending | Medium | Need K8s manifests |
| **Documentation** | API Documentation | ✅ Complete | High | OpenAPI/Swagger implemented |
| | Deployment Guide | ✅ Complete | High | Docker deployment guide available |
| | Monitoring Guide | ✅ Complete | High | Prometheus & Grafana setup guide available |

## 🚀 Features

- Basic authentication with admin credentials
- Real-time parcel tracking
- RESTful API endpoints
- Database migration support with Flyway
- Comprehensive API documentation with SpringDoc OpenAPI
- Caching mechanism for improved performance
- Robust error handling and validation
- Unit and integration testing with JUnit 5 and TestContainers
- Prometheus metrics collection
- Docker containerization
- Grafana dashboards

## 🛠️ Technologies

- **Java 17**
- **Spring Boot 3.4.4**
- **Spring Security** - For authentication and authorization
- **Spring Data JPA** - For database operations
- **PostgreSQL 15** - Primary database
- **H2 Database 2.2.224** - For local developing and testing
- **Flyway 9.22.3** - Database migration
- **Lombok 1.18.30** - To reduce boilerplate code
- **MapStruct 1.5.5.Final** - For object mapping
- **SpringDoc OpenAPI 2.7.0** - API documentation
- **Maven 3.9.6** - Dependency management and build tool
- **JUnit 5 & Mockito** - Testing framework
- **TestContainers 1.19.7** - For integration testing
- **Prometheus 2.45.0** - Metrics collection and monitoring
- **Grafana 10.0.3** - Metrics visualization
- **Docker 24.0.7** - Containerization

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.9.6 or higher
- Docker 24.0.7 and Docker Compose 2.23.3
- PostgreSQL 15 (for production)

## 🚀 Getting Started

### Local Development Setup

1. Clone the repository
```bash
git clone https://github.com/yourusername/parcel-tracker.git
cd parcel-tracker
```

2. Configure the database
Create a PostgreSQL database and update the `application-local.yml` file with your database configuration:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/your_database_name
    username: your_username
    password: your_password
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: none
  flyway:
    enabled: true
    locations: classpath:db/migration/{vendor}
```

3. Build the project
```bash
mvn clean install
```

4. Run the application
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

The application will be available at `http://localhost:8080`

### Docker Deployment

1. Configure environment variables
Create a `.env` file in the `docker` directory with the following variables:
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432
SPRING_DATASOURCE_DATABASE=parcel_tracker
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_PROFILES_ACTIVE=dev
```

2. Build the Docker image
```bash
docker build -t parcel-tracker .
```

3. Start the application with Docker Compose
```bash
cd docker
docker-compose up -d
```

This will start:
- Parcel Tracker application
- PostgreSQL database
- Prometheus for metrics collection
- Grafana for metrics visualization

Access points:
- Application: `http://localhost:8080`
- Grafana: `http://localhost:3000`
- Prometheus: `http://localhost:9090`

## 📚 API Documentation

Once the application is running, you can access the API documentation at:
- Swagger UI: `http://localhost:8080/api/docs/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 🧪 Running Tests

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/hotel/parceltracker/
│   │       ├── config/         # Configuration classes
│   │       ├── controller/     # REST controllers
│   │       ├── dto/           # Data Transfer Objects
│   │       ├── entity/        # JPA entities
│   │       ├── mapper/        # MapStruct mappers
│   │       ├── repository/    # Data repositories
│   │       ├── service/       # Business logic
│   │       └── security/      # Security configurations
│   └── resources/
│       ├── application.yml    # Main application configuration
│       ├── application-dev.yml    # Development profile
│       ├── application-prod.yml   # Production profile
│       ├── application-local.yml  # Local development profile
│       ├── application-test.yml   # Testing profile
│       ├── db/migration/      # Flyway migrations
│       └── logback-spring.xml # Logging configuration
├── test/
│   └── java/                  # Test classes
└── docker/
    ├── prometheus/           # Prometheus configuration
    ├── grafana/             # Grafana dashboards
    ├── postgres/            # PostgreSQL configuration
    ├── docker-compose.yml   # Docker services configuration
    └── .env                 # Environment variables
```

## 🔐 Security

The application uses Basic Authentication with the following default credentials:
- Username: admin
- Password: secret

Protected endpoints require these credentials in the Authorization header:

```Authorization: Basic <base64(admin:secret)>
```

## 📊 Monitoring

### Prometheus Metrics

The application exposes Prometheus metrics at:
- Metrics endpoint: `http://localhost:8080/actuator/prometheus`

Key metrics available:
- JVM metrics
- HTTP request metrics
- Database connection pool metrics
- Custom business metrics

### Grafana Dashboards

Access Grafana at `http://localhost:3000` with default credentials:
- Username: admin
- Password: admin

Pre-configured dashboards:
- JVM Metrics Dashboard
- HTTP Request Dashboard
- Database Metrics Dashboard
- Custom Business Metrics Dashboard

## 🚧 Pending Tasks

### High Priority
1. Implement rate limiting for API endpoints
2. Add security headers and CORS configuration
3. Set up CI/CD pipeline
4. Implement proper role-based access control
5. Add more comprehensive monitoring alerts

### Medium Priority
1. Implement distributed tracing
2. Create Kubernetes manifests
3. Add performance optimization
4. Implement backup strategy
5. Add more Grafana dashboards

### Low Priority
1. Add more comprehensive API documentation
2. Implement additional caching strategies
3. Add more test coverage
4. Add more JMeter test scenarios
5. Implement automated backup system

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- Toan Nguyen



