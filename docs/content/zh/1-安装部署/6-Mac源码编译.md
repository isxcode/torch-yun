---
title: "Mac源码编译"
---

## Mac系统源码编译

### 1. 安装并启动Docker

```bash
brew install docker --cask
```

### 2. 打开终端下载代码

![20250428145831](https://img.isxcode.com/picgo/20250428145831.png)

> 下载源码

```bash
cd ~/Downloads
git clone https://github.com/isxcode/torch-yun.git
```

### 3. 使用镜像打包源码

> 将${clone_path}替换成项目路径，例如：/Users/ispong/Downloads/torch-yun
> M系列架构，使用Arm镜像 `zhishuyun-build:arm-latest`

```bash
docker run --rm \
  -v ${clone_path}/torch-yun:/torch-yun \
  -w /torch-yun -it registry.cn-shanghai.aliyuncs.com/isxcode/zhishuyun-build:arm-latest \
  /bin/bash -c "source /etc/profile && gradle install clean package"
```

### 4. 解压安装包运行

> 安装包路径：torch-yun/torch-yun-dist/build/distributions/zhishuyun.tar.gz

```bash
cd /Users/ispong/Downloads/torch-yun/torch-yun-dist/build/distributions
tar -vzxf zhishuyun.tar.gz
cd zhishuyun/lib
java -jar zhishuyun.jar
```

### 5. 访问系统

- 访问地址: http://localhost:8080 
- 管理员账号：`admin` 
- 管理员密码：`admin123`