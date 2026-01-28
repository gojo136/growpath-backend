@echo off
echo Testing SelfGrowth Backend (port 8082)...
echo.

echo 0. Health check (no auth)...
curl -s -X GET http://localhost:8082/api/auth/health
echo.
echo.

echo 1. Testing Signup...
curl -s -X POST http://localhost:8082/api/auth/signup ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"test@example.com\",\"password\":\"TestPassword123\",\"confirmPassword\":\"TestPassword123\",\"fullName\":\"Test User\",\"age\":25}"

echo.
echo.

echo 2. Testing Login...
curl -s -X POST http://localhost:8082/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"test@example.com\",\"password\":\"TestPassword123\"}"

echo.
echo.
echo Tests completed. Check responses above.
pause
