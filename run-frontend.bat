@echo off

REM TaskMagnet Frontend Runner Script (Windows Batch)
REM This script starts the React development server

echo 🧲 TaskMagnet Frontend Runner
echo =============================

REM Get script directory and frontend path
set SCRIPT_DIR=%~dp0
set FRONTEND_DIR=%SCRIPT_DIR%src\frontend\taskmagnet-frontend

if not exist "%FRONTEND_DIR%" (
    echo ❌ Error: Frontend directory not found at %FRONTEND_DIR%
    pause
    exit /b 1
)

echo 📂 Changing to frontend directory: %FRONTEND_DIR%
cd /d "%FRONTEND_DIR%"

REM Check if node_modules exists
if not exist "node_modules" (
    echo 📦 Installing npm dependencies...
    npm install
    if errorlevel 1 (
        echo ❌ Error: Failed to install dependencies
        pause
        exit /b 1
    )
)

echo.
echo 🚀 Starting TaskMagnet Frontend Development Server
echo    - Frontend URL: http://localhost:3000
echo    - Backend API should be running on: http://localhost:8081
echo.
echo 💡 Press Ctrl+C to stop the server
echo.

REM Start the development server
npm start

pause
