#!/bin/bash

echo "开始安装"

# 检查tar命令
if ! command -v tar &>/dev/null; then
  echo "【安装结果】：未检测到tar命令,请安装tar"
  exit 1
fi

# 检查java命令
if ! command -v java &>/dev/null; then
  echo "【安装结果】：未检测到java命令，请安装java"
  exit 1
fi

# 检查node命令
if ! command -v node &>/dev/null; then
  echo "【安装结果】：未检测到node命令，请安装nodejs"
  exit 1
fi

# 检查pnpm命令
if ! command -v pnpm &>/dev/null; then
   echo "【提示】：未检测到pnpm命令，已经执行命令: npm install pnpm@9.0.6 -g"
   npm install pnpm@9.0.6 -g
fi

# 进入项目目录
BASE_PATH=$(cd "$(dirname "$0")" || exit ; pwd)
cd "${BASE_PATH}" || exit

OSS_DOWNLOAD_URL=https://isxcode.oss-cn-shanghai.aliyuncs.com/zhishuyun/install

# 创建系统驱动目录
JDBC_DIR="${BASE_PATH}"/resources/jdbc/system
if [ ! -d "${JDBC_DIR}" ]; then
    mkdir -p "${JDBC_DIR}"
fi

# 下载数据库驱动文件
if [ ! -f "${JDBC_DIR}"/mysql-connector-j-8.1.0.jar ]; then
  echo "mysql-connector-j-8.1.0.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/mysql-connector-j-8.1.0.jar -o ${JDBC_DIR}/mysql-connector-j-8.1.0.jar
  echo "mysql-connector-j-8.1.0.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/postgresql-42.6.0.jar ]; then
  echo "postgresql-42.6.0.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/postgresql-42.6.0.jar -o ${JDBC_DIR}/postgresql-42.6.0.jar
  echo "postgresql-42.6.0.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/Dm8JdbcDriver18-8.1.1.49.jar ]; then
  echo "Dm8JdbcDriver18-8.1.1.49.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/Dm8JdbcDriver18-8.1.1.49.jar -o ${JDBC_DIR}/Dm8JdbcDriver18-8.1.1.49.jar
  echo "Dm8JdbcDriver18-8.1.1.49.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/clickhouse-jdbc-0.5.0.jar ]; then
  echo "clickhouse-jdbc-0.5.0.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/clickhouse-jdbc-0.5.0.jar -o ${JDBC_DIR}/clickhouse-jdbc-0.5.0.jar
  echo "clickhouse-jdbc-0.5.0.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/ngdbc-2.18.13.jar ]; then
  echo "ngdbc-2.18.13.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/ngdbc-2.18.13.jar -o ${JDBC_DIR}/ngdbc-2.18.13.jar
  echo "ngdbc-2.18.13.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/mysql-connector-java-5.1.49.jar ]; then
  echo "mysql-connector-java-5.1.49.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/mysql-connector-java-5.1.49.jar -o ${JDBC_DIR}/mysql-connector-java-5.1.49.jar
  echo "mysql-connector-java-5.1.49.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/mssql-jdbc-12.4.2.jre8.jar ]; then
  echo "mssql-jdbc-12.4.2.jre8.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/mssql-jdbc-12.4.2.jre8.jar -o ${JDBC_DIR}/mssql-jdbc-12.4.2.jre8.jar
  echo "mssql-jdbc-12.4.2.jre8.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/hive-jdbc-3.1.3-standalone.jar ]; then
  echo "hive-jdbc-3.1.3-standalone.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/hive-jdbc-3.1.3-standalone.jar -o ${JDBC_DIR}/hive-jdbc-3.1.3-standalone.jar
  echo "hive-jdbc-3.1.3-standalone.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/hive-jdbc-uber-2.6.3.0-235.jar ]; then
  echo "hive-jdbc-2.1.1-standalone.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/hive-jdbc-uber-2.6.3.0-235.jar -o ${JDBC_DIR}/hive-jdbc-uber-2.6.3.0-235.jar
  echo "hive-jdbc-2.1.1-standalone.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/ojdbc8-19.23.0.0.jar ]; then
  echo "ojdbc8-19.23.0.0.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/ojdbc8-19.23.0.0.jar -o ${JDBC_DIR}/ojdbc8-19.23.0.0.jar
  echo "ojdbc8-19.23.0.0.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/oceanbase-client-2.4.6.jar ]; then
  echo "oceanbase-client-2.4.6.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/oceanbase-client-2.4.6.jar -o ${JDBC_DIR}/oceanbase-client-2.4.6.jar
  echo "oceanbase-client-2.4.6.jar驱动下载成功"
fi
if [ ! -f "${JDBC_DIR}"/jcc-11.5.8.0.jar ]; then
  echo "jcc-11.5.8.0.jar驱动开始下载"
  curl -ssL "${OSS_DOWNLOAD_URL}"/jcc-11.5.8.0.jar -o ${JDBC_DIR}/jcc-11.5.8.0.jar
  echo "jcc-11.5.8.0.jar驱动下载成功"
fi

# 返回状态
echo "【安装结果】：安装成功"