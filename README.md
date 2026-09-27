# The Development of Prefect Management System for the Office of Student Discipline (OSD) – Backend

## Description

The OSD Backend is the central REST API server of the Prefect Management System for the Office of Student Discipline. It connects the prefect desktop application and the student mobile app to one shared Oracle Database, so that both always work with the same, up-to-date records.

The server handles user authentication, student and enrollment data, disciplinary records, offenses, appeals, and permission requests. It also powers an AI Support Module that reads uploaded appeal documents and suggests relevant guidance, and an AI chat assistant that helps students understand their own records. By centralizing these services, the system keeps data consistent, protects it with role-based access, and makes disciplinary case management faster and more reliable.

## Features

- User Authentication: Secure login, token refresh, and logout using JWT (JSON Web Tokens).
- Role-Based Access Control: Separate permissions for Students, Staff, Prefects, and Administrators. Students can only access their own records.
- Student & Enrollment Management: Manages student profiles, guardians, and enrollment details.
- Offense Management: Maintains the list of offenses, including setting offenses as active or inactive.
- Disciplinary Records: Allows recording, updating, and resolving student violations and their disciplinary actions.
- Appeals Management: Receives student appeals and lets prefects approve or deny them.
- Permission Requests: Handles submission of requests and their approval or denial by the assigned department.
- AI Support Module: Reads uploaded appeal documents using OCR (Tesseract), extracts keywords, and matches them to relevant suggestions for prefects.
- AI Chat Assistant: Answers students' questions about their own records and appeals using a local AI model (Ollama).

## Technologies Used

- Java 21
- Spring Boot (Web MVC, Data JPA, Security)
- JWT Authentication (Auth0 java-jwt)
- Layered Architecture (Controller – Service – Repository)
- Oracle Database
- Hibernate / JPA
- Ollama (local AI model, `llama3.2` by default)
- Apache Tika & Tesseract OCR (Tess4J)
- Lombok
- Gradle
- JUnit 5 & Mockito (Testing)

## Setup Development Environment

Follow these steps to run the server locally:

1. Install Required Software
   - IntelliJ IDEA
   - JDK 21
   - Oracle Database and Oracle SQL Developer
   - Git
   - Ollama (for the AI Chat Assistant)
   - Tesseract OCR (for the AI Support Module)
2. Clone the Repository
   ```bash
   git clone https://github.com/bsit5012025/rc-osd-backend.git
   ```
3. Open the Project in IntelliJ IDEA
   - Launch IntelliJ IDEA.
   - Select Open and navigate to the project folder.
   - Wait for Gradle to finish loading all project dependencies.
4. Set Up the Oracle Database
   - Open Oracle SQL Developer.
   - Create a new database connection.
   - Execute the provided `.sql` scripts to create the necessary tables and schema.
5. Configure the Application
   - Open `src/main/resources/application.yaml`, or set the matching environment variables:
     - Database URL (`DB_URL`)
     - Username (`DB_USERNAME`)
     - Password (`DB_PASSWORD`)
     - JWT Secret (`JWT_SECRET`), which should be changed from the default before deployment
     - Ollama URL and model (`OLLAMA_BASE_URL`, `OLLAMA_MODEL`)
     - Tesseract data folder (`tesseract.tessdata-path`)
6. Set Up the AI Services
   - Install Ollama and download the model:
     ```bash
     ollama pull llama3.2
     ```
   - Make sure Ollama is running on `http://localhost:11434`.
   - Install Tesseract OCR and note the location of its `tessdata` folder.
7. Build and Run the Project
   - Build the project:
     ```bash
     gradlew build
     ```
   - Run the main class `OsdrmsaApplApplication`, or use:
     ```bash
     gradlew bootRun
     ```
   - The server starts at `http://localhost:8080`.

## System Requirements

- Windows 10 or higher
- Minimum 8 GB RAM (recommended for running the AI model locally)
- Available port `8080` for the API server

## Contributors

- Wilrow Bayona
- Carl Justine Cain
- Mark Joshua Camama
- John Zenith Cruz
- Angel Lowyza De Gala
- Geoffrey Allen De Rojas
- Keith Jasper Quevada
- Leeane Glazel Reyes
