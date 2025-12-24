#!/bin/bash

# =============================================================================
# 至数云依赖安装脚本
# =============================================================================

set -e  # 遇到错误立即退出

# 路径配置
readonly BASE_PATH=$(cd "$(dirname "$0")" && pwd)
readonly RESOURCE_DIR="${BASE_PATH}/resources"

# =============================================================================
# 工具函数
# =============================================================================

# 检查命令是否存在
check_command() {
    local cmd=$1
    local install_msg=$2

    if ! command -v "$cmd" &>/dev/null; then
        echo "未检测到 $cmd 命令，$install_msg" >&2
        exit 1
    fi
    echo "$cmd 命令检查通过"
}

# 创建目录
create_dir() {
    local dir=$1
    if [[ ! -d "$dir" ]]; then
        mkdir -p "$dir"
        echo "创建目录: $dir"
    fi
}

# =============================================================================
# 安装函数
# =============================================================================

# 检查系统依赖
check_system_dependencies() {
    echo "检查系统依赖..."

    check_command "java" "请安装 Java"
    check_command "node" "请安装 Node.js"

    # 检查并安装 pnpm
    if ! command -v pnpm &>/dev/null; then
        echo "未检测到 pnpm，正在安装..."
        npm install pnpm@9.0.6 -g
        echo "pnpm 安装完成"
    else
        echo "pnpm 命令检查通过"
    fi

    # 检查并安装 langChain
    check_command "python" "请安装 Python"
    check_command "pip" "请安装 Pip"

    if ! python -c "import langchain_core, langchain_openai" &>/dev/null; then
        echo "未检测到 pip，正在安装..."
        pip install langchain-openai langchain-core
        echo "langChain 安装完成"
    else
        echo "langChain 环境检查通过"
    fi
}

# =============================================================================
# 主要安装流程
# =============================================================================

main() {
    echo "开始安装至数云项目依赖..."

    # 1. 检查系统依赖
    check_system_dependencies

    echo "项目依赖安装完成！"
}

# =============================================================================
# 脚本入口
# =============================================================================

# 切换到脚本所在目录
cd "$BASE_PATH" || {
    echo "无法切换到项目目录: $BASE_PATH" >&2
    exit 1
}

# 执行主函数
main