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
 * @version $Id: $Id
 */
@Slf4j
public class MongodbBeanContextAware implements ApplicationContextAware {


    /** {@inheritDoc} */
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
