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

package cn.bbwres.biscuit.security.oauth2.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 登录密码加密配置
 *
 * @author zhanglinfeng
 */
@Data
@ConfigurationProperties("biscuit.security.password")
public class PasswordSecurityProperties {

    /**
     * 是否启用密码 解密
     */
    private boolean enabled = true;
    /**
     * 算法套件
     */
    private String transformation = "RSA/ECB/PKCS1Padding";
    /**
     * iv
     */
    private String iv;

    /**
     * 时间戳有效窗口（秒），默认 5 分钟
     * 客户端时间戳与服务端时间差超过该值，视为已过期
     */
    private long timestampWindowSeconds = 300L;

    /**
     * RSA 私钥（PKCS#8 Base64 格式，去掉 PEM 头尾换行）
     */
    private String privateKey;
}
