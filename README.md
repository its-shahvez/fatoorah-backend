
# 🇸🇦 Fatoorah Pro - Saudi ZATCA Phase 2 E-Invoicing System (Backend)

Enterprise-grade, secure Java Spring Boot backend built for **Saudi ZATCA Phase 2 (Fatoora Integration & Clearance)** compliance. It implements cryptographic stamp generation using ECDSA (`secp256k1`), blockchain-style invoice hash-chaining (ICV & PIH), UBL 2.1 XML generation, TLV QR code encoding, and persistent H2 database storage.

This repository powers the backend REST API consumed by the [Fatoorah Pro React Frontend](https://github.com/its-shahvez/fatoorah-pro-frontend).

---

## 🌟 Core Backend Features

* **ZATCA Cryptographic Engine (`ZatcaCryptoEngine`)**:
  * BouncyCastle `secp256k1` key pair generation and digital signatures.
  * SHA-256 XML canonicalization and hashing.
  * TLV (Tag-Length-Value) Base64 QR Code generation (Tags 1–8).
* **Hash-Chain Security & Tamper Detection**:
  * Sequential Invoice Counter Value (`ICV`) tracking.
  * Previous Invoice Hash (`PIH`) blockchain linkage to prevent historical ledger manipulation.
  * Tamper simulation and integrity verification endpoints.
* **REST API Endpoints (`FatoorahApiController`)**:
  * Full Bootstrap payload (`/api/v1/bootstrap`) loading organization, customers, products, invoices, audit logs, and chain status in a single call.
  * Invoice clearance and UBL 2.1 XML exporters.
  * CSID onboarding simulation via OTP.
* **AI Fiscal Copilot**: Integrated rule-based and context-aware financial assistant (`AiCopilotService`).
* **Persistent Database**: H2 embedded database configured for reliable record persistence.

---

## 🛠️ Tech Stack

* **Language & Framework**: Java 21, Spring Boot 3.3.5
* **Security & Crypto**: Spring Security, BouncyCastle Crypto APIs
* **Database**: H2 Database Engine (Persistent Mode)
* **Build Tool**: Maven 3.9+
* **Deployment**: Docker, Render (Cloud PaaS)

---

## 🚀 Getting Started Locally

### Prerequisites
* Java 21 JDK installed
* Maven (or use included `./mvnw` wrapper)

### Installation & Run

1. **Clone the repository**:
   ```bash
   git clone [https://github.com/its-shahvez/fatoorah-backend.git](https://github.com/its-shahvez/fatoorah-backend.git)
   cd fatoorah-backend
Configure Port & Environment:
Check src/main/resources/application.properties:

Properties
server.port=${PORT:8080}
Run the Spring Boot Application:

Bash
./mvnw spring-boot:run
The backend will start on http://localhost:8080.

Verify Live Bootstrap Endpoint:
Open http://localhost:8080/api/v1/bootstrap to view live organizational JSON data, invoices, and cryptographic verification statuses.

🐳 Docker Deployment
The application includes a root Dockerfile optimized for containerized cloud deployment (Render, AWS, Railway):

Dockerfile
# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
🌐 Live Production Deployment
Backend REST Gateway: Deployed on Render Cloud (https://fatoorah-backend.onrender.com/api/v1)

Frontend Integration: Connected via CORS-enabled REST endpoints supporting secure cross-origin requests from Netlify.

📄 License
This project is licensed under the MIT License.

Developed by Mohd Shahvez 


-
