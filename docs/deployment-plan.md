# TaskMagnet Deployment Plan
**Multi-Platform Deployment Strategy for Enterprise Environments**

## Document Information
- **Version**: 1.0
- **Last Updated**: August 4, 2025
- **Status**: Active
- **Author**: DevOps Team

---

## 📋 **Deployment Overview**

TaskMagnet supports multiple deployment strategies to meet diverse enterprise requirements:

1. **IBM WebSphere (Windows)** - Traditional enterprise deployment
2. **Docker Containerization** - Modern container-based deployment
3. **Kubernetes (K8s)** - Container orchestration platform
4. **Azure Cloud** - Microsoft cloud platform deployment

---

## 🏢 **1. IBM WebSphere Deployment (Windows)**

### **1.1 Architecture Overview**
```
┌─────────────────────────────────────────────────────────┐
│                IBM WebSphere (Windows)                  │
│  ┌─────────────────────────────────────────────────────┐│
│  │                Single WAR Deployment                ││
│  │  ┌─────────────────┐    ┌─────────────────────────┐ ││
│  │  │   React Build   │    │    Spring Boot API     │ ││
│  │  │  (Static Files) │    │   (Backend Services)   │ ││
│  │  │                 │    │                         │ ││
│  │  │  - index.html   │    │  - REST Controllers     │ ││
│  │  │  - CSS/JS       │    │  - Business Logic      │ ││
│  │  │  - Assets       │    │  - Database Layer      │ ││
│  │  └─────────────────┘    └─────────────────────────┘ ││
│  └─────────────────────────────────────────────────────┘│
│                         Port: 9080                      │
└─────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────┐
│                 Oracle Database                         │
│                    Port: 1521                          │
└─────────────────────────────────────────────────────────┘
```

### **1.2 Prerequisites**

**Software Requirements:**
- Windows Server 2019/2022 or Windows 10/11
- IBM WebSphere Liberty 23.x or WebSphere ND 9.x
- Oracle Database XE 21c or higher
- Java 17 or higher
- Maven 3.8+
- Node.js 18+ (for building React app)

**Hardware Requirements:**
- **Development**: 8GB RAM, 2 CPU cores, 50GB disk
- **Production**: 16GB RAM, 4+ CPU cores, 200GB disk

### **1.3 Installation Steps**

**Step 1: WebSphere Liberty Setup**
```powershell
# Download WebSphere Liberty from IBM Developer
# Extract to C:\IBM\wlp

# Create server instance
cd C:\IBM\wlp\bin
.\server.bat create taskmagnet

# Verify installation
.\server.bat version
```

**Step 2: Configure WebSphere Server**
```xml
<!-- C:\IBM\wlp\usr\servers\taskmagnet\server.xml -->
<server description="TaskMagnet Application Server">
    <featureManager>
        <feature>webProfile-10.0</feature>
        <feature>jpa-3.1</feature>
        <feature>jdbc-4.3</feature>
        <feature>jaxrs-3.1</feature>
        <feature>jsonp-2.1</feature>
        <feature>cdi-4.0</feature>
        <feature>servlet-6.0</feature>
    </featureManager>

    <!-- HTTP/HTTPS Endpoints -->
    <httpEndpoint id="defaultHttpEndpoint"
                  httpPort="9080"
                  httpsPort="9443"
                  host="*" />

    <!-- Application Configuration -->
    <application id="taskmagnet"
                 location="taskmagnet.war"
                 type="war">
        <classloader delegation="parentLast" />
    </application>

    <!-- Oracle DataSource -->
    <dataSource id="OracleDataSource"
                jndiName="jdbc/TaskMagnetDS"
                type="javax.sql.DataSource">
        <jdbcDriver>
            <library>
                <fileset dir="C:/oracle/jdbc/lib" includes="ojdbc11.jar"/>
            </library>
        </jdbcDriver>
        <properties.oracle URL="jdbc:oracle:thin:@localhost:1521:XE"
                           user="taskmagnet"
                           password="{aes}AEncryptedPassword"
                           connectionSharing="MatchCurrentState"/>
    </dataSource>

    <!-- Connection Pool Settings -->
    <connectionManager id="ConMgr1"
                       maxPoolSize="50"
                       minPoolSize="5"
                       connectionTimeout="30s"
                       maxIdleTime="1800s"
                       maxConnectionsPerThread="10"/>

    <!-- Logging Configuration -->
    <logging traceSpecification="*=info:com.mahedee.taskmagnet.*=debug"
             maxFileSize="20"
             maxFiles="10"
             traceFormat="ENHANCED" />

    <!-- Security Configuration -->
    <basicRegistry id="basic" realm="BasicRealm">
        <user name="admin" password="{aes}AdminPassword" />
        <group name="administrators">
            <member name="admin" />
        </group>
    </basicRegistry>

    <!-- SSL Configuration -->
    <ssl id="defaultSSLConfig"
         keyStoreRef="defaultKeyStore"
         trustStoreRef="defaultTrustStore" />
</server>
```

**Step 3: Application Configuration**

