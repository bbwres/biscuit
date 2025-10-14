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

import cn.bbwres.biscuit.security.oauth2.service.redis.RedisOAuth2AuthorizationConsentService;
import cn.bbwres.biscuit.security.oauth2.service.redis.RedisOAuth2AuthorizationService;
import cn.bbwres.biscuit.security.oauth2.service.redis.repository.OAuth2AuthorizationGrantAuthorizationRepository;
import cn.bbwres.biscuit.security.oauth2.service.redis.repository.OAuth2UserConsentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.redis.repository.support.RedisRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.jdbc.core.JdbcOperations;
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
         * @param registeredClientRepository 客户端信息
         * @param authorizationGrantAuthorizationRepository 资源信息
         * @return OAuth2AuthorizationService
         */
        @Bean
        public OAuth2AuthorizationService authorizationService(RegisteredClientRepository registeredClientRepository,
                                                                    @Qualifier("redisOAuth2AuthorizationGrantAuthorizationRepository") OAuth2AuthorizationGrantAuthorizationRepository authorizationGrantAuthorizationRepository) {
            return new RedisOAuth2AuthorizationService(registeredClientRepository,
                    authorizationGrantAuthorizationRepository);
        }

        /**
         * 基于redis 认证的 OAuth2AuthorizationService
         * @param oauth2UserConsentRepository 资源信息
         * @return OAuth2AuthorizationConsentService
         */
        @Bean
        public OAuth2AuthorizationConsentService authorizationConsentService(@Qualifier("redisOAuth2UserConsentRepository") OAuth2UserConsentRepository oauth2UserConsentRepository) {
            return new RedisOAuth2AuthorizationConsentService(oauth2UserConsentRepository);
        }

        @Bean("oauth2RedisTemplate")
        public RedisTemplate<?, ?> oauth2RedisTemplate(RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(redisConnectionFactory);
            return redisTemplate;
        }


        /**
         * 手动创建并注册 OAuth2UserConsentRepository 的 Bean。
         *
         * @param oauth2RedisTemplate Spring Boot 自动配置好的 RedisOperations (RedisTemplate)。
         * @return StarterARepository 的实例。
         */
        @Bean("redisOAuth2UserConsentRepository")
        @ConditionalOnBean(RedisOperations.class)
        public OAuth2UserConsentRepository oauth2UserConsentRepository(@Qualifier("oauth2RedisTemplate") RedisTemplate<?, ?> oauth2RedisTemplate) {
            RedisKeyValueAdapter redisKeyValueAdapter = new RedisKeyValueAdapter(oauth2RedisTemplate);
            RedisKeyValueTemplate redisKeyValueTemplate = new RedisKeyValueTemplate(redisKeyValueAdapter, new RedisMappingContext());
            RepositoryFactorySupport factory = new RedisRepositoryFactory(redisKeyValueTemplate);
            return factory.getRepository(OAuth2UserConsentRepository.class);
        }

        /**
         * 手动创建并注册 OAuth2UserConsentRepository 的 Bean。
         *
         * @param oauth2RedisTemplate Spring Boot 自动配置好的 RedisOperations (RedisTemplate)。
         * @return StarterARepository 的实例。
         */
        @Bean("redisOAuth2AuthorizationGrantAuthorizationRepository")
        @ConditionalOnBean(RedisOperations.class)
        public OAuth2AuthorizationGrantAuthorizationRepository oauth2AuthorizationGrantAuthorizationRepository(@Qualifier("oauth2RedisTemplate") RedisTemplate<?, ?> oauth2RedisTemplate) {
            RedisKeyValueAdapter redisKeyValueAdapter = new RedisKeyValueAdapter(oauth2RedisTemplate);
            RedisKeyValueTemplate redisKeyValueTemplate = new RedisKeyValueTemplate(redisKeyValueAdapter, new RedisMappingContext());
            RepositoryFactorySupport factory = new RedisRepositoryFactory(redisKeyValueTemplate);
            return factory.getRepository(OAuth2AuthorizationGrantAuthorizationRepository.class);
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
