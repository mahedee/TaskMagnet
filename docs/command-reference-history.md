# TaskMagnet - Command Reference & History

## Document Information
- **Version**: 1.0
- **Last Updated**: August 4, 2025
- **Status**: Active
- **Author**: TaskMagnet Development Team

---

## 📋 Overview

This document serves as a comprehensive reference for all commands used in the TaskMagnet project development lifecycle. It includes historical commands that have been executed and future commands that will be needed for ongoing development, testing, and deployment.

---

## 🏗️ Project Setup Commands

### Initial Project Creation
```bash
# Create project directory
mkdir TaskMagnet
cd TaskMagnet

# Initialize Git repository
git init
git remote add origin https://github.com/mahedee/TaskMagnet.git
git branch -M main
git checkout -b dev

# Create project structure
mkdir -p src/backend
mkdir -p src/frontend
mkdir -p docs
mkdir -p sql
```

### Maven Multi-Module Setup
```bash
# Navigate to backend directory
cd src/backend

# Create parent POM structure
# (Manual creation of pom.xml files)

# Create core module
mkdir taskmagnet-core
cd taskmagnet-core
mkdir -p src/main/java/com/mahedee/taskmagnet/core
mkdir -p src/main/resources
mkdir -p src/test/java/com/mahedee/taskmagnet/core

# Create web module
cd ..
mkdir taskmagnet-web
cd taskmagnet-web
mkdir -p src/main/java/com/mahedee/backend
mkdir -p src/main/resources
mkdir -p src/test/java/com/mahedee/backend
```

---

## 🔨 Build & Compilation Commands

### Maven Build Commands
```bash
# Clean and compile project
mvn clean compile

# Clean and package
mvn clean package

# Skip tests during build (for quick builds)
mvn clean package -DskipTests

# Run specific module
cd taskmagnet-web
mvn spring-boot:run

# Build with specific profile
mvn clean package -Pdev
mvn clean package -Pprod

# Install to local repository
mvn clean install

# Generate dependency tree
mvn dependency:tree

# Check for dependency updates
mvn versions:display-dependency-updates

# Update Maven wrapper
mvn wrapper:wrapper
```

### Build Verification Commands
```bash
# Verify build integrity
mvn verify

# Compile test sources
mvn test-compile

# Run only compilation phase
mvn compile

# Check for compile errors
mvn compile -q
```

---

## 🏃‍♂️ Application Runtime Commands

### Spring Boot Application Commands
```bash
# Run application from root
cd src/backend
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Run with JVM arguments
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx1024m -Xms512m"

# Run with environment variables
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081

# Run in debug mode
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### JAR Execution Commands
```bash
# Run packaged JAR
java -jar taskmagnet-web/target/taskmagnet-web-0.0.1-SNAPSHOT.jar

# Run with profile
java -jar -Dspring.profiles.active=dev taskmagnet-web/target/taskmagnet-web-0.0.1-SNAPSHOT.jar

# Run with custom port
java -jar -Dserver.port=8081 taskmagnet-web/target/taskmagnet-web-0.0.1-SNAPSHOT.jar

# Run with heap memory settings
java -Xmx1024m -Xms512m -jar taskmagnet-web/target/taskmagnet-web-0.0.1-SNAPSHOT.jar
```

---

## 🧪 Testing Commands

### Unit Testing Commands
```bash
# Run all tests
mvn test

# Run tests for specific module
cd taskmagnet-core
mvn test

# Run specific test class
mvn test -Dtest=BaseResponseTest

# Run specific test method
mvn test -Dtest=BaseResponseTest#testSuccessResponse

# Run tests with coverage
mvn test jacoco:report

# Run tests in parallel
mvn test -DforkCount=4

# Run tests with specific profile
mvn test -Ptest

# Skip tests
mvn package -DskipTests
mvn package -Dmaven.test.skip=true
```

### Integration Testing Commands
```bash
# Run integration tests
mvn integration-test

# Run integration tests with TestContainers
mvn test -Dtest=*IntegrationTest

# Run full test suite
mvn verify

# Run tests with database
mvn test -Dspring.profiles.active=test
```

### Test Coverage Commands
```bash
# Generate coverage report
mvn jacoco:report

# Check coverage thresholds
mvn jacoco:check

# Generate site with coverage
mvn site

# Open coverage report
# Navigate to target/site/jacoco/index.html
```

---

## 🗄️ Database Commands

### Oracle Database Commands
```sql
-- Connect to Oracle Database
sqlplus taskmagnet/mahedee.net@localhost:1521/XEPDB1

-- Create user and schema
CREATE USER taskmagnet IDENTIFIED BY "mahedee.net";
GRANT CONNECT, RESOURCE TO taskmagnet;
GRANT CREATE SESSION TO taskmagnet;
GRANT CREATE TABLE TO taskmagnet;
GRANT CREATE SEQUENCE TO taskmagnet;
GRANT CREATE VIEW TO taskmagnet;

-- Check database connection
SELECT * FROM dual;

-- Show current user
SELECT USER FROM dual;

-- List all tables
SELECT table_name FROM user_tables;

-- Check table structure
DESCRIBE users;
DESCRIBE roles;
DESCRIBE user_roles;

-- Insert default roles
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
COMMIT;
```

### Database Migration Commands
```bash
# Using Flyway (if integrated)
mvn flyway:migrate
mvn flyway:clean
mvn flyway:info
mvn flyway:validate

# Using Liquibase (if integrated)
mvn liquibase:update
mvn liquibase:rollback
mvn liquibase:status
```

---

## 🔍 Development & Debugging Commands

### Code Analysis Commands
```bash
# Find Java files
find . -name "*.java" -type f

# Search for specific patterns
grep -r "TODO" src/
grep -r "@TODO" src/
grep -r "FIXME" src/

# Count lines of code
find src/ -name "*.java" | xargs wc -l

# Check code style with Checkstyle
mvn checkstyle:check

# Run SpotBugs analysis
mvn spotbugs:check

# Run PMD analysis
mvn pmd:check
```

### Git Commands (Historical & Future)
```bash
# Initialize and setup
git init
git remote add origin https://github.com/mahedee/TaskMagnet.git
git checkout -b dev

# Daily workflow
git status
git add .
git commit -m "feat: implement TASK-ARCH-001 core module setup"
git push origin dev

# Feature branch workflow
git checkout -b feature/TASK-ARCH-002-database-architecture
git add .
git commit -m "feat: implement database domain boundaries"
git push origin feature/TASK-ARCH-002-database-architecture

# Merge workflow
git checkout dev
git merge feature/TASK-ARCH-002-database-architecture
git push origin dev

# Tag releases
git tag v1.0.0
git push origin v1.0.0

# View history
git log --oneline
git log --graph --oneline --all

# Check differences
git diff
git diff HEAD~1
```

---

## 🌐 API Testing Commands

### cURL Commands for API Testing
```bash
# Health check
curl -X GET http://localhost:8080/actuator/health

# User registration
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "role": ["user"]
  }'

# User login
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# Authenticated request
curl -X GET http://localhost:8080/api/test/user \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get API documentation
curl -X GET http://localhost:8080/v3/api-docs
```

### HTTPie Commands (Alternative to cURL)
```bash
# Install HTTPie
pip install httpie

# User registration
http POST localhost:8080/api/auth/signup \
  username=testuser \
  email=test@example.com \
  password=password123 \
  role:='["user"]'

# User login
http POST localhost:8080/api/auth/signin \
  username=testuser \
  password=password123

# Authenticated request
http GET localhost:8080/api/test/user \
  "Authorization:Bearer YOUR_JWT_TOKEN"
```

---

## 📦 Dependency Management Commands

### Maven Dependency Commands
```bash
# Add dependency (manual edit pom.xml then)
mvn dependency:resolve

# Check for vulnerabilities
mvn org.owasp:dependency-check-maven:check

# Update dependencies
mvn versions:use-latest-versions

# Display dependency tree
mvn dependency:tree -Dverbose

# Analyze dependencies
mvn dependency:analyze

# Download sources
mvn dependency:sources

# Download Javadocs
mvn dependency:resolve -Dclassifier=javadoc
```

---

## 🚀 Deployment Commands

### Docker Commands (Future Implementation)
```bash
# Build Docker image
docker build -t taskmagnet:latest .

# Run container
docker run -p 8080:8080 taskmagnet:latest

# Run with environment variables
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e ORACLE_URL=jdbc:oracle:thin:@db:1521/XE \
  taskmagnet:latest

# Docker Compose
docker-compose up -d
docker-compose down
docker-compose logs taskmagnet

# Build multi-stage
docker build -f Dockerfile.prod -t taskmagnet:prod .
```

### Kubernetes Commands (Future Implementation)
```bash
# Apply configurations
kubectl apply -f k8s/

