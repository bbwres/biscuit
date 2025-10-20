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

package cn.bbwres.biscuit.security.oauth2.service.redis;

import cn.bbwres.biscuit.security.oauth2.properties.BiscuitSecurityProperties;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

/**
 * The following listing shows the RedisOAuth2AuthorizationConsentService, which uses an OAuth2UserConsentRepository for persisting an OAuth2UserConsent
 * and maps to and from the OAuth2AuthorizationConsent domain object, using the ModelMapper utility class.
 *
 * @author zhanglinfeng
 */
public class RedisOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {


    private final RedisOperations<Object, Object> redisOperations;
    private final BiscuitSecurityProperties biscuitSecurityProperties;

    public RedisOAuth2AuthorizationConsentService(RedisOperations<Object, Object> redisOperations,
                                                  BiscuitSecurityProperties biscuitSecurityProperties) {
        this.redisOperations = redisOperations;
        this.biscuitSecurityProperties = biscuitSecurityProperties;
    }

    /**
     * 保存OAuth2授权同意信息
     *
     * @param authorizationConsent 授权同意信息，不能为null
     * @throws IllegalArgumentException 当authorizationConsent为null时抛出
     */
    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        redisOperations.opsForValue().set(buildKey(authorizationConsent), authorizationConsent, biscuitSecurityProperties.getAuthorizationConsentExpireSecond(), TimeUnit.SECONDS);
    }

    /**
     * 移除OAuth2授权同意信息
     *
     * @param authorizationConsent 授权同意信息，不能为null
     * @throws IllegalArgumentException 当authorizationConsent为null时抛出
     */
    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        redisOperations.delete(buildKey(authorizationConsent));
    }

    /**
     * 根据注册客户端ID和主体名称查找OAuth2授权同意信息
     *
     * @param registeredClientId 注册客户端ID，不能为空
     * @param principalName      主体名称，不能为空
     * @return 查找到的OAuth2授权同意信息，可能为null
     */
    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        Assert.hasText(registeredClientId, "registeredClientId cannot be empty");
        Assert.hasText(principalName, "principalName cannot be empty");
        Object result = redisOperations.opsForValue().get(buildKey(registeredClientId, principalName));
        return ObjectUtils.isEmpty(result) ? null : (OAuth2AuthorizationConsent) result;
    }

    /**
     * 构建授权确认信息的key
     *
     * @param registeredClientId 注册客户端ID
     * @param principalName      主体名称
     * @return 拼接后的key字符串
     */
    private static String buildKey(String registeredClientId, String principalName) {
        return "token:consent:" + registeredClientId + ":" + principalName;
    }

    /**
     * 构建授权同意的键值
     *
     * @param authorizationConsent 授权同意对象
     * @return 构建的键值字符串
     */
    private static String buildKey(OAuth2AuthorizationConsent authorizationConsent) {
        return buildKey(authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName());
    }
}
