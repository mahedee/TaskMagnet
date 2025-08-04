#!/usr/bin/env pwsh

# TaskMagnet Frontend Runner Script (PowerShell)
# This script starts the React development server

Write-Host "🧲 TaskMagnet Frontend Runner" -ForegroundColor Cyan
Write-Host "=============================" -ForegroundColor Cyan

# Get script directory and frontend path
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$FrontendDir = Join-Path $ScriptDir "src\frontend\taskmagnet-frontend"

if (!(Test-Path $FrontendDir)) {
    Write-Host "❌ Error: Frontend directory not found at $FrontendDir" -ForegroundColor Red
    exit 1
}

Write-Host "📂 Changing to frontend directory: $FrontendDir" -ForegroundColor Yellow
Set-Location $FrontendDir

# Check if node_modules exists
if (!(Test-Path "node_modules")) {
    Write-Host "📦 Installing npm dependencies..." -ForegroundColor Yellow
    npm install
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Error: Failed to install dependencies" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "🚀 Starting TaskMagnet Frontend Development Server" -ForegroundColor Green
Write-Host "   - Frontend URL: http://localhost:3000" -ForegroundColor White
Write-Host "   - Backend API should be running on: http://localhost:8081" -ForegroundColor White
Write-Host ""
Write-Host "💡 Press Ctrl+C to stop the server" -ForegroundColor Yellow
Write-Host ""

# Start the development server
npm start
