# ================================
# TaskMagnet - Commit and Push Script
# ================================
# This script adds all changes, commits with timestamp, and pushes to current branch

Write-Host "================================" -ForegroundColor Cyan
Write-Host "TaskMagnet - Commit and Push" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# Get current date and time
$currentDateTime = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
$defaultMessage = "feat: Complete TASK-P1-005 Task Management UI with kanban board and full CRUD operations - $currentDateTime"

Write-Host "Current Date/Time: $currentDateTime" -ForegroundColor Yellow
Write-Host "Default Commit Message: $defaultMessage" -ForegroundColor Yellow
Write-Host ""

# Allow user to customize commit message
$userMessage = Read-Host "Enter custom commit message (or press Enter to use default)"
if ([string]::IsNullOrWhiteSpace($userMessage)) {
    $commitMessage = $defaultMessage
} else {
    $commitMessage = $userMessage
}

Write-Host "Final Commit Message: $commitMessage" -ForegroundColor Yellow
Write-Host ""

# Check if we're in a git repository
if (-not (Test-Path ".git")) {
    Write-Host "Error: Not in a git repository!" -ForegroundColor Red
    Write-Host "Please run this script from the root of your git repository." -ForegroundColor Red
    exit 1
}

# Get current branch name
try {
    $currentBranch = git rev-parse --abbrev-ref HEAD
    Write-Host "Current Branch: $currentBranch" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "Error: Could not determine current branch!" -ForegroundColor Red
    exit 1
}

# Check git status
Write-Host "Checking git status..." -ForegroundColor Blue
git status --porcelain
Write-Host ""

# Add all changes
Write-Host "Adding all changes..." -ForegroundColor Blue
try {
    git add .
    Write-Host "✓ All changes added successfully" -ForegroundColor Green
} catch {
    Write-Host "Error: Failed to add changes!" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}

# Show what will be committed
Write-Host ""
Write-Host "Files to be committed:" -ForegroundColor Blue
git diff --cached --name-status
Write-Host ""

# Confirm before committing
$confirmation = Read-Host "Do you want to proceed with the commit? (y/N)"
if ($confirmation -ne 'y' -and $confirmation -ne 'Y') {
    Write-Host "Operation cancelled by user." -ForegroundColor Yellow
    exit 0
}

# Commit changes
Write-Host ""
Write-Host "Committing changes..." -ForegroundColor Blue
try {
    git commit -m $commitMessage
    Write-Host "✓ Changes committed successfully" -ForegroundColor Green
} catch {
    Write-Host "Error: Failed to commit changes!" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}

# Push to current branch
Write-Host ""
Write-Host "Pushing to branch '$currentBranch'..." -ForegroundColor Blue
try {
    git push origin $currentBranch
    Write-Host "✓ Changes pushed successfully to '$currentBranch'" -ForegroundColor Green
} catch {
    Write-Host "Error: Failed to push changes!" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
    Write-Host "This might happen if:" -ForegroundColor Yellow
    Write-Host "- Remote branch doesn't exist (try: git push -u origin $currentBranch)" -ForegroundColor Yellow
    Write-Host "- Authentication failed" -ForegroundColor Yellow
    Write-Host "- Network connectivity issues" -ForegroundColor Yellow
    Write-Host "- Remote repository is not accessible" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "SUCCESS!" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Cyan
Write-Host "All changes have been committed and pushed to '$currentBranch'" -ForegroundColor Green
Write-Host "Commit: $commitMessage" -ForegroundColor Green
Write-Host ""

# Show final git status
Write-Host "Final git status:" -ForegroundColor Blue
git status
Write-Host ""

# Show recent commits
Write-Host "Recent commits:" -ForegroundColor Blue
git log --oneline -5
Write-Host ""

Write-Host "Script completed successfully! 🎉" -ForegroundColor Green
