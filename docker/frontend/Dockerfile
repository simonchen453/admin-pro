# ==================================================
# Stage 1: Build Frontend
# ==================================================
FROM node:20-alpine AS frontend-builder

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

COPY docker/frontend/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

