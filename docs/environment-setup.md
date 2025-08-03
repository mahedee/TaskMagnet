# Environment Setup Guide - TaskMagnet

This guide will help you set up the development environment for TaskMagnet project.

## 📋 Prerequisites

### Required Software
- **Java 17+** - Programming language
- **Maven 3.6+** - Build tool
- **Oracle Database XE 21c** - Database
- **Node.js 18+** - For frontend development
- **VS Code** - Recommended IDE
- **Git** - Version control

### System Requirements
- **OS**: Windows 10/11, macOS, or Linux
- **RAM**: Minimum 8GB (16GB recommended)
- **Storage**: At least 5GB free space
- **Internet**: Required for downloading dependencies

---

## 🔧 Installation Steps

### 1. Java Development Kit (JDK)
```bash
# Check if Java is installed
java -version

# Download and install OpenJDK 17 from:
# https://adoptium.net/temurin/releases/
```

### 2. Apache Maven
```bash
# Check if Maven is installed
mvn -version

# Download from: https://maven.apache.org/download.cgi
# Add to PATH environment variable
```

### 3. Oracle Database XE
```bash
# Download Oracle XE 21c from:
# https://www.oracle.com/database/technologies/xe-downloads.html

# Install and configure with default settings
# Default port: 1521
# Service name: XE
# PDB name: XEPDB1
```

### 4. Node.js (for Frontend)
```bash
# Download from: https://nodejs.org/
# Install LTS version

# Verify installation
node --version
npm --version
```

### 5. VS Code Extensions
Install these recommended extensions:
- **Extension Pack for Java**
- **Spring Boot Extension Pack**
- **Oracle Developer Tools**
- **REST Client**
- **GitLens**

---

## 🌍 Environment Variables

### Windows
```cmd
# Set JAVA_HOME
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.x

# Set MAVEN_HOME
set MAVEN_HOME=C:\Program Files\Apache\maven-3.x.x

# Set ORACLE_HOME
set ORACLE_HOME=C:\app\oracle\product\21c\dbhomeXE

# Add to PATH
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%ORACLE_HOME%\bin;%PATH%
```

### macOS/Linux
```bash
# Add to ~/.bashrc or ~/.zshrc
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export MAVEN_HOME=/opt/maven
export ORACLE_HOME=/opt/oracle/product/21c/dbhomeXE
export PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$ORACLE_HOME/bin:$PATH
```

---

## ✅ Verification

### Test Java Installation
```bash
java -version
# Expected: openjdk version "17.x.x"
```

### Test Maven Installation
```bash
mvn -version
# Expected: Apache Maven 3.x.x
```

### Test Oracle Database
```bash
# Start Oracle services (Windows)
net start OracleServiceXE
net start OracleXETNSListener

# Test connection
sqlplus / as sysdba
```

### Test Node.js Installation
```bash
node --version
npm --version
```

---

## 🛠️ IDE Configuration

### VS Code Settings
Create `.vscode/settings.json`:
```json
{
    "java.home": "C:\\Program Files\\Eclipse Adoptium\\jdk-17.0.x",
    "maven.executable.path": "C:\\Program Files\\Apache\\maven-3.x.x\\bin\\mvn.cmd",
    "java.configuration.updateBuildConfiguration": "automatic",
    "java.compile.nullAnalysis.mode": "automatic"
}
```

### Oracle SQL Developer (Optional)
- Download from Oracle website
- Configure connection to localhost:1521/XEPDB1
- Use for database administration

---

## 🚨 Troubleshooting

### Common Issues

#### Java Issues
```bash
# Issue: java command not found
# Solution: Add JAVA_HOME/bin to PATH

# Issue: Wrong Java version
# Solution: Update JAVA_HOME to point to JDK 17+
```

#### Maven Issues
```bash
# Issue: mvn command not found
# Solution: Add MAVEN_HOME/bin to PATH

# Issue: Maven using wrong Java version
# Solution: Set JAVA_HOME properly
```

#### Oracle Issues
```bash
# Issue: ORA-12560 TNS protocol adapter error
# Solution: Start Oracle services
net start OracleServiceXE
net start OracleXETNSListener

# Issue: Cannot connect to database
# Solution: Check Oracle listener status
lsnrctl status
```

---

## 📚 Additional Resources

- [Java Documentation](https://docs.oracle.com/en/java/)
- [Maven Documentation](https://maven.apache.org/guides/)
- [Oracle Database Documentation](https://docs.oracle.com/en/database/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

---

## 🎯 Next Steps

After completing the environment setup:
1. Clone the TaskMagnet repository
2. Follow the [How to Run the App](how-to-run-the-app.md) guide
3. Read the [Technical Guide](technical-guide.md) for development details

---

*Last Updated: August 3, 2025*
*Version: 1.0*
