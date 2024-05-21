package cn.bbwres.biscuit.redis.lock.config;

import cn.bbwres.biscuit.redis.lock.aop.RedisLockAspect;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.List;

/**
 * redis 监听配置
 *
 * @author zhanglinfeng
 */
@Slf4j
@AutoConfiguration
public class RedisListenerConfig {


    /**
     * 初始化redisson的配置文件
     *
     * @param redisProperties
     * @return
     */
    @Bean
    @ConditionalOnClass(RedisProperties.class)
    public Config redissonConfig(RedisProperties redisProperties) {
        Config config = new Config();
        String redisLink = "redis://%s";
        if (redisProperties.isSsl()) {
            redisLink = "rediss://%s";
        }
        //主从模式
        if (redisProperties.getCluster() != null) {
            RedisProperties.Cluster cluster = redisProperties.getCluster();
            config.useClusterServers()
                    .setPassword(redisProperties.getPassword())
                    .setConnectTimeout(redisProperties.getTimeout().toMillisPart())
                    .addNodeAddress(convertAddress(redisLink, cluster.getNodes()));
            return config;
        }

        //哨兵配置
        if (redisProperties.getSentinel() != null) {
            RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
            config.useSentinelServers()
                    .setPassword(redisProperties.getPassword())
                    .setConnectTimeout(redisProperties.getTimeout() != null ? redisProperties.getTimeout().toMillisPart() : 10000)
                    .setMasterName(sentinel.getMaster())
                    .setSentinelPassword(sentinel.getPassword())
                    .setDatabase(redisProperties.getDatabase())
                    .addSentinelAddress(convertAddress(redisLink, sentinel.getNodes()));
            return config;
        }
        //单机模式
        config.useSingleServer()
                .setPassword(redisProperties.getPassword())
                .setAddress(String.format(redisLink, redisProperties.getHost() + ":" + redisProperties.getPort()))
                .setConnectTimeout(redisProperties.getTimeout() != null ? redisProperties.getTimeout().toMillisPart() : 10000)
                .setDatabase(redisProperties.getDatabase());

        return config;

    }

    /**
     * redis 操作类
     *
     * @return
     */
    @Bean
    @ConditionalOnClass(Config.class)
    public RedissonClient redissonClient(Config redissonConfig) {
        return Redisson.create(redissonConfig);
    }


    /**
     * 初始化注解信息
     *
     * @return
     */
    @Bean
    @ConditionalOnClass(RedissonClient.class)
    public RedisLockAspect redisLockAspect(RedissonClient redissonClient,
                                           ObjectProvider<SpelExpressionParser> spelExpressionParserObjectProvider) {
        return new RedisLockAspect(redissonClient, spelExpressionParserObjectProvider.getIfAvailable(SpelExpressionParser::new));
    }


    /**
     * 转换链接参数
     *
     * @return
     */
    private String[] convertAddress(String baseUrl, List<String> configUrl) {
        return configUrl.stream().map(s -> String.format(baseUrl, s)).toArray(String[]::new);
    }
}
