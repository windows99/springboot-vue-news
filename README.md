
> 项目是用来毕业设计的，课题是《基于VUE框架的实时新闻推送系统》，目前还在完善，预计在4月底的时候实现所有功能。项目基于一个快速模版进行开发[Vue3+SpringBoot项目快速开发模板](https://gitee.com/fspStudy/quick-develop-template)，希望大家多多start！

# 一.功能预览

为了提高模板的通用性与精简性，只实现一些基础的功能：

- 登录 \ 注册
- 密码修改 \ 重置
- 用户管理（增删改分页）
- 个人信息查看 \ 修改
- 头像上传
- 根据后端接口快速生成前端请求接口

## (1) 登录

登录、注册、修改密码、重置密码表单后端已实现基础的格式校验。
![1](assert/1.jpg)


## (2) 注册

验证码方面都是采用邮箱验证码，可根据个人需要进行配置或者修改。参考博客：

- [手机验证码](https://blog.csdn.net/m0_66570338/article/details/129041619)
- [邮箱验证码](https://blog.csdn.net/m0_66570338/article/details/128994951)
![1](assert/2.jpg)


## (3) 重置密码
![1](assert/3.jpg)

## (4) 主页
![1](assert/4.jpg)


## (5) 用户管理

基础的增删改查功能，包括图片上传（admin角色状态展示）

![1](assert/5.jpg)


## (6) 修改密码
![1](assert/6.jpg)


## (7) 个人信息
![1](assert/7.jpg)

## (8) 编辑个人信息
![1](assert/8.jpg)


# 二.必备配置

上述预览效果必要配置项：

- 前端
  - 使用如下指令安装依赖包即可 
  
  ```bash
  # 1. 安装依赖包
  pnpm install
  # 2. 启动项目
  pnpm dev
  ```
  
  - 修改后端项目后可执行如下命令快速生成接口
  
  ```bash
  pnpm openapi
  ```

- 后端
  - 根据`/sql`目录下的`/create_table.sql`创建表
  - 修改`application.yml`文件中的 `MySQL数据库连接`、`Redis数据库连接`
  - 修改`/utils/CodeUtils.java`中的邮箱配置
  - 完善阿里云oss配置

# 三.代码仓库

- 仓库地址：[https://gitee.com/fspStudy/quick-develop-template.git ](https://gitee.com/fspStudy/quick-develop-template.git) （前后端项目放在了同一个父目录中，可以根据需要分别管理）

# 四.更新日志

- **2025/02/22**
  - 后端：去除es
  - 后端：使用thumbnailator对上传图片进行压缩

- **2024/12/17**
  - 后端：完善文件上传功能，使用工厂模式 + Spring配置化 配置多种对象存储源

- **2024/11/29**
  - 前端：添加图片/图片列表上传组件
  - 前端：用户管理添加筛选查询
  - 前端：完善顶部导航栏和左侧导航栏
  - 前端：抽离头像下拉框组件
  - 前端：借鉴ant design pro修改登录界面
  - 前端：主页添加背景图片


- **2024/11/8**
  - 前端：整合`@umijs/plugin-openapi`可根据后端接口文档[快速生成前端接口](https://www.npmjs.com/package/@umijs/plugin-openapi)
  - 前端：重新封装`pure admin`请求工具类适配接口生成工具
  - 项目：去除账号字段，将邮箱改为登录账号

- **2024/6/23**
  - 前端：使用 `Prettier/ESLint` 规范代码
  - 前端：去除动态路由配置
  - 前端：登录记住密码
  - 后端：修复全局替换导致的配置错误













