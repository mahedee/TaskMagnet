@echo off

REM TaskMagnet Full Stack Runner Script (Windows Batch)
REM This script starts both backend and frontend servers

echo 🧲 TaskMagnet Full Stack Runner
echo ===============================

REM Get script directory
set SCRIPT_DIR=%~dp0
set BACKEND_DIR=%SCRIPT_DIR%src\backend
set FRONTEND_DIR=%SCRIPT_DIR%src\frontend\taskmagnet-frontend

REM Check if directories exist
if not exist "%BACKEND_DIR%" (
    echo ❌ Error: Backend directory not found at %BACKEND_DIR%
    pause
    exit /b 1
)

if not exist "%FRONTEND_DIR%" (
    echo ❌ Error: Frontend directory not found at %FRONTEND_DIR%
    pause
    exit /b 1
)

echo.
echo 🔧 Starting TaskMagnet Full Stack Application
echo    - Backend will start on: http://localhost:8081
echo    - Frontend will start on: http://localhost:3000
echo.
echo ⚡ Starting backend server...

REM Start backend in a new command prompt window
start "TaskMagnet Backend" cmd /c "%SCRIPT_DIR%run-app.bat"

REM Wait a moment for backend to start
timeout /t 3 /nobreak >nul

echo ⚡ Starting frontend server...

REM Start frontend in a new command prompt window
start "TaskMagnet Frontend" cmd /c "%SCRIPT_DIR%run-frontend.bat"

echo.
echo ✅ Both servers are starting up!
echo.
echo 🌐 Application URLs:
echo    Frontend: http://localhost:3000
echo    Backend API: http://localhost:8081
echo    Backend Health: http://localhost:8081/actuator/health
echo.
echo 💡 Both servers are running in separate windows.
echo    Close those windows to stop the servers.
echo.
echo 📊 Full Stack Status Monitor
echo Press any key to exit this monitor (servers will continue running)...

pause >nul
