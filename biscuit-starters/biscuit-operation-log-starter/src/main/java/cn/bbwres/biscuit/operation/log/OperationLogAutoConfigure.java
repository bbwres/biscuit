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

package cn.bbwres.biscuit.operation.log;

import cn.bbwres.biscuit.operation.log.aspect.OperationLogAspect;
import cn.bbwres.biscuit.operation.log.properties.OperationLogProperties;
import cn.bbwres.biscuit.operation.log.service.EnhanceOperationLogService;
import cn.bbwres.biscuit.operation.log.service.OperationLogSaveService;
import cn.bbwres.biscuit.operation.log.service.impl.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;

import java.util.List;

/**
 * 操作日志自动注入类
 *
 * @author zhanglinfeng
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({OperationLogProperties.class})
public class OperationLogAutoConfigure {


    /**
     * 切面配置
     *
     * @return OperationLogAspect
     */
    @Bean
    public OperationLogAspect operationLogAspect(List<EnhanceOperationLogService> enhanceOperationLogServices) {
        return new OperationLogAspect(enhanceOperationLogServices);
    }


    /**
     * 操作日志基本的属性设置服务
     *
     * @param environment 当前环境变量
     * @return enhanceOperationLogBaseService
     */
    @Bean("enhanceOperationLogBaseService")
    @Order(1)
    public EnhanceOperationLogService enhanceOperationLogBaseService(Environment environment) {
        return new EnhanceOperationLogBaseServiceImpl(environment);
    }

    /**
     * 操作日志请求参数设置服务
     *
     * @return enhanceOperationLogParamsService
     */
    @Bean("enhanceOperationLogParamsService")
    @Order(100)
    public EnhanceOperationLogService enhanceOperationLogParamsService() {
        return new EnhanceOperationLogParamsServiceImpl();
    }

    /**
     * 操作日志用户信息补充参数
     *
     * @return enhanceOperationLogParamsService
     */
    @Bean("enhanceOperationLogUserService")
    @Order(200)
    public EnhanceOperationLogService enhanceOperationLogUserService(OperationLogProperties operationLogProperties) {
        return new EnhanceOperationLogUserServiceImpl(operationLogProperties);
    }

    /**
     * 操作日志spel 表达式信息
     *
     * @return enhanceOperationLogSpringElService
     */
    @Bean("enhanceOperationLogSpringElService")
    @Order(300)
    public EnhanceOperationLogService enhanceOperationLogSpringElService() {
        return new EnhanceOperationLogSpringElServiceImpl(new SpelExpressionParser());
    }


    /**
     * 当包含webmvc包时注入
     */
    @ConditionalOnClass(DelegatingWebMvcConfiguration.class)
    protected static class WebEnhanceOperationLogConfiguration {
        /**
         * 操作日志web 相关参数
         *
         * @return enhanceOperationLogWebService
         */
        @Bean("enhanceOperationLogWebService")
        @Order(500)
        public EnhanceOperationLogService enhanceOperationLogWebService() {
            return new EnhanceOperationLogWebServiceImpl();
        }
    }

    /**
     * 当有操作日志保存实现包时注入
     */
    @ConditionalOnBean(OperationLogSaveService.class)
    protected static class SaveEnhanceOperationLogConfiguration {
        /**
         * 操作日志的保存
         *
         * @return saveOperationLogService
         */
        @Bean("saveOperationLogService")
        @Order(999999)
        public EnhanceOperationLogService saveOperationLogService(OperationLogSaveService operationLogSaveService) {
            return new SaveOperationLogServiceImpl(operationLogSaveService);
        }
    }


}
