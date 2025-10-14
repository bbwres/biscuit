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
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.security.oauth2.core.OAuth2UserCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * The entity model for the OAuth2Authorization domain class is designed with a class hierarchy based on authorization grant type.
 * <p>
 * The following listing shows the OAuth2AuthorizationGrantAuthorization base entity,
 * which defines common attributes for each authorization grant type.
 *
 * @author zhanglinfeng
 */
@Data
@RedisHash("oauth2_authorization")
public class OAuth2AuthorizationGrantAuthorization implements Serializable {

    @Serial
    private static final long serialVersionUID = -1713045444285335930L;

    @Id
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
     * accessToken的值
     */
    @Indexed
    private String accessTokenValue;

    /**
     * accessToken的TokenType
     */
    private String accessTokenTokenType;
    /**
     * accessToken的发放时间
     */
    private Instant accessTokenIssuedAt;
    /**
     * accessToken的过期时间
     */
    private Instant accessTokenExpiresAt;

    /**
     * accessToken 授权范围
     */
    private Set<String> accessTokenScopes;


    /**
     * refreshToken
     */
    @Indexed
    private String refreshTokenValue;

    /**
     * refreshToken的发放时间
     */
    private Instant refreshTokenIssuedAt;
    /**
     * refreshToken的过期时间
     */
    private Instant refreshTokenExpiresAt;


    /**
     * authorizationCodeToken
     */
    @Indexed
    private String authorizationCodeTokenValue;

    /**
     * authorizationCodeToken的发放时间
     */
    private Instant authorizationCodeIssuedAt;
    /**
     * authorizationCodeToken的过期时间
     */
    private Instant authorizationCodeExpiresAt;

    /**
     * 认证state
     */
    @Indexed
    private String state;

    /**
     * 设备code tokenValue
     */
    private String deviceCodeTokenValue;
    /**
     * 设备code的发放时间
     */
    private Instant deviceCodeIssuedAt;
    /**
     * 设备code 的过期时间
     */
    private Instant deviceCodeExpiresAt;


    /**
     * 用户codee tokenValue
     */
    private String userCodeTokenValue;
    /**
     * 用户code的发放时间
     */
    private Instant userCodeIssuedAt;
    /**
     * 用户code 的过期时间
     */
    private Instant userCodeExpiresAt;


    /**
     * 用户codee tokenValue
     */
    @Indexed
    private String idTokenTokenValue;
    /**
     * 用户code的发放时间
     */
    private Instant idTokenIssuedAt;
    /**
     * 用户code 的过期时间
     */
    private Instant idTokenExpiresAt;

    /**
     * the claims about the authentication of the End-User
     */
    private Map<String, Object> idTokenClaims;

    /**
     * 参数信息
     */
    private Map<String, Object> attributes;

    /**
     * token 过期时间
     */
    @TimeToLive
    private Long timeToLive;

}
