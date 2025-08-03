# Oracle Database Guide - TaskMagnet

This guide covers Oracle Database setup, configuration, and management for the TaskMagnet project.

## 📋 Overview

TaskMagnet uses Oracle Database XE (Express Edition) as the primary database. This guide will help you:
- Install and configure Oracle XE
- Set up the TaskMagnet database schema
- Manage database connections
- Perform common database operations

---

## 🔧 Oracle XE Installation

### Download and Install
1. **Download Oracle XE 21c**
   - Visit: https://www.oracle.com/database/technologies/xe-downloads.html
   - Download for your operating system

2. **Installation Process**
   ```cmd
   # Run the installer with administrator privileges
   # Accept default settings:
   # - Port: 1521
   # - Service Name: XE
   # - PDB Name: XEPDB1
   # - Set strong passwords for SYS and SYSTEM accounts
   ```

### Post-Installation Setup
```cmd
# Start Oracle services
net start OracleServiceXE
net start OracleXETNSListener

# Verify installation
sqlplus / as sysdba
```

---

## 🏗️ Database Schema Setup

### 1. Create TaskMagnet User
```sql
-- Connect as SYSDBA
sqlplus / as sysdba

-- Switch to PDB
ALTER SESSION SET CONTAINER = XEPDB1;

-- Create user
CREATE USER taskmagnet IDENTIFIED BY "mahedee.net";

-- Grant privileges
GRANT CONNECT TO taskmagnet;
GRANT RESOURCE TO taskmagnet;
GRANT CREATE SESSION TO taskmagnet;
GRANT CREATE TABLE TO taskmagnet;
GRANT CREATE VIEW TO taskmagnet;
GRANT CREATE SEQUENCE TO taskmagnet;
GRANT CREATE PROCEDURE TO taskmagnet;
GRANT UNLIMITED TABLESPACE TO taskmagnet;

-- Verify user creation
SELECT username, account_status FROM dba_users WHERE username = 'TASKMAGNET';
```

### 2. Test Connection
```cmd
# Test new user connection
sqlplus taskmagnet/"mahedee.net"@localhost:1521/XEPDB1
```

---

## 📊 Database Schema

### Tables Created by Hibernate
The following tables are automatically created when the application starts:

#### 1. ROLES Table
```sql
CREATE TABLE roles (
    id NUMBER(10,0) GENERATED AS IDENTITY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    name VARCHAR2(20 CHAR) CHECK (name IN ('ROLE_USER','ROLE_MODERATOR','ROLE_ADMIN')),
    PRIMARY KEY (id)
);
```

#### 2. USERS Table
```sql
CREATE TABLE users (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    email VARCHAR2(250 CHAR),
    password VARCHAR2(250 CHAR),
    username VARCHAR2(50 CHAR),
    PRIMARY KEY (id),
    CONSTRAINT UKr43af9ap4edm43mmtq01oddj6 UNIQUE (username),
    CONSTRAINT UK6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email)
);
```

#### 3. USER_ROLES Table
```sql
CREATE TABLE user_roles (
    user_id NUMBER(19,0) NOT NULL,
    role_id NUMBER(10,0) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT FKh8ciramu9cc9q3qcqiv4ue8a6 
        FOREIGN KEY (role_id) REFERENCES roles,
    CONSTRAINT FKhfh9dx7w3ubf1co1vdev94g3f 
        FOREIGN KEY (user_id) REFERENCES users
);
```

#### 4. TASK_CATEGORY Table
```sql
CREATE TABLE task_category (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    name VARCHAR2(250 CHAR),
    PRIMARY KEY (id)
);
```

#### 5. TASK_STATUS Table
```sql
CREATE TABLE task_status (
    id NUMBER(19,0) GENERATED AS IDENTITY,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    name VARCHAR2(250 CHAR),
    PRIMARY KEY (id)
);
```

---

## 🌱 Seed Data

### Automatic Seed Data
The application automatically inserts seed data on startup through the `DataSeeder` class:

#### Default Roles
- `ROLE_USER`
- `ROLE_MODERATOR`
- `ROLE_ADMIN`

