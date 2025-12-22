# ==================================================
# Stage 1: Build Frontend
# ==================================================
FROM node:20-alpine AS frontend-builder

ARG VITE_BASE_URL
ENV VITE_BASE_URL=$VITE_BASE_URL

RUN corepack enable && corepack prepare pnpm@latest --activate

WORKDIR /app

COPY frontend/package.json frontend/pnpm-lock.yaml ./

RUN pnpm install --frozen-lockfile

COPY frontend/ .

RUN pnpm exec vite build

# ==================================================
# Stage 2: Nginx Runtime
# ==================================================
FROM nginx:alpine

WORKDIR /usr/share/nginx/html

RUN rm -rf /usr/share/nginx/html/*

COPY --from=frontend-builder /app/dist /usr/share/nginx/html

# ==================================================
# Nginx 动态配置机制说明 (Envsubst)
# ==================================================
# 1. 存放位置：将模板文件复制到 /etc/nginx/templates/ 并且必须以 .template 结尾
# 2. 自动替换：Nginx 容器启动时，会自动扫描该目录下的模板文件
# 3. 变量注入：使用 docker-compose 传入的环境变量 (如 APP_BASE_PATH) 替换模板中的变量
# 4. 生成配置：替换后会在 /etc/nginx/conf.d/ 自动生成最终的 default.conf 文件
COPY docker/frontend/nginx.conf.template /etc/nginx/templates/default.conf.template

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

