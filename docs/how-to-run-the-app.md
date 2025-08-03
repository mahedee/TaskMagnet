# How to Run the TaskMagnet Application

This guide provides step-by-step instructions to run the TaskMagnet application successfully in your development environment.

## 🚀 Quick Start Guide

### Prerequisites Checklist
Before running the application, ensure you have:
- ✅ Java 17 installed and configured
- ✅ Maven 3.6+ installed
- ✅ Oracle Database XE 21c installed and running
- ✅ TaskMagnet database user created
- ✅ VS Code with Java extensions (optional but recommended)

---

## 🔧 Step-by-Step Setup

### Step 1: Verify Java Installation
```powershell
# Check Java version
java -version

# Expected output:
# java version "17.0.x" 2023-xx-xx LTS
# Java(TM) SE Runtime Environment (build 17.0.x+xx-LTS-xxx)
# Java HotSpot(TM) 64-Bit Server VM (build 17.0.x+xx-LTS-xxx, mixed mode, sharing)
```

### Step 2: Verify Maven Installation
```powershell
# Check Maven version
mvn -version

# Expected output:
# Apache Maven 3.x.x
# Maven home: C:\Program Files\Apache\maven
# Java version: 17.0.x
```

### Step 3: Verify Oracle Database
```powershell
# Check Oracle services status
Get-Service -Name "OracleServiceXE"
Get-Service -Name "OracleXETNSListener"

# Both services should show Status: Running
```

### Step 4: Database Connection Test
```powershell
# Connect to Oracle (optional verification)
sqlplus taskmagnet/mahedee.net@localhost:1521/XEPDB1

# If successful, you should see:
# Connected to:
# Oracle Database 21c Express Edition Release 21.0.0.0.0 - Production
```

---

## 🏃‍♂️ Running the Application

### Method 1: Using Maven (Recommended)

#### Option A: Direct Maven Run
```powershell
# Navigate to backend directory
cd "D:\Projects\Github\TaskMagnet\src\backend"

# Clean and run the application
mvn clean spring-boot:run
```

#### Option B: Package and Run
```powershell
# Navigate to backend directory
cd "D:\Projects\Github\TaskMagnet\src\backend"

# Clean and package
mvn clean package

# Run the packaged JAR
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### Method 2: Using VS Code

#### Option A: Run Configuration
1. Open VS Code
2. Navigate to `src/backend`
3. Open `BackendApplication.java`
4. Click the "Run" button above the `main` method
5. Or press `F5` to debug

#### Option B: Terminal in VS Code
1. Open VS Code terminal (`Ctrl + ``)
2. Navigate to backend directory:
   ```powershell
   cd src/backend
   ```
3. Run Maven command:
   ```powershell
   mvn spring-boot:run
   ```

### Method 3: Using Command Line
```powershell
# Change to project directory
cd "D:\Projects\Github\TaskMagnet\src\backend"

# Run with Maven wrapper (if available)
.\mvnw spring-boot:run

# Or use system Maven
mvn spring-boot:run
```

---

## 📊 Application Startup Process

### Expected Startup Sequence
1. **Banner Display**: Spring Boot banner appears
2. **Configuration Loading**: Properties files loaded
3. **Database Connection**: Oracle connection established
4. **JPA Initialization**: Hibernate entities mapped
5. **Seed Data Execution**: Roles and admin user created
6. **Security Configuration**: JWT and Spring Security initialized
7. **Swagger Setup**: API documentation configured
8. **Server Start**: Tomcat server starts on port 8080

### Startup Logs to Watch For
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::               (v3.1.5)

2025-08-03 10:30:00.123 INFO  --- [main] c.m.backend.BackendApplication    : Starting BackendApplication
2025-08-03 10:30:01.456 INFO  --- [main] c.m.b.configuration.DataSeeder    : Starting data seeding process...
2025-08-03 10:30:02.789 INFO  --- [main] c.m.b.configuration.DataSeeder    : Seeded role: ROLE_USER
2025-08-03 10:30:02.891 INFO  --- [main] c.m.b.configuration.DataSeeder    : Seeded role: ROLE_MODERATOR
2025-08-03 10:30:02.992 INFO  --- [main] c.m.b.configuration.DataSeeder    : Seeded role: ROLE_ADMIN
2025-08-03 10:30:03.123 INFO  --- [main] c.m.b.configuration.DataSeeder    : Admin user created successfully
2025-08-03 10:30:04.456 INFO  --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port(s): 8080 (http)
2025-08-03 10:30:04.567 INFO  --- [main] c.m.backend.BackendApplication    : Started BackendApplication in 15.234 seconds
```

---

## 🌐 Accessing the Application

### Main Endpoints
Once the application is running, you can access:

#### Swagger API Documentation
```
URL: http://localhost:8080/swagger-ui.html
Purpose: Interactive API documentation and testing
Authentication: No authentication required
```

#### API Base URL
```
URL: http://localhost:8080/api
Purpose: REST API endpoints
Authentication: JWT token required (except auth endpoints)
```

#### Health Check
```
URL: http://localhost:8080/actuator/health
Purpose: Application health status
Authentication: No authentication required
```

### Default Login Credentials
```
Username: admin
Password: Taskmagnet@2025
Role: ADMIN
```

---

## 🧪 Testing the Application

### Test API Endpoints

#### 1. User Registration
```bash
# Using curl
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test@123456",
    "role": ["user"]
  }'
```

#### 2. User Login
```bash
# Using curl
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Taskmagnet@2025"
  }'
```

#### 3. Access Protected Endpoint
```bash
# Using curl (replace YOUR_JWT_TOKEN with actual token)
curl -X GET http://localhost:8080/api/test/admin \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Using Swagger UI for Testing
1. Open http://localhost:8080/swagger-ui.html
2. Click on "auth-controller"
3. Try the `/api/auth/signin` endpoint
4. Use admin credentials to get JWT token
5. Click "Authorize" button and enter the token
6. Test other protected endpoints

---

## 🛑 Stopping the Application

### Graceful Shutdown

#### If Running with Maven
```powershell
# Press Ctrl+C in the terminal where the application is running
# Wait for graceful shutdown message:
# "Shutdown completed."
```

#### If Running as JAR
```powershell
# Press Ctrl+C or send SIGTERM signal
# Application will shut down gracefully
```

#### Force Stop (if needed)
```powershell
# Find Java processes
Get-Process java

# Kill specific process (replace XXXX with actual PID)
Stop-Process -Id XXXX -Force
```

---

## 🔧 Development Workflow

### Making Changes

#### Code Changes
1. Stop the application (`Ctrl+C`)
2. Make your code changes
3. Save all files
4. Restart the application:
   ```powershell
   mvn spring-boot:run
   ```

#### Database Schema Changes
1. Stop the application
2. Modify entity classes
3. Update application properties if needed
4. Restart application (Hibernate will update schema)

#### Configuration Changes
1. Stop the application
2. Update properties files
3. Restart the application

### Hot Reload (Optional)
For faster development, you can enable Spring Boot DevTools:

1. Add DevTools dependency to `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-devtools</artifactId>
       <scope>runtime</scope>
       <optional>true</optional>
   </dependency>
   ```

2. Restart the application
3. Changes to Java classes will trigger automatic restart

---

## 🐛 Troubleshooting

### Common Issues and Solutions

#### Issue 1: Application Won't Start
**Error**: `Port 8080 is already in use`
```powershell
# Solution 1: Find and kill process using port 8080
netstat -ano | findstr :8080
taskkill /PID <PID_NUMBER> /F

# Solution 2: Change server port in application.properties
echo "server.port=8081" >> src/main/resources/application.properties
```

#### Issue 2: Database Connection Failed
**Error**: `ORA-12560: TNS:protocol adapter error`
```powershell
# Solution: Start Oracle services
net start OracleServiceXE
net start OracleXETNSListener
```

#### Issue 3: Authentication Issues
**Error**: `Invalid credentials`
```powershell
# Solution: Ensure seed data was created
# Check application logs for "Data seeding process completed"
# If not, check database connection and restart application
```

#### Issue 4: Maven Build Failed
**Error**: `The process cannot access the file because it is being used by another process`
```powershell
# Solution: Stop running application first
# Press Ctrl+C to stop application
# Then run Maven commands
```

#### Issue 5: Out of Memory Error
**Error**: `OutOfMemoryError: Java heap space`
```powershell
# Solution: Increase heap size
set MAVEN_OPTS=-Xmx1024m -Xms512m
mvn spring-boot:run
```

### Debug Mode
To run in debug mode for troubleshooting:
```powershell
# Enable debug logging
mvn spring-boot:run -Dspring.profiles.active=dev -Ddebug=true
```

---

## 📊 Performance Monitoring

### Application Metrics
Once running, monitor:
- **Memory Usage**: Check console for memory warnings
- **Startup Time**: Should be ~15-20 seconds
- **API Response Time**: Monitor via Swagger UI
- **Database Connections**: Check Oracle connection pool

### Log Files
Application logs are displayed in console. For production, configure file logging:
```properties
# Add to application.properties
logging.file.name=taskmagnet.log
logging.level.com.mahedee=INFO
```

---

## 🚀 Production Readiness

### Before Deploying to Production
1. **Change Default Passwords**: Update admin password
2. **Configure HTTPS**: Enable SSL/TLS
3. **Environment Variables**: Externalize configuration
4. **Database Optimization**: Tune Oracle settings
5. **Monitoring Setup**: Configure application monitoring
6. **Backup Strategy**: Implement database backups

### Production Run Command
```bash
# Use production profile
java -jar -Dspring.profiles.active=prod target/backend-0.0.1-SNAPSHOT.jar
```

---

## 📞 Support and Help

### Getting Help
- **Documentation**: Check `docs/` folder for guides
- **Logs**: Check application startup logs for errors
- **Database**: Verify Oracle services are running
- **Configuration**: Review properties files for correct settings

### Common Commands Reference
```powershell
# Build project
mvn clean compile

# Run tests
mvn test

# Package application
mvn clean package

# Run application
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring.profiles.active=dev

# Debug mode
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

---

*Happy Coding! 🎉*

---

*Guide Last Updated: August 3, 2025*
*Version: 1.0*
*For: TaskMagnet Development Team*
