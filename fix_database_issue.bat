@echo off
echo 🔧 Fixing checkbox_data column type mismatch...

echo.
echo 1. Stop your Spring Boot application first
echo.

echo 2. Run this SQL in your PostgreSQL database:
echo    ALTER TABLE notes ALTER COLUMN checkbox_data TYPE TEXT;
echo    UPDATE notes SET checkbox_data = checkbox_data::text WHERE checkbox_data IS NOT NULL;
echo.

echo 3. Clean and rebuild the project:
mvn clean compile

echo.
echo 4. Restart your Spring Boot application
echo.

echo ✅ Fix completed! Your app should now work without the JSONB type mismatch error.
pause