.PHONY: help build up down restart logs ps clean

# 默认目标
help:
	@echo "Docker 部署命令:"
	@echo "  make build      - 构建所有镜像"
	@echo "  make up         - 启动所有服务（生产环境）"
	@echo "  make down       - 停止所有服务"
	@echo "  make restart    - 重启所有服务"
	@echo "  make logs       - 查看所有服务日志"
	@echo "  make logs-backend - 查看后端日志"
	@echo "  make logs-frontend - 查看前端日志"
	@echo "  make ps         - 查看服务状态"
	@echo "  make clean      - 清理所有容器和镜像"
	@echo "  make clean-all  - 清理所有容器、镜像和 volumes"

# 构建镜像
build:
	docker-compose build

# 启动服务（生产环境）
up:
	docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# 停止服务
down:
	docker-compose down

# 重启服务
restart:
	docker-compose restart

# 查看日志
logs:
	docker-compose logs -f

logs-backend:
	docker-compose logs -f backend

logs-frontend:
	docker-compose logs -f frontend

# 查看服务状态
ps:
	docker-compose ps

# 清理容器和镜像
clean:
	docker-compose down --rmi all

# 清理所有（包括 volumes）
clean-all:
	docker-compose down -v --rmi all

