## 基于mybatis-plus 的代码生成的maven插件

### 使用说明

1. 项目引入插件包

```xml

<plugin>
    <groupId>cn.bbwres</groupId>
    <artifactId>biscuit-generator-code-maven-plugin</artifactId>
    <version>${project.version}</version>
</plugin>
```

2. 项目目前默认引入了mysql的驱动包，如果项目使用的是其他数据库

````xml

<plugin>
    <groupId>cn.bbwres</groupId>
    <artifactId>biscuit-generator-code-maven-plugin</artifactId>
    <version>${project.version}</version>
    <dependencies>
        <!--数据库驱动-->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>8.2.0</version>
        </dependency>
    </dependencies>
</plugin>
````

3.该插件同时支持配置和交互式两种代码生成方案，默认情况下会读取项目resource目录下的generator.properties文件如果没有该文件则需要使用交互式提供

generator.properties配置参数说明

| 参数名称       | 参数说明              | 
|------------|-------------------|
| author     | 作者                | 
| parentPath | 包名                |   
| tableNames | 表名，多个英文逗号         |   
| outputDir  | 文件件生成目录           |   
| dbUrl      | 数据库链接信息           |  
| dbUsername | 数据库账号接信息          |  
| dbPassword | 数据库密码接信息          |  
| useTenant  | 是否使用租户对象，默认为false |  


