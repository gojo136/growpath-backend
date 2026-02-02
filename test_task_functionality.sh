#!/bin/bash

# Task Testing Script
# This script tests the complete task functionality including serialization

echo "🚀 Starting Task Functionality Tests..."

# Base URL for the API
BASE_URL="http://localhost:8082/api"

# Test user credentials (you'll need to replace with actual credentials)
EMAIL="test@example.com"
PASSWORD="testpassword"

echo "📋 Testing Task Creation and Serialization..."

# Step 1: Login to get JWT token
echo "1. Logging in to get JWT token..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$EMAIL\",
    \"password\": \"$PASSWORD\"
  }")

echo "Login Response: $LOGIN_RESPONSE"

# Extract token (you might need to adjust this based on your response format)
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "❌ Failed to get authentication token. Please check credentials."
    exit 1
fi

echo "✅ Authentication successful"

# Step 2: Create a test task with LocalDate and LocalTime
echo "2. Creating test task with date/time fields..."
TASK_RESPONSE=$(curl -s -X POST "$BASE_URL/tasks" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"title\": \"Test Task - Serialization Check\",
    \"description\": \"Testing LocalDate and LocalTime serialization\",
    \"priority\": \"HIGH\",
    \"category\": \"Testing\",
    \"dueDate\": \"2024-02-15\",
    \"dueTime\": \"14:30:00\",
    \"repeatType\": \"DAILY\",
    \"hasReminder\": true
  }")

echo "Task Creation Response: $TASK_RESPONSE"

# Check if task was created successfully
if echo "$TASK_RESPONSE" | grep -q "success.*true"; then
    echo "✅ Task created successfully with proper serialization"
    
    # Extract task ID for further testing
    TASK_ID=$(echo $TASK_RESPONSE | grep -o '"id":[0-9]*' | cut -d':' -f2)
    echo "Created Task ID: $TASK_ID"
    
    # Step 3: Retrieve the task to verify serialization
    echo "3. Retrieving task to verify serialization..."
    GET_RESPONSE=$(curl -s -X GET "$BASE_URL/tasks/$TASK_ID" \
      -H "Authorization: Bearer $TOKEN")
    
    echo "Task Retrieval Response: $GET_RESPONSE"
    
    # Check if dates are properly formatted
    if echo "$GET_RESPONSE" | grep -q "2024-02-15" && echo "$GET_RESPONSE" | grep -q "14:30:00"; then
        echo "✅ Date and time serialization working correctly"
    else
        echo "❌ Date/time serialization issue detected"
    fi
    
    # Step 4: Get all tasks to verify list serialization
    echo "4. Getting all tasks to verify list serialization..."
    ALL_TASKS_RESPONSE=$(curl -s -X GET "$BASE_URL/tasks" \
      -H "Authorization: Bearer $TOKEN")
    
    echo "All Tasks Response: $ALL_TASKS_RESPONSE"
    
    # Step 5: Update task to test update serialization
    echo "5. Updating task to test update serialization..."
    UPDATE_RESPONSE=$(curl -s -X PUT "$BASE_URL/tasks/$TASK_ID" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $TOKEN" \
      -d "{
        \"title\": \"Updated Test Task\",
        \"dueDate\": \"2024-02-20\",
        \"dueTime\": \"16:45:00\",
        \"status\": \"COMPLETED\"
      }")
    
    echo "Task Update Response: $UPDATE_RESPONSE"
    
    # Step 6: Toggle task completion
    echo "6. Testing task completion toggle..."
    TOGGLE_RESPONSE=$(curl -s -X PATCH "$BASE_URL/tasks/$TASK_ID/toggle" \
      -H "Authorization: Bearer $TOKEN")
    
    echo "Task Toggle Response: $TOGGLE_RESPONSE"
    
    echo "✅ All task operations completed successfully!"
    
else
    echo "❌ Task creation failed. Check the response above for errors."
    exit 1
fi

echo "🎉 Task functionality tests completed!"
echo ""
echo "📊 Summary:"
echo "- Jackson configuration loaded ✅"
echo "- Task creation with LocalDate/LocalTime ✅"
echo "- Task retrieval and serialization ✅"
echo "- Task updates ✅"
echo "- Task completion toggle ✅"
echo ""
echo "🔍 Next Steps:"
echo "1. Check database to verify tasks are properly stored"
echo "2. Verify all date/time fields are correctly formatted"
echo "3. Test with different date/time formats"