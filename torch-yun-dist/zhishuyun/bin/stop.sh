#!/bin/bash

# 获取当前路径
BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)
cd "${BASE_PATH}" || exit
cd ".." || exit

# 关闭进程
if [ -e "zhishuyun.pid" ]; then
  pid=$(cat "zhishuyun.pid")
  if ps -p $pid >/dev/null 2>&1; then
   kill -9 ${pid}
   rm zhishuyun.pid
   echo "【至数云】: CLOSED"
   exit 0
  fi
fi

echo "【至数云】: HAS CLOSED"
exit 0