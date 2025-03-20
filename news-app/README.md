# 新闻资讯平台 - Vue 3 + Vite + TypeScript 项目

## 项目概述

这是一个基于 Vue 3 和 Vite 构建的新闻资讯平台，采用 TypeScript 开发，包含以下主要功能：

- 新闻分类浏览
- 新闻详情查看
- 用户评论功能
- 用户个人中心
- 热门新闻推荐
- 新闻搜索功能

## 技术栈

- **前端框架**: Vue 3
- **构建工具**: Vite
- **语言**: TypeScript
- **UI 组件库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router
- **HTTP 请求**: Axios

## 项目结构

```
src/
├── api/            # API 接口管理
├── assets/         # 静态资源
├── components/     # 公共组件
├── model/          # 数据模型
├── router/         # 路由配置
├── stores/         # 状态管理
├── utils/          # 工具函数
└── views/          # 页面组件
```

## 主要功能模块

### 1. 新闻浏览
- 首页新闻轮播
- 新闻分类标签
- 新闻列表展示
- 新闻详情查看
- 新闻点赞功能

### 2. 用户功能
- 用户登录/注册
- 个人信息管理
- 评论功能
- 浏览历史记录

### 3. 其他功能
- 热门新闻推荐
- 新闻搜索
- 响应式布局
- 暗黑模式支持

## 开发环境配置

1. 克隆项目
```bash
git clone https://github.com/your-repo/news-app.git
```

2. 安装依赖
```bash
npm install
```

3. 启动开发服务器
```bash
npm run dev
```

4. 构建生产环境
```bash
npm run build
```

## 项目依赖

- `vue`: ^3.3.0
- `vite`: ^4.4.0
- `element-plus`: ^2.3.0
- `pinia`: ^2.1.0
- `axios`: ^1.4.0
- `typescript`: ^5.1.0

## 贡献指南

欢迎提交 Pull Request 来改进本项目。在提交之前，请确保：

1. 代码符合 ESLint 规范
2. 添加必要的单元测试
3. 更新相关文档

