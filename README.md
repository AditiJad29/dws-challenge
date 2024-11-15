# Money-Transfer-App-Challenge
A basic Spring Boot application that handles money transfers between user accounts. 
This application demonstrates key concepts such as RESTful API development, transaction management, and secure money transfers in a Spring Boot environment. 
The application is built with Maven.

## Prerequisites

Before you begin, ensure you have met the following requirements:

- **Java 17** or later
- **Maven** (for building the project)
- **IDE** (e.g., IntelliJ IDEA, Eclipse)

## Installation

1. **Clone the repository**:
    ```bash
    git clone https://github.com/AditiJad29/dws-challenge.git
    cd dws-challenge
    ```

2. **Install dependencies**:
    Use Maven to install the necessary dependencies:
    ```bash
    mvn clean install
    ```

3. **Start the application**:
   Execute the main method in the com/dws/challenge/ChallengeApplication.java class from your IDE.
   This will start the application on the port (`18080`).

# Further improvements that can be done in the project for production-readiness
## 1. Implement secure authentication and authorization.
  a. Integrate with authentication & authorization systems to provide role-based access control to the APIs.
  b. Secure the REST APIs by using HTTPS
  c. Encrypt sensitive data like account details, transactions, etc., both at rest and in transit. Use AES or RSA encryption for sensitive information storage.
## Set up database transactions and backups.
  a. Integrate with a Database Management System to persist account and transaction details.
  b. Use Springboot-data-jpa to save, get, update data in the database.
  c. Use Spring's @Transactional to ensure money transfers are atomic. If any part of the transfer fails (e.g., insufficient balance, network issue), you want to roll back the entire transaction to avoid inconsistencies.
## Apply rate limiting check
  a. Use API rate limiting to prevent abuse or excessive load on the system. This is crucial for production environments to protect against DDoS attacks or accidental spikes.
  b. Tools like Bucket4J or Spring Rate Limiter can help manage this.
## Ensure robust logging, monitoring, and alerting.
  a. Use monitoring tools like Prometheus, Grafana, or Micrometer to track key metrics like transaction processing time, database latency, and error rates.
  b. Implement health checks (/actuator/health in Spring Boot) to ensure the app is functioning properly in production.
  c. Set up alerting for issues like high response times or failed transactions to catch problems early.
## Use CI/CD for efficient deployments.
