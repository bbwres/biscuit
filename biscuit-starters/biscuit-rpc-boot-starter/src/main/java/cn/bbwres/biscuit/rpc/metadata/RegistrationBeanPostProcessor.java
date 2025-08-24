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

package cn.bbwres.biscuit.rpc.metadata;

import cn.bbwres.biscuit.rpc.properties.RpcSecurityProperties;
import cn.bbwres.biscuit.rpc.security.RpcSecurityAlgorithmContainer;
import cn.bbwres.biscuit.rpc.security.RpcSecurityAlgorithmSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.client.serviceregistry.Registration;

import java.util.Map;
import java.util.Objects;

/**
 * 服务注册处理
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
public class RegistrationBeanPostProcessor implements BeanPostProcessor {
    public RegistrationBeanPostProcessor(RpcSecurityProperties rpcSecurityProperties,
                                         RpcSecurityAlgorithmContainer rpcSecurityAlgorithmContainer) {
        this.rpcSecurityProperties = rpcSecurityProperties;
        this.rpcSecurityAlgorithmContainer = rpcSecurityAlgorithmContainer;
    }

    private final RpcSecurityProperties rpcSecurityProperties;

    private final RpcSecurityAlgorithmContainer rpcSecurityAlgorithmContainer;


    /**
     * {@inheritDoc}
     * <p>
     * 初始化
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Registration) {
            Map<String, String> metadata = ((Registration) bean).getMetadata();
            RpcSecurityAlgorithmSupport support = rpcSecurityAlgorithmContainer.getRpcSecurityAlgorithmSupport(rpcSecurityProperties.getSecurityAlgorithm(), false);
            if (Objects.isNull(support)) {
                return bean;
            }
            metadata.putAll(support.initSecurityMetadata());
        }
        return bean;
    }
}
