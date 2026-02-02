@echo off
echo Starting SelfGrowth Backend on port 8095...
echo.
cd /d "%~dp0"
mvn spring-boot:run
pause