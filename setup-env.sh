#!/bin/bash
# ============================================================
#  Hospital Vida Salud – Configuración de variables de entorno
#  Ejecutar UNA vez:
#      chmod +x setup-env.sh && ./setup-env.sh
# ============================================================

echo ""
echo "=== Hospital Vida Salud – Setup de variables de entorno ==="
echo ""

read -p "Usuario de PostgreSQL [postgres]: " DB_USERNAME
DB_USERNAME=${DB_USERNAME:-postgres}

read -s -p "Contraseña de PostgreSQL: " DB_PASSWORD
echo ""

# Detectar archivo de perfil
PROFILE="$HOME/.bashrc"
[[ "$SHELL" == */zsh ]] && PROFILE="$HOME/.zshrc"

# Limpiar entradas anteriores
sed -i '/^export DB_/d'       "$PROFILE" 2>/dev/null
sed -i '/^export EUREKA_/d'   "$PROFILE" 2>/dev/null
sed -i '/^export RABBITMQ_/d' "$PROFILE" 2>/dev/null

cat >> "$PROFILE" <<EOF

# Hospital Vida Salud – variables de entorno
export DB_USERNAME="$DB_USERNAME"
export DB_PASSWORD="$DB_PASSWORD"
EOF

echo ""
echo "Variables guardadas en $PROFILE"
echo ""
echo "Cada servicio necesita su propia DB_URL. Agrégalas en tu IDE o expórtalas:"
echo "  export DB_URL=r2dbc:postgresql://localhost:5432/pacientes_db"
echo "  export DB_URL=r2dbc:postgresql://localhost:5432/citas_db"
echo "  export DB_URL=r2dbc:postgresql://localhost:5432/notificaciones_db"
echo "  export DB_URL=r2dbc:postgresql://localhost:5432/auditoria_db"
echo ""
echo "Ejecuta: source $PROFILE"
echo ""