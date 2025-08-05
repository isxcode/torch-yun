#!/bin/bash

######################
# 停止脚本
######################

BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)
cd "${BASE_PATH}" || exit
cd ".." || exit

if [ -e "zhishuyun-agent.pid" ]; then
  pid=$(cat "zhishuyun-agent.pid")
  if ps -p $pid >/dev/null 2>&1; then
   kill -9 ${pid}
   rm zhishuyun-agent.pid
   echo "【至数云代理】: CLOSED"
   exit 0
  fi
fi

echo "【至数云代理】: HAS CLOSED"
exit 0