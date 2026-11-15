@echo off
echo ========================================================
echo  Starting Car Expense Tracking System (Dev Mode - H2)
echo  No Oracle installation required!
echo ========================================================
set "PATH=%PATH%;C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.2\plugins\maven-plugin\lib\maven3\bin"
cd /d "%~dp0"
mvn spring-boot:run -Dspring-boot.run.profiles=dev
pause
