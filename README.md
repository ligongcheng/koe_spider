

- 基础框架：Spring Boot 2.0.3.RELEASE

- 持久层框架：Mybatis 3.4.5

- 安全框架：Apache Shiro 1.4.0

- 摸板引擎：Thymeleaf 3.0.9.RELEASE

- 数据库连接池：阿里巴巴Druid 1.1.10

- 缓存框架：Redis

- 日志打印：logback

- 其他：fastjson，poi，javacsv，quartz等。

#### 前端
 
- 基础框架：Bootstrap 4

- JavaScript框架：jQuery

- 消息组件：Bootstrap notify

- 提示框插件：SweetAlert2

- 树形插件：jsTree

- 树形表格插件：jqTreeGrid

- 表格插件：BootstrapTable

- 表单校验插件：jQuery-validate

- 多选下拉框插件：multiple-select

- 图表插件：Highcharts

- 时间插件：daterangepicker

#### 开发环境

- 语言：Java 8

- IDE：Eclipse Oxygen & IDEA 2018.1.4(Ultimate Edition)

- 依赖管理：Maven

- 数据库：Oracle 11g & MySQL5.7

- 版本管理：SVN，git

## 系统预览
 


### 开发与部署

码云地址： https://gitee.com/github-16661027/project 

GitHub 地址： https://github.com/wuyouzhuguli/FEBS

下载后以Maven项目的方式导入Eclipse或者IDEA。

开发时直接使用Spring Boot的入口类`cc.mrbird.Application`启动即可，访问地址[localhost:8080](localhost:8080)，账号mrbird，密码123456。

部署时，使用Maven将项目打包成febs.jar，然后使用命令`java -jar febs.jar`启动即可。在Linux下部署Spring Boot jar，并编写启停脚本可参考链接[https://mrbird.cc/Linux%20Spring-Boot-jar.html](https://mrbird.cc/Linux%20Spring-Boot-jar.html)。