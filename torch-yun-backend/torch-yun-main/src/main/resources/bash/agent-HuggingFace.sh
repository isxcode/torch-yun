#!/bin/bash

##########################################
# 安装前,检测huggingFace模式是否允许安装
##########################################

# 获取脚本文件当前路径
BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)

# 初始化环境变量
if [[ "$OSTYPE" == "linux-gnu" ]]; then
    source /etc/profile
    source ~/.bashrc
elif [[ "$OSTYPE" == "darwin"* ]]; then
    source /etc/profile
    source ~/.zshrc
else
    json_output="{ \
                      \"status\": \"INSTALL_ERROR\", \
                      \"log\": \"该系统不支持安装\" \
                    }"
      echo $json_output
      rm ${BASE_PATH}/agent-HuggingFace.sh
      exit 0
fi

# 获取外部参数
home_path=""
agent_port=""
for arg in "$@"; do
  case "$arg" in
  --home-path=*) home_path="${arg#*=}" ;;
  --agent-port=*) agent_port="${arg#*=}" ;;
  *) echo "未知参数: $arg" && exit 1 ;;
  esac
done

# 初始化agent_path
agent_path="${home_path}/zhishuyun-agent"

# 判断home_path目录是否存在
if [ ! -d "${agent_path}" ]; then
  mkdir -p "${agent_path}"
fi

# 帮助用户初始化agent-env.sh文件
if [ ! -f "${agent_path}/conf/agent-env.sh" ]; then
  mkdir "${agent_path}/conf"
  touch "${agent_path}/conf/agent-env.sh"
fi

# 导入用户自己配置的环境变量
source "${agent_path}/conf/agent-env.sh"

# 判断当前是否安装代理
if [ -e "${agent_path}/zhishuyun-agent.pid" ]; then
  pid=$(cat "${agent_path}/zhishuyun-agent.pid")
  if ps -p $pid >/dev/null 2>&1; then
    json_output="{ \
            \"status\": \"RUNNING\", \
            \"log\": \"正在运行中\" \
          }"
    echo $json_output
    rm ${BASE_PATH}/agent-HuggingFace.sh
    exit 0
  else
    json_output="{ \
            \"status\": \"STOP\", \
            \"log\": \"已安装，请启动\" \
          }"
    echo $json_output
    rm ${BASE_PATH}/agent-HuggingFace.sh
    exit 0
  fi
fi

# 判断tar解压命令是否存在
if ! command -v tar &>/dev/null; then
  json_output="{ \
        \"status\": \"INSTALL_ERROR\", \
        \"log\": \"未检测到tar命令\" \
      }"
  echo $json_output
  rm ${BASE_PATH}/agent-HuggingFace.sh
  exit 0
fi

# 判断unzip解压命令是否存在
if ! command -v unzip &>/dev/null; then
  json_output="{ \
        \"status\": \"INSTALL_ERROR\", \
        \"log\": \"未检测到unzip命令，建议命令yum install -y unzip\" \
      }"
  echo $json_output
  rm ${BASE_PATH}/agent-HuggingFace.sh
  exit 0
fi

# 判断是否有java命令
if ! command -v java &>/dev/null; then
  # 如果没有java命令,判断用户是否配置了JAVA_HOME环境变量
  if [ ! -n "$JAVA_HOME" ]; then
    json_output="{ \
    \"status\": \"INSTALL_ERROR\", \
    \"log\": \"未检测到java环境,节点请安装java1.8+\" \
    }"
    echo $json_output
    rm ${BASE_PATH}/agent-HuggingFace.sh
    exit 0
  fi
fi

# 检查是否安装了 python3
if ! command -v python3 &>/dev/null; then
    json_output="{ \
    \"status\": \"INSTALL_ERROR\", \
    \"log\": \"未检测到python3环境,节点请安装python3\" \
    }"
    echo $json_output
    rm ${BASE_PATH}/agent-HuggingFace.sh
    exit 0
fi

# 获取 Python 版本号并提取主版本和次版本
python_version=$(python3 -V 2>&1 | awk '{print $2}')
major_version=$(echo $python_version | cut -d '.' -f 1)
minor_version=$(echo $python_version | cut -d '.' -f 2)

# 判断版本号是否小于 3.9
if [[ $major_version -lt 3 || ($major_version -eq 3 && $minor_version -lt 9) ]]; then
    json_output="{ \
    \"status\": \"INSTALL_ERROR\", \
    \"log\": \"当前Python版本为$python_version, 需要安装Python 3.9或更高版本\" \
    }"
    echo $json_output
    rm ${BASE_PATH}/agent-HuggingFace.sh
    exit 0
fi

# 检查是否安装了torch
if ! python3 -c "import torch" &>/dev/null; then
    json_output="{ \
    \"status\": \"INSTALL_ERROR\", \
    \"log\": \"当前Python环境中未安装torch依赖, 请运行 pip3 install torch 安装\" \
    }"
    echo $json_output
    rm ${BASE_PATH}/agent-HuggingFace.sh
    exit 0
fi

# 检查是否安装了torch
if ! python3 -c "import transformers" &>/dev/null; then
    json_output="{ \
    \"status\": \"INSTALL_ERROR\", \
    \"log\": \"当前Python环境中未安装transformers依赖, 请运行 pip3 install 'transformers[torch]' 安装\" \
    }"
    echo $json_output
    rm ${BASE_PATH}/agent-HuggingFace.sh
    exit 0
fi

# 检查是否安装了fastapi
if ! python3 -c "import fastapi" &>/dev/null; then
    json_output="{ \
    \"status\": \"INSTALL_ERROR\", \
    \"log\": \"当前Python环境中未安装fastapi依赖, 请运行 pip3 install fastapi 安装\" \
    }"
    echo $json_output
    rm ${BASE_PATH}/agent-HuggingFace.sh
    exit 0
fi

# 判断uvicorn解压命令是否存在
if ! command -v uvicorn &>/dev/null; then
  json_output="{ \
        \"status\": \"INSTALL_ERROR\", \
        \"log\": \"未检测到uvicorn命令, 请运行 pip3 install uvicorn安装\" \
      }"
  echo $json_output
  rm ${BASE_PATH}/agent-HuggingFace.sh
  exit 0
fi

# 返回可以安装
json_output="{ \
          \"status\": \"CAN_INSTALL\", \
          \"log\": \"允许安装\" \
        }"
echo $json_output

# 删除本地检测脚本
rm ${BASE_PATH}/agent-HuggingFace.sh