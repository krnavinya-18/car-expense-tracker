Write-Host "========================================================" -ForegroundColor Cyan
Write-Host " Starting Car Expense Tracking System (Dev Mode - H2)   " -ForegroundColor Green
Write-Host " No Oracle installation required!                       " -ForegroundColor Yellow
Write-Host "========================================================" -ForegroundColor Cyan

$env:PATH += ";C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.2\plugins\maven-plugin\lib\maven3\bin"
Set-Location $PSScriptRoot
& "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.2\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" spring-boot:run "-Dspring-boot.run.profiles=dev"