# Check deployments
kubectl get deployments
kubectl get pods
kubectl get services

# View logs
kubectl logs -f deployment/taskmagnet

# Scale application
kubectl scale deployment taskmagnet --replicas=3

# Update deployment
kubectl set image deployment/taskmagnet taskmagnet=taskmagnet:v2.0.0
```

---

## 🔧 Environment Setup Commands

### Java Environment
```bash
# Check Java version
java -version
javac -version

# Set JAVA_HOME (Windows)
set JAVA_HOME=C:\Program Files\Java\jdk-17

# Set JAVA_HOME (Linux/Mac)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
echo $JAVA_HOME

# Check Java installation
which java
java -XshowSettings:properties -version
```

### Maven Environment
```bash
# Check Maven version
mvn -version

# Set MAVEN_HOME (Windows)
set MAVEN_HOME=C:\Program Files\Apache\maven\apache-maven-3.9.0

# Set MAVEN_HOME (Linux/Mac)
export MAVEN_HOME=/opt/maven
echo $MAVEN_HOME

# Update Maven settings
mvn help:effective-settings
```

### Oracle Database Setup
```bash
# Start Oracle Database (Windows Service)
net start OracleServiceXE
net start OracleXETNSListener

# Stop Oracle Database
net stop OracleServiceXE
net stop OracleXETNSListener

# Check Oracle processes (Linux)
ps -ef | grep oracle

# Connect via SQL*Plus
sqlplus / as sysdba
sqlplus taskmagnet/mahedee.net@localhost:1521/XEPDB1

# Check listener status
lsnrctl status
lsnrctl start
lsnrctl stop
```

---

## 📊 Monitoring & Logging Commands

### Application Monitoring
```bash
# Check application status
curl http://localhost:8080/actuator/health

# View application info
curl http://localhost:8080/actuator/info

# Check metrics
curl http://localhost:8080/actuator/metrics

# View environment
curl http://localhost:8080/actuator/env

# Check configuration properties
curl http://localhost:8080/actuator/configprops
```

### Log Commands
```bash
# View application logs (if using file logging)
tail -f logs/application.log
tail -100 logs/application.log

# Search logs
grep "ERROR" logs/application.log
grep "JWT" logs/application.log

# Monitor real-time logs
tail -f logs/application.log | grep ERROR

# Log rotation check
ls -la logs/
```

---

## 🔒 Security & Performance Commands

### Security Scanning
```bash
# OWASP Dependency Check
mvn org.owasp:dependency-check-maven:check

# SpotBugs security analysis
mvn com.github.spotbugs:spotbugs-maven-plugin:check

# Security audit with Snyk (if integrated)
snyk test
snyk monitor
```

### Performance Testing
```bash
# JMeter load testing (future)
jmeter -n -t performance-test.jmx -l results.jtl

# Apache Bench testing
ab -n 1000 -c 10 http://localhost:8080/api/auth/signin

# Siege load testing
siege -c 10 -t 60s http://localhost:8080/actuator/health
```

---

## 🚀 CI/CD Commands (Future Implementation)

### GitHub Actions
```bash
# Trigger workflow manually
gh workflow run ci.yml

# View workflow status
gh run list
gh run view <run-id>

# Download artifacts
gh run download <run-id>
```

### Jenkins Commands
```bash
# Build job
curl -X POST http://jenkins:8080/job/taskmagnet/build

# Get build status
curl http://jenkins:8080/job/taskmagnet/lastBuild/api/json
```

---

## 📁 File & Directory Operations

### Common File Operations
```bash
# Find files by pattern
find . -name "*.java" -type f
find . -name "pom.xml" -type f

# Search content in files
grep -r "BaseResponse" src/
grep -r "@Entity" src/

# Count lines in Java files
find src/ -name "*.java" -exec wc -l {} + | sort -n

# Check file permissions
ls -la src/
chmod +x scripts/build.sh

# Create directory structure
mkdir -p src/main/java/com/mahedee/taskmagnet/{core,security,web}
```

### Documentation Commands
```bash
# Generate Javadoc
mvn javadoc:javadoc

# Generate site documentation
mvn site

# Serve documentation locally
mvn site:run
```

---

## 🔄 Maintenance Commands

### Cleanup Commands
```bash
# Clean Maven build artifacts
mvn clean

# Clean all target directories
find . -name "target" -type d -exec rm -rf {} +

# Clean IDE files
find . -name ".DS_Store" -delete
find . -name "*.iml" -delete
find . -name ".idea" -type d -exec rm -rf {} +

# Clean logs
rm -rf logs/*
```

### Backup Commands
```bash
# Backup database
# Oracle Database backup
exp taskmagnet/mahedee.net@localhost:1521/XEPDB1 file=taskmagnet_backup.dmp

# Backup project
tar -czf taskmagnet-backup-$(date +%Y%m%d).tar.gz TaskMagnet/

# Git backup
git bundle create taskmagnet-backup.bundle --all
```

---

## 📝 Command Templates for Future Tasks

### TASK-ARCH-002: Database Architecture Commands
```bash
# Create migration scripts
mkdir -p src/main/resources/db/migration

# Run database schema creation
sqlplus taskmagnet/mahedee.net@localhost:1521/XEPDB1 @src/sql/create_schema.sql

# Validate schema
mvn flyway:validate
```

### TASK-ARCH-003: Redis Integration Commands
```bash
# Start Redis server
redis-server

# Connect to Redis CLI
redis-cli

# Check Redis connection
redis-cli ping

# Monitor Redis
redis-cli monitor
```

### TASK-SEC-001: Security Module Commands
```bash
# Run security tests
mvn test -Dtest=*SecurityTest

# Generate security report
mvn org.owasp:dependency-check-maven:check

# Test JWT functionality
curl -X POST http://localhost:8080/api/auth/test-jwt
```

### Frontend Development Commands (Future)
```bash
# Create React app
npx create-react-app taskmagnet-frontend
cd taskmagnet-frontend

# Install dependencies
npm install @reduxjs/toolkit react-redux
npm install axios react-router-dom

# Start development server
npm start

# Build for production
npm run build

# Run tests
npm test

# Run ESLint
npm run lint
```

---

## 🆘 Troubleshooting Commands

### Common Issues & Solutions
```bash
# Port already in use
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # Linux/Mac
kill -9 <PID>                 # Kill process

# Maven dependency issues
mvn dependency:purge-local-repository
mvn clean install -U

# Oracle connection issues
sqlplus taskmagnet/mahedee.net@localhost:1521/XEPDB1
tnsping localhost:1521

# Java heap issues
export MAVEN_OPTS="-Xmx2048m -Xms1024m"
mvn clean install

# Permission issues (Linux/Mac)
chmod +x mvnw
sudo chown -R $USER:$USER .
```

### Debug Commands
```bash
# Debug Maven build
mvn clean install -X -e

# Debug Spring Boot application
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Check classpath
mvn dependency:build-classpath

# Verbose garbage collection
java -XX:+PrintGC -XX:+PrintGCDetails -jar app.jar
```

---

## 📋 Command Execution History

### Completed Commands (August 4, 2025)
```bash
# Project setup and multi-module creation
cd src/backend
mvn clean compile
mvn clean package -DskipTests

# Application testing
mvn spring-boot:run
curl http://localhost:8080/actuator/health
curl http://localhost:8080/swagger-ui/index.html

# Documentation updates
# (File edits performed via development tools)
```

### Scheduled Commands (Next Implementation Phase)
```bash
# TASK-ARCH-002 Implementation
sqlplus taskmagnet/mahedee.net@localhost:1521/XEPDB1
mvn flyway:migrate

# TASK-ARCH-003 Implementation
redis-server
mvn test -Dtest=*CacheTest

# TASK-SEC-001 Implementation
mvn test -Dtest=*SecurityTest
curl -X POST http://localhost:8080/api/auth/test-security
```

---

## 📚 Reference Links

### Command Documentation
- [Maven Commands Reference](https://maven.apache.org/ref/current/maven-embedder/cli.html)
- [Spring Boot Maven Plugin](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/)
- [Oracle SQL*Plus Commands](https://docs.oracle.com/en/database/oracle/oracle-database/21/sqpug/)
- [Git Commands Reference](https://git-scm.com/docs)

### Tool Documentation
- [Docker Commands](https://docs.docker.com/engine/reference/commandline/)
- [Kubernetes kubectl](https://kubernetes.io/docs/reference/kubectl/)
- [cURL Manual](https://curl.se/docs/manpage.html)

---

*Last Updated: August 4, 2025*
*Version: 1.0*
*Document Owner: TaskMagnet Development Team*
