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

package cn.bbwres.biscuit.mybatis.application;

import cn.bbwres.biscuit.mybatis.handler.BiscuitMybatisEnumTypeHandler;
import com.mybatisflex.core.handler.CompositeEnumTypeHandler;
import com.mybatisflex.spring.boot.MybatisFlexProperties;
import org.apache.ibatis.type.EnumTypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * mybatis config 参数配置
 *
 * @author zhanglinfeng
 */
public class MybatisFlexPropertiesBeanPostProcessor implements BeanPostProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(MybatisFlexPropertiesBeanPostProcessor.class);

    /**
     * 初始化
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof MybatisFlexProperties mybatisFlexProperties) {
            Class<?> typeHandler = mybatisFlexProperties.getConfiguration().getDefaultEnumTypeHandler();
            LOG.info("MybatisConfigurationInitializer:old: typeHandler = {}", typeHandler);
            if (typeHandler == null || EnumTypeHandler.class.equals(typeHandler)
                    || CompositeEnumTypeHandler.class.equals(typeHandler)) {
                //默认的枚举处理类为空，或者为默认的枚举处理类，则替换为自定义的枚举处理类
                mybatisFlexProperties.getConfiguration().setDefaultEnumTypeHandler(BiscuitMybatisEnumTypeHandler.class);
                LOG.info("MybatisConfigurationInitializer:new: typeHandler = {}", BiscuitMybatisEnumTypeHandler.class);
            }
            //设置sql插入
            MybatisFlexProperties.GlobalConfig globalConfig = mybatisFlexProperties.getGlobalConfig();
            return bean;
        }
        return bean;
    }
}
