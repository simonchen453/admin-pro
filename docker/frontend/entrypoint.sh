#!/bin/sh
set -e

# 如果没有设置 APP_BASE_PATH，默认使用 /adminpro
APP_BASE_PATH=${APP_BASE_PATH:-/adminpro}

# 1. 处理 APP_BASE_PATH (供 Nginx 使用): 确保以 / 开头，且除非是根路径 /，否则移除尾部斜杠
case "$APP_BASE_PATH" in
  /*) ;;
  *) APP_BASE_PATH="/$APP_BASE_PATH" ;;
esac

if [ "$APP_BASE_PATH" != "/" ] && [ "${APP_BASE_PATH##*/}" = "" ]; then
    # 如果不是根路径且以 / 结尾，去掉尾部 /
    APP_BASE_PATH=${APP_BASE_PATH%/}
fi

# 2. 生成 VITE_BASE (供前端静态资源替换使用)
# 我们的目标是替换 vite.config.ts 中定义的 '/__VITE_BASE_URL_PLACEHOLDER__/'
if [ "$APP_BASE_PATH" = "/" ]; then
  VITE_BASE="/"
else
  VITE_BASE="$APP_BASE_PATH/"
fi

echo "Applying runtime base path: Nginx=$APP_BASE_PATH, Vite=$VITE_BASE"

# 3. 替换 HTML 中的 base 路径引用
# Vite 配置中使用了 '/__VITE_BASE_URL_PLACEHOLDER__/'，所以我们替换这个精确的字符串
# 注意：Vite 构建后的源码中会出现 src="/__VITE_BASE_URL_PLACEHOLDER__/assets/..."
find /usr/share/nginx/html -type f \( -name '*.html' -o -name '*.js' -o -name '*.css' \) -exec sed -i "s|/__VITE_BASE_URL_PLACEHOLDER__/|$VITE_BASE|g" {} +

# 4. 动态生成 config.js，注入运行时环境变量
API_BASE_URL=${API_BASE_URL:-/api}

cat > /usr/share/nginx/html/config.js << EOF
// 运行时配置文件，Docker部署时可挂载此文件以覆盖配置
// 在index.html中引用: <script src="/config.js"></script>
window._env_ = {
  API_BASE_URL: '${API_BASE_URL}',
};
EOF

echo "Runtime injection complete. API_BASE_URL=${API_BASE_URL}"

# 5. 导出环境变量供 Nginx 使用
# docker-entrypoint.sh 会使用 envsubst 处理 nginx.conf.template
export API_BASE_URL

# 6. 启动 Nginx (使用原来的 entrypoint 逻辑来处理 nginx.conf.template)
# 注意：docker-entrypoint.sh 是 Nginx 官方镜像的入口脚本，负责 envsubst
/docker-entrypoint.sh "$@"
