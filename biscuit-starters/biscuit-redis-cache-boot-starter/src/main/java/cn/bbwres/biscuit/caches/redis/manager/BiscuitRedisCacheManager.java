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

package cn.bbwres.biscuit.caches.redis.manager;

import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;

/**
 * 缓存管理器
 *
 * @author zhanglinfeng
 */
public class BiscuitRedisCacheManager extends RedisCacheManager {

    private final String delimitSymbol;


    /**
     * Creates new {@link RedisCacheManager} using given {@link RedisCacheWriter} and default
     * {@link RedisCacheConfiguration}.
     *
     * @param cacheWriter                must not be {@literal null}.
     * @param defaultCacheConfiguration  must not be {@literal null}. Maybe just use
     *                                   {@link RedisCacheConfiguration#defaultCacheConfig()}.
     * @param initialCacheConfigurations Map of known cache names along with the configuration to use for those caches.
     *                                   Must not be {@literal null}.
     * @param allowInFlightCacheCreation if set to {@literal false} this cache manager is limited to the initial cache
     *                                   configurations and will not create new caches at runtime.
     * @since 2.0.4
     */
    public BiscuitRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration,
                                    Map<String, RedisCacheConfiguration> initialCacheConfigurations,
                                    boolean allowInFlightCacheCreation, String delimitSymbol) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations, allowInFlightCacheCreation);
        this.delimitSymbol = delimitSymbol;
    }

    /**
     * @param name        must not be {@literal null}.
     * @param cacheConfig can be {@literal null}.
     * @return RedisCache
     */
    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        if (name.contains(delimitSymbol)) {
            String[] array = StringUtils.delimitedListToStringArray(name, delimitSymbol);
            name = array[0];
            // 解析TTL
            if (array.length > 1) {
                long ttl = Long.parseLong(array[1]);
                cacheConfig = cacheConfig.entryTtl(Duration.ofSeconds(ttl));
            }
        }
        return super.createRedisCache(name, cacheConfig);
    }


    public static class BiscuitRedisCacheManagerBuilder {

        private @Nullable RedisCacheWriter cacheWriter;
        private CacheStatisticsCollector statisticsCollector = CacheStatisticsCollector.none();
        private RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        private final Map<String, RedisCacheConfiguration> initialCaches = new LinkedHashMap<>();
        private boolean enableTransactions;
        boolean allowInFlightCacheCreation = true;

        /**
         * 缓存时间分隔符
         */
        private String delimitSymbol;


        private BiscuitRedisCacheManagerBuilder() {
        }

        private BiscuitRedisCacheManagerBuilder(RedisCacheWriter cacheWriter) {
            this.cacheWriter = cacheWriter;
        }

        /**
         * Entry point for builder style {@link RedisCacheManager} configuration.
         *
         * @param connectionFactory must not be {@literal null}.
         * @return new {@link RedisCacheManager.RedisCacheManagerBuilder}.
         */
        public static BiscuitRedisCacheManagerBuilder fromConnectionFactory(RedisConnectionFactory connectionFactory) {

            Assert.notNull(connectionFactory, "ConnectionFactory must not be null!");

            return new BiscuitRedisCacheManagerBuilder(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory));
        }

        /**
         * Entry point for builder style {@link RedisCacheManager} configuration.
         *
         * @param cacheWriter must not be {@literal null}.
         * @return new {@link RedisCacheManager.RedisCacheManagerBuilder}.
         */
        public static BiscuitRedisCacheManagerBuilder fromCacheWriter(RedisCacheWriter cacheWriter) {

            Assert.notNull(cacheWriter, "CacheWriter must not be null!");

            return new BiscuitRedisCacheManagerBuilder(cacheWriter);
        }

        /**
         * Define a default {@link RedisCacheConfiguration} applied to dynamically created {@link RedisCache}s.
         *
         * @param defaultCacheConfiguration must not be {@literal null}.
         * @return this {@link RedisCacheManager.RedisCacheManagerBuilder}.
         */
        public BiscuitRedisCacheManagerBuilder cacheDefaults(RedisCacheConfiguration defaultCacheConfiguration) {

            Assert.notNull(defaultCacheConfiguration, "DefaultCacheConfiguration must not be null!");

            this.defaultCacheConfiguration = defaultCacheConfiguration;

            return this;
        }

        /**
         * Configure a {@link RedisCacheWriter}.
         *
         * @param cacheWriter must not be {@literal null}.
         * @return this {@link BiscuitRedisCacheManagerBuilder}.
         * @since 2.3
         */
        public BiscuitRedisCacheManagerBuilder cacheWriter(RedisCacheWriter cacheWriter) {

            Assert.notNull(cacheWriter, "CacheWriter must not be null!");

            this.cacheWriter = cacheWriter;

            return this;
        }

        /**
         * Enable {@link RedisCache}s to synchronize cache put/evict operations with ongoing Spring-managed transactions.
         *
         * @return this {@link BiscuitRedisCacheManagerBuilder}.
         */
        public BiscuitRedisCacheManagerBuilder transactionAware() {

            this.enableTransactions = true;

            return this;
        }

        /**
         * Append a {@link Set} of cache names to be pre initialized with current {@link RedisCacheConfiguration}.
         * <strong>NOTE:</strong> This calls depends on {@link #cacheDefaults(RedisCacheConfiguration)} using whatever
         * default {@link RedisCacheConfiguration} is present at the time of invoking this method.
         *
         * @param cacheNames must not be {@literal null}.
         * @return this {@link BiscuitRedisCacheManagerBuilder}.
         */
        public BiscuitRedisCacheManagerBuilder initialCacheNames(Set<String> cacheNames) {

            Assert.notNull(cacheNames, "CacheNames must not be null!");

            cacheNames.forEach(it -> withCacheConfiguration(it, defaultCacheConfiguration));
            return this;
        }

        /**
         * Append a {@link Map} of cache name/{@link RedisCacheConfiguration} pairs to be pre initialized.
         *
         * @param cacheConfigurations must not be {@literal null}.
         * @return this {@link BiscuitRedisCacheManagerBuilder}.
         */
        public BiscuitRedisCacheManagerBuilder withInitialCacheConfigurations(
                Map<String, RedisCacheConfiguration> cacheConfigurations) {

            Assert.notNull(cacheConfigurations, "CacheConfigurations must not be null!");
            cacheConfigurations.forEach((cacheName, configuration) -> Assert.notNull(configuration,
                    String.format("RedisCacheConfiguration for cache %s must not be null!", cacheName)));

            this.initialCaches.putAll(cacheConfigurations);
            return this;
        }

        /**
         * @param cacheName
         * @param cacheConfiguration
         * @return this {@link BiscuitRedisCacheManagerBuilder}.
         * @since 2.2
         */
        public BiscuitRedisCacheManagerBuilder withCacheConfiguration(String cacheName,
                                                                      RedisCacheConfiguration cacheConfiguration) {

            Assert.notNull(cacheName, "CacheName must not be null!");
            Assert.notNull(cacheConfiguration, "CacheConfiguration must not be null!");

            this.initialCaches.put(cacheName, cacheConfiguration);
            return this;
        }

        /**
         * Disable in-flight {@link org.springframework.cache.Cache} creation for unconfigured caches.
         * <p>
         * {@link RedisCacheManager#getMissingCache(String)} returns {@literal null} for any unconfigured
         * {@link org.springframework.cache.Cache} instead of a new {@link RedisCache} instance. This allows eg.
         * {@link org.springframework.cache.support.CompositeCacheManager} to chime in.
         *
         * @return this {@link BiscuitRedisCacheManagerBuilder}.
         * @since 2.0.4
         */
        public BiscuitRedisCacheManagerBuilder disableCreateOnMissingCache() {

            this.allowInFlightCacheCreation = false;
            return this;
        }

        /**
         * Get the {@link Set} of cache names for which the builder holds {@link RedisCacheConfiguration configuration}.
         *
         * @return an unmodifiable {@link Set} holding the name of caches for which a {@link RedisCacheConfiguration
         * configuration} has been set.
         * @since 2.2
         */
        public Set<String> getConfiguredCaches() {
            return Collections.unmodifiableSet(this.initialCaches.keySet());
        }

        /**
         * Get the {@link RedisCacheConfiguration} for a given cache by its name.
         *
         * @param cacheName must not be {@literal null}.
         * @return {@link Optional#empty()} if no {@link RedisCacheConfiguration} set for the given cache name.
         * @since 2.2
         */
        public Optional<RedisCacheConfiguration> getCacheConfigurationFor(String cacheName) {
            return Optional.ofNullable(this.initialCaches.get(cacheName));
        }

        /**
         * @return
         * @since 2.4
         */
        public BiscuitRedisCacheManagerBuilder enableStatistics() {

            this.statisticsCollector = CacheStatisticsCollector.create();
            return this;
        }

        /**
         * 设置分隔符
         *
         * @param delimitSymbol 分隔符
         * @return
         */
        public BiscuitRedisCacheManagerBuilder setDelimitSymbol(String delimitSymbol) {
            this.delimitSymbol = delimitSymbol;
            return this;
        }

        /**
         * Create new instance of {@link BiscuitRedisCacheManager} with configuration options applied.
         *
         * @return new instance of {@link BiscuitRedisCacheManager}.
         */
        public BiscuitRedisCacheManager build() {

            Assert.state(cacheWriter != null,
                    "CacheWriter must not be null! You can provide one via 'RedisCacheManagerBuilder#cacheWriter(RedisCacheWriter)'.");

            RedisCacheWriter theCacheWriter = cacheWriter;

            if (!statisticsCollector.equals(CacheStatisticsCollector.none())) {
                theCacheWriter = cacheWriter.withStatisticsCollector(statisticsCollector);
            }

            BiscuitRedisCacheManager cm = new BiscuitRedisCacheManager(theCacheWriter, defaultCacheConfiguration,
                    initialCaches, allowInFlightCacheCreation, delimitSymbol);

            cm.setTransactionAware(enableTransactions);

            return cm;
        }
    }
}
