# TaskMagnet Application Runner Scripts

This directory contains several scripts to easily run the TaskMagnet Spring Boot application across different platforms and preferences.

## Available Scripts

### 1. PowerShell Script (Recommended for Windows)
**File:** `run-app.ps1`

The most feature-rich script with comprehensive error handling, validation, and configuration options.

#### Features:
- ✅ Comprehensive error checking and validation
- ✅ Configurable port and Spring profiles
- ✅ Clean build and test skipping options
- ✅ Port availability checking
- ✅ Colored output and user-friendly messages
- ✅ Help documentation built-in
- ✅ URL display for easy access

#### Usage:
```powershell
# Basic usage (runs on port 8081 with dev profile)
.\run-app.ps1

# Run on different port
.\run-app.ps1 -Port 8080

# Run with production profile
.\run-app.ps1 -Profile prod

# Clean build and skip tests
.\run-app.ps1 -Clean -SkipTests

# Show help
.\run-app.ps1 -Help
```

#### Parameters:
- `-Port <number>`: Port number (default: 8081)
- `-Profile <name>`: Spring profile - dev, prod, test (default: dev)
- `-Clean`: Perform clean build
- `-SkipTests`: Skip tests during build
- `-Help`: Show help information

### 2. Shell Script (For Linux/macOS/WSL)
**File:** `run-app.sh`

Cross-platform shell script with similar functionality to the PowerShell version.

#### Usage:
```bash
# Make executable (first time only)
chmod +x run-app.sh

# Basic usage
./run-app.sh

# Run on different port
./run-app.sh --port 8080

# Run with production profile
./run-app.sh --profile prod

# Clean build and skip tests
./run-app.sh --clean --skip-tests

# Show help
./run-app.sh --help
```

#### Options:
- `-p, --port <number>`: Port number (default: 8081)
- `-f, --profile <name>`: Spring profile - dev, prod, test (default: dev)
- `-c, --clean`: Perform clean build
- `-s, --skip-tests`: Skip tests during build
- `-h, --help`: Show help information

### 3. Batch Script (Basic Windows Support)
**File:** `run-app.bat`

Simple batch script for Windows systems. Automatically uses PowerShell script if available, otherwise provides basic functionality.

#### Usage:
```cmd
# Basic usage
run-app.bat

# Run on different port
run-app.bat -port 8080

# Run with production profile
run-app.bat -profile prod

# Show help
run-app.bat -help
```

## Prerequisites

Before running any script, ensure you have:

1. **Java 17+** installed and in PATH
2. **Maven 3.6+** installed and in PATH
3. **Oracle XE Database** running and accessible
4. **Database schema** properly set up (run TASK-ARCH-002A if not done)

## Application URLs

Once the application starts, it will be available at:

- **Main Application**: `http://localhost:{port}`
- **Swagger UI**: `http://localhost:{port}/swagger-ui.html`
- **API Documentation**: `http://localhost:{port}/api-docs`
- **Health Check**: `http://localhost:{port}/actuator/health`

Default port is `8081` unless specified otherwise.

## Spring Profiles

The application supports three main profiles:

- **dev** (default): Development configuration with detailed logging
- **prod**: Production configuration with optimized settings
- **test**: Testing configuration with in-memory database options

## Troubleshooting

### Port Already in Use
If you get a port conflict error:
```powershell
# Use a different port
.\run-app.ps1 -Port 8082
```

### Build Failures
If the build fails:
```powershell
# Try a clean build
.\run-app.ps1 -Clean

# Or skip tests if they're failing
.\run-app.ps1 -Clean -SkipTests
```

### Database Connection Issues
Ensure:
1. Oracle XE is running
2. Database credentials in `application.properties` are correct
3. Database schema exists and is accessible

### Maven/Java Not Found
Ensure both Maven and Java are:
1. Properly installed
2. Added to your system PATH
3. Java version is 17 or higher

## Script Architecture

All scripts follow the same pattern:
1. **Validation**: Check prerequisites (Java, Maven, directories)
2. **Configuration**: Set up ports, profiles, and build options
3. **Build**: Compile the application with specified options
4. **Run**: Start the Spring Boot application with Maven
5. **Monitoring**: Display URLs and provide shutdown instructions

## Development Notes

- Scripts are located in the root directory for easy access
- Backend application files are in `src/backend/`
- The main application module is `taskmagnet-web`
- All scripts support the same core functionality with platform-specific implementations
- PowerShell script provides the richest experience with better error handling and user feedback

## Examples

### Quick Start (Development)
```powershell
# Start with defaults - perfect for development
.\run-app.ps1
```

### Production Deployment
```powershell
# Clean build with production profile
.\run-app.ps1 -Clean -Profile prod -Port 80
```

### Testing Setup
```powershell
# Quick build without tests for rapid iteration
.\run-app.ps1 -SkipTests -Profile test
```

### Port Conflict Resolution
```powershell
# Try different ports until one works
.\run-app.ps1 -Port 8082
```

## Support

For issues with the scripts:
1. Check the error messages - they provide specific guidance
2. Ensure all prerequisites are met
3. Try the clean build option
4. Check the application logs in the terminal output

For application issues:
1. Check the Spring Boot startup logs
2. Verify database connectivity
3. Check the Swagger UI for API documentation
4. Review the application configuration files
