## 平台简介
* 后端采用Spring Boot、Spring Security、Redis & Jwt。
* 权限认证使用Jwt，支持多终端认证系统。
* 支持加载动态权限菜单。

## 环境依赖
- jdk1.8+
- maven
- lombok插件

## 项目目录
```
digital
  |-  digital-basic              基础/公共服务
  |-  digital-devops             技术运维
  |      \-  digital-deploy      快速部署模块
  |-  digital-module             业务模块及项目
  \-  digital-platform           基础平台
         |-  digital-admin       后台主模块
         |-  digital-common      通用模块
         |-  digital-framework   架构模块
         |-  digital-generator   代码生成器
         |-  digital-quartz      定时任务模块
         \-  digital-system      基础平台业务模块
```

## 内置功能
1.  用户管理：用户是系统操作者，该功能主要完成系统用户配置。
2.  部门管理：配置系统组织机构（公司、部门、小组），树结构展现支持数据权限。
3.  岗位管理：配置系统用户所属担任职务。
4.  菜单管理：配置系统菜单，操作权限，按钮权限标识等。
5.  角色管理：角色菜单权限分配、设置角色按机构进行数据范围权限划分。
6.  字典管理：对系统中经常使用的一些较为固定的数据进行维护。
7.  参数管理：对系统动态配置常用参数。
8.  通知公告：系统通知公告信息发布维护。
9.  操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。
10. 登录日志：系统登录日志记录查询包含登录异常。
11. 在线用户：当前系统中活跃用户状态监控。
12. 定时任务：在线（添加、修改、删除)任务调度包含执行结果日志。
14. 系统接口：根据业务代码自动生成相关的api接口文档。
15. 服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。
16. 在线构建器：拖动表单元素生成相应的HTML代码。
17. 连接池监视：监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。
18. 自动CRUD接口：根据数据库表信息，自动生成crud接口及对应swagger文档
19. 缓存切换：可以根据情况在ehcache和redis之前进行切换。
20. 静态文件扫描：可以将前端项目静态文件作为后端项目的静态资源进行访问，不必单独部署。
21. 集成通用mapper：省去逆向工程的步骤，直接进行单表数据库操作。
22. 使用validate参数校验：基于全局异常捕获，使用validate注解进行参数校验，简化Controller代码。
23. 快速发布模块：一个小巧的项目部署工具，方便前后端项目发布。

## 项目启动
1. 使用`sql/init.sql`初始化数据库
2. 在`digital-platform/digital-admin`模块配置文件中配置数据库信息
3. 运行`digital-platform/digital-admin`模块DigitalApplication的main方法

## 项目打包
运行`bin/package.bat`,或在项目目录运行命令`mvn clean package`,可运行jar包生成在`digital-platform/digital-admin/target/digital-admin.jar`