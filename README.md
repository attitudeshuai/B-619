# 酒店管理系统

现代化酒店运营管理平台，基于 Spring Boot 3.2 + Vue 3 + MySQL 8.0 构建。

![Version](https://img.shields.io/badge/version-1.0.0-blue) ![Docker](https://img.shields.io/badge/docker-ready-green) ![License](https://img.shields.io/badge/license-MIT-orange)

## 📚 核心文档 (Documentation)

项目包含完善的工程文档，请根据角色查阅：

| 文档名称 | 目标受众 | 内容简介 |
|----------|----------|----------|
| [📖 用户手册](docs/user_manual.md) | 最终用户 | 系统功能操作指南、常见业务流程 |
| [🏗️ 架构文档](docs/architecture.md) | 架构师/开发者 | 系统架构图、技术栈、容器化设计 |
| [🎨 设计文档](docs/design_doc.md) | 开发者 | 数据库 ER 图、API 设计接口定义 |
| [💻 开发指南](docs/development_guide.md) | 贡献者 | 环境配置、本地启动、代码规范 |
| [🧪 测试指南](docs/testing_guide.md) | 测试人员 | 自动化测试、手动验证清单 |

## 🚀 快速启动 (Quick Start)

### 前置要求
* Docker Desktop 已安装并运行

### 一键启动
```bash
# 1. 克隆项目
git clone <repository-url>

# 2. 启动服务 (自动构建前后端)
docker compose up --build -d

# 3. 查看日志
docker compose logs -f
```

等待约 30-60 秒，直到看到 "Started HotelApplication" 日志。

## 🔗 服务地址

| 服务 | 地址 | 默认账号 |
|------|------|----------|
| **前端页面** | http://localhost:3000 | `admin` / `admin123` |
| **数据库** | localhost:3306 | `root` / `root` |

## ✅ 验证

完成 `docker compose up --build -d` 后，可按以下步骤验证核心功能：

1. **登录系统**
打开 `http://localhost:3000`，使用 `admin / admin123` 登录，进入工作台。

2. **验证工作台与客房统计**
在工作台查看入住率和统计卡片是否正常加载

## 📸 核心功能

1. **工作台**: 实时展示入住率、待办事项、快捷入口。
2. **客房管理**: 房态图（空闲/预订/入住/维护）、批量操作。
3. **预订管理**: 日历视图、预订录入、取消与确认。
4. **前台接待**: 散客入住、预订转入住、退房结算。
5. **会员中心**: 会员档案、积分变动、充值记录。
6. **财务报表**: 多维度收入统计图表、经营数据导出。

## 📦 项目结构

```
hotel-mgnt/
├── docker-compose.yml      # 容器编排
├── docs/                   # 工程文档目录
├── frontend/               # Vue 3 前端工程
│   ├── src/api/            # 接口封装
│   └── src/pages/          # 页面组件
└── backend/                # Spring Boot 后端工程
    ├── src/main/java/      # Java 源码
    └── src/main/resources/ # 配置文件
```

## 🛠 开发与维护

详见 [开发指南](docs/development_guide.md)。

### 常用命令
* **停止服务**: `docker compose down`
* **重置数据**: `docker compose down -v` (警告：将清空数据库)
* **后端打包**: `mvn clean package`
* **前端构建**: `npm run build`
 
