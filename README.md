# 📚 学生信息管理系统

基于 **Spring Boot 3 + Vue 3** 的全栈学生信息管理平台，实现学生、课程、考试、成绩、用户权限等核心业务管理功能。


## ✨ 功能特点

- 🔐 **JWT 登录认证**：安全可靠的 Token 认证机制
- 👨‍🎓 **学生管理**：增删改查、分页搜索、数据回显
- 📚 **课程管理**：课程 CRUD、选课管理（添加/移除学生）
- 📝 **考试管理**：考试 CRUD、状态自动计算（待考/进行中/已结束）
- 📊 **成绩管理**：成绩录入（等级自动换算 A/B/C/D/F）、成绩列表、成绩统计（ECharts 图表）
- 📈 **仪表盘**：统计卡片 + 图表可视化（及格率分布、课程平均分对比、近期考试安排）
- 👥 **用户管理**：用户 CRUD、角色分配、密码重置
- 🔑 **角色管理**：角色 CRUD、菜单权限分配
- 📁 **菜单管理**：菜单树形结构、按钮权限标识
- 🏫 **班级管理**：班级 CRUD
- 📜 **操作日志**：AOP 自动记录操作日志、多条件筛选


## 🛠️ 技术栈

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.1 | 后端核心框架 |
| MyBatis-Plus | 3.5.5 | ORM 框架，简化数据库操作 |
| MySQL | 8.0+ | 关系型数据库 |
| JWT | 0.11.5 | 用户认证与授权 |
| Spring AOP | — | 操作日志切面 |
| Lombok | — | 简化 Java Bean 代码 |
| Maven | — | 项目构建与依赖管理 |

### 前端
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue 3 | 3.4.x | 前端核心框架 |
| Vite | 5.x | 构建工具与开发服务器 |
| Element Plus | 2.8.x | UI 组件库 |
| Axios | 1.7.x | HTTP 请求库 |
| ECharts | 5.5.x | 数据可视化图表 |
| Vue Router | 4.x | 前端路由管理 |


## 📁 项目结构
student-management/
├── backend/ # 后端 Spring Boot 项目
│ ├── src/main/java/com/example/backend/
│ │ ├── aspect/ # AOP 切面（操作日志）
│ │ ├── config/ # 配置类（CORS、MyBatis-Plus）
│ │ ├── controller/ # 控制器层（11 个 Controller）
│ │ ├── entity/ # 实体类（11 个 Entity）
│ │ ├── mapper/ # MyBatis-Plus Mapper
│ │ ├── service/ # 业务逻辑层
│ │ │ └── impl/ # 业务实现
│ │ └── util/ # 工具类（JWT）
│ ├── src/main/resources/
│ │ └── application.yml # 配置文件（数据库、端口等）
│ └── pom.xml # Maven 依赖管理
│
├── frontend/ # 前端 Vue 3 项目
│ ├── src/
│ │ ├── api/ # API 请求封装（9 个模块）
│ │ ├── assets/ # 静态资源
│ │ ├── components/ # 公共组件（选课弹窗等）
│ │ ├── layout/ # 布局组件（侧边栏 + 头部）
│ │ ├── router/ # 路由配置
│ │ ├── utils/ # 工具类（Axios 拦截器）
│ │ ├── views/ # 页面组件
│ │ │ ├── course/ # 课程管理
│ │ │ ├── dashboard/ # 仪表盘
│ │ │ ├── exam/ # 考试管理
│ │ │ ├── login/ # 登录页
│ │ │ ├── score/ # 成绩管理（录入/列表/统计）
│ │ │ ├── student/ # 学生管理
│ │ │ └── system/ # 系统管理（菜单/角色/用户/班级/日志）
│ │ ├── App.vue # 根组件
│ │ └── main.js # 入口文件
│ ├── index.html # HTML 入口
│ ├── package.json # npm 依赖管理
│ └── vite.config.js # Vite 配置（含代理）
│
├── sql/
│ └── init.sql # 完整建表脚本 + 测试数据
└── README.md # 项目说明文档


## 🚀 快速启动

### 📌 环境要求
| 环境 | 版本要求 |
|------|----------|
| JDK | 17 或更高版本 |
| MySQL | 8.0 或更高版本 |
| Node.js | 18.x 或更高版本（推荐 20.x LTS） |
| Maven | 3.6 或更高版本（可选，IDEA 自带） |

### 1️⃣ 克隆项目
```bash
git clone https://github.com/hy1255/student-management.git
cd student-management'

### 2️⃣ 数据库配置
```bash
#### 环境要求
- MySQL 8.0 或更高版本
- MySQL 服务已启动

#### 步骤一：创建数据库并建表

**方式一：在 IDEA 中操作（推荐）**

1. 打开 IDEA 右侧的 **Database** 工具窗口。
2. 点击 `+` → `Data Source` → `MySQL`。
3. 填写连接信息：
   | 字段 | 值 |
   |------|-----|
   | Host | `localhost` |
   | Port | `3306` |
   | User | `root` |
   | Password | 你的 MySQL 密码 |
4. 点击 **Test Connection**，显示 `Succeeded` 后点击 `OK`。
5. 右键点击连接 → `New` → `Query Console`。
6. 复制 `sql/init.sql` 文件中的全部内容，粘贴到 Console 中执行。
7. 执行完成后，右键点击连接 → `Refresh`，确认 11 张表已创建。

**方式二：在命令行中操作**

```bash
mysql -u root -p
source D:/student-management/sql/init.sql
3️⃣ 启动后端
bash
cd backend
# 方式一：在 IDEA 中运行 BackendApplication.java
# 方式二：使用 Maven 命令
mvn clean package -DskipTests
java -jar target/backend-0.0.1-SNAPSHOT.jar
启动成功后，控制台显示：Started BackendApplication in X seconds
4️⃣ 启动前端
bash
cd frontend
npm install --registry=https://registry.npmmirror.com
npm run dev
启动成功后，控制台显示：Local: http://localhost:5173/
5️⃣ 登录系统
访问 http://localhost:5173/login，使用以下账号登录：

用户名	密码	角色	权限说明
admin	123456	管理员	全部权限
teacher	123456	教师	管理课程、考试、成绩
student	123456	学生	查看个人信息
📡 API 接口概览
认证模块 (/api/auth)
方法	路径	说明
POST	/login	用户登录
GET	/me	获取当前用户信息
学生管理 (/api/student)
方法	路径	说明
GET	/page	分页查询（含模糊搜索）
GET	/{id}	查询详情
POST	/	新增/修改
DELETE	/{id}	删除
课程管理 (/api/course)
方法	路径	说明
GET	/page	分页查询
GET	/{id}	查询详情
POST	/	新增/修改
DELETE	/{id}	删除
考试管理 (/api/exam)
方法	路径	说明
GET	/page	分页查询（含课程筛选）
GET	/{id}	查询详情
POST	/	新增/修改
DELETE	/{id}	删除
成绩管理 (/api/score)
方法	路径	说明
GET	/exam/{examId}	获取某考试所有学生成绩（录入用）
POST	/batch	批量保存成绩
GET	/page	分页查询成绩列表
GET	/stats/{examId}	获取考试统计数据
DELETE	/{id}	删除成绩
选课管理 (/api/student-course)
方法	路径	说明
GET	/course/{courseId}	获取课程已选学生
GET	/course/{courseId}/available	获取未选学生（可添加）
POST	/batch	批量添加学生到课程
DELETE	/course/{courseId}/student/{studentId}	从课程移除学生
系统管理
模块	路径前缀	说明
菜单管理	/api/menu	菜单树、增删改
角色管理	/api/role	角色 CRUD、分配菜单权限
用户管理	/api/user	用户 CRUD、重置密码
班级管理	/api/class	班级 CRUD
操作日志	/api/log	日志列表、详情
仪表盘 (/api/dashboard)
方法	路径	说明
GET	/stats	获取统计卡片 + 图表数据 + 近期考试
📦 打包与部署
后端打包
bash
cd backend
mvn clean package -DskipTests
# JAR 文件生成在 target/backend-0.0.1-SNAPSHOT.jar
java -jar target/backend-0.0.1-SNAPSHOT.jar
前端打包
bash
cd frontend
npm run build
# 静态文件生成在 dist/ 文件夹
npx serve -s dist   # 预览
⚠️ 注意：打包后如需预览，请先修改 frontend/src/utils/request.js 中的 baseURL 为实际后端地址（如 http://localhost:8080/api），否则会出现网络异常。
部署到 Nginx
bash
# 将 dist/ 文件夹所有内容复制到 Nginx 的 html 目录
cp -r dist/* /usr/share/nginx/html/
Nginx 反向代理配置（推荐）
nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        root /var/www/student-management/dist;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
❓ 常见问题
Q1: 登录时提示“网络异常”
检查后端是否已启动（访问 http://localhost:8080/api/auth/login 看是否响应）

检查后端端口与前端 Vite 代理配置是否一致（默认 8080）

Q2: 打包后预览页面“网络异常”
开发模式（npm run dev）有 Vite 代理；预览模式没有代理

修改 frontend/src/utils/request.js 中的 baseURL 为实际后端地址，重新打包

Q3: 成绩录入页面看不到学生
需要先将学生添加到课程中（课程管理 → 选课学生 / 考试管理 → 添加考生）

成绩录入依赖于 student_course 选课表的数据

Q4: 考试状态没有自动更新
考试状态通过 ExamServiceImpl.saveOrUpdate() 自动计算

通过直接 SQL 插入的数据，状态需要手动更新或通过页面修改

Q5: 仪表盘图表不显示
检查 score 表中是否有成绩数据

图表需要至少一条成绩数据才能渲染

Q6: 数据库连接失败（Access denied）
检查 application.yml 中的 password 是否与 MySQL 密码一致

Q7: 端口被占用
后端端口：修改 application.yml 中的 server.port

前端端口：修改 vite.config.js 中的 server.port

👨‍💻 作者
项目开发：实习生项目

日期：2026年7月

📄 许可
本项目仅供学习和参考使用，不涉及任何商业用途。
🎉 如果你觉得这个项目对你有帮助，欢迎 Star ⭐！

text

---

## ✅ 操作步骤

1. **在项目根目录 `D:\student-management\` 下创建 `README.md` 文件。**
2. **复制上面的内容，粘贴到 `README.md` 中。**
3. **保存文件（Ctrl+S）。**
4. **推送到 GitHub**：
   ```bash
   git add README.md
   git commit -m "docs: 添加完整项目 README 文档"
   git push