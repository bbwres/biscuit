# 🌟 SpringBoot 快速开发脚手架

一个集成主流开源组件的 SpringBoot 快速开发脚手架，旨在简化项目初始化流程，提供企业级应用所需的核心功能，助力开发者快速构建稳定、高效的后端服务。

## 📋 特性一览

- **ORM 框架**：集成 MyBatis-Plus，简化数据库操作，支持分页、乐观锁、逻辑删除枚举、自动映射转换等特性
- **服务治理**：整合 Nacos 实现配置中心与服务注册发现
- **spring cloud gateway 网关**：内置网关组件，支持路由转发、限流、认证等网关核心能力
- **代码生成插件**：引入插件之后可以一键生成 Entity、Mapper、Service、Controller 等分层代码，大幅提升开发效率
- **国际化**：支持多语言 i18n 配置，轻松适配全球化业务场景
- **操作日志**：自动记录用户操作行为，支持自定义日志规则与排除路径
- **缓存支持**：集成 Redis，提供高效缓存解决方案，支持缓存穿透/击穿防护，并提供分布式锁的支持

## 🚀 快速开始

### 环境要求

| 依赖组件 | 版本要求       |
|------|------------|
| JDK  | 21+        |
| 构建工具 | Maven 3.9+ |

### 引入依赖

#### Maven 方式

```xml

<dependency>
    <groupId>cn.bbwres</groupId>
    <artifactId>biscuit-dependencies</artifactId>
    <version>${biscuit.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

