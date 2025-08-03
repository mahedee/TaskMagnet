@echo off
REM ================================
REM TaskMagnet - Commit and Push Script
REM ================================
REM This script adds all changes, commits with timestamp, and pushes to current branch

echo ================================
echo TaskMagnet - Commit and Push
echo ================================
echo.

REM Check if we're in a git repository
if not exist ".git" (
    echo Error: Not in a git repository!
    echo Please run this script from the root of your git repository.
    pause
    exit /b 1
)

REM Get current date and time
for /f "tokens=1-4 delims=/ " %%i in ("%date%") do set mydate=%%k-%%j-%%i
for /f "tokens=1-2 delims=: " %%i in ("%time%") do set mytime=%%i:%%j
set "currentDateTime=%mydate% %mytime%"
set "commitMessage=Commit everything at %currentDateTime%"

echo Current Date/Time: %currentDateTime%
echo Commit Message: %commitMessage%
echo.

REM Get current branch name
for /f %%i in ('git rev-parse --abbrev-ref HEAD') do set currentBranch=%%i
echo Current Branch: %currentBranch%
echo.

REM Check git status
echo Checking git status...
git status --porcelain
echo.

REM Add all changes
echo Adding all changes...
git add .
if errorlevel 1 (
    echo Error: Failed to add changes!
    pause
    exit /b 1
)
echo All changes added successfully
echo.

REM Show what will be committed
echo Files to be committed:
git diff --cached --name-status
echo.

REM Confirm before committing
set /p confirmation="Do you want to proceed with the commit? (y/N): "
if /i not "%confirmation%"=="y" (
    echo Operation cancelled by user.
    pause
    exit /b 0
)

REM Commit changes
echo.
echo Committing changes...
git commit -m "%commitMessage%"
if errorlevel 1 (
    echo Error: Failed to commit changes!
    pause
    exit /b 1
)
echo Changes committed successfully
echo.

REM Push to current branch
echo Pushing to branch '%currentBranch%'...
git push origin %currentBranch%
if errorlevel 1 (
    echo Error: Failed to push changes!
    echo This might happen if:
    echo - Remote branch doesn't exist (try: git push -u origin %currentBranch%)
    echo - Authentication failed
    echo - Network connectivity issues
    echo - Remote repository is not accessible
    pause
    exit /b 1
)
echo Changes pushed successfully to '%currentBranch%'
echo.

echo ================================
echo SUCCESS!
echo ================================
echo All changes have been committed and pushed to '%currentBranch%'
echo Commit: %commitMessage%
echo.

REM Show final git status
echo Final git status:
git status
echo.

REM Show recent commits
echo Recent commits:
git log --oneline -5
echo.

echo Script completed successfully!
pause
