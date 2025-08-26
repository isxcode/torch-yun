---
title: "Windows源码编译"
---

## Windows系统源码编译

### 1. 安装Docker

- [Docker Desktop 安装参考文档](https://docs.docker.com/desktop/setup/install/windows-install/)

### 2. 打开cmd下载代码

![20250423204034](https://img.isxcode.com/picgo/20250423204034.png)

> 下载源码

```bash
cd C:\Users\ispong\Downloads
git clone https://github.com/isxcode/torch-yun.git
```

### 3. 使用镜像打包源码

> 将${clone_path}替换成项目路径，例如：C:\Users\ispong\Downloads\torch-yun

```bash
docker run --rm ^
  -v ${clone_path}\torch-yun:/torch-yun ^
  -w /torch-yun ^
  -it registry.cn-shanghai.aliyuncs.com/isxcode/zhishuyun-build:amd-latest ^
  /bin/bash -c "source /etc/profile && gradle install clean package"
```

### 4. 解压安装包运行

> 安装包路径：torch-yun\torch-yun-dist\build\distributions\zhishuyun.tar.gz

```bash
cd C:\Users\ispong\Downloads\torch-yun\torch-yun-dist\build\distributions
tar -vzxf zhishuyun.tar.gz
cd zhishuyun/lib
java -jar zhishuyun.jar
```

### 5. 访问系统

- 访问地址: http://localhost:8080 
- 管理员账号：`admin` 
- 管理员密码：`admin123`