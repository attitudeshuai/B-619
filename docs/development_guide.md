# 开发指南 (Development Guide)

## 1. 环境准备

在开始开发前，请确保您的环境已安装以下工具：

- **JDK 17+**: 后端开发环境
- **Node.js 18+**: 前端开发环境
- **MySQL 8.0**: 数据库（推荐使用 Docker 安装）
- **Maven 3.8+**: Java 构建工具
- **Docker & Docker Compose**: 容器化环境

## 2. 本地开发 (Local Development)

### 后端启动
1. 进入 backend 目录:
   ```bash
   cd backend
   ```
2. 配置 `src/main/resources/application.yml` 中的数据库连接（如使用本地 MySQL）。
3. 运行应用:
   ```bash
   mvn spring-boot:run
   ``` 

### 前端启动
1. 进入 frontend 目录:
   ```bash
   cd frontend
   ```
2. 安装依赖:
   ```bash
   npm install
   ```
3. 启动开发服务器:
   ```bash
   npm run dev
   ```
   服务将运行在 `http://localhost:3000`.
   > 注意：开发环境下需在 `vite.config.js` 配置代理以转发 API 请求至后端。

## 3. 容器化构建与部署

推荐使用 Docker Compose 进行一体化构建。

```bash
# 在项目根目录执行
docker compose up --build
```
该命令会自动：
1. 构建后端 Maven 项目并打包镜像
2. 构建前端 Vue 项目并打包至 Nginx 镜像
3. 启动 MySQL 容器并初始化数据
4. 编排网络连接

## 4. 常用命令

| 操作 | 命令 |
|------|------|
| **后端打包** | `mvn clean package -DskipTests` |
| **前端构建** | `npm run build` |
| **Docker清理** | `docker compose down -v` (警告：会清除数据库数据) |
| **查看日志** | `docker compose logs -f` |

## 5. 编码规范

- **Java**: 修改 Entity 后通过 `@PrePersist` / `@PreUpdate` 处理审计字段。尽可能使用 `@Transactional` 标注 Service 方法。
- **Vue**: 使用 `<script setup>` 语法糖。API 调用封装在 `src/api` 目录。样式推荐使用 CSS 变量以保持主题统一。
