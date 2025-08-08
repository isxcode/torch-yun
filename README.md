# 至数云-超轻量级人工智能应用平台

[![Docker Pulls](https://img.shields.io/docker/pulls/isxcode/zhishuyun)](https://hub.docker.com/r/isxcode/zhishuyun)
[![build](https://github.com/isxcode/torch-yun/actions/workflows/build-app.yml/badge.svg?branch=main)](https://github.com/isxcode/torch-yun/actions/workflows/build-app.yml)
[![GitHub Repo stars](https://img.shields.io/github/stars/isxcode/torch-yun)](https://github.com/isxcode/torch-yun)
[![GitHub forks](https://img.shields.io/github/forks/isxcode/torch-yun)](https://github.com/isxcode/torch-yun/fork)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fisxcode%2Ftorch-yun.svg?type=shield&issueType=license)](https://app.fossa.com/projects/git%2Bgithub.com%2Fisxcode%2Ftorch-yun?ref=badge_shield&issueType=license)
[![GitHub License](https://img.shields.io/github/license/isxcode/torch-yun)](https://github.com/isxcode/torch-yun/blob/main/LICENSE)

|        |                                                                     |
|--------|---------------------------------------------------------------------|
| 产品官网:  | https://zhishuyun.isxcode.com                                       |
| 源码仓库:  | https://github.com/isxcode/torch-yun                                |
| 演示环境:  | https://zhishuyun-demo.isxcode.com                                  |
| 部署文档:  | https://zhishuyun.isxcode.com/zh/docs/zh/1/2                        |
| 安装包下载: | https://zhishuyun-demo.isxcode.com/tools/open/file/zhishuyun.tar.gz |
| 许可证下载: | https://zhishuyun-demo.isxcode.com/tools/open/file/license.lic      |
| 友情链接:  | [至轻云 - 超轻量级智能化大数据中心](https://zhiqingyun.isxcode.com)                |
| 关键词:   | 大模型训练, 智能中心, 模型调用, 模型编排, 人工智能, torch, torch, Docker                 |
|        |                                                                     |

### 产品介绍

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;至数云是一款超轻量级、超轻量级人工智能应用平台，基于torch生态打造。一键部署，开箱即用。快速实现大模型离线部署、离线训练、模型调用、机器人代理、模型编排、自定义接口等多种功能，为企业提供高效便捷的大模型解决方案。

### 功能特点

- **轻量级产品**: 无需安装额外组件，一键部署，开箱即用。
- **云原生私有化**: 兼容云原生架构，支持多节点安装与高可用集群部署。

### 立即体验

> [!TIP]
> 演示地址：https://zhishuyun-demo.isxcode.com </br>
> 体验账号：zhiyao </br>
> 账号密码：zhiyao123

### 快速部署

> [!NOTE]
> 访问地址：http://localhost:8080 <br/>
> 管理员账号：admin <br/>
> 管理员密码：admin123

```bash
docker run -p 8080:8080 -d isxcode/zhishuyun
```

### 相关文档

- [快速入门](https://zhishuyun.isxcode.com/zh/docs/zh/1/0)
- [产品手册](https://zhishuyun.isxcode.com/zh/docs/zh/2/0)
- [开发手册](https://zhishuyun.isxcode.com/zh/docs/zh/5/1)
- [博客](https://ispong.isxcode.com/tags/pytorch/)

### 源码构建

> [!IMPORTANT]
> 安装包路径: /tmp/torch-yun/torch-yun-dist/build/distributions/zhishuyun.tar.gz

```bash
cd /tmp
git clone https://github.com/isxcode/torch-yun.git
docker run --rm \
  -v /tmp/torch-yun:/torch-yun \
  -w /torch-yun \
  -it registry.cn-shanghai.aliyuncs.com/isxcode/zhishuyun-build:amd-latest \
  /bin/bash -c "source /etc/profile && gradle install clean package"
```
