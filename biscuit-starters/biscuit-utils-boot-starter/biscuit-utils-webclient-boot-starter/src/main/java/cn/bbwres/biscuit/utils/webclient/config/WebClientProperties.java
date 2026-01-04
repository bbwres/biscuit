/*
 *
 *  * Copyright 2024 bbwres
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package cn.bbwres.biscuit.utils.webclient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 *
 * WebClient 配置信息
 *
 * @author zlf
 * @version 1.0
 * @since 2025-12-30  17:44
 */
@Data
@ConfigurationProperties("biscuit.utils.webclient")
public class WebClientProperties {

    /**
     * 最大连接数
     */
    private Integer maxConnections = 200;

    /**
     * 最大空闲时间：空闲连接超过该时间将被释放
     */
    private Duration maxIdleTime;
    /**
     * 最大生命周期：连接创建后超过该时间将被释放
     */
    private Duration maxLifeTime;
    /**
     * 响应 超时时间
     */
    private Duration responseTimeout = Duration.ofSeconds(120);

    /**
     * 获取连接超时时间：等待获取连接超过该时间抛出异常
     */
    private Duration pendingAcquireTimeout = Duration.ofSeconds(50);

    /**
     * 最大等待队列长度：-1 表示无限制，可根据业务设置（如1000）
     */
    private Integer pendingAcquireMaxCount = -1;

    /**
     * 链接超时时间
     */
    private Integer connectTimeoutMillis = 50;
    /**
     * 写超时时间
     */
    private Integer writeTimeoutSeconds;
    /**
     * 读超时时间
     */
    private Integer readTimeoutSeconds;

    /**
     * 打印监听日志
     */
    private Boolean wiretap = false;


    /**
     * 该参数用于限制 Spring WebFlux 客户端（核心为 WebClient）在解码 HTTP 响应体时，允许加载到内存中的最大数据大小。
     * default  256 * 1024 =256k
     * 默认设置为16M
     */
    private Integer maxInMemorySize = 16 * 1024 * 1024;

}
