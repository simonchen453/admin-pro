#!/bin/sh
set -e

# 如果没有设置 APP_BASE_PATH，默认使用 /adminpro/
APP_BASE_PATH=${APP_BASE_PATH:-/adminpro/}

# 确保路径以 / 结尾和开头
case "$APP_BASE_PATH" in
  */) ;;
  *) APP_BASE_PATH="$APP_BASE_PATH/" ;;
esac
case "$APP_BASE_PATH" in
  /*) ;;
  *) APP_BASE_PATH="/$APP_BASE_PATH" ;;
esac

echo "Applying runtime base path: $APP_BASE_PATH"

# 1. 替换 HTML 中的 base 路径引用和 JS/CSS 引用
# 我们在构建时使用了 __VITE_BASE_URL_PLACEHOLDER__ 作为占位符
# 使用 sed 遍历所有 html, js, css 文件进行替换

find /usr/share/nginx/html -type f \( -name '*.html' -o -name '*.js' -o -name '*.css' \) -exec sed -i "s|__VITE_BASE_URL_PLACEHOLDER__|$APP_BASE_PATH|g" {} +

echo "Runtime injection complete."

# 2. 启动 Nginx (使用原来的 entrypoint 逻辑来处理 nginx.conf.template)
# 注意：docker-entrypoint.sh 是 Nginx 官方镜像的入口脚本，负责 envsubst
/docker-entrypoint.sh "$@"
