#!/usr/bin/env pwsh

# TaskMagnet Quick Start Script
# This script provides the fastest way to start TaskMagnet

Write-Host ""
Write-Host "⚡ TaskMagnet Quick Start" -ForegroundColor Green
Write-Host "========================" -ForegroundColor Green
Write-Host ""

# Check if we're in the right directory
if (-not (Test-Path "src/backend")) {
    Write-Host "❌ Please run this from the TaskMagnet root directory" -ForegroundColor Red
    exit 1
}

# Quick start with defaults
Write-Host "🚀 Starting TaskMagnet with default settings..." -ForegroundColor Cyan
Write-Host "   Port: 8081" -ForegroundColor White
Write-Host "   Profile: dev" -ForegroundColor White
Write-Host ""

& .\run-app.ps1
