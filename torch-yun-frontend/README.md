#### 构建指定分支环境

- https://github.com/isxcode/torch-yun/actions/workflows/deploy-dev.yml

#### 远程环境配置

```bash
code .env.dev
```

```bash
VITE_VUE_APP_BASE_DOMAIN=http://localhost:8080
#替换
VITE_VUE_APP_BASE_DOMAIN=http://47.116.172.217:8080
```

#### 启动项目

```bash
pnpm run dev
```

访问 http://localhost:5173