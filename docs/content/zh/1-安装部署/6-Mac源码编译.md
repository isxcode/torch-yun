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

#### 编译加速

> 至数云使用gradle构建，可以开启gradle的多并发调试配置

```bash
vim gradle.properties
```

```properties
# 打包进程参数
org.gradle.jvmargs=-Xmx4096m -Xms1024m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8

# 改成true，默认关闭并发
org.gradle.parallel=true

# 开启2并发
org.gradle.parallel.threads=2

# 最大可用8个并发
org.gradle.workers.max=8
```

### 6. 本地完全体代码编译

> 邮箱中检查压缩包

- torch-yun-main.zip
- torch-yun-vip-main.zip
- resources.zip
- license.lic

```bash
cd ~/Downloads/
unzip torch-yun-main.zip
mv torch-yun-main torch-yun
unzip torch-yun-vip-main.zip
mv torch-yun-vip-main ./torch-yun/torch-yun-vip
unzip resources.zip
mv resources ./torch-yun
cd torch-yun
gradle install package
# 启动项目
gradle start
```