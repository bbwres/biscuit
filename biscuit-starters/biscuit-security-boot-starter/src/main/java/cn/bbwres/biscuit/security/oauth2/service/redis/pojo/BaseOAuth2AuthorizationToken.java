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

package cn.bbwres.biscuit.security.oauth2.service.redis.pojo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * 基础的token信息
 *
 * @author zhanglinfeng
 */
@Data
public abstract class BaseOAuth2AuthorizationToken implements Serializable {

    @Serial
    private static final long serialVersionUID = -5351481023175776312L;

    public static final String KEY_FORMATE = "oauth2_authorization:%s:%s";

    /**
     * id
     */
    private String id;


    /**
     * 客户端id
     */
    private String registeredClientId;

    /**
     * 主体名称
     */
    private String principalName;


    /**
     * scope
     */
    private Set<String> authorizedScopes;

    /**
     * authorizationGrantType
     */
    private String authorizationGrantType;


    /**
     * Token的值
     */
    private String tokenValue;

    /**
     * the claims about the authentication of the End-User
     */
    private Map<String, Object> metadata;

    /**
     * Token的发放时间
     */
    private Instant issuedAt;
    /**
     * Token的过期时间
     */
    private Instant expiresAt;

    /**
     * 过期时间
     */
    private Long timeToLive;

    /**
     * 获取redis的key
     *
     * @return
     */
    public String getRedisKey() {
        return String.format(KEY_FORMATE, this.getClass().getSimpleName(), tokenValue);
    }

    ;


}
