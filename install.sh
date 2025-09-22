#!/bin/bash

# =============================================================================
# 至轻云依赖安装脚本
# =============================================================================

set -e  # 遇到错误立即退出

# 配置项
readonly MODEL_FILE="Qwen2.5-0.5B.zip"
readonly OSS_DOWNLOAD_URL="https://zhishuyun-demo.isxcode.com/tools/open/file"

# 路径配置
readonly BASE_PATH=$(cd "$(dirname "$0")" && pwd)
readonly TMP_DIR="${BASE_PATH}/resources/tmp"

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

# 下载文件
download_file() {
    local url=$1
    local output_path=$2
    local description=$3

    if [[ -f "$output_path" ]]; then
        echo "$description 已存在，跳过下载"
        return 0
    fi

    echo "开始下载 $description..."
    if curl -ssL "$url" -o "$output_path"; then
      if head -n 1 "$output_path" | grep -q "<?xml"; then
        if grep -q "<Error>" "$output_path" && grep -q "<Code>NoSuchKey</Code>" "$output_path"; then
            rm -rf "$output_path"
            echo "下载失败，请联系管理员: ispong@outlook.com" >&2
            exit 1
        fi
      fi
      echo "$description 下载成功"
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
    check_command "python" "请安装 Python"
    check_command "pip" "请安装 Pip"

    # 检查并安装 pnpm
    if ! command -v pnpm &>/dev/null; then
        echo "未检测到 pnpm，正在安装..."
        npm install pnpm@9.0.6 -g
        echo "pnpm 安装完成"
    else
        echo "pnpm 命令检查通过"
    fi

    # 检查并安装 langChain
    if ! python -c "import langchain_core, langchain_openai" &>/dev/null; then
        echo "未检测到 pnpm，正在安装..."
        pip install langchain-openai langchain-core
        echo "langChain 安装完成"
    else
        echo "langChain 命令检查通过"
    fi
}

# 下载模型
install_model() {
    echo "安装 模型 ${MODEL_FILE}..."

    # 创建必要目录
    create_dir "$TMP_DIR"

    # 下载 Spark
    local model_url="${OSS_DOWNLOAD_URL}/${MODEL_FILE}"
    local model_path="${TMP_DIR}/${MODEL_FILE}"
    download_file "$model_url" "$model_path" "大模型文件 ${MODEL_FILE} 二进制文件，请耐心等待"
}


# 安装项目依赖
install_project_dependencies() {
    echo "安装项目依赖..."

    # 创建项目依赖目录
    create_dir "$LIBS_DIR"

    # 下载项目 JAR 依赖
    for jar in "${PROJECT_JARS[@]}"; do
        local jar_url="${OSS_DOWNLOAD_URL}/${jar}"
        local jar_path="${LIBS_DIR}/${jar}"
        download_file "$jar_url" "$jar_path" "项目依赖: $jar"
    done
}

# =============================================================================
# 主要安装流程
# =============================================================================

main() {
    echo "开始安装至轻云项目依赖..."

    # 1. 检查系统依赖
    check_system_dependencies

    # 2. 安装模型
    install_model

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