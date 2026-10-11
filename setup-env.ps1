# ============================================================
#  Hospital Vida Salud – Configuración de variables de entorno
#  Ejecutar UNA vez desde PowerShell:
#      .\setup-env.ps1
# ============================================================

Write-Host ""
Write-Host "=== Hospital Vida Salud – Setup de variables de entorno ===" -ForegroundColor Cyan
Write-Host ""

$dbUsername = Read-Host "Usuario de PostgreSQL  [postgres]"
if ([string]::IsNullOrWhiteSpace($dbUsername)) { $dbUsername = "postgres" }

$dbPassword = Read-Host "Contraseña de PostgreSQL"

[System.Environment]::SetEnvironmentVariable("DB_USERNAME", $dbUsername, "User")
[System.Environment]::SetEnvironmentVariable("DB_PASSWORD", $dbPassword, "User")

Write-Host ""
Write-Host "Variables guardadas correctamente:" -ForegroundColor Green
Write-Host "  DB_USERNAME = $dbUsername"
Write-Host "  DB_PASSWORD = ********"
Write-Host ""
Write-Host "IMPORTANTE: Cierra y vuelve a abrir IntelliJ para que surtan efecto." -ForegroundColor Yellow
Write-Host ""
