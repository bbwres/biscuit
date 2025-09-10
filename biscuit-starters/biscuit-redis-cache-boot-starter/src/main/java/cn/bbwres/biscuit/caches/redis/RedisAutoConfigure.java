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

package cn.bbwres.biscuit.caches.redis;

import cn.bbwres.biscuit.caches.redis.manager.BiscuitRedisCacheManager;
import cn.bbwres.biscuit.utils.JsonUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 开启注解式缓存
 * 设置EnableCaching为最高优先级，在aop时，最先执行，最后释放。
 * 可以确保 当@Transactional 与@CacheEvict一起使用时，默认设置的情况下，可能会因为先清除缓存后提交事务，从而产生缓存和数据库数据不一致的问题。
 *
 * @author zlf
 */
@EnableCaching(order = Ordered.HIGHEST_PRECEDENCE)
@AutoConfiguration
@EnableConfigurationProperties({BiscuitRedisProperties.class})
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class RedisAutoConfigure {

    /**
     * 配置 RedisTemplate
     *
     * @param connectionFactory           redis链接信息
     * @param jackson2JsonRedisSerializer json序列化
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory,
                                                       ObjectProvider<Jackson2JsonRedisSerializer<Object>> jackson2JsonRedisSerializer) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        jackson2JsonRedisSerializer.ifAvailable(serializer -> {
            //设置value 序列化
            template.setValueSerializer(serializer);
            //设置hash value 序列化
            template.setHashValueSerializer(serializer);
        });
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置序列化
     *
     * @return jackson2JsonRedisSerializer
     */
    @Bean
    @ConditionalOnProperty(prefix = "biscuit.redis", name = "enable-json-serializer-value", havingValue = "true", matchIfMissing = true)
    public Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        serializer.setObjectMapper(JsonUtil.getObjectMapper());
        return serializer;
    }

    /**
     * 缓存管理器
     *
     * @param redisConnectionFactory      redis链接信息
     * @param jackson2JsonRedisSerializer json序列化
     * @param biscuitRedisProperties      参数配置
     * @return CacheManager
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory,
                                     Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer,
                                     BiscuitRedisProperties biscuitRedisProperties) {

        BiscuitRedisCacheManager.BiscuitRedisCacheManagerBuilder builder = BiscuitRedisCacheManager
                .BiscuitRedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .setDelimitSymbol(biscuitRedisProperties.getDelimitSymbol())
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer)));
        return builder.build();
    }


}
