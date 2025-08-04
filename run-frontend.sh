#!/bin/bash

# TaskMagnet Frontend Runner Script
# This script starts the React development server

echo "🧲 TaskMagnet Frontend Runner"
echo "============================="

# Check if we're in the correct directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FRONTEND_DIR="$SCRIPT_DIR/src/frontend/taskmagnet-frontend"

if [ ! -d "$FRONTEND_DIR" ]; then
    echo "❌ Error: Frontend directory not found at $FRONTEND_DIR"
    exit 1
fi

echo "📂 Changing to frontend directory: $FRONTEND_DIR"
cd "$FRONTEND_DIR"

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo "📦 Installing npm dependencies..."
    npm install
    if [ $? -ne 0 ]; then
        echo "❌ Error: Failed to install dependencies"
        exit 1
    fi
fi

echo ""
echo "🚀 Starting TaskMagnet Frontend Development Server"
echo "   - Frontend URL: http://localhost:3000"
echo "   - Backend API should be running on: http://localhost:8081"
echo ""
echo "💡 Press Ctrl+C to stop the server"
echo ""

# Start the development server
npm start
