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

PROFILE="$HOME/.bashrc"
[[ "$SHELL" == */zsh ]] && PROFILE="$HOME/.zshrc"

sed -i '/^export DB_USERNAME=/d' "$PROFILE" 2>/dev/null
sed -i '/^export DB_PASSWORD=/d' "$PROFILE" 2>/dev/null

cat >> "$PROFILE" <<EOF

# Hospital Vida Salud – variables de entorno
export DB_USERNAME="$DB_USERNAME"
export DB_PASSWORD="$DB_PASSWORD"
EOF

echo ""
echo "Variables guardadas en $PROFILE"
echo "Ejecuta: source $PROFILE"
echo ""
