package cn.bbwres.biscuit.mongodb.processor;

import cn.bbwres.biscuit.mongodb.config.MongodbParams;
import cn.bbwres.biscuit.mongodb.config.MongodbProperties;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * 初始化Mongodb的管理bean
 *
 * @author zhanglinfeng
 */
@Slf4j
public class MongodbBeanContextAware implements ApplicationContextAware {


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MongodbProperties properties = applicationContext.getBean(MongodbProperties.class);
        Map<String, MongodbParams> config = properties.getConfig();
        if (CollectionUtils.isEmpty(config)) {
            log.info("当前未配置多数据源的mongodb参数");
            return;
        }
        MappingMongoConverter converter = applicationContext.getBean(MappingMongoConverter.class);

        for (String name : config.keySet()) {
            MongodbParams params = config.get(name);
            ConnectionString connectionString = new ConnectionString(params.getConnectionString());
            MongoClient mongoClient = MongoClients.create(connectionString);
            //获取BeanFactory
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
            defaultListableBeanFactory.registerSingleton(name + "MongoClient", mongoClient);
            //创建bean信息.
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(MongoTemplate.class);
            beanDefinitionBuilder.addConstructorArgValue(new SimpleMongoClientDatabaseFactory(mongoClient,connectionString.getDatabase()));
            beanDefinitionBuilder.addConstructorArgValue(converter);
            //动态注册bean.
            defaultListableBeanFactory.registerBeanDefinition(name + "MongoTemplate", beanDefinitionBuilder.getBeanDefinition());
        }
    }
}
