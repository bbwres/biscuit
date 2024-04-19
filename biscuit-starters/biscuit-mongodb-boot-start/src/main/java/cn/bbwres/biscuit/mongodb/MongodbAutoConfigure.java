package cn.bbwres.biscuit.mongodb;

import cn.bbwres.biscuit.mongodb.config.MongodbProperties;
import cn.bbwres.biscuit.mongodb.dao.ExtendSimpleMongoRepository;
import cn.bbwres.biscuit.mongodb.processor.MongodbBeanContextAware;
import com.mongodb.client.MongoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Mongodb 多数据源 自动配置
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
@Configuration
@ConditionalOnClass(MongoClient.class)
@EnableConfigurationProperties(MongodbProperties.class)
@EnableTransactionManagement
@EnableMongoRepositories(basePackages = "${spring.data.mongodb.repositories.base-packages}",
        repositoryBaseClass = ExtendSimpleMongoRepository.class)
public class MongodbAutoConfigure {


    /**
     * <p>mongodbBeanContextAware.</p>
     *
     * @return a {@link cn.bbwres.biscuit.mongodb.processor.MongodbBeanContextAware} object
     */
    @Bean
    public MongodbBeanContextAware mongodbBeanContextAware() {
        return new MongodbBeanContextAware();
    }


    /**
     * 配置移除class
     *
     * @param factory
     * @param context
     * @param conversions
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring.data.mongodb.remove", name = "class", havingValue = "true", matchIfMissing = false)
    MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory factory, MongoMappingContext context,
                                                MongoCustomConversions conversions) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
        mappingConverter.setCustomConversions(conversions);
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return mappingConverter;
    }


}
