#!/usr/bin/env bash
set -Eeuo pipefail

readonly SDK_DEFAULT="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-$HOME/Android/Sdk}}"
readonly JAVA_DEFAULT="/usr/lib64/jvm/java-21-openjdk-21"
readonly ENV_FILE="$HOME/.config/indicio/android-env.sh"
readonly PROFILE_FILES=("$HOME/.bashrc" "$HOME/.profile")
readonly FISH_CONF_D="$HOME/.config/fish/conf.d/indicio-android.fish"

dry_run=false
sdk_dir="$SDK_DEFAULT"
java_dir="${JAVA_HOME:-$JAVA_DEFAULT}"

usage() {
    cat <<'EOF'
Uso: ./scripts/configure-android-dev.sh [opções]

Configura o ambiente local de desenvolvimento Android do Indício.

Opções:
  --sdk DIR       Usa DIR como Android SDK (padrão: ~/Android/Sdk).
  --java DIR      Usa DIR como JDK 21 (padrão: /usr/lib64/jvm/java-21-openjdk-21).
  --dry-run       Apenas mostra as alterações, sem escrever arquivos.
  -h, --help      Mostra esta ajuda.

Ferramentas ausentes (como cmdline-tools) são apenas reportadas.
EOF
}

while (($#)); do
    case "$1" in
        --sdk)
            (($# >= 2)) || { printf '%s\n' '--sdk requer um diretório.' >&2; exit 2; }
            sdk_dir="$2"; shift 2 ;;
        --java)
            (($# >= 2)) || { printf '%s\n' '--java requer um diretório.' >&2; exit 2; }
            java_dir="$2"; shift 2 ;;
        --dry-run) dry_run=true; shift ;;
        -h|--help) usage; exit 0 ;;
        *) printf 'Opção desconhecida: %s\n\n' "$1" >&2; usage >&2; exit 2 ;;
    esac
done

sdk_dir="${sdk_dir%/}"
java_dir="${java_dir%/}"
[[ -d "$sdk_dir" ]] || { printf 'Android SDK não encontrado: %s\n' "$sdk_dir" >&2; exit 1; }
[[ -x "$java_dir/bin/java" && -x "$java_dir/bin/javac" ]] || {
    printf 'JDK inválido ou incompleto: %s\n' "$java_dir" >&2
    exit 1
}

env_contents="$(cat <<EOF
# Gerado por scripts/configure-android-dev.sh
export JAVA_HOME=$(printf '%q' "$java_dir")
export ANDROID_HOME=$(printf '%q' "$sdk_dir")
export ANDROID_SDK_ROOT="\$ANDROID_HOME"
export PATH="\$JAVA_HOME/bin:\$ANDROID_HOME/platform-tools:\$ANDROID_HOME/emulator:\$ANDROID_HOME/cmdline-tools/latest/bin:\$PATH"
EOF
)"

fish_contents="$(cat <<EOF
# Gerado por scripts/configure-android-dev.sh
set -gx JAVA_HOME "$java_dir"
set -gx ANDROID_HOME "$sdk_dir"
set -gx ANDROID_SDK_ROOT "\$ANDROID_HOME"
fish_add_path -g "\$JAVA_HOME/bin" "\$ANDROID_HOME/platform-tools" "\$ANDROID_HOME/emulator" "\$ANDROID_HOME/cmdline-tools/latest/bin"
EOF
)"

if $dry_run; then
    printf '%s\n' "[$ENV_FILE]"
    printf '%s\n' "$env_contents"
    printf 'Perfis: %s\n' "${PROFILE_FILES[*]}"
    if [[ -d "$HOME/.config/fish" ]]; then
        printf '\n%s\n' "[$FISH_CONF_D]"
        printf '%s\n' "$fish_contents"
    fi
    exit 0
fi

mkdir -p "$(dirname "$ENV_FILE")"
printf '%s\n' "$env_contents" > "$ENV_FILE"
chmod 0644 "$ENV_FILE"

for profile in "${PROFILE_FILES[@]}"; do
    touch "$profile"
    if ! grep -Fqx ". $ENV_FILE" "$profile"; then
        {
            printf '\n# >>> Indício Android environment >>>\n'
            printf '. %s\n' "$ENV_FILE"
            printf '# <<< Indício Android environment <<<\n'
        } >> "$profile"
    fi
done

if [[ -d "$HOME/.config/fish" ]]; then
    mkdir -p "$(dirname "$FISH_CONF_D")"
    printf '%s\n' "$fish_contents" > "$FISH_CONF_D"
    chmod 0644 "$FISH_CONF_D"
fi

# shellcheck disable=SC1090
source "$ENV_FILE"
printf 'Ambiente salvo em %s\n' "$ENV_FILE"
if [[ -d "$HOME/.config/fish" ]]; then
    printf 'Ambiente fish salvo em %s\n' "$FISH_CONF_D"
fi
printf 'JAVA_HOME=%s\nANDROID_HOME=%s\n' "$JAVA_HOME" "$ANDROID_HOME"

missing=0
for tool in sdkmanager avdmanager emulator adb; do
    if command -v "$tool" >/dev/null 2>&1; then
        printf 'OK        %-12s %s\n' "$tool" "$(command -v "$tool")"
    else
        printf 'PENDENTE  %-10s não encontrado\n' "$tool"
        missing=1
    fi
done
if ((missing)); then
    printf '\nFaltam componentes do SDK; instale Android SDK Command-line Tools pelo SDK Manager.\n'
fi
printf '\nAbra um novo terminal ou execute: . %s\n' "$ENV_FILE"
