# 系统架构文档 (System Architecture)

## 1. 架构概览

酒店管理系统采用经典的前后端分离架构，基于 Docker 容器化部署。系统主要由客户端（浏览器）、反向代理（Nginx）、前端应用（Vue 3）、后端服务（Spring Boot）和数据存储（MySQL）组成。

```mermaid
graph TD
    User[用户 (Browser)] -->|HTTP/HTTPS| Nginx[Nginx 反向代理]
    
    subgraph Docker Host
        Nginx -->|Static Assets| Frontend[Frontend (Vue 3)]
        Nginx -->|/api/*| Backend[Backend (Spring Boot 3.2)]
        
        Backend -->|JDBC| MySQL[(MySQL 8.0)]
    end
```

## 2. 技术栈 (Technology Stack)

### 前端 (Frontend)
- **核心框架**: Vue 3 (Composition API)
- **构建工具**: Vite 5
- **UI 组件库**: Element Plus
- **状态管理**: Pinia
- **路由管理**: Vue Router 4
- **HTTP 客户端**: Axios
- **CSS 预处理**: Vanilla CSS (with CSS Variables)

### 后端 (Backend)
- **核心框架**: Spring Boot 3.2
- **ORM 框架**: Spring Data JPA (Hibernate)
- **数据库连接池**: HikariCP
- **安全认证**: JWT (JSON Web Token)
- **工具库**: Lombok, HuTool (可选)
- **构建工具**: Maven

### 数据存储 (Datebase)
- **数据库**: MySQL 8.0
- **字符集**: utf8mb4

### 基础设施 (Infrastructure)
- **容器化**: Docker Engine
- **编排**: Docker Compose
- **Web 服务器**: Nginx (Alpine)
- **运行环境**: Eclipse Temurin OpenJDK 17 (Alpine), Node 20 (Alpine)

## 3. 部署架构

系统设计为云原生应用，支持一键容器化部署。

| 服务名称 | 容器名 | 端口映射 | 说明 |
|----------|--------|----------|------|
| frontend | hotel-frontend | 3000:80 | Nginx 托管静态资源，反向代理 API |
| backend | hotel-backend | 8080:8080 | Spring Boot 应用服务 |
| db | hotel-mysql | 3306:3306 | MySQL 数据库服务 |

* **网络**: 所有服务运行在 `hotel-network` 桥接网络中。
* **存储**: MySQL 数据持久化挂载于 `mysql_data` 卷。
