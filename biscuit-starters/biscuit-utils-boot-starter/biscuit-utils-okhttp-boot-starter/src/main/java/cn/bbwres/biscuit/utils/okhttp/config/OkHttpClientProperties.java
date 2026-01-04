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

package cn.bbwres.biscuit.utils.okhttp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.net.ssl.SSLSocketFactory;
import java.time.Duration;

/**
 *
 * okhttp client 配置信息
 *
 * @author zlf
 * @version 1.0
 * @since 2025-12-30  17:44
 */
@Data
@ConfigurationProperties("biscuit.utils.okhttp")
public class OkHttpClientProperties {

    /**
     * 最大空闲连接数
     */
    private Integer maxIdleConnections = 100;

    /**
     * 连接保持时间 单位秒
     */
    private Long keepAliveDuration = 5 * 60L;

    /**
     * 最大等待队列长度：-1 表示无限制，可根据业务设置（如1000）
     */
    private Integer pendingAcquireMaxCount = -1;

    /**
     * 链接超时时间 单位为秒
     */
    private Long connectTimeout = 10L;
    /**
     * 写超时时间
     */
    private Long writeTimeoutSeconds;
    /**
     * 读超时时间
     */
    private Long readTimeoutSeconds = 30L;

    /**
     * 忽略证书的hostname 验证
     */
    private Boolean ignoreHostnameVerifier = true;

    /**
     * ssl 证书信息
     */
    private SSLSocketFactory sslSocketFactory;

    /**
     * 是否启用日志
     */
    private Boolean enableLog = false;

    /**
     * 日志前缀
     */
    private String logTag;

}
