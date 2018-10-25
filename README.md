#### 说明

- 本项目为使用webmagic搭建的一个koe网站内容爬取工具

#### 用法

- 编译本项目
- 配置数据库
- 运行 java -jar 编译jar包名.jar
- 程序会自动开始下载，下载目录为 D:/audio/
- 访问 http://localhost:9090/koe/info 查看详细下载信息

#### 开发环境

- 语言：Java 8

- 基础框架：Spring Boot

- 持久层框架：Jpa

- IDE：IDEA

- 依赖管理：Maven

- 数据库：MySQL5.5

- 版本管理：SVN，git

- 数据库：MySQL5.5

- 版本管理：SVN，git


#### ps
- webmagic用来提取网页内容还是挺方便的，支持的提取规则多（xpath, selecter等），在Chrome里可以方便获取这2种规则。同时支持多线程，超时，重试，持久化内容等功能，可能这里只用到了不多webmagic的功能。
- python 还是比较适合用来写爬虫，代码精简，不用搭建复杂的运行环境，占用系统资源小，开发迅速

