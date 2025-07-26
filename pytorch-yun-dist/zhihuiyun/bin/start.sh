#!/bin/bash

# 获取当前路径
BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)
cd "${BASE_PATH}" || exit
cd ".." || exit

print_log="true"
for arg in "$@"; do
  case "$arg" in
    --print-log=*) print_log="${arg#*=}" ;;
    *) echo "未知参数: $arg" && exit 1 ;;
  esac
done

# 导入用户指定环境变量
source "conf/zhishuyun-env.sh"

# 项目已经在运行中
if [ -e "zhishuyun.pid" ]; then
  pid=$(cat "zhishuyun.pid")
  if ps -p $pid >/dev/null 2>&1; then
    echo "【至慧云】: HAS RUNNING"
    exit 0
  fi
fi

# 判断pytorch-yun.log是否存在,不存在则新建
if [ ! -f logs/pytorch-yun.log ]; then
  mkdir logs
  touch logs/pytorch-yun.log
fi

# 运行至慧云程序
if [ -n "$JAVA_HOME" ]; then
  nohup $JAVA_HOME/bin/java -jar -Xmx2048m lib/zhishuyun.jar --spring.profiles.active=local --spring.config.additional-location=conf/ > /dev/null 2>&1 &
else
  nohup java -jar -Xmx2048m lib/zhishuyun.jar --spring.profiles.active=local --spring.config.additional-location=conf/ > /dev/null 2>&1 &
fi
echo $! >zhishuyun.pid

echo "【至慧云】: STARTING"
if [ "$print_log" == "true" ]; then
  tail -f logs/pytorch-yun.log
fi