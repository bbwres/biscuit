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

package cn.bbwres.biscuit.mybatis.config;

import cn.bbwres.biscuit.entity.BaseEntity;
import cn.bbwres.biscuit.mybatis.application.MybatisFlexPropertiesBeanPostProcessor;
import cn.bbwres.biscuit.mybatis.listener.BiscuitFieldFillListener;
import cn.bbwres.biscuit.mybatis.listener.BiscuitTenantFactory;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.keygen.KeyGenerators;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * mybatis-plus 自动装配
 *
 * @author zhanglinfeng
 */
@AutoConfiguration
@EnableTransactionManagement
@EnableConfigurationProperties({MybatisProperties.class, MybatisTenantProperties.class})
@MapperScan(value = "${mybatis-config.mapper.base-packages}", annotationClass = Mapper.class)
public class MybatisFlexAutoConfigure {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(MybatisFlexAutoConfigure.class);


    /**
     * mybatis flex 参数配置
     *
     * @return mybatisFlexPropertiesBeanPostProcessor
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "mybatis-config", name = "enable-customize", havingValue = "true", matchIfMissing = true)
    public MybatisFlexPropertiesBeanPostProcessor mybatisFlexPropertiesBeanPostProcessor() {
        return new MybatisFlexPropertiesBeanPostProcessor();
    }

    /**
     * MyBatisFlex 配置参数
     */
    @Configuration
    public static class MyBatisFlexConfiguration implements MyBatisFlexCustomizer {


        private final BiscuitFieldFillListener biscuitFieldFillListener;
        private final MybatisProperties mybatisProperties;

        @Autowired
        public MyBatisFlexConfiguration(BiscuitFieldFillListener biscuitFieldFillListener,
                                        MybatisProperties mybatisProperties) {

            this.biscuitFieldFillListener = biscuitFieldFillListener;
            this.mybatisProperties = mybatisProperties;
        }

        @Override
        public void customize(FlexGlobalConfig flexGlobalConfig) {
            if (mybatisProperties.getEnableFieldFill()) {
                //设置BaseEntity类启用
                flexGlobalConfig.registerInsertListener(biscuitFieldFillListener, BaseEntity.class);
                flexGlobalConfig.registerUpdateListener(biscuitFieldFillListener, BaseEntity.class);
            }
            //默认使用雪花算法的id
            FlexGlobalConfig.KeyConfig keyConfig = new FlexGlobalConfig.KeyConfig();
            keyConfig.setKeyType(KeyType.Generator);
            keyConfig.setValue(KeyGenerators.snowFlakeId);
            flexGlobalConfig.setKeyConfig(keyConfig);
        }
    }


    /**
     * 监听器配置
     *
     * @param mybatisProperties
     * @param mybatisTenantProperties
     * @return
     */
    @Bean
    public BiscuitFieldFillListener biscuitFieldFillListener(MybatisProperties mybatisProperties,
                                                             MybatisTenantProperties mybatisTenantProperties) {
        return new BiscuitFieldFillListener(mybatisProperties, mybatisTenantProperties);
    }


    /**
     * 租户配置
     *
     * @param mybatisProperties
     * @param mybatisTenantProperties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "mybatis-config.tenant", name = "enabled", havingValue = "true")
    public BiscuitTenantFactory biscuitTenantFactory(MybatisProperties mybatisProperties,
                                                     MybatisTenantProperties mybatisTenantProperties) {
        return new BiscuitTenantFactory(mybatisTenantProperties, mybatisProperties);
    }


}
