@echo off
REM TaskMagnet Application Runner - Batch Script
REM Author: TaskMagnet Development Team
REM Version: 1.0.0
REM Description: Simple batch script to run TaskMagnet application

setlocal

echo.
echo ^🚀 TaskMagnet Application Runner (Batch)
echo =========================================
echo.

REM Check if PowerShell is available and use the full script
where powershell >nul 2>&1
if %ERRORLEVEL% == 0 (
    echo 📋 Using PowerShell script for enhanced functionality...
    echo.
    powershell -ExecutionPolicy Bypass -File "%~dp0run-app.ps1" %*
    goto :end
)

REM Fallback for systems without PowerShell
echo ⚠️  PowerShell not found, using basic batch functionality
echo.

REM Set default values
set PORT=8081
set PROFILE=dev

REM Parse basic arguments
:parse_args
if "%1"=="" goto :run_app
if "%1"=="-port" (
    set PORT=%2
    shift
    shift
    goto :parse_args
)
if "%1"=="-profile" (
    set PROFILE=%2
    shift
    shift
    goto :parse_args
)
if "%1"=="-help" (
    goto :show_help
)
if "%1"=="--help" (
    goto :show_help
)
shift
goto :parse_args

:show_help
echo Usage: run-app.bat [OPTIONS]
echo.
echo Options:
echo   -port ^<number^>     Port number (default: 8081)
echo   -profile ^<name^>    Spring profile: dev, prod, test (default: dev)
echo   -help              Show this help message
echo.
echo Examples:
echo   run-app.bat                    # Run with defaults
echo   run-app.bat -port 8080        # Run on port 8080
echo   run-app.bat -profile prod     # Run with production profile
echo.
echo For enhanced functionality, use run-app.ps1 instead
goto :end

:run_app
echo ⚙️  Configuration:
echo    Port: %PORT%
echo    Profile: %PROFILE%
echo.

REM Check if backend directory exists
if not exist "src\backend" (
    echo ❌ Error: Backend directory not found
    echo    Please run this script from the TaskMagnet root directory
    goto :error
)

REM Change to backend directory
cd src\backend

REM Check if Maven is available
where mvn >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ❌ Error: Maven is not installed or not in PATH
    goto :error
)

echo ✅ Maven detected
echo.

REM Build and run the application
echo 🔨 Building and starting TaskMagnet application...
echo.
mvn spring-boot:run -pl taskmagnet-web -Dspring-boot.run.arguments="--server.port=%PORT% --spring.profiles.active=%PROFILE%"

goto :end

:error
echo.
echo 💡 For better error handling and features, use run-app.ps1
exit /b 1

:end
endlocal
