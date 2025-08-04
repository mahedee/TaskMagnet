#!/bin/bash

# TaskMagnet Application Runner - Shell Script
# Author: TaskMagnet Development Team
# Version: 1.0.0
# Description: Runs the TaskMagnet Spring Boot application with configurable options

# Default values
PORT=8081
PROFILE="dev"
CLEAN=false
SKIP_TESTS=false
HELP=false

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
GRAY='\033[0;37m'
NC='\033[0m' # No Color

# Function to display help
show_help() {
    echo ""
    echo -e "${GREEN}TaskMagnet Application Runner${NC}"
    echo -e "${GREEN}=============================${NC}"
    echo ""
    echo -e "${YELLOW}Usage: ./run-app.sh [OPTIONS]${NC}"
    echo ""
    echo -e "${CYAN}Options:${NC}"
    echo -e "  ${WHITE}-p, --port <number>    Port number to run the application on (default: 8081)${NC}"
    echo -e "  ${WHITE}-f, --profile <name>   Spring profile to use: dev, prod, test (default: dev)${NC}"
    echo -e "  ${WHITE}-c, --clean           Clean build before running${NC}"
    echo -e "  ${WHITE}-s, --skip-tests      Skip tests during build${NC}"
    echo -e "  ${WHITE}-h, --help            Show this help message${NC}"
    echo ""
    echo -e "${CYAN}Examples:${NC}"
    echo -e "  ${GRAY}./run-app.sh                           # Run with defaults (port 8081, dev profile)${NC}"
    echo -e "  ${GRAY}./run-app.sh -p 8080                  # Run on port 8080${NC}"
    echo -e "  ${GRAY}./run-app.sh --profile prod           # Run with production profile${NC}"
    echo -e "  ${GRAY}./run-app.sh --clean --skip-tests     # Clean build and skip tests${NC}"
    echo ""
    echo -e "${CYAN}Application URLs:${NC}"
    echo -e "  ${WHITE}Application: http://localhost:$PORT${NC}"
    echo -e "  ${WHITE}Swagger UI:  http://localhost:$PORT/swagger-ui.html${NC}"
    echo -e "  ${WHITE}API Docs:    http://localhost:$PORT/api-docs${NC}"
    echo ""
}

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check if port is in use
check_port() {
    if command_exists lsof; then
        if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null 2>&1; then
            return 0
        fi
    elif command_exists netstat; then
        if netstat -ln 2>/dev/null | grep ":$1 " >/dev/null; then
            return 0
        fi
    elif command_exists ss; then
        if ss -ln 2>/dev/null | grep ":$1 " >/dev/null; then
            return 0
        fi
    fi
    return 1
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -p|--port)
            PORT="$2"
            shift 2
            ;;
        -f|--profile)
            PROFILE="$2"
            shift 2
            ;;
        -c|--clean)
            CLEAN=true
            shift
            ;;
        -s|--skip-tests)
            SKIP_TESTS=true
            shift
            ;;
        -h|--help)
            HELP=true
            shift
            ;;
        *)
            echo -e "${RED}❌ Error: Unknown option $1${NC}"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

# Display help if requested
if [ "$HELP" = true ]; then
    show_help
    exit 0
fi

# Display banner
echo ""
echo -e "${GREEN}🚀 TaskMagnet Application Runner${NC}"
echo -e "${GREEN}=================================${NC}"
echo ""

# Validate port number
if ! [[ "$PORT" =~ ^[0-9]+$ ]] || [ "$PORT" -lt 1 ] || [ "$PORT" -gt 65535 ]; then
    echo -e "${RED}❌ Error: Invalid port number '$PORT'. Port must be between 1 and 65535${NC}"
    exit 1
fi

# Validate profile
case $PROFILE in
    dev|prod|test)
        ;;
    *)
        echo -e "${RED}❌ Error: Invalid profile '$PROFILE'. Valid profiles are: dev, prod, test${NC}"
        exit 1
        ;;
esac

# Check if Maven is available
if ! command_exists mvn; then
    echo -e "${RED}❌ Error: Maven is not installed or not in PATH${NC}"
    echo -e "${YELLOW}   Please install Maven and ensure it's in your PATH${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Maven detected${NC}"

# Check if Java is available
if ! command_exists java; then
    echo -e "${RED}❌ Error: Java is not installed or not in PATH${NC}"
    echo -e "${YELLOW}   Please install Java 17+ and ensure it's in your PATH${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Java detected${NC}"

# Set working directory to backend
BACKEND_PATH="src/backend"
if [ ! -d "$BACKEND_PATH" ]; then
    echo -e "${RED}❌ Error: Backend directory not found at $BACKEND_PATH${NC}"
    echo -e "${YELLOW}   Please run this script from the TaskMagnet root directory${NC}"
    exit 1
fi

echo -e "${CYAN}📁 Changing to backend directory: $BACKEND_PATH${NC}"
cd "$BACKEND_PATH" || exit 1

# Build parameters
BUILD_PARAMS=()
if [ "$CLEAN" = true ]; then
    BUILD_PARAMS+=("clean")
    echo -e "${YELLOW}🧹 Clean build enabled${NC}"
fi
BUILD_PARAMS+=("compile")

if [ "$SKIP_TESTS" = true ]; then
    BUILD_PARAMS+=("-DskipTests")
    echo -e "${YELLOW}⏭️  Tests will be skipped${NC}"
fi

# Build the application
echo ""
echo -e "${CYAN}🔨 Building TaskMagnet application...${NC}"
echo -e "${GRAY}Command: mvn ${BUILD_PARAMS[*]}${NC}"

if ! mvn "${BUILD_PARAMS[@]}"; then
    echo -e "${RED}❌ Build failed. Please check the output above for errors.${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Build completed successfully${NC}"

# Display configuration
echo ""
echo -e "${CYAN}⚙️  Application Configuration:${NC}"
echo -e "   ${WHITE}Port: $PORT${NC}"
echo -e "   ${WHITE}Profile: $PROFILE${NC}"
echo -e "   ${WHITE}Module: taskmagnet-web${NC}"

# Display URLs
echo ""
echo -e "${CYAN}🌐 Application will be available at:${NC}"
echo -e "   ${WHITE}Application: http://localhost:$PORT${NC}"
echo -e "   ${WHITE}Swagger UI:  http://localhost:$PORT/swagger-ui.html${NC}"
echo -e "   ${WHITE}API Docs:    http://localhost:$PORT/api-docs${NC}"

# Check if port is available
if check_port "$PORT"; then
    echo ""
    echo -e "${YELLOW}⚠️  Warning: Port $PORT appears to be in use${NC}"
    echo -e "${YELLOW}   The application may fail to start if another service is using this port${NC}"
fi

# Prepare run parameters
RUN_PARAMS=(
    "spring-boot:run"
    "-pl" "taskmagnet-web"
    "-Dspring-boot.run.arguments=--server.port=$PORT --spring.profiles.active=$PROFILE"
)

# Start the application
echo ""
echo -e "${GREEN}🚀 Starting TaskMagnet application...${NC}"
echo -e "${GRAY}Command: mvn ${RUN_PARAMS[*]}${NC}"
echo ""
echo -e "${YELLOW}Press Ctrl+C to stop the application${NC}"
echo ""

# Set up signal handler for graceful shutdown
trap 'echo -e "\n${YELLOW}👋 TaskMagnet application stopped${NC}"; exit 0' INT TERM

# Run the application
if ! mvn "${RUN_PARAMS[@]}"; then
    echo ""
    echo -e "${RED}❌ Application startup failed${NC}"
    exit 1
fi

echo ""
echo -e "${YELLOW}👋 TaskMagnet application stopped${NC}"
