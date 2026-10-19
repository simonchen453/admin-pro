.PHONY: help dev dev-backend dev-frontend build up down restart logs ps clean

# 默认目标
help:
	@echo "本地开发命令（需先配置并启动 MySQL）:"
	@echo "  make dev           - 同时启动后端和管理后台（Ctrl+C 停止）"
	@echo "  make dev-backend   - 安装后端模块依赖并启动后端（dev 配置）"
	@echo "  make dev-frontend  - 安装前端依赖并启动管理后台"
	@echo ""
	@echo "Docker 部署命令:"
	@echo "  make build         - 构建所有镜像"
	@echo "  make up            - 启动所有服务（生产环境）"
	@echo "  make up-frontend   - 仅启动前端（生产环境）"
	@echo "  make up-backend    - 仅启动后端（生产环境）"
	@echo "  make up-hybrid     - 启动前端模式（连接到本地 host:8080 后端）"
	@echo "  make down          - 停止所有服务"
	@echo "  make restart       - 重启所有服务"
	@echo "  make logs          - 查看所有服务日志"
	@echo "  make logs-backend  - 查看后端日志"
	@echo "  make logs-frontend - 查看前端日志"
	@echo "  make ps            - 查看服务状态"
	@echo "  make clean         - 清理所有容器和镜像"
	@echo "  make clean-all     - 清理所有容器、镜像和 volumes"

# 本地开发：并行启动后端和管理后台，日志输出到当前终端
dev:
	$(MAKE) -j2 dev-backend dev-frontend

# 先安装多模块依赖，再使用开发配置启动后端
dev-backend:
	mvn -pl adminpro-web -am install -DskipTests
	mvn -pl adminpro-web spring-boot:run -Dspring-boot.run.profiles=dev

# 安装锁文件中的依赖并启动 Vite 开发服务器
dev-frontend:
	cd frontend && pnpm install --frozen-lockfile && pnpm dev

# 构建镜像
build:
	docker-compose build

# 启动服务（生产环境）
up:
	docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# 仅启动前端（生产环境）
up-frontend:
	docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d frontend

# 仅启动后端（生产环境）
up-backend:
	docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d backend

# 混合模式：启动前端容器，连接到本地宿主机后端 (host local:8080)
# 此命令会临时覆盖 BACKEND_HOST 环境变量
up-hybrid:
	BACKEND_HOST=host.docker.internal docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d frontend

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

