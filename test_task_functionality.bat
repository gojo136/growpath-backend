@echo off
REM Task Testing Script for Windows
REM This script tests the complete task functionality including serialization

echo 🚀 Starting Task Functionality Tests...

REM Base URL for the API
set BASE_URL=http://localhost:8085/api

REM Test user credentials (you'll need to replace with actual credentials)
set EMAIL=test@example.com
set PASSWORD=testpassword

echo 📋 Testing Task Creation and Serialization...

REM Step 1: Login to get JWT token
echo 1. Logging in to get JWT token...
curl -s -X POST "%BASE_URL%/auth/login" ^
  -H "Content-Type: application/json" ^
  -d "{\"email\": \"%EMAIL%\", \"password\": \"%PASSWORD%\"}" ^
  -o login_response.json

type login_response.json
echo.

REM You'll need to manually extract the token from login_response.json
REM For automated testing, you might want to use a JSON parser

echo Please extract the JWT token from login_response.json and set it as TOKEN variable
set /p TOKEN=Enter JWT Token: 

if "%TOKEN%"=="" (
    echo ❌ No token provided. Exiting.
    exit /b 1
)

echo ✅ Authentication token set

REM Step 2: Create a test task with LocalDate and LocalTime
echo 2. Creating test task with date/time fields...
curl -s -X POST "%BASE_URL%/tasks" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -d "{\"title\": \"Test Task - Serialization Check\", \"description\": \"Testing LocalDate and LocalTime serialization\", \"priority\": \"HIGH\", \"category\": \"Testing\", \"dueDate\": \"2024-02-15\", \"dueTime\": \"14:30:00\", \"repeatType\": \"DAILY\", \"hasReminder\": true}" ^
  -o task_creation_response.json

echo Task Creation Response:
type task_creation_response.json
echo.

REM Step 3: Get all tasks
echo 3. Getting all tasks to verify serialization...
curl -s -X GET "%BASE_URL%/tasks" ^
  -H "Authorization: Bearer %TOKEN%" ^
  -o all_tasks_response.json

echo All Tasks Response:
type all_tasks_response.json
echo.

echo ✅ Task functionality tests completed!
echo.
echo 📊 Summary:
echo - Jackson configuration should be loaded ✅
echo - Task creation with LocalDate/LocalTime tested ✅
echo - Task retrieval and serialization tested ✅
echo.
echo 🔍 Next Steps:
echo 1. Check the JSON responses above for proper date formatting
echo 2. Verify database contains the tasks with correct date/time values
echo 3. Look for any serialization errors in the server logs

pause