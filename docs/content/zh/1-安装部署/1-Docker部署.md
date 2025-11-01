---
title: "Docker部署"
---

## 使用Docker部署至数云

### 镜像选择

##### Docker Hub官方镜像仓库

```bash
docker run -p 8080:8080 isxcode/zhishuyun
```

##### 阿里云镜像仓库(国内用户推荐）

```bash
registry.cn-shanghai.aliyuncs.com/isxcode/zhishuyun:latest-amd64
registry.cn-shanghai.aliyuncs.com/isxcode/zhishuyun:latest-arm64
```

**架构说明：**

- `latest-amd64`: 适用于 x86_64 架构服务器（常见的 Intel/AMD 处理器）
- `latest-arm64`: 适用于 ARM64 架构服务器（如 Apple M1/M2 芯片的 macOS）

### 快速启动

```bash
docker run \
    --restart=always \
    --name zhishuyun \
    -e ADMIN_PASSWORD=admin1234 \
    -p 8088:8080 \
    -d registry.cn-shanghai.aliyuncs.com/isxcode/zhishuyun:latest-amd64
```

### 启动成后访问

**访问地址**: http://localhost:8088  
**管理员账号**: `admin`  
**管理员密码**: `admin1234`

> ⚠️ **安全提示**: 生产环境请务必修改默认密码！

### 变量配置

| 变量名            | 说明           | 默认值      | 示例                           |
|:---------------|:-------------|:---------|:-----------------------------|
| ADMIN_PASSWORD | 管理员初始密码      | admin123 | admin1234                    |
| LOG_LEVEL      | 日志级别         | info     | info, debug, warn            |
| ACTIVE_ENV     | 环境配置文件       | docker   | dev, prod                    |
| PARAMS         | SpringBoot参数 | -        | --spring.flyway.enabled=true |

> 📝 **注意**: `ADMIN_PASSWORD` 仅在首次启动时生效，密码会保存到数据库中。如需修改密码，请在系统中操作或清空数据库重新初始化。

### 数据持久化

> 创建本地目录

```bash
mkdir -p /data/zhishuyun/data
mkdir -p /data/zhishuyun/conf
```

### 配置文件下载

```bash
# 下载默认配置文件
curl -o /data/zhishuyun/conf/application-docker.yml \
  https://raw.githubusercontent.com/isxcode/torch-yun/refs/heads/main/torch-yun-backend/torch-yun-main/src/main/resources/application-docker.yml
```

### 挂载说明

| 容器路径                   | 说明              | 推荐挂载                    |
|------------------------|-----------------|-------------------------|
| `/var/lib/zhishuyun`  | 数据存储目录（数据库、文件等） | `/data/zhishuyun/data` |
| `/etc/zhishuyun/conf` | 配置文件目录          | `/data/zhishuyun/conf` |

### 参考命令

```bash
docker run \
    --restart=always \
    --name zhishuyun \
    -e ADMIN_PASSWORD=admin123 \
    -e LOG_LEVEL=info \
    -e ACTIVE_ENV=docker \
    -e PARAMS="--spring.flyway.enabled=true" \
    -v /data/zhishuyun/data:/var/lib/zhishuyun \
    -v /data/zhishuyun/conf:/etc/zhishuyun/conf \
    -p 8088:8080 \
    -d registry.cn-shanghai.aliyuncs.com/isxcode/zhishuyun:latest-amd64
```

### 查看容器状态

```bash
# 查看容器运行状态
docker ps -a | grep zhishuyun

# 查看容器日志
docker logs -f zhishuyun

# 停止容器
docker stop zhishuyun

# 重启容器
docker restart zhishuyun

# 停止并删除旧容器和镜像
docker stop zhishuyun && docker rm zhishuyun
docker tag registry.cn-shanghai.aliyuncs.com/isxcode/zhishuyun:latest-amd64 registry.cn-shanghai.aliyuncs.com/isxcode/zhishuyun:latest-amd64-bak-20250728 
docker rmi registry.cn-shanghai.aliyuncs.com/isxcode/zhishuyun:latest-amd64

# 拉取最新镜像
docker pull registry.cn-shanghai.aliyuncs.com/isxcode/zhishuyun:latest-amd64
```

### Docker arthas调试

```bash
# 下载arthas
cd /data/zhishuyun/data
wget https://arthas.aliyun.com/download/latest_version?mirror=aliyun -O arthas.zip
unzip arthas.zip

# 进入容器启动arthas
docker exec -it zhishuyun bash
cd /var/lib/zhishuyun
java -jar arthas-boot.jar
```