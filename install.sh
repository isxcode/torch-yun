#!/bin/bash

# =============================================================================
# ZhiShuYun Dependency Installation Script
# =============================================================================

set -e  # Exit immediately on error

# Path configuration
readonly BASE_PATH=$(cd "$(dirname "$0")" && pwd)
readonly RESOURCE_DIR="${BASE_PATH}/resources"

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

# =============================================================================
# Installation functions
# =============================================================================

# Check system dependencies
check_system_dependencies() {
    echo "Checking system dependencies..."

    check_command "java" "Please install Java"
    check_command "node" "Please install Node.js"

    # Check and install pnpm
    if ! command -v pnpm &>/dev/null; then
        echo "pnpm not detected, installing..."
        npm install pnpm@9.0.6 -g
        echo "pnpm installation complete"
    else
        echo "pnpm command check passed"
    fi

    # Check and install langChain
    check_command "python3" "Please install Python3"
    check_command "pip3" "Please install Pip"

    if ! python3 -c "import langchain_core, langchain_openai" &>/dev/null; then
        echo "langChain not detected, installing..."
        pip install langchain-openai langchain-core
        echo "langChain installation complete"
    else
        echo "langChain environment check passed"
    fi
}

# =============================================================================
# Main installation process
# =============================================================================

main() {
    echo "Starting ZhiShuYun project dependency installation..."

    # 1. Check system dependencies
    check_system_dependencies

    echo "Project dependency installation complete!"
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