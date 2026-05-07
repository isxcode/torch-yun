#!/bin/bash

# =============================================================================
# ZhiShuYun Dependency Download Script
# =============================================================================

set -e  # Exit immediately on error

# Configuration
readonly MODEL_FILE="Qwen2.5-0.5B.zip"
readonly OSS_DOWNLOAD_URL="https://zhishuyun-demo.isxcode.com/tools/open/file"

# Path configuration
readonly BASE_PATH=$(cd "$(dirname "$0")" && pwd)
readonly TMP_DIR="${BASE_PATH}/resources/tmp"

# =============================================================================
# Utility functions
# =============================================================================

# Check if a command exists
check_command() {
    local cmd=$1
    local install_msg=$2

    if ! command -v "$cmd" &>/dev/null; then
        echo "$cmd command not detected, $install_msg" >&2
        exit 1
    fi
    echo "$cmd command check passed"
}

# Create directory
create_dir() {
    local dir=$1
    if [[ ! -d "$dir" ]]; then
        mkdir -p "$dir"
        echo "Created directory: $dir"
    fi
}

# Download file
download_file() {
    local url=$1
    local output_path=$2
    local description=$3

    if [[ -f "$output_path" ]]; then
        echo "$description already exists, skipping download"
        return 0
    fi

    echo "Starting download of $description..."
    if curl -ssL "$url" -o "$output_path"; then
      if head -n 1 "$output_path" | grep -q "<?xml"; then
        if grep -q "<Error>" "$output_path" && grep -q "<Code>NoSuchKey</Code>" "$output_path"; then
            rm -rf "$output_path"
            echo "Download failed, please contact administrator: ispong@outlook.com" >&2
            exit 1
        fi
      fi
      echo "$description downloaded successfully"
    fi
}

# =============================================================================
# Download functions
# =============================================================================

# Download model
download_model() {
    echo "Downloading model ${MODEL_FILE}..."

    # Create necessary directories
    create_dir "$TMP_DIR"

    # Download model
    local model_url="${OSS_DOWNLOAD_URL}/${MODEL_FILE}"
    local model_path="${TMP_DIR}/${MODEL_FILE}"
    download_file "$model_url" "$model_path" "Large model file ${MODEL_FILE} binary, please be patient"
}

# Download project dependencies
install_project_dependencies() {
    echo "Downloading project dependencies..."

    # Create project dependencies directory
    create_dir "$LIBS_DIR"

    # Download project JAR dependencies
    for jar in "${PROJECT_JARS[@]}"; do
        local jar_url="${OSS_DOWNLOAD_URL}/${jar}"
        local jar_path="${LIBS_DIR}/${jar}"
        download_file "$jar_url" "$jar_path" "Project dependency: $jar"
    done
}

# =============================================================================
# Main download process
# =============================================================================

main() {
    echo "Starting ZhiShuYun project dependency download..."

    # 1. Download model
    download_model

    echo "Project dependency download complete!"
}

# =============================================================================
# Script entry point
# =============================================================================

# Switch to script directory
cd "$BASE_PATH" || {
    echo "Cannot switch to project directory: $BASE_PATH" >&2
    exit 1
}

# Execute main function
main