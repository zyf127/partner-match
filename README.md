# 挚友阁 - 寻找志同道合的朋友
## 项目介绍
基于 Vue + Spring Boot 的移动端交友网站（适配手机浏览器），实现了用户注册登录、按标签检索用户、推荐相似用户、组队、头像上传、添加好友、消息聊天等功能。
## 在线访问
访问地址：[挚友阁](http://zhiyouge.top) 

账号可以自行注册，也可以使用以下账号直接登录：

账号：`guest`

密码：`12345678`
## 技术栈
### 前端
Vue

Vite 脚手架

Vant UI 组件库
### 后端
Spring Boot

MyBatis

MyBatis-Plus

MySQL

Redis

Swagger
## 核心功能
用户注册和登录：用户通过注册和登录账号即可使用本网站。

标签匹配：用户选择标签，系统会根据标签匹配合适的其他用户。

组队功能：用户可以与其他用户组建队伍，交流合作。

添加好友：用户可以向其他用户发送好友申请，添加对方为好友。

消息聊天：用户可以进行世界聊天、队伍聊天、私聊。
## 项目亮点
使用 MyBatis + MyBatis-Plus 进行数据访问层开发，自动生成基础 CRUD 代码，提升项目开发效率。

自定义统一的错误码，并封装了全局异常处理器，规范了异常返回、屏蔽了项目冗余的报错细节。

使用 Redis 实现分布式 Session 登录，使用 Redis 缓存首页高频访问的用户列表，缩短接口响应时长。

在加入队伍时，使用 Redisson 分布式锁解决用户重复加入队伍、入队人数超出限制的问题。

使用 MinIO 搭建对象存储服务器，基于 MinIO 实现头像上传功能。

基于 WebSocket 协议实现世界聊天、队伍聊天、私聊等功能。

使用 Knife4j + Swagger 自动生成后端接口文档，避免了人工编写维护文档的麻烦。
## 快速启动
### 前端
安装依赖：`npm run install`

运行：`npm run dev`

打包：`npm run build`
### 后端

修改 application-dev.yml 中的数据库配置、Redis 配置，修改 application.yml 中的 MinIO 配置

运行 sql 目录下的 create_table.sql 建表、insert_data.sql 插入标签数据

运行项目
## 界面展示
### 登录、注册和个人信息页面
<img src="https://github.com/zyf127/partner-match/blob/main/img/1.png" />
<img src="https://github.com/zyf127/partner-match/blob/main/img/2.png" />
<img src="https://github.com/zyf127/partner-match/blob/main/img/3.png" />

### 主页和查看用户信息
<img src="https://github.com/zyf127/partner-match/blob/main/img/4.png" />
<img src="https://github.com/zyf127/partner-match/blob/main/img/5.png" />

### 队伍、创建队伍、修改队伍和查看队伍信息
<img src="https://github.com/zyf127/partner-match/blob/main/img/6.png" />
<img src="https://github.com/zyf127/partner-match/blob/main/img/7.png" />
<img src="https://github.com/zyf127/partner-match/blob/main/img/8.png" />
<img src="https://github.com/zyf127/partner-match/blob/main/img/9.png" />

### 私聊、队伍聊天和世界聊天
<img src="https://github.com/zyf127/partner-match/blob/main/img/10.png" />
<img src="https://github.com/zyf127/partner-match/blob/main/img/11.png" />
<img src="https://github.com/zyf127/partner-match/blob/main/img/12.png" />

### 好友界面
<img src="https://github.com/zyf127/partner-match/blob/main/img/13.png" />
