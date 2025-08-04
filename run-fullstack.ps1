#!/usr/bin/env pwsh

# TaskMagnet Full Stack Runner Script (PowerShell)
# This script starts both backend and frontend servers

Write-Host "🧲 TaskMagnet Full Stack Runner" -ForegroundColor Cyan
Write-Host "===============================" -ForegroundColor Cyan

# Get script directory
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$BackendDir = Join-Path $ScriptDir "src\backend"
$FrontendDir = Join-Path $ScriptDir "src\frontend\taskmagnet-frontend"

# Check if directories exist
if (!(Test-Path $BackendDir)) {
    Write-Host "❌ Error: Backend directory not found at $BackendDir" -ForegroundColor Red
    exit 1
}

if (!(Test-Path $FrontendDir)) {
    Write-Host "❌ Error: Frontend directory not found at $FrontendDir" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🔧 Starting TaskMagnet Full Stack Application" -ForegroundColor Green
Write-Host "   - Backend will start on: http://localhost:8081" -ForegroundColor White
Write-Host "   - Frontend will start on: http://localhost:3000" -ForegroundColor White
Write-Host ""
Write-Host "⚡ Starting backend server..." -ForegroundColor Yellow

# Start backend in a new PowerShell window
$BackendScript = Join-Path $ScriptDir "run-app.ps1"
Start-Process powershell -ArgumentList "-NoExit", "-File", "`"$BackendScript`""

# Wait a moment for backend to start
Start-Sleep -Seconds 3

Write-Host "⚡ Starting frontend server..." -ForegroundColor Yellow

# Start frontend in a new PowerShell window
$FrontendScript = Join-Path $ScriptDir "run-frontend.ps1"
Start-Process powershell -ArgumentList "-NoExit", "-File", "`"$FrontendScript`""

Write-Host ""
Write-Host "✅ Both servers are starting up!" -ForegroundColor Green
Write-Host ""
Write-Host "🌐 Application URLs:" -ForegroundColor Cyan
Write-Host "   Frontend: http://localhost:3000" -ForegroundColor White
Write-Host "   Backend API: http://localhost:8081" -ForegroundColor White
Write-Host "   Backend Health: http://localhost:8081/actuator/health" -ForegroundColor White
Write-Host ""
Write-Host "💡 Both servers are running in separate windows." -ForegroundColor Yellow
Write-Host "   Close those windows or press Ctrl+C in them to stop the servers." -ForegroundColor Yellow
Write-Host ""

# Keep this script running to show status
Write-Host "📊 Full Stack Status Monitor" -ForegroundColor Cyan
Write-Host "Press any key to exit this monitor (servers will continue running)..." -ForegroundColor Yellow

$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
