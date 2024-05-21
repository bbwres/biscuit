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

import cn.bbwres.biscuit.mybatis.application.MybatisPlusPropertiesBeanPostProcessor;
import cn.bbwres.biscuit.mybatis.handler.DefaultDataFieldFillHandler;
import cn.bbwres.biscuit.mybatis.handler.DefaultTenantLineHandler;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * mybatis-plus 自动装配
 *
 * @author zhanglinfeng
 */
@AutoConfiguration
@EnableTransactionManagement
@EnableConfigurationProperties({MybatisProperties.class, MybatisTenantProperties.class})
@MapperScan(value = "${mybatis-plus.mapper.base-packages}", annotationClass = Mapper.class)
public class MybatisPlusAutoConfigure {


    /**
     * mybatis plus 参数配置
     *
     * @return MybatisPlusPropertiesBeanPostProcessor
     */
    @Bean
    public MybatisPlusPropertiesBeanPostProcessor mybatisPlusPropertiesBeanPostProcessor() {
        return new MybatisPlusPropertiesBeanPostProcessor();
    }


    /**
     * 配置分页插件
     *
     * @return MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    /**
     * 元数据填充
     *
     * @param mybatisTenantProperties
     * @param mybatisProperties
     * @return
     */
    @Bean
    public MetaObjectHandler metaObjectHandler(MybatisTenantProperties mybatisTenantProperties,
                                               MybatisProperties mybatisProperties) {
        return new DefaultDataFieldFillHandler(mybatisProperties, mybatisTenantProperties);
    }


    /**
     * 租户插件
     * 默认为不启用
     *
     * @param mybatisTenantProperties
     * @param mybatisProperties
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "mybatis-plus.tenant", name = "enabled", havingValue = "true")
    public TenantLineHandler defaultTenantLineHandler(MybatisTenantProperties mybatisTenantProperties,
                                                      MybatisProperties mybatisProperties) {
        return new DefaultTenantLineHandler(mybatisTenantProperties, mybatisProperties);
    }

}
