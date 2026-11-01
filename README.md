# 至数云-轻量级智能应用平台

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
        <td>托管平台</td>
        <td><a href="https://zhishuyun-saas.isxcode.com">https://zhishuyun-saas.isxcode.com</a></td>
    </tr>
    <tr>
        <td>GitHub</td>
        <td><a href="https://github.com/isxcode/torch-yun">https://github.com/isxcode/torch-yun</a></td>
    </tr>
    <tr>
        <td>Gitee</td>
        <td><a href="https://gitee.com/isxcode/torch-yun">https://gitee.com/isxcode/torch-yun</a></td>
    </tr>
    <tr>
        <td>友情推荐</td>
        <td><a href="https://zhiqingyun.isxcode.com">[至轻云] - 轻量级智能数据中心</a></td>
    </tr>
    <tr>
        <td>关键词</td>
        <td>大模型训练, 智能中心, 模型调用, 模型编排, 人工智能, Pytorch, Docker, HuggingFace</td>
    </tr>
</table>

### 产品介绍

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;至数云是一款企业级、智能化应用平台。一键部署，开箱即用。可快速实现智能集成、应用构建、流程配置、应用发布、应用监控、应用运维、应用安全、应用分析等功能，助力企业构建新一代智慧应用平台。


### 功能列表「开源免费」

| 模块     | 功能                                                       |
| :------- | :--------------------------------------------------------- |
| 资源管理 | 计算集群、资源中心                       |
| 智能应用 | AI对话、历史记录、应用列表      |
| 后台管理 | 租户成员、角色管理                                         |
| 平台管理 | 用户中心、租户管理、登录方式、登录日志、行为日志、平台授权 |
| 个人中心 | 基础信息、修改密码、修改手机、修改邮箱、修改语言           |

### 功能列表「企业收费」

| 模块     | 功能                                                                                               |
| :------- | :------------------------------------------------------------------------------------------------- |
| 至数智能 | 智能应用、提示词管理、MCP开放                                                  |
| 资源管理 | 计算集群、数据来源、文件资源                                                                       |
| 业务规范 | 业务术语、字典代码、字段标准、数据标准、数据分级                                                   |
| 项目管理 | 项目列表、项目成员、项目资源                                                                       |
| 扩展中心 | 组件中心、组件审核、插件中心、插件审核、连接管理                                                   |
| 应用管理 | 应用列表、应用模板、使用统计                                                                       |
| 应用设计 | 数据模型、页面设计、菜单设计、流程设计、业务规则                                                   |
| 应用安全 | 应用用户、应用角色、应用组织、权限配置、数据权限、字段权限、数据脱敏                               |
| 应用集成 | 外部接口、应用接口、接口凭据、访问授权、流量控制、集成记录                                         |
| 发布管理 | 环境管理、应用发布、版本管理、发布审批、发布回滚、变更记录                                         |
| 运维监控 | 运行监控、流程实例、流程任务、异常处理、告警管理、运行日志、登录日志、操作日志 |
| 数据维护 | 数据管理、导入导出                                                                                 |
| 应用分析 | 图表分析、报表分析                                                                                 |
| 后台管理 | 成员管理、角色管理、组织架构、通知配置、智能配置、后台设置                                         |
| 平台管理 | 用户中心、租户管理、免密登录、登录方式、登录日志、行为日志、平台授权、平台设置                     |
| 个人中心 | 基础信息、修改密码、修改手机、修改邮箱、安全中心、偏好设置、消息中心                               |

### 托管平台

平台地址：https://zhishuyun-saas.isxcode.com </br>
账号注册：登录方式 > 短信登录 「添加咨询微信，申请审批」</br>
咨询微信：fanZqyccc

### 快速部署

```bash
# 访问地址：http://localhost:8080
# 管理员账号：admin 
# 管理员密码：admin123
docker run -p 8080:8080 -d isxcode/zhishuyun
```

### 源码构建

- Mac或Linux「terminal中执行」

```bash
# 安装包路径: /tmp/torch-yun/torch-yun-dist/build/distributions/zhishuyun.tar.gz
cd /tmp
git clone https://github.com/isxcode/torch-yun.git
docker run --rm \
  -v /tmp/torch-yun:/torch-yun \
  -it isxcode/zhishuyun-build \
  gradle package
```

- Windows「CMD中执行」

```bash
# 安装包路径: C:\Users\isxcode\Downloads\torch-yun\torch-yun-dist\build\distributions\zhishuyun.tar.gz
cd Downloads
git clone https://github.com/isxcode/torch-yun.git
docker run --rm ^
  -v C:\Users\isxcode\Downloads\torch-yun:/torch-yun ^
  -it isxcode/zhishuyun-build ^
  gradle package
```

### 相关文档

- [快速体验](https://zhishuyun.isxcode.com/zh/docs/1/0)
- [产品手册](https://zhishuyun.isxcode.com/zh/docs/2/0)
- [开发手册](https://zhishuyun.isxcode.com/zh/docs/2/1)
- [博客](https://ispong.isxcode.com/tags/)

### 产品展示

<table>
    <tr>
        <td><img src="https://zhishuyun-saas.isxcode.com/tools/open/file/web-product-5.png" alt="登录页" width="400"/></td>
        <td><img src="https://zhishuyun-saas.isxcode.com/tools/open/file/web-product-6.png" alt="首页" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://zhishuyun-saas.isxcode.com/tools/open/file/web-product-7.png" alt="数据源" width="400"/></td>
        <td><img src="https://zhishuyun-saas.isxcode.com/tools/open/file/web-product-8.png" alt="作业流" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://zhishuyun-saas.isxcode.com/tools/open/file/web-product-9.png" alt="pySpark" width="400"/></td>
        <td><img src="https://zhishuyun-saas.isxcode.com/tools/open/file/web-product-10.png" alt="运行结果" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://zhishuyun-saas.isxcode.com/tools/open/file/web-product-11.png" alt="作业配置" width="400"/></td>
        <td><img src="https://zhishuyun-saas.isxcode.com/tools/open/file/web-product-12.png" alt="任务调度" width="400"/></td>
    </tr>
    <tr>
        <td><img src="https://zhishuyun-saas.isxcode.com/tools/open/file/web-product-13.png" alt="数据分层" width="400"/></td>
        <td><img src="https://zhishuyun-saas.isxcode.com/tools/open/file/web-product-14.png" alt="数据地图" width="400"/></td>
    </tr>
</table>
