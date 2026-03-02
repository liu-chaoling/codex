# 个人网站系统（Java 8 + MySQL，微博风格）

这是一个基于 **Java 8 + Spring Boot + MySQL** 的个人网站后端项目，聚焦微博式动态发布与图片管理能力。

## 一、核心能力

### 1) 帖子发布（支持多图）
- 用户登录后可发动态（文本内容 + 多张图片）。
- 支持时间线倒序浏览（最新帖子优先）。
- 帖子关联发布者信息（昵称、头像等）。

### 2) 相册上传与下载
- 用户可上传相册图片（带标题）。
- 可查询当前登录用户的相册列表。
- 图片文件统一由 `/api/files/{category}/{name}` 下载/访问。

### 3) 分用户登录体系
- 支持注册 / 登录。
- 使用 JWT 鉴权（无状态）。
- 用户信息至少包含：
  - `username` 账户
  - `password` 密码（BCrypt 加密存储）
  - `avatarUrl` 头像
  - `displayName` 昵称

---

## 二、技术栈

- Java 8
- Spring Boot 2.7.x
- Spring Web / Spring Data JPA / Spring Security
- MySQL 8+
- JWT（jjwt）
- 本地文件存储（上传目录）

---

## 三、项目结构

```text
src/main/java/com/personal/site
├── common            # 统一返回体、异常处理
├── config            # Security 配置
├── controller        # 接口层
├── dto               # 请求/响应对象
├── entity            # JPA 实体
├── repository        # 数据访问层
├── security          # JWT 工具与鉴权过滤器
└── service           # 业务层
```

---

## 四、数据库设计（简化版）

已提供 `src/main/resources/schema.sql`。

- `users`: 用户表（账号、密码、头像、昵称、简介）
- `posts`: 帖子主表
- `post_images`: 帖子图片表（一对多）
- `album_images`: 相册图片表（用户维度）

---

## 五、接口清单

> 统一前缀：`/api`

### 1. 认证

#### 注册
- `POST /api/auth/register`
- Body(JSON):
```json
{
  "username": "alice",
  "password": "12345678",
  "displayName": "Alice"
}
```

#### 登录
- `POST /api/auth/login`
- Body(JSON):
```json
{
  "username": "alice",
  "password": "12345678"
}
```

登录成功返回 JWT：
```json
{
  "success": true,
  "data": {
    "token": "...",
    "userId": 1,
    "username": "alice",
    "displayName": "Alice",
    "avatarUrl": "avatar/xxx.png"
  },
  "message": "OK"
}
```

### 2. 用户

#### 获取当前用户信息
- `GET /api/users/me`
- Header: `Authorization: Bearer <token>`

#### 上传头像
- `POST /api/users/avatar`
- Header: `Authorization: Bearer <token>`
- FormData:
  - `file`: 头像文件

### 3. 帖子

#### 发布帖子（可多图）
- `POST /api/posts`
- Header: `Authorization: Bearer <token>`
- FormData:
  - `content`: 文本内容
  - `images`: 图片文件（可多个）

#### 时间线列表
- `GET /api/posts`
- Header: `Authorization: Bearer <token>`

### 4. 相册

#### 上传相册图片
- `POST /api/album`
- Header: `Authorization: Bearer <token>`
- FormData:
  - `title`: 图片标题
  - `file`: 图片

#### 我的相册列表
- `GET /api/album`
- Header: `Authorization: Bearer <token>`

### 5. 文件访问

#### 图片下载/访问
- `GET /api/files/{category}/{name}`
- 无需登录。

`category` 可能为：`avatar`、`post`、`album`。

---

## 六、运行步骤

### 1) 初始化数据库
```sql
CREATE DATABASE personal_site DEFAULT CHARSET utf8mb4;
```

### 2) 修改配置
编辑 `src/main/resources/application.yml`：
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.datasource.url`

### 3) 启动项目
```bash
mvn spring-boot:run
```

---

## 七、后续可扩展方向（微博化增强）

- 点赞 / 收藏 / 转发 / 评论系统
- 关注与粉丝关系
- 话题标签（#话题#）与搜索
- @提及用户
- 帖子可见性（公开、仅粉丝、私密）
- 图片压缩、CDN 存储（OSS/MinIO）
- 管理后台（内容审核、用户封禁）



## 八、IDEA 打开与目录自检（含“outside of module source root”修复）

如果 IDEA 提示：

> Java file is located outside of the module source root, so it won't be compiled

通常是 **源码根目录标记** 或 **文件路径与包名不一致** 导致。

### 1) 先确认目录结构

标准 Maven 结构应为：

```text
pom.xml
src/main/java
src/main/resources
src/test/java
```

并且 Java 文件路径要和 `package` 一致，例如：

- 包名：`package com.personal.site;`
- 文件路径必须是：`src/main/java/com/personal/site/PersonalSiteApplication.java`

> 如果你把 `PersonalSiteApplication.java` 放在 `src/main/java/` 根下（而不是 `com/personal/site/` 目录），IDEA 就可能报你截图中的警告。

### 2) 在 IDEA 里修复 Source Root

1. 右键 `src/main/java` -> **Mark Directory as -> Sources Root**。
2. 右键 `src/test/java` -> **Mark Directory as -> Test Sources Root**（如果存在）。
3. 右键 `src/main/resources` -> **Mark Directory as -> Resources Root**。

### 3) 重新导入 Maven

1. 打开右侧 Maven 工具窗口，点击 **Reload All Maven Projects**。
2. `File -> Project Structure -> Modules`，确认模块的 Sources 中 `src/main/java` 已是蓝色 Sources Root。
3. 若仍异常，执行 `File -> Invalidate Caches / Restart`。

---

## 九、IDEA 无数据库“虚拟启动”

为了在不连接 MySQL 的情况下先把 Spring Boot 跑起来，项目提供 `application-nodb.yml` 配置。

### 1) 在 IDEA 中添加启动配置

- Run/Debug Configurations -> Spring Boot
- Main class: `com.personal.site.PersonalSiteApplication`
- Active profiles: `nodb`

或使用 VM options：

```bash
-Dspring.profiles.active=nodb
```

### 2) 该模式会做什么

- 禁用 `DataSourceAutoConfiguration`
- 禁用 `HibernateJpaAutoConfiguration`
- 禁用 JPA Repository 自动创建

因此可用于：
- 先验证 Spring 容器是否可启动；
- 联调不依赖数据库的接口；
- 验证上传目录、JWT 配置等基础能力。

### 3) 命令行等价启动方式

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=nodb
```

### 4) 若启动类右上角没有绿色运行按钮

通常是 IDEA 没把该文件识别为模块源码：

- 先完成“第 2 步 Source Root 标记”；
- 再执行 Maven Reload；
- 最后右键启动类，选择 **Run 'PersonalSiteApplication.main()'**。

