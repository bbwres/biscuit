# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概览

biscuit 是一个 Spring Boot 快速开发脚手架框架，提供 20+ 个 Spring Boot Starter 组件，发布到 Maven Central（groupId: `cn.bbwres`，当前版本 `3.0.3-SNAPSHOT`）。

技术栈：Java 21、Maven 3.9+、Spring Boot 3.5.9、Spring Cloud 2025.0.1、Spring Cloud Alibaba 2025.0.0.0、Spring AI 1.1.2。

## 构建命令

```bash
# 全量构建（跳过测试）
mvn clean install -DskipTests

# 运行测试（含 JaCoCo 覆盖率报告，结果在各模块 target/site/jacoco/）
mvn clean test

# 指定模块构建
mvn clean install -pl biscuit-starters/biscuit-mybatis-boot-starter -DskipTests

# 发布到 Maven Central（需配置 Sonatype 账号和 GPG 签名）
mvn clean deploy -DskipTests -P release
```

## 架构

### 模块分层

```
biscuit/
├── biscuit-dependencies/                # BOM 依赖清单（统一管控所有第三方和自身 Starter 版本）
├── biscuit-starters/                    # Starter 组件集合
│   ├── biscuit-common-boot-starter             # 核心：Result/Page DTO、异常体系、脱敏、校验分组
│   ├── biscuit-web-boot-starter                # Web 通用配置（Jackson、全局异常处理、国际化消息）
│   ├── biscuit-web-file-boot-starter           # 文件上传/下载
│   ├── biscuit-gateway-boot-starter            # Spring Cloud Gateway（AuthFilter、XSS、Nacos 动态路由）
│   ├── biscuit-security-boot-starter           # OAuth2 授权服务器（自定义 grant type、RBAC、Token 管理）
│   ├── biscuit-security-captcha-boot-starter   # 验证码（tianai-captcha）
│   ├── biscuit-mybatis-boot-starter            # MyBatis-Plus（基础实体、多租户行级插件、批量 Mapper、Hana 方言）
│   ├── biscuit-mybatis-flex-boot-starter       # MyBatis-Flex 替代实现（同样支持多租户、字段填充）
│   ├── biscuit-nacos-boot-starter              # Nacos 配置中心集成（配置订阅回调）
│   ├── biscuit-redis-cache-boot-starter        # Redis 缓存（防穿透/击穿，自定义 RedisCacheManager）
│   ├── biscuit-redis-lock-boot-starter         # Redisson 分布式锁（@DistributedLock 注解 + AOP）
│   ├── biscuit-id-generator-boot-starter       # ID 生成（雪花算法 / Redis 分段两种策略）
│   ├── biscuit-scheduler-boot-starter          # Quartz 调度（@JobDefinition 注解式声明）
│   ├── biscuit-i18n-boot-starter               # 国际化（I18nFilter + 多语言 MessageSource）
│   ├── biscuit-operation-log-starter           # 操作日志审计（@OperationLog 注解 + SpEL 表达式提取）
│   ├── biscuit-mongodb-boot-starter            # MongoDB（ExtendMongoRepository / ExtendMongoService）
│   ├── biscuit-rpc-boot-starter                # 服务间 RPC 安全校验（签名算法：MD5/SHA1/SHA256）
│   ├── biscuit-micrometer-boot-starter         # Micrometer 指标
│   ├── biscuit-utils-boot-starter              # HTTP 客户端（OkHttp / WebClient 两个子模块）
│   └── biscuit-generator-code-maven-plugin     # 代码生成 Maven 插件（Entity → Mapper → Service → Controller）
```

### 关键架构决策

- **依赖版本管控**：所有第三方版本集中在 `biscuit-dependencies/pom.xml` 的 `<dependencyManagement>` 中，Starter 模块通过 BOM import 继承，不再单独声明版本
- **双 ORM 支持**：`biscuit-mybatis-boot-starter`（MyBatis-Plus）和 `biscuit-mybatis-flex-boot-starter`（MyBatis-Flex）并行提供，两者都包含 `BaseEntity`、多租户、枚举 TypeHandler，但实现各自独立
- **枚举统一处理**：实现 `BaseEnum` 接口的枚举通过 `BiscuitMybatisEnumTypeHandler` 自动与数据库互转，无需手动配置
- **多租户**：MyBatis-Plus 通过 `DefaultTenantLineHandler` 行级插件实现；MyBatis-Flex 通过 `BiscuitTenantFactory` 实现；均由 `MybatisTenantProperties` 控制开关和忽略表
- **网关认证链**：`biscuit-gateway-boot-starter` 内置 AuthFilter → AuthorizationManager → ResourceServerConfig 三层认证，资源信息通过 `ResourceCacheService` 缓存
- **代码生成**：`biscuit-generator-code-maven-plugin` 支持 MyBatis-Plus 和 MyBatis-Flex 两种模板（配置文件在 `src/main/resources/generator/` 下），业务工程只需在 `generator.properties` 中配置表名和包路径即可使用
- **发布流程**：使用 `central-publishing-maven-plugin` 发布到 Sonatype Central Portal，`maven-gpg-plugin` 签名，`maven-release-plugin` 管理版本标签

## Java 包结构

所有 Starter 遵循统一包结构：`cn.bbwres.biscuit.{模块名}`，Spring Boot 自动配置类命名为 `{模块名}AutoConfigure`，注册在 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`。
