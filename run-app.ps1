# TaskMagnet Application Runner - PowerShell Script
# Author: TaskMagnet Development Team
# Version: 1.0.0
# Description: Runs the TaskMagnet Spring Boot application with configurable options

param(
    [Parameter(HelpMessage="Port number to run the application on (default: 8081)")]
    [int]$Port = 8081,
    
    [Parameter(HelpMessage="Spring profile to use (dev, prod, test)")]
    [string]$Profile = "dev",
    
    [Parameter(HelpMessage="Clean build before running")]
    [switch]$Clean,
    
    [Parameter(HelpMessage="Skip tests during build")]
    [switch]$SkipTests,
    
    [Parameter(HelpMessage="Show help information")]
    [switch]$Help
)

# Function to display help
function Show-Help {
    Write-Host ""
    Write-Host "TaskMagnet Application Runner" -ForegroundColor Green
    Write-Host "=============================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Usage: .\run-app.ps1 [OPTIONS]" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Options:" -ForegroundColor Cyan
    Write-Host "  -Port <number>    Port number to run the application on (default: 8081)" -ForegroundColor White
    Write-Host "  -Profile <name>   Spring profile to use: dev, prod, test (default: dev)" -ForegroundColor White
    Write-Host "  -Clean           Clean build before running" -ForegroundColor White
    Write-Host "  -SkipTests       Skip tests during build" -ForegroundColor White
    Write-Host "  -Help            Show this help message" -ForegroundColor White
    Write-Host ""
    Write-Host "Examples:" -ForegroundColor Cyan
    Write-Host "  .\run-app.ps1                           # Run with defaults (port 8081, dev profile)" -ForegroundColor Gray
    Write-Host "  .\run-app.ps1 -Port 8080               # Run on port 8080" -ForegroundColor Gray
    Write-Host "  .\run-app.ps1 -Profile prod            # Run with production profile" -ForegroundColor Gray
    Write-Host "  .\run-app.ps1 -Clean -SkipTests        # Clean build and skip tests" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Application URLs:" -ForegroundColor Cyan
    Write-Host "  Application: http://localhost:$Port" -ForegroundColor White
    Write-Host "  Swagger UI:  http://localhost:$Port/swagger-ui.html" -ForegroundColor White
    Write-Host "  API Docs:    http://localhost:$Port/api-docs" -ForegroundColor White
    Write-Host ""
}

# Display help if requested
if ($Help) {
    Show-Help
    exit 0
}

# Display banner
Write-Host ""
Write-Host "🚀 TaskMagnet Application Runner" -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Green
Write-Host ""

# Validate profile
$validProfiles = @("dev", "prod", "test")
if ($Profile -notin $validProfiles) {
    Write-Host "❌ Error: Invalid profile '$Profile'. Valid profiles are: $($validProfiles -join ', ')" -ForegroundColor Red
    exit 1
}

# Check if Maven is available
try {
    $mavenVersion = mvn --version 2>$null
    if ($LASTEXITCODE -ne 0) {
        throw "Maven not found"
    }
    Write-Host "✅ Maven detected" -ForegroundColor Green
} catch {
    Write-Host "❌ Error: Maven is not installed or not in PATH" -ForegroundColor Red
    Write-Host "   Please install Maven and ensure it's in your PATH" -ForegroundColor Yellow
    exit 1
}

# Check if Java is available
try {
    $javaVersion = java -version 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "Java not found"
    }
    Write-Host "✅ Java detected" -ForegroundColor Green
} catch {
    Write-Host "❌ Error: Java is not installed or not in PATH" -ForegroundColor Red
    Write-Host "   Please install Java 17+ and ensure it's in your PATH" -ForegroundColor Yellow
    exit 1
}

# Set working directory to backend
$backendPath = "src\backend"
if (-not (Test-Path $backendPath)) {
    Write-Host "❌ Error: Backend directory not found at $backendPath" -ForegroundColor Red
    Write-Host "   Please run this script from the TaskMagnet root directory" -ForegroundColor Yellow
    exit 1
}

Write-Host "📁 Changing to backend directory: $backendPath" -ForegroundColor Cyan
Set-Location $backendPath

# Build parameters
$buildParams = @()
if ($Clean) {
    $buildParams += "clean"
    Write-Host "🧹 Clean build enabled" -ForegroundColor Yellow
}
$buildParams += "compile"

if ($SkipTests) {
    $buildParams += "-DskipTests"
    Write-Host "⏭️  Tests will be skipped" -ForegroundColor Yellow
}

# Build the application
Write-Host ""
Write-Host "🔨 Building TaskMagnet application..." -ForegroundColor Cyan
Write-Host "Command: mvn $($buildParams -join ' ')" -ForegroundColor Gray

try {
    & mvn @buildParams
    if ($LASTEXITCODE -ne 0) {
        throw "Build failed"
    }
    Write-Host "✅ Build completed successfully" -ForegroundColor Green
} catch {
    Write-Host "❌ Build failed. Please check the output above for errors." -ForegroundColor Red
    exit 1
}

# Prepare run parameters
$runParams = @(
    "spring-boot:run",
    "-pl", "taskmagnet-web",
    "-Dspring-boot.run.arguments=--server.port=$Port --spring.profiles.active=$Profile"
)

# Display configuration
Write-Host ""
Write-Host "⚙️  Application Configuration:" -ForegroundColor Cyan
Write-Host "   Port: $Port" -ForegroundColor White
Write-Host "   Profile: $Profile" -ForegroundColor White
Write-Host "   Module: taskmagnet-web" -ForegroundColor White

# Display URLs
Write-Host ""
Write-Host "🌐 Application will be available at:" -ForegroundColor Cyan
Write-Host "   Application: http://localhost:$Port" -ForegroundColor White
Write-Host "   Swagger UI:  http://localhost:$Port/swagger-ui.html" -ForegroundColor White
Write-Host "   API Docs:    http://localhost:$Port/api-docs" -ForegroundColor White

# Check if port is available
try {
    $portInUse = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue
    if ($portInUse) {
        Write-Host ""
        Write-Host "⚠️  Warning: Port $Port appears to be in use" -ForegroundColor Yellow
        Write-Host "   The application may fail to start if another service is using this port" -ForegroundColor Yellow
    }
} catch {
    # Ignore errors from Get-NetTCPConnection (not available on all systems)
}

# Start the application
Write-Host ""
Write-Host "🚀 Starting TaskMagnet application..." -ForegroundColor Green
Write-Host "Command: mvn $($runParams -join ' ')" -ForegroundColor Gray
Write-Host ""
Write-Host "Press Ctrl+C to stop the application" -ForegroundColor Yellow
Write-Host ""

try {
    & mvn @runParams
} catch {
    Write-Host ""
    Write-Host "❌ Application startup failed" -ForegroundColor Red
} finally {
    Write-Host ""
    Write-Host "👋 TaskMagnet application stopped" -ForegroundColor Yellow
}