#### Default Admin User
- **Username**: `admin`
- **Email**: `admin@gmail.com`
- **Password**: `Taskmagnet@2025`
- **Role**: `ADMIN`

### Manual Data Insertion (if needed)
```sql
-- Connect to TaskMagnet database
sqlplus taskmagnet/"mahedee.net"@localhost:1521/XEPDB1

-- Insert roles manually (if seed data fails)
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
COMMIT;

-- View inserted data
SELECT * FROM roles;
SELECT * FROM users;
SELECT * FROM user_roles;
```

---

## 🔗 Connection Configuration

### Spring Boot Configuration
File: `src/main/resources/application-dev.properties`
```properties
# Oracle Database Configuration
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.datasource.username=taskmagnet
spring.datasource.password=mahedee.net
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1

# JPA/Hibernate Configuration
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.Oracle12cDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Maven Dependencies
File: `pom.xml`
```xml
<!-- Oracle Database Driver -->
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc8</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

## 🛠️ Database Management

### Common SQL Commands
```sql
-- View all tables
SELECT table_name FROM user_tables;

-- View table structure
DESCRIBE users;

-- Check table data
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM roles;

-- View user roles
SELECT u.username, u.email, r.name as role
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id;
```

### Database Backup
```cmd
# Export schema
expdp taskmagnet/mahedee.net@localhost:1521/XEPDB1 \
  schemas=taskmagnet \
  dumpfile=taskmagnet_backup.dmp \
  directory=DATA_PUMP_DIR

# Import schema
impdp taskmagnet/mahedee.net@localhost:1521/XEPDB1 \
  schemas=taskmagnet \
  dumpfile=taskmagnet_backup.dmp \
  directory=DATA_PUMP_DIR
```

---

## 🚨 Troubleshooting

### Common Issues and Solutions

#### 1. TNS Protocol Adapter Error
```cmd
# Problem: ORA-12560: TNS:protocol adapter error
# Solution: Start Oracle services
net start OracleServiceXE
net start OracleXETNSListener
```

#### 2. User Authentication Failed
```sql
-- Problem: ORA-01017: invalid username/password
-- Solution: Reset user password
sqlplus / as sysdba
ALTER SESSION SET CONTAINER = XEPDB1;
ALTER USER taskmagnet IDENTIFIED BY "mahedee.net";
```

#### 3. Insufficient Privileges
```sql
-- Problem: ORA-00942: table or view does not exist
-- Solution: Grant additional privileges
GRANT ALL PRIVILEGES TO taskmagnet;
```

#### 4. Connection Refused
```cmd
# Check listener status
lsnrctl status

# Start listener if stopped
lsnrctl start
```

---

## 📊 Monitoring and Performance

### View Database Status
```sql
-- Check database status
SELECT name, open_mode FROM v$database;

-- Check PDB status
SELECT name, open_mode FROM v$pdbs;

-- View active sessions
SELECT username, status, count(*) 
FROM v$session 
GROUP BY username, status;
```

### Performance Queries
```sql
-- Check table sizes
SELECT table_name, num_rows, blocks, avg_row_len
FROM user_tables
ORDER BY num_rows DESC;

-- View recent SQL activity
SELECT sql_text, executions, elapsed_time
FROM v$sql
WHERE parsing_user_id = USER_ID
ORDER BY elapsed_time DESC;
```

---

## 🔒 Security Best Practices

### User Management
- Use strong passwords for database accounts
- Limit user privileges to necessary permissions only
- Regularly review user access and roles
- Enable database audit logging

### Connection Security
- Use encrypted connections in production
- Implement connection pooling
- Set appropriate timeout values
- Monitor failed login attempts

---

## 📚 Additional Resources

- [Oracle Database Documentation](https://docs.oracle.com/en/database/)
- [Oracle XE Installation Guide](https://docs.oracle.com/en/database/oracle/oracle-database/21/xeinw/)
- [Oracle SQL Reference](https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/)
- [Spring Boot Oracle Configuration](https://spring.io/guides/gs/accessing-data-jpa/)

---

*Last Updated: August 3, 2025*
*Version: 1.0*
