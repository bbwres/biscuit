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

package cn.bbwres.biscuit.security.oauth2.config;

import cn.bbwres.biscuit.security.oauth2.properties.BiscuitSecurityProperties;
import cn.bbwres.biscuit.security.oauth2.service.redis.RedisOAuth2AuthorizationConsentService;
import cn.bbwres.biscuit.security.oauth2.service.redis.RedisOAuth2AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

/**
 * 认证服务的token处理
 *
 * @author zhanglinfeng
 */
@Slf4j
@AutoConfiguration
public class Oauth2AuthorizationServiceConfig {


    /**
     * 认证服务的token配置
     */
    @Configuration
    @ConditionalOnProperty(prefix = "biscuit.security", name = "token-store-type", havingValue = "in_memory")
    public static class InMemoryOauth2AuthorizationServiceConfig {
        /**
         * 基于内存的OAuth2AuthorizationService服务
         *
         * @return OAuth2AuthorizationService
         */
        @Bean
        @ConditionalOnMissingBean
        public OAuth2AuthorizationService inMemoryOauth2AuthorizationService() {
            return new InMemoryOAuth2AuthorizationService();

        }

        /**
         * 基于内存的OAuth2AuthorizationConsentService服务
         *
         * @return OAuth2AuthorizationConsentService
         */
        @Bean
        @ConditionalOnMissingBean
        public OAuth2AuthorizationConsentService authorizationConsentService() {
            return new InMemoryOAuth2AuthorizationConsentService();
        }
    }

    /**
     * 认证服务的token配置
     */
    @Configuration
    @ConditionalOnProperty(prefix = "biscuit.security", name = "token-store-type", havingValue = "redis")
    @ConditionalOnClass(RedisOperations.class)
    public static class RedisOauth2AuthorizationServiceConfig {

        /**
         * 基于redis 认证的 OAuth2AuthorizationService
         *
         * @param registeredClientRepository 客户端信息
         * @param redisTemplate              资源信息
         * @param biscuitSecurityProperties  配置信息
         * @return OAuth2AuthorizationService
         */
        @Bean
        public OAuth2AuthorizationService authorizationService(RegisteredClientRepository registeredClientRepository,
                                                               @Qualifier("oauth2RedisTemplate") RedisTemplate<Object, Object> redisTemplate,
                                                               UserDetailsService userDetailsService,
                                                               BiscuitSecurityProperties biscuitSecurityProperties) {
            return new RedisOAuth2AuthorizationService(registeredClientRepository,
                    redisTemplate, userDetailsService, biscuitSecurityProperties);
        }

        /**
         * 基于redis 认证的 OAuth2AuthorizationService
         *
         * @param biscuitSecurityProperties 配置信息
         * @param redisTemplate             资源信息
         * @return OAuth2AuthorizationConsentService
         */
        @Bean
        public OAuth2AuthorizationConsentService authorizationConsentService(@Qualifier("oauth2RedisTemplate") RedisTemplate<Object, Object> redisTemplate,
                                                                             BiscuitSecurityProperties biscuitSecurityProperties) {
            return new RedisOAuth2AuthorizationConsentService(redisTemplate, biscuitSecurityProperties);
        }

        @Bean("oauth2RedisTemplate")
        public RedisTemplate<Object, Object> oauth2RedisTemplate(RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setConnectionFactory(redisConnectionFactory);
            return redisTemplate;
        }


    }

    /**
     * jdbc认证服务的token配置
     */
    @Configuration
    @ConditionalOnProperty(prefix = "biscuit.security", name = "token-store-type", havingValue = "jdbc")
    @ConditionalOnClass(JdbcOperations.class)
    public static class JdbcOauth2AuthorizationServiceConfig {
        /**
         * 基于jdbc的OAuth2AuthorizationService服务
         *
         * @param jdbcOperations             jdbc
         * @param registeredClientRepository 客户端信息
         * @return OAuth2AuthorizationService
         */
        @Bean
        @ConditionalOnMissingBean
        public OAuth2AuthorizationService jdbcOauth2AuthorizationService(JdbcOperations jdbcOperations,
                                                                         RegisteredClientRepository registeredClientRepository) {
            return new JdbcOAuth2AuthorizationService(jdbcOperations, registeredClientRepository);
        }

        /**
         * 基于jdbc的OAuth2AuthorizationConsentService服务
         *
         * @return OAuth2AuthorizationConsentService
         */
        @Bean
        @ConditionalOnMissingBean
        public OAuth2AuthorizationConsentService authorizationConsentService(JdbcOperations jdbcOperations,
                                                                             RegisteredClientRepository registeredClientRepository) {
            return new JdbcOAuth2AuthorizationConsentService(jdbcOperations, registeredClientRepository);
        }
    }
}
