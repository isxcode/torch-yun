# 至数云-超轻量智能应用平台

[![Docker Pulls](https://img.shields.io/docker/pulls/isxcode/zhishuyun)](https://hub.docker.com/r/isxcode/zhishuyun)
[![build](https://github.com/isxcode/torch-yun/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/isxcode/torch-yun/actions/workflows/build.yml)
[![GitHub Repo stars](https://img.shields.io/github/stars/isxcode/torch-yun)](https://github.com/isxcode/torch-yun)
[![GitHub forks](https://img.shields.io/github/forks/isxcode/torch-yun)](https://github.com/isxcode/torch-yun/fork)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fisxcode%2Ftorch-yun.svg?type=shield&issueType=license)](https://app.fossa.com/projects/git%2Bgithub.com%2Fisxcode%2Ftorch-yun?ref=badge_shield&issueType=license)
[![GitHub License](https://img.shields.io/github/license/isxcode/torch-yun)](https://github.com/isxcode/torch-yun/blob/main/LICENSE)

<table>
    <tr>
        <td>产品官网</td>
        <td><a href="https://zhishuyun.isxcode.com">https://zhishuyun.isxcode.com</a></td>
    </tr>
    <tr>
        <td>源码仓库</td>
        <td><a href="https://github.com/isxcode/torch-yun">https://github.com/isxcode/torch-yun</a></td>
    </tr>
    <tr>
        <td>码云仓库</td>
        <td><a href="https://gitee.com/isxcode/torch-yun">https://gitee.com/isxcode/torch-yun</a></td>
    </tr>
    <tr>
        <td>演示环境</td>
        <td><a href="https://zhishuyun-demo.isxcode.com">https://zhishuyun-demo.isxcode.com</a></td>
    </tr>
    <tr>
        <td>产品推荐</td>
        <td><a href="https://zhiqingyun.isxcode.com">[至轻云] - 超轻量智能数据中心</a></td>
    </tr>
    <tr>
        <td>关键词</td>
        <td>大模型训练, 智能中心, 模型调用, 模型编排, 人工智能, Pytorch, Docker, HuggingFace</td>
    </tr>
</table>

### 产品介绍

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;至数云是一款超轻量、企业级人工智能应用平台。一键部署，开箱即用。可快速实现AI应用构建、大模型部署、模型训练、接口开放、可视化流程等场景。兼容通义千问和DeepSeek模型，助力企业拥抱人工智能新时代。

### 功能列表

| 模块   | 功能(包含商业版)           |
|:-----|:--------------------|
| 首页   | AI对话、历史记录、应用列表      |
| 资源管理 | 计算集群、资源中心           |
| AI中心 | 模型广场、模型仓库、智能体、应用管理  |
| 后台管理 | 用户中心、租户列表、租户成员、证书安装 |

### 立即体验

演示地址：https://zhishuyun-demo.isxcode.com </br>
体验账号：邮箱注册 </br>
体验时间：1天 [已禁用，联系管理员] </br>
添加微信：wx_ispong

### 相关文档

- [快速体验](https://zhishuyun.isxcode.com/docs/zh/1/0)
- [产品手册](https://zhishuyun.isxcode.com/docs/zh/2/0)
- [开发手册](https://zhishuyun.isxcode.com/docs/zh/5/1)
- [博客](https://ispong.isxcode.com)

### 快速部署

```bash
# 访问地址：http://localhost:8080
# 管理员账号：admin 
# 管理员密码：admin123
docker run -p 8080:8080 -d isxcode/zhishuyun
```

### 源码构建

- Mac或Linux

```bash
# 安装包路径: /tmp/torch-yun/torch-yun-dist/build/distributions/zhishuyun.tar.gz
cd /tmp
git clone https://github.com/isxcode/torch-yun.git
docker run --rm \
  -v /tmp/torch-yun:/torch-yun \
  -it isxcode/zhishuyun-build \
  gradle package
```

- Windows

```bash
# 安装包路径: C:\Users\isxcode\Downloads\torch-yun\torch-yun-dist\build\distributions\zhishuyun.tar.gz
cd Downloads
git clone https://github.com/isxcode/torch-yun.git
docker run --rm ^
  -v C:\Users\isxcode\Downloads\torch-yun:/torch-yun ^
  -it isxcode/zhishuyun-build ^
  gradle package
```

### 产品展示

<table>
    <tr>
        <td><img src="https://img.isxcode.com/picgo/ty-1.png" alt="登录页" width="400"/></td>
        <td><img src="https://img.isxcode.com/picgo/ty-2.png" alt="首页" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://img.isxcode.com/picgo/ty-9.png" alt="数据源" width="400"/></td>
        <td><img src="https://img.isxcode.com/picgo/ty-3.png" alt="作业流" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://img.isxcode.com/picgo/ty-5.png" alt="pySpark" width="400"/></td>
        <td><img src="https://img.isxcode.com/picgo/ty-4.png" alt="运行结果" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://img.isxcode.com/picgo/ty-6.png" alt="作业配置" width="400"/></td>
        <td><img src="https://img.isxcode.com/picgo/ty-7.png" alt="任务调度" width="400"/></td>
    </tr>
    <tr>
       <td><img src="https://img.isxcode.com/picgo/ty-11.png" alt="数据分层" width="400"/></td>
       <td><img src="https://img.isxcode.com/picgo/ty-10.png" alt="数据地图" width="400"/></td>
    </tr>
</table>