**Spring Boot Configuration for WebSphere:**
```java
// src/main/java/com/mahedee/taskmagnet/TaskMagnetApplication.java
@SpringBootApplication
public class TaskMagnetApplication extends SpringBootServletInitializer {
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TaskMagnetApplication.class);
    }
    
    public static void main(String[] args) {
        SpringApplication.run(TaskMagnetApplication.class, args);
    }
}
```

**Maven Configuration:**
```xml
<!-- pom.xml modifications for WebSphere deployment -->
<packaging>war</packaging>

<properties>
    <java.version>17</java.version>
    <spring-boot.version>3.1.2</spring-boot.version>
    <node.version>v18.17.0</node.version>
    <npm.version>9.6.7</npm.version>
</properties>

<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Exclude embedded Tomcat for WebSphere -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-tomcat</artifactId>
        <scope>provided</scope>
    </dependency>
    
    <!-- Oracle Database -->
    <dependency>
        <groupId>com.oracle.database.jdbc</groupId>
        <artifactId>ojdbc11</artifactId>
    </dependency>
</dependencies>

<build>
    <plugins>
        <!-- Frontend Maven Plugin for React Build -->
        <plugin>
            <groupId>com.github.eirslett</groupId>
            <artifactId>frontend-maven-plugin</artifactId>
            <version>1.13.4</version>
            <configuration>
                <workingDirectory>../frontend</workingDirectory>
            </configuration>
            <executions>
                <execution>
                    <id>install node and npm</id>
                    <goals>
                        <goal>install-node-and-npm</goal>
                    </goals>
                    <configuration>
                        <nodeVersion>${node.version}</nodeVersion>
                        <npmVersion>${npm.version}</npmVersion>
                    </configuration>
                </execution>
                <execution>
                    <id>npm install</id>
                    <goals>
                        <goal>npm</goal>
                    </goals>
                    <configuration>
                        <arguments>install</arguments>
                    </configuration>
                </execution>
                <execution>
                    <id>npm build</id>
                    <goals>
                        <goal>npm</goal>
                    </goals>
                    <configuration>
                        <arguments>run build</arguments>
                    </configuration>
                </execution>
            </executions>
        </plugin>
        
        <!-- Copy React build to static resources -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-resources-plugin</artifactId>
            <executions>
                <execution>
                    <id>copy-react-build</id>
                    <phase>process-resources</phase>
                    <goals>
                        <goal>copy-resources</goal>
                    </goals>
                    <configuration>
                        <outputDirectory>target/classes/static</outputDirectory>
                        <resources>
                            <resource>
                                <directory>../frontend/build</directory>
                                <filtering>false</filtering>
                            </resource>
                        </resources>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

**Step 4: Build and Deploy**
```powershell
# Build the application
cd D:\Projects\Github\TaskMagnet\src\backend
mvn clean package -P websphere

# Deploy to WebSphere
copy target\taskmagnet.war C:\IBM\wlp\usr\servers\taskmagnet\apps\

# Start WebSphere server
cd C:\IBM\wlp\bin
.\server.bat start taskmagnet

# Monitor logs
.\server.bat dump taskmagnet --include=logs
```

**Step 5: Verification**
```powershell
# Test application
curl http://localhost:9080/taskmagnet/
curl http://localhost:9080/taskmagnet/api/health

# Check WebSphere admin console (if using ND)
# https://localhost:9443/ibm/console
```

---

## 🐳 **2. Docker Containerization**

### **2.1 Architecture Overview**
```
┌─────────────────────────────────────────────────────────┐
│                    Docker Host                          │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────┐ │
│  │  Frontend       │  │   Backend       │  │Database │ │
│  │  Container      │  │   Container     │  │Container│ │
│  │                 │  │                 │  │         │ │
│  │  Nginx          │  │  Spring Boot    │  │ Oracle  │ │
│  │  React Build    │  │  Embedded Tomcat│  │   XE    │ │
│  │  Port: 80       │  │  Port: 8080     │  │Port:1521│ │
│  └─────────────────┘  └─────────────────┘  └─────────┘ │
│           │                     │                │      │
│           └─────────────────────┼────────────────┘      │
│                                 │                       │
│                    Docker Network: taskmagnet-net       │
└─────────────────────────────────────────────────────────┘
```

### **2.2 Dockerfile Configuration**

**Backend Dockerfile:**
```dockerfile
# src/backend/Dockerfile
FROM openjdk:17-jdk-alpine AS builder

# Install Maven
RUN apk add --no-cache maven

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jre-alpine

# Create app user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/taskmagnet-*.jar app.jar

# Change ownership
RUN chown appuser:appgroup /app/app.jar

# Switch to app user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
```

**Frontend Dockerfile:**
```dockerfile
# src/frontend/Dockerfile
# Build stage
FROM node:18-alpine AS builder

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy source code
COPY . .

# Build React app
RUN npm run build

# Production stage
FROM nginx:alpine

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Copy built React app
COPY --from=builder /app/build /usr/share/nginx/html

# Copy nginx configuration for React Router
COPY nginx-default.conf /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD curl -f http://localhost/ || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
```

**Nginx Configuration:**
```nginx
# src/frontend/nginx-default.conf
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;

    # Handle React Router
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API proxy to backend
    location /api/ {
        proxy_pass http://taskmagnet-backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Static assets caching
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, no-transform";
    }
}
```

### **2.3 Docker Compose Configuration**

```yaml
# docker-compose.yml
version: '3.8'

services:
  # Oracle Database
  oracle-db:
    image: container-registry.oracle.com/database/express:21.3.0-xe
    container_name: taskmagnet-oracle
    environment:
      - ORACLE_PWD=OraclePassword123
      - ORACLE_CHARACTERSET=AL32UTF8
    ports:
      - "1521:1521"
      - "5500:5500"
    volumes:
      - oracle_data:/opt/oracle/oradata
      - ./sql:/docker-entrypoint-initdb.d
    networks:
      - taskmagnet-net
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "sqlplus", "-S", "sys/OraclePassword123@//localhost:1521/XE as sysdba", "<<<", "SELECT 1 FROM DUAL;"]
      interval: 30s
      timeout: 10s
      retries: 5

  # Backend Service
  taskmagnet-backend:
    build:
      context: ./src/backend
      dockerfile: Dockerfile
    container_name: taskmagnet-backend
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SPRING_DATASOURCE_URL=jdbc:oracle:thin:@oracle-db:1521:XE
      - SPRING_DATASOURCE_USERNAME=taskmagnet
      - SPRING_DATASOURCE_PASSWORD=taskmagnet123
      - SPRING_JPA_HIBERNATE_DDL_AUTO=update
      - JAVA_OPTS=-Xmx1g -Xms512m
    ports:
      - "8080:8080"
    depends_on:
      oracle-db:
        condition: service_healthy
    networks:
      - taskmagnet-net
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
    volumes:
      - ./logs:/app/logs

  # Frontend Service
  taskmagnet-frontend:
    build:
      context: ./src/frontend
      dockerfile: Dockerfile
    container_name: taskmagnet-frontend
    ports:
      - "80:80"
      - "443:443"
    depends_on:
      - taskmagnet-backend
    networks:
      - taskmagnet-net
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost/"]
      interval: 30s
      timeout: 10s
      retries: 3

  # Redis Cache (Optional)
  redis:
    image: redis:7-alpine
    container_name: taskmagnet-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - taskmagnet-net
    restart: unless-stopped
    command: redis-server --appendonly yes
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  oracle_data:
    driver: local
  redis_data:
    driver: local

networks:
  taskmagnet-net:
    driver: bridge
    ipam:
      config:
        - subnet: 172.20.0.0/16
```

### **2.4 Docker Deployment Commands**

```powershell
# Build and start all services
docker-compose up -d --build

# View logs
docker-compose logs -f taskmagnet-backend
docker-compose logs -f taskmagnet-frontend

# Scale backend service
docker-compose up -d --scale taskmagnet-backend=3

# Stop services
docker-compose down

# Remove everything including volumes
docker-compose down -v --remove-orphans

# Build individual services
docker build -t taskmagnet-backend:latest ./src/backend
docker build -t taskmagnet-frontend:latest ./src/frontend
```

---

## ☸️ **3. Kubernetes (K8s) Deployment**

### **3.1 Architecture Overview**
```
┌─────────────────────────────────────────────────────────────────┐
│                     Kubernetes Cluster                          │
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │   Namespace:    │  │   Namespace:    │  │   Namespace:    │ │
│  │  taskmagnet-dev │  │ taskmagnet-stg  │  │ taskmagnet-prod │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │                    Ingress Controller                        ││
│  │              (NGINX / Azure App Gateway)                     ││
│  └─────────────────────────────────────────────────────────────┘│
│                              │                                  │
│         ┌────────────────────┼────────────────────┐             │
│         │                    │                    │             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │   Frontend      │  │    Backend      │  │    Database     │ │
│  │   Service       │  │    Service      │  │    Service      │ │
│  │                 │  │                 │  │                 │ │
│  │ ┌─────────────┐ │  │ ┌─────────────┐ │  │ ┌─────────────┐ │ │
│  │ │   Pod 1     │ │  │ │   Pod 1     │ │  │ │StatefulSet  │ │ │
│  │ │   Pod 2     │ │  │ │   Pod 2     │ │  │ │   Pod       │ │ │
│  │ │   Pod 3     │ │  │ │   Pod 3     │ │  │ └─────────────┘ │ │
│  │ └─────────────┘ │  │ └─────────────┘ │  │                 │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │                    Persistent Volumes                        ││
│  │            (Database + Logs + Configuration)                 ││
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

### **3.2 Kubernetes Manifests**

**Namespace:**
```yaml
# k8s/namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: taskmagnet-prod
  labels:
    name: taskmagnet-prod
    environment: production
---
apiVersion: v1
kind: Namespace
metadata:
  name: taskmagnet-stg
  labels:
    name: taskmagnet-stg
    environment: staging
---
apiVersion: v1
kind: Namespace
metadata:
  name: taskmagnet-dev
  labels:
    name: taskmagnet-dev
    environment: development
```

**ConfigMap:**
```yaml
# k8s/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: taskmagnet-config
  namespace: taskmagnet-prod
data:
  application.properties: |
    spring.profiles.active=kubernetes
    spring.jpa.hibernate.ddl-auto=validate
    spring.jpa.show-sql=false
    spring.jpa.properties.hibernate.format_sql=false
    
    # Database Configuration
    spring.datasource.url=jdbc:oracle:thin:@oracle-service:1521:XE
    spring.datasource.username=taskmagnet
    spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
    
    # Connection Pool
    spring.datasource.hikari.maximum-pool-size=20
    spring.datasource.hikari.minimum-idle=5
    spring.datasource.hikari.connection-timeout=30000
    spring.datasource.hikari.idle-timeout=600000
    spring.datasource.hikari.max-lifetime=1800000
    
    # Redis Configuration
    spring.redis.host=redis-service
    spring.redis.port=6379
    spring.redis.timeout=2000ms
    
    # Logging
    logging.level.com.mahedee.taskmagnet=INFO
    logging.level.org.springframework.security=WARN
    logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

**Secrets:**
```yaml
# k8s/secrets.yaml
apiVersion: v1
kind: Secret
metadata:
  name: taskmagnet-secrets
  namespace: taskmagnet-prod
type: Opaque
data:
  # Base64 encoded values
  database-password: dGFza21hZ25ldDEyMw==  # taskmagnet123
  jwt-secret: bXlTZWNyZXRKV1RLZXlGb3JUYXNrTWFnbmV0QXBwbGljYXRpb24=
  oracle-password: T3JhY2xlUGFzc3dvcmQxMjM=  # OraclePassword123
```

**Database Deployment:**
```yaml
# k8s/oracle-deployment.yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: oracle-db
  namespace: taskmagnet-prod
spec:
  serviceName: oracle-service
  replicas: 1
  selector:
    matchLabels:
      app: oracle-db
  template:
    metadata:
      labels:
        app: oracle-db
    spec:
      containers:
      - name: oracle
        image: container-registry.oracle.com/database/express:21.3.0-xe
        ports:
        - containerPort: 1521
        - containerPort: 5500
        env:
        - name: ORACLE_PWD
          valueFrom:
            secretKeyRef:
              name: taskmagnet-secrets
              key: oracle-password
        - name: ORACLE_CHARACTERSET
          value: "AL32UTF8"
        volumeMounts:
        - name: oracle-storage
          mountPath: /opt/oracle/oradata
        - name: init-scripts
          mountPath: /docker-entrypoint-initdb.d
        resources:
          requests:
            memory: "2Gi"
            cpu: "1000m"
          limits:
            memory: "4Gi"
            cpu: "2000m"
        livenessProbe:
          exec:
            command:
            - /bin/bash
            - -c
            - "echo 'SELECT 1 FROM DUAL;' | sqlplus -S sys/$(ORACLE_PWD)@//localhost:1521/XE as sysdba"
          initialDelaySeconds: 120
          periodSeconds: 30
        readinessProbe:
          exec:
            command:
            - /bin/bash
            - -c
            - "echo 'SELECT 1 FROM DUAL;' | sqlplus -S sys/$(ORACLE_PWD)@//localhost:1521/XE as sysdba"
          initialDelaySeconds: 60
          periodSeconds: 15
      volumes:
      - name: init-scripts
        configMap:
          name: oracle-init-scripts
  volumeClaimTemplates:
  - metadata:
      name: oracle-storage
    spec:
      accessModes: ["ReadWriteOnce"]
      resources:
        requests:
          storage: 20Gi
      storageClassName: fast-ssd
---
apiVersion: v1
kind: Service
metadata:
  name: oracle-service
  namespace: taskmagnet-prod
spec:
  selector:
    app: oracle-db
  ports:
  - name: oracle
    port: 1521
    targetPort: 1521
  - name: apex
    port: 5500
    targetPort: 5500
  type: ClusterIP
```

**Backend Deployment:**
```yaml
# k8s/backend-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: taskmagnet-backend
  namespace: taskmagnet-prod
  labels:
    app: taskmagnet-backend
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 1
  selector:
    matchLabels:
      app: taskmagnet-backend
  template:
    metadata:
      labels:
        app: taskmagnet-backend
    spec:
      containers:
      - name: backend
        image: taskmagnet-backend:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: taskmagnet-secrets
              key: database-password
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: taskmagnet-secrets
              key: jwt-secret
        - name: JAVA_OPTS
          value: "-Xmx1g -Xms512m -XX:+UseG1GC"
        volumeMounts:
        - name: config-volume
          mountPath: /app/config
        - name: logs-volume
          mountPath: /app/logs
        resources:
          requests:
            memory: "1Gi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 15
      volumes:
      - name: config-volume
        configMap:
          name: taskmagnet-config
      - name: logs-volume
        persistentVolumeClaim:
          claimName: logs-pvc
---
apiVersion: v1
kind: Service
metadata:
  name: taskmagnet-backend-service
  namespace: taskmagnet-prod
spec:
  selector:
    app: taskmagnet-backend
  ports:
  - port: 8080
    targetPort: 8080
  type: ClusterIP
```

**Frontend Deployment:**
```yaml
# k8s/frontend-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: taskmagnet-frontend
  namespace: taskmagnet-prod
  labels:
    app: taskmagnet-frontend
spec:
  replicas: 2
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: taskmagnet-frontend
  template:
    metadata:
      labels:
        app: taskmagnet-frontend
    spec:
      containers:
      - name: frontend
        image: taskmagnet-frontend:latest
        ports:
        - containerPort: 80
        resources:
          requests:
            memory: "128Mi"
            cpu: "100m"
          limits:
            memory: "256Mi"
            cpu: "200m"
        livenessProbe:
          httpGet:
            path: /
            port: 80
          initialDelaySeconds: 30
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /
            port: 80
          initialDelaySeconds: 5
          periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: taskmagnet-frontend-service
  namespace: taskmagnet-prod
spec:
  selector:
    app: taskmagnet-frontend
  ports:
  - port: 80
    targetPort: 80
  type: ClusterIP
```

**Ingress Configuration:**
```yaml
# k8s/ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: taskmagnet-ingress
  namespace: taskmagnet-prod
  annotations:
    kubernetes.io/ingress.class: nginx
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/use-regex: "true"
    cert-manager.io/cluster-issuer: letsencrypt-prod
spec:
  tls:
  - hosts:
    - taskmagnet.company.com
    secretName: taskmagnet-tls
  rules:
  - host: taskmagnet.company.com
    http:
      paths:
      - path: /api/(.*)
        pathType: Prefix
        backend:
          service:
            name: taskmagnet-backend-service
            port:
              number: 8080
      - path: /(.*)
        pathType: Prefix
        backend:
          service:
            name: taskmagnet-frontend-service
            port:
              number: 80
```

### **3.3 Kubernetes Deployment Commands**

```bash
# Apply all manifests
kubectl apply -f k8s/

# Check deployment status
kubectl get pods -n taskmagnet-prod
kubectl get services -n taskmagnet-prod
kubectl get ingress -n taskmagnet-prod

# View logs
kubectl logs -f deployment/taskmagnet-backend -n taskmagnet-prod
kubectl logs -f deployment/taskmagnet-frontend -n taskmagnet-prod

# Scale deployments
kubectl scale deployment taskmagnet-backend --replicas=5 -n taskmagnet-prod

# Update deployment
kubectl set image deployment/taskmagnet-backend backend=taskmagnet-backend:v2.0 -n taskmagnet-prod

# Port forwarding for testing
kubectl port-forward service/taskmagnet-frontend-service 8080:80 -n taskmagnet-prod
```

---

## ☁️ **4. Azure Cloud Deployment**

### **4.1 Architecture Overview**
```
┌─────────────────────────────────────────────────────────────────┐
│                        Azure Cloud                             │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │                  Azure Front Door                           ││
│  │              (Global Load Balancer)                         ││
│  └─────────────────────────────────────────────────────────────┘│
│                              │                                  │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │                Application Gateway                           ││
│  │                (Regional Load Balancer)                      ││
│  └─────────────────────────────────────────────────────────────┘│
│                              │                                  │
│         ┌────────────────────┼────────────────────┐             │
│         │                    │                    │             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │   Azure App     │  │      AKS        │  │   Azure SQL     │ │
│  │   Service       │  │   (Kubernetes)  │  │   Database      │ │
│  │   (Frontend)    │  │   (Backend)     │  │   (Oracle)      │ │
│  │                 │  │                 │  │                 │ │
│  │ ┌─────────────┐ │  │ ┌─────────────┐ │  │ ┌─────────────┐ │ │
│  │ │  React SPA  │ │  │ │Spring Boot  │ │  │ │   Oracle    │ │ │
│  │ │   Static    │ │  │ │  Containers │ │  │ │  Database   │ │ │
│  │ │  Website    │ │  │ │             │ │  │ │             │ │ │
│  │ └─────────────┘ │  │ └─────────────┘ │  │ └─────────────┘ │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │                   Supporting Services                        ││
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────┐││
│  │  │Azure Cache  │ │Azure Storage│ │ Key Vault   │ │Monitor  │││
│  │  │ for Redis   │ │   Account   │ │             │ │ & Logs  │││
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────┘││
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

### **4.2 Azure Resource Deployment**

**Azure CLI Deployment Script:**
```bash
#!/bin/bash
# azure-deploy.sh

# Variables
RESOURCE_GROUP="rg-taskmagnet-prod"
LOCATION="East US"
ACR_NAME="acrtaskmagnetprod"
AKS_NAME="aks-taskmagnet-prod"
APP_SERVICE_PLAN="asp-taskmagnet-prod"
WEB_APP_NAME="app-taskmagnet-frontend"
ORACLE_SERVER="oracle-taskmagnet-prod"
KEY_VAULT_NAME="kv-taskmagnet-prod"

# Create Resource Group
az group create --name $RESOURCE_GROUP --location "$LOCATION"

# Create Azure Container Registry
az acr create \
    --resource-group $RESOURCE_GROUP \
    --name $ACR_NAME \
    --sku Premium \
    --location "$LOCATION"

# Create AKS Cluster
az aks create \
    --resource-group $RESOURCE_GROUP \
    --name $AKS_NAME \
    --node-count 3 \
    --node-vm-size Standard_D4s_v3 \
    --kubernetes-version 1.27.3 \
    --attach-acr $ACR_NAME \
    --enable-addons monitoring,azure-keyvault-secrets-provider \
    --enable-managed-identity \
    --zones 1 2 3 \
    --location "$LOCATION"

# Create App Service Plan for Frontend
az appservice plan create \
    --name $APP_SERVICE_PLAN \
    --resource-group $RESOURCE_GROUP \
    --sku P1v3 \
    --is-linux \
    --location "$LOCATION"

# Create Web App for Frontend
az webapp create \
    --resource-group $RESOURCE_GROUP \
    --plan $APP_SERVICE_PLAN \
    --name $WEB_APP_NAME \
    --deployment-container-image-name nginx:latest

# Create Azure Database for Oracle (Alternative: VM with Oracle)
# Note: Azure doesn't have managed Oracle, using VM approach
az vm create \
    --resource-group $RESOURCE_GROUP \
    --name oracle-vm \
    --image Oracle:oracle-database-19-3:oracle-database-19-0904:latest \
    --size Standard_D4s_v3 \
    --admin-username oracleadmin \
    --generate-ssh-keys \
    --location "$LOCATION"

# Create Azure Cache for Redis
az redis create \
    --name redis-taskmagnet-prod \
    --resource-group $RESOURCE_GROUP \
    --location "$LOCATION" \
    --sku Premium \
    --vm-size P1

# Create Key Vault
az keyvault create \
    --name $KEY_VAULT_NAME \
    --resource-group $RESOURCE_GROUP \
    --location "$LOCATION" \
    --sku standard

# Create Storage Account
az storage account create \
    --name sttaskmagnetprod \
    --resource-group $RESOURCE_GROUP \
    --location "$LOCATION" \
    --sku Standard_LRS \
    --kind StorageV2

# Store secrets in Key Vault
az keyvault secret set --vault-name $KEY_VAULT_NAME --name "database-password" --value "YourSecurePassword123"
az keyvault secret set --vault-name $KEY_VAULT_NAME --name "jwt-secret" --value "YourJWTSecretKey"
az keyvault secret set --vault-name $KEY_VAULT_NAME --name "oracle-password" --value "OraclePassword123"

echo "Azure resources created successfully!"
```

**Azure DevOps Pipeline:**
```yaml
# azure-pipelines.yml
trigger:
  branches:
    include:
    - main
    - dev
  paths:
    include:
    - src/*

variables:
  # Azure Service Connection
  azureSubscription: 'Azure-Service-Connection'
  
  # Azure Container Registry
  containerRegistry: 'acrtaskmagnetprod.azurecr.io'
  
  # Image names
  backendImageName: 'taskmagnet-backend'
  frontendImageName: 'taskmagnet-frontend'
  
  # AKS
  aksResourceGroup: 'rg-taskmagnet-prod'
  aksClusterName: 'aks-taskmagnet-prod'
  
  # Build Agent
  vmImageName: 'ubuntu-latest'

stages:
- stage: Build
  displayName: 'Build Stage'
  jobs:
  - job: BuildBackend
    displayName: 'Build Backend'
    pool:
      vmImage: $(vmImageName)
    steps:
    - task: Maven@3
      displayName: 'Maven Build'
      inputs:
        mavenPomFile: 'src/backend/pom.xml'
        goals: 'clean package'
        options: '-DskipTests'
        
    - task: Docker@2
      displayName: 'Build Backend Docker Image'
      inputs:
        command: 'buildAndPush'
        containerRegistry: '$(containerRegistry)'
        repository: '$(backendImageName)'
        dockerfile: 'src/backend/Dockerfile'
        tags: |
          $(Build.BuildId)
          latest

  - job: BuildFrontend
    displayName: 'Build Frontend'
    pool:
      vmImage: $(vmImageName)
    steps:
    - task: NodeTool@0
      displayName: 'Use Node.js 18'
      inputs:
        versionSpec: '18.x'
        
    - script: |
        cd src/frontend
        npm ci
        npm run build
      displayName: 'Build React App'
      
    - task: Docker@2
      displayName: 'Build Frontend Docker Image'
      inputs:
        command: 'buildAndPush'
        containerRegistry: '$(containerRegistry)'
        repository: '$(frontendImageName)'
        dockerfile: 'src/frontend/Dockerfile'
        tags: |
          $(Build.BuildId)
          latest

- stage: DeployToAKS
  displayName: 'Deploy to AKS'
  dependsOn: Build
  condition: succeeded()
  jobs:
  - deployment: DeployToAKS
    displayName: 'Deploy to AKS'
    pool:
      vmImage: $(vmImageName)
    environment: 'production'
    strategy:
      runOnce:
        deploy:
          steps:
          - task: AzureCLI@2
            displayName: 'Get AKS Credentials'
            inputs:
              azureSubscription: '$(azureSubscription)'
              scriptType: 'bash'
              scriptLocation: 'inlineScript'
              inlineScript: |
                az aks get-credentials --resource-group $(aksResourceGroup) --name $(aksClusterName)
                
          - task: KubernetesManifest@0
            displayName: 'Deploy to AKS'
            inputs:
              action: 'deploy'
              kubernetesServiceConnection: 'aks-connection'
              namespace: 'taskmagnet-prod'
              manifests: |
                k8s/namespace.yaml
                k8s/configmap.yaml
                k8s/secrets.yaml
                k8s/oracle-deployment.yaml
                k8s/backend-deployment.yaml
                k8s/frontend-deployment.yaml
                k8s/ingress.yaml
              containers: |
                $(containerRegistry)/$(backendImageName):$(Build.BuildId)
                $(containerRegistry)/$(frontendImageName):$(Build.BuildId)

- stage: DeployFrontendToAppService
  displayName: 'Deploy Frontend to App Service'
  dependsOn: Build
  condition: succeeded()
  jobs:
  - deployment: DeployFrontend
    displayName: 'Deploy Frontend to App Service'
    pool:
      vmImage: $(vmImageName)
    environment: 'production'
    strategy:
      runOnce:
        deploy:
          steps:
          - task: AzureWebAppContainer@1
            displayName: 'Deploy Frontend Container'
            inputs:
              azureSubscription: '$(azureSubscription)'
              appName: 'app-taskmagnet-frontend'
              containers: '$(containerRegistry)/$(frontendImageName):$(Build.BuildId)'
```

### **4.3 Azure App Service Configuration**

**Frontend App Service Settings:**
```json
{
  "appSettings": [
    {
      "name": "WEBSITES_ENABLE_APP_SERVICE_STORAGE",
      "value": "false"
    },
    {
      "name": "DOCKER_REGISTRY_SERVER_URL",
      "value": "https://acrtaskmagnetprod.azurecr.io"
    },
    {
      "name": "DOCKER_REGISTRY_SERVER_USERNAME",
      "value": "@Microsoft.KeyVault(SecretUri=https://kv-taskmagnet-prod.vault.azure.net/secrets/acr-username/)"
    },
    {
      "name": "DOCKER_REGISTRY_SERVER_PASSWORD",
      "value": "@Microsoft.KeyVault(SecretUri=https://kv-taskmagnet-prod.vault.azure.net/secrets/acr-password/)"
    },
    {
      "name": "REACT_APP_API_URL",
      "value": "https://api.taskmagnet.company.com"
    }
  ]
}
```

**Application Gateway Configuration:**
```json
{
  "properties": {
    "sku": {
      "name": "WAF_v2",
      "tier": "WAF_v2",
      "capacity": 2
    },
    "gatewayIPConfigurations": [
      {
        "name": "appGatewayIpConfig",
        "properties": {
          "subnet": {
            "id": "/subscriptions/{subscription-id}/resourceGroups/rg-taskmagnet-prod/providers/Microsoft.Network/virtualNetworks/vnet-taskmagnet/subnets/AppGatewaySubnet"
          }
        }
      }
    ],
    "frontendIPConfigurations": [
      {
        "name": "appGatewayFrontendIP",
        "properties": {
          "publicIPAddress": {
            "id": "/subscriptions/{subscription-id}/resourceGroups/rg-taskmagnet-prod/providers/Microsoft.Network/publicIPAddresses/pip-appgateway"
          }
        }
      }
    ],
    "frontendPorts": [
      {
        "name": "appGatewayFrontendPort80",
        "properties": {
          "port": 80
        }
      },
      {
        "name": "appGatewayFrontendPort443",
        "properties": {
          "port": 443
        }
      }
    ],
    "backendAddressPools": [
      {
        "name": "aks-backend-pool",
        "properties": {
          "backendAddresses": [
            {
              "fqdn": "aks-taskmagnet-prod-dns.eastus.cloudapp.azure.com"
            }
          ]
        }
      },
      {
        "name": "appservice-frontend-pool",
        "properties": {
          "backendAddresses": [
            {
              "fqdn": "app-taskmagnet-frontend.azurewebsites.net"
            }
          ]
        }
      }
    ],
    "httpListeners": [
      {
        "name": "appGatewayHttpListener",
        "properties": {
          "frontendIPConfiguration": {
            "id": "/subscriptions/{subscription-id}/resourceGroups/rg-taskmagnet-prod/providers/Microsoft.Network/applicationGateways/appgw-taskmagnet/frontendIPConfigurations/appGatewayFrontendIP"
          },
          "frontendPort": {
            "id": "/subscriptions/{subscription-id}/resourceGroups/rg-taskmagnet-prod/providers/Microsoft.Network/applicationGateways/appgw-taskmagnet/frontendPorts/appGatewayFrontendPort443"
          },
          "protocol": "Https",
          "sslCertificate": {
            "id": "/subscriptions/{subscription-id}/resourceGroups/rg-taskmagnet-prod/providers/Microsoft.Network/applicationGateways/appgw-taskmagnet/sslCertificates/taskmagnet-ssl-cert"
          }
        }
      }
    ],
    "requestRoutingRules": [
      {
        "name": "api-routing-rule",
        "properties": {
          "ruleType": "PathBasedRouting",
          "httpListener": {
            "id": "/subscriptions/{subscription-id}/resourceGroups/rg-taskmagnet-prod/providers/Microsoft.Network/applicationGateways/appgw-taskmagnet/httpListeners/appGatewayHttpListener"
          },
          "urlPathMap": {
            "id": "/subscriptions/{subscription-id}/resourceGroups/rg-taskmagnet-prod/providers/Microsoft.Network/applicationGateways/appgw-taskmagnet/urlPathMaps/taskmagnet-path-map"
          }
        }
      }
    ],
    "urlPathMaps": [
      {
        "name": "taskmagnet-path-map",
        "properties": {
          "defaultBackendAddressPool": {
            "id": "/subscriptions/{subscription-id}/resourceGroups/rg-taskmagnet-prod/providers/Microsoft.Network/applicationGateways/appgw-taskmagnet/backendAddressPools/appservice-frontend-pool"
          },
          "defaultBackendHttpSettings": {
            "id": "/subscriptions/{subscription-id}/resourceGroups/rg-taskmagnet-prod/providers/Microsoft.Network/applicationGateways/appgw-taskmagnet/backendHttpSettingsCollection/appGatewayBackendHttpSettings"
          },
          "pathRules": [
            {
              "name": "api-path-rule",
              "properties": {
                "paths": [
                  "/api/*"
                ],
                "backendAddressPool": {
                  "id": "/subscriptions/{subscription-id}/resourceGroups/rg-taskmagnet-prod/providers/Microsoft.Network/applicationGateways/appgw-taskmagnet/backendAddressPools/aks-backend-pool"
                },
                "backendHttpSettings": {
                  "id": "/subscriptions/{subscription-id}/resourceGroups/rg-taskmagnet-prod/providers/Microsoft.Network/applicationGateways/appgw-taskmagnet/backendHttpSettingsCollection/appGatewayBackendHttpSettings"
                }
              }
            }
          ]
        }
      }
    ]
  }
}
```

---

## 📊 **5. Deployment Comparison Matrix**

| **Criteria** | **WebSphere** | **Docker** | **Kubernetes** | **Azure** |
|--------------|---------------|------------|----------------|-----------|
| **Complexity** | High | Low | Medium | Medium |
| **Setup Time** | 2-3 days | 2-4 hours | 1-2 days | 1 day |
| **Scalability** | Manual | Limited | Excellent | Excellent |
| **Cost** | High | Low | Medium | Variable |
| **Maintenance** | High | Low | Medium | Low |
| **Enterprise Features** | Excellent | Basic | Good | Excellent |
| **Security** | Excellent | Good | Good | Excellent |
| **Monitoring** | Built-in | Manual | Third-party | Built-in |
| **High Availability** | Built-in | Manual | Built-in | Built-in |
| **Backup/Recovery** | Manual | Manual | Manual | Automated |
| **Team Skills Required** | WebSphere Admin | Docker basics | K8s expertise | Azure knowledge |

---

## 🎯 **6. Recommended Deployment Strategy**

### **6.1 Phased Approach**

**Phase 1: Development (Docker)**
- Local development with Docker Compose
- Fast iteration and testing
- Cost-effective for development teams

**Phase 2: Staging (Kubernetes)**
- On-premises or cloud K8s cluster
- Production-like environment testing
- CI/CD pipeline integration

**Phase 3: Production (Azure + WebSphere)**
- **Option A**: Azure cloud-native deployment
- **Option B**: On-premises WebSphere for compliance
- **Option C**: Hybrid approach

### **6.2 Decision Matrix**

**Choose WebSphere if:**
- Enterprise already uses IBM stack
- Strict compliance requirements
- Existing WebSphere expertise
- Traditional enterprise environment

**Choose Docker if:**
- Development/testing environments
- Quick deployment needs
- Limited infrastructure
- Learning container technologies

**Choose Kubernetes if:**
- Cloud-native strategy
- Microservices architecture
- Auto-scaling requirements
- DevOps maturity

**Choose Azure if:**
- Microsoft ecosystem preference
- Managed services preferred
- Global scaling needs
- Rapid cloud adoption

---

## 📚 **7. Additional Resources**

### **7.1 Documentation Links**
- [IBM WebSphere Liberty Documentation](https://www.ibm.com/docs/en/was-liberty)
- [Docker Official Documentation](https://docs.docker.com/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Azure App Service Documentation](https://docs.microsoft.com/en-us/azure/app-service/)

### **7.2 Training Resources**
- WebSphere Administration Training
- Docker and Kubernetes Certification
- Azure Solutions Architect Certification
- Spring Boot in Enterprise Environments

### **7.3 Support Contacts**
- **WebSphere**: IBM Support Portal
- **Azure**: Microsoft Azure Support
- **Kubernetes**: Community Support + CNCF
- **Application**: TaskMagnet DevOps Team

---

**Document End**

*This deployment plan should be reviewed and updated quarterly to reflect changes in technology, requirements, and best practices.*
