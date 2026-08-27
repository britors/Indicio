#!/usr/bin/env bash

set -Eeuo pipefail

readonly EXPECTED_ID="lyra-os"
readonly EXPECTED_VERSION="27.02-alpha6"
readonly ANDROID_STUDIO_APP_ID="com.google.AndroidStudio"

install_android_studio=false

usage() {
    cat <<'EOF'
Uso: ./scripts/setup-lyra-os.sh [opções]

Instala no Lyra OS 27.02 Alpha 6 as ferramentas básicas para desenvolver o Indício.

Opções:
  --with-android-studio  Instala o Android Studio pelo Flathub (pacote comunitário).
  -h, --help             Mostra esta ajuda.

Sem opções, instala apenas pacotes dos repositórios do Lyra OS. O Android Studio
pode então ser instalado pelo arquivo oficial disponível em developer.android.com.
EOF
}

for argument in "$@"; do
    case "$argument" in
        --with-android-studio)
            install_android_studio=true
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        *)
            printf 'Opção desconhecida: %s\n\n' "$argument" >&2
            usage >&2
            exit 2
            ;;
    esac
done

if [[ ! -r /etc/os-release ]]; then
    printf 'Não foi possível identificar o sistema: /etc/os-release ausente.\n' >&2
    exit 1
fi

# shellcheck disable=SC1091
source /etc/os-release

if [[ "${ID:-}" != "$EXPECTED_ID" || "${VERSION_ID:-}" != "$EXPECTED_VERSION" ]]; then
    printf 'Este script requer Lyra OS %s; encontrado: %s %s.\n' \
        "$EXPECTED_VERSION" "${ID:-desconhecido}" "${VERSION_ID:-desconhecida}" >&2
    exit 1
fi

if [[ "$(uname -m)" != "x86_64" ]]; then
    printf 'O Android Studio para Linux requer arquitetura x86_64.\n' >&2
    exit 1
fi

if [[ "${EUID}" -eq 0 ]]; then
    printf 'Execute como usuário comum; o script solicitará pkexec quando necessário.\n' >&2
    exit 1
fi

if ! command -v pkexec >/dev/null 2>&1; then
    printf 'O comando pkexec é necessário para instalar os pacotes do sistema.\n' >&2
    exit 1
fi

packages=(
    android-tools
    curl
    flatpak
    git-core
    java-21-openjdk-devel
    unzip
    zip
)

printf 'Instalando ferramentas dos repositórios do Lyra OS...\n'
pkexec zypper --non-interactive install --no-recommends "${packages[@]}"

if "$install_android_studio"; then
    printf 'Configurando o Flathub para o usuário atual...\n'
    flatpak remote-add --user --if-not-exists flathub \
        https://dl.flathub.org/repo/flathub.flatpakrepo

    printf 'Instalando o Android Studio (pacote comunitário do Flathub)...\n'
    flatpak install --user --noninteractive flathub "$ANDROID_STUDIO_APP_ID"
fi

printf '\nFerramentas encontradas:\n'
java -version
git --version
adb version | head -n 1

cat <<'EOF'

Instalação básica concluída.

Próximos passos:
1. Instale/abra o Android Studio.
2. Conclua o Setup Wizard para instalar o Android SDK recomendado.
3. Abra este repositório no Android Studio.

Se escolheu --with-android-studio, abra-o com:
  flatpak run com.google.AndroidStudio

O emulador exige virtualização de hardware habilitada. Também é possível usar um
dispositivo Android físico com depuração USB, sem configurar virtualização local.
EOF

