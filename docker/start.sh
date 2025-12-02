#!/bin/bash

# Docker 部署启动脚本

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查 Docker 是否运行
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}错误: Docker 未运行，请先启动 Docker${NC}"
    exit 1
fi

# 检查 docker-compose 是否安装
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}错误: docker-compose 未安装${NC}"
    exit 1
fi

# 检查 .env 文件（可选）
if [ ! -f .env ]; then
    echo -e "${YELLOW}提示: .env 文件不存在，将使用环境变量默认值${NC}"
    echo -e "${YELLOW}如需自定义配置，请创建 .env 文件${NC}"
fi

# 解析命令行参数
ENV=${1:-prod}

case $ENV in
    prod)
        COMPOSE_FILES="-f docker-compose.yml -f docker-compose.prod.yml"
        echo -e "${GREEN}使用生产环境配置${NC}"
        ;;
    *)
        COMPOSE_FILES="-f docker-compose.yml"
        echo -e "${GREEN}使用基础配置${NC}"
        ;;
esac

# 创建必要的目录
echo -e "${YELLOW}创建必要的目录...${NC}"
mkdir -p logs upload/public upload/private

# 构建并启动服务
echo -e "${YELLOW}构建 Docker 镜像...${NC}"
docker-compose $COMPOSE_FILES build

echo -e "${YELLOW}启动服务...${NC}"
docker-compose $COMPOSE_FILES up -d

# 等待服务启动
echo -e "${YELLOW}等待服务启动...${NC}"
sleep 5

# 检查服务状态
echo -e "${GREEN}服务状态:${NC}"
docker-compose $COMPOSE_FILES ps

echo -e "${GREEN}部署完成！${NC}"
echo -e "${GREEN}前端地址: http://localhost${NC}"
echo -e "${GREEN}后端地址: http://localhost:8080${NC}"
echo -e "${GREEN}查看日志: docker-compose logs -f${NC}"

