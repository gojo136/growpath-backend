@echo off
echo Testing SelfGrowth Backend Authentication...
echo.

echo 1. Testing Signup...
curl -X POST http://localhost:8081/api/auth/signup ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"test@example.com\",\"password\":\"TestPassword123\",\"confirmPassword\":\"TestPassword123\",\"fullName\":\"Test User\",\"age\":25}"

echo.
echo.

echo 2. Testing Login...
curl -X POST http://localhost:8081/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"test@example.com\",\"password\":\"TestPassword123\"}"

echo.
echo.
echo Tests completed. Check responses above.
pause