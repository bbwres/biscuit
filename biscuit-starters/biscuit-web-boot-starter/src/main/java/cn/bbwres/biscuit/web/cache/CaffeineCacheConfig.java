package cn.bbwres.biscuit.web.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * CaffeineCache 配置
 *
 * @author zhanglinfeng
 */
@Slf4j
@Configuration
public class CaffeineCacheConfig {

    /**
     * 客户端本地缓存
     *
     * @return
     */
    @Bean
    public LoadingCache<String, String> clientCache() {
        return Caffeine
                .newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .expireAfter(new Expiry<String, String>() {
                    @Override
                    public long expireAfterCreate(@NonNull String key, @NonNull String value, long currentTime) {
                        return 0;
                    }

                    @Override
                    public long expireAfterUpdate(@NonNull String key, @NonNull String value, long currentTime, @NonNegative long currentDuration) {
                        return 0;
                    }
                    @Override
                    public long expireAfterRead(@NonNull String key, @NonNull String value, long currentTime, @NonNegative long currentDuration) {
                        return 0;
                    }
                })
                .maximumSize(1000).build(new CacheLoader<>() {

                    @Override
                    public @Nullable String load(@NonNull String key) throws Exception {
                        return null;
                    }

                });
    }


    public void test() {
        LoadingCache<String, String> cache = Caffeine
                .newBuilder()
                .maximumSize(1000).build(new CacheLoader<>() {

                    @Override
                    public @Nullable String load(@NonNull String key) throws Exception {
                        return null;
                    }

                });
    }


}
