# ============================================================
#  Hospital Vida Salud – Configuración de variables de entorno
#  Ejecutar UNA vez desde PowerShell:
#      .\setup-env.ps1
# ============================================================

Write-Host ""
Write-Host "=== Hospital Vida Salud – Setup de variables de entorno ===" -ForegroundColor Cyan
Write-Host ""

# ── PostgreSQL ────────────────────────────────────────────────
$dbUsername = Read-Host "Usuario de PostgreSQL  [postgres]"
if ([string]::IsNullOrWhiteSpace($dbUsername)) { $dbUsername = "postgres" }

$dbPassword = Read-Host "Contraseña de PostgreSQL"

# ── Bases de datos (URLs por defecto, se pueden cambiar) ──────
$dbUrlPacientes     = "r2dbc:postgresql://localhost:5432/pacientes_db"
$dbUrlCitas         = "r2dbc:postgresql://localhost:5432/citas_db"
$dbUrlNotificaciones= "r2dbc:postgresql://localhost:5432/notificaciones_db"
$dbUrlAuditoria     = "r2dbc:postgresql://localhost:5432/auditoria_db"

# ── Guardar variables de entorno permanentes (usuario actual) ─
[System.Environment]::SetEnvironmentVariable("DB_USERNAME",              $dbUsername,          "User")
[System.Environment]::SetEnvironmentVariable("DB_PASSWORD",              $dbPassword,          "User")
[System.Environment]::SetEnvironmentVariable("DB_URL",                   $dbUrlPacientes,      "User")

# Cada servicio sobreescribe DB_URL con la suya al arrancar si se necesita,
# pero para desarrollo local con IntelliJ se puede lanzar cada servicio
# con su propia DB_URL via Edit Configurations > Environment variables.

Write-Host ""
Write-Host "Variables guardadas correctamente:" -ForegroundColor Green
Write-Host "  DB_USERNAME  = $dbUsername"
Write-Host "  DB_PASSWORD  = ********"
Write-Host "  DB_URL       = $dbUrlPacientes (ejemplo pacientes)"
Write-Host ""
Write-Host "Para cada servicio en IntelliJ, configura en Run > Edit Configurations > Environment variables:" -ForegroundColor Yellow
Write-Host "  pacientes-service  →  DB_URL=$dbUrlPacientes"      -ForegroundColor White
Write-Host "  citas-service      →  DB_URL=$dbUrlCitas"           -ForegroundColor White
Write-Host "  notificaciones     →  DB_URL=$dbUrlNotificaciones"  -ForegroundColor White
Write-Host "  auditoria-service  →  DB_URL=$dbUrlAuditoria"       -ForegroundColor White
Write-Host ""
Write-Host "IMPORTANTE: Cierra y vuelve a abrir IntelliJ para que surtan efecto." -ForegroundColor Yellow
Write-Host ""