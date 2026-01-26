@echo off
echo ========================================
echo Testing SelfGrowth Backend Media Upload
echo ========================================

echo.
echo 1. Testing Backend Server Status...
curl -s -o nul -w "Backend Status: %%{http_code}\n" http://localhost:8081/api/notes

echo.
echo 2. Testing Image Upload Endpoint...
echo Note: This will fail without proper authentication and file
curl -X POST http://localhost:8081/api/notes/upload/image ^
  -H "Content-Type: multipart/form-data" ^
  -w "Image Upload Endpoint Status: %%{http_code}\n" ^
  -s -o nul

echo.
echo 3. Testing Voice Upload Endpoint...
echo Note: This will fail without proper authentication and file
curl -X POST http://localhost:8081/api/notes/upload/voice ^
  -H "Content-Type: multipart/form-data" ^
  -w "Voice Upload Endpoint Status: %%{http_code}\n" ^
  -s -o nul

echo.
echo ========================================
echo Test Complete!
echo ========================================
echo.
echo Expected Results:
echo - Backend Status: 401 (Unauthorized - normal without token)
echo - Image Upload: 400 or 401 (Bad Request/Unauthorized - normal without file/token)
echo - Voice Upload: 400 or 401 (Bad Request/Unauthorized - normal without file/token)
echo.
echo If you see 404 errors, the endpoints don't exist.
echo If you see connection errors, the backend isn't running.
echo.
pause