#!/bin/bash

##############################
# 环境检测通过,开始执行安装
##############################

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
      rm ${BASE_PATH}/agent-install.sh
      exit 0
fi

# 获取外部参数
home_path=""
agent_port=""
spark_local="false"
for arg in "$@"; do
  case "$arg" in
  --home-path=*) home_path="${arg#*=}" ;;
  --agent-port=*) agent_port="${arg#*=}" ;;
  --spark-local=*) spark_local="${arg#*=}" ;;
  *) echo "未知参数: $arg" && exit 1 ;;
  esac
done

# 初始化agent_path
agent_path="${home_path}/zhishuyun-agent"

# 导入用户自己配置的环境变量
source ${agent_path}/conf/agent-env.sh

# 将文件解压到指定目录
tar -xf ${BASE_PATH}/zhishuyun-agent.tar.gz -C ${home_path} > /dev/null

# 将spark-min的文件夹改成755权限
chmod -R 755 ${agent_path}/spark-min

# 进入代理目录,防止logs文件夹生成错位
cd ${agent_path}

# 判断zhishuyun-agent.log是否存在,不存在则新建
if [ ! -f "${agent_path}/logs/zhishuyun-agent.log" ]; then
  mkdir "${agent_path}/logs"
  touch "${agent_path}/logs/zhishuyun-agent.log"
fi

# 运行代理程序
if ! command -v java &>/dev/null; then
  nohup $JAVA_HOME/bin/java -jar -Xmx2048m ${agent_path}/lib/zhishuyun-agent.jar --server.port=${agent_port} --spring.config.additional-location=${agent_path}/conf/ > /dev/null 2>&1 &
else
  nohup java -jar -Xmx2048m ${agent_path}/lib/zhishuyun-agent.jar --server.port=${agent_port} --spring.config.additional-location=${agent_path}/conf/ > /dev/null 2>&1 &
fi
echo $! >${agent_path}/zhishuyun-agent.pid

# 检查是否安装
if [ -e "${agent_path}/zhishuyun-agent.pid" ]; then
  pid=$(cat "${agent_path}/zhishuyun-agent.pid")
  sleep 10
  if ps -p $pid >/dev/null 2>&1; then
    json_output="{ \
              \"status\": \"RUNNING\",\
              \"log\": \"安装成功\" \
            }"
    echo $json_output
  else
    json_output="{ \
              \"status\": \"STOP\",\
              \"log\": \"已安装，请激活\" \
            }"
    echo $json_output
  fi
fi

# 删除安装包 和 安装脚本
rm ${BASE_PATH}/zhishuyun-agent.tar.gz && rm ${BASE_PATH}/agent-install.sh