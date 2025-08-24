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

package cn.bbwres.biscuit.rpc.utils;

import cn.bbwres.biscuit.rpc.security.RpcSecurityAlgorithmContainer;
import cn.bbwres.biscuit.rpc.security.RpcSecurityAlgorithmSupport;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring bean 工具类
 *
 * @author zhanglinfeng
 */
public class SecurityUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        if (applicationContext == null) {
            applicationContext = context;
        }
    }


    /**
     * 获取安全算法实现
     *
     * @param securityAlgorithm 算法名称
     * @return
     */
    public static RpcSecurityAlgorithmSupport getRpcSecurityAlgorithmSupport(String securityAlgorithm, Boolean checkNull) {
        RpcSecurityAlgorithmContainer rpcSecurityAlgorithmContainer = applicationContext.getBean(RpcSecurityAlgorithmContainer.class);
        return rpcSecurityAlgorithmContainer.getRpcSecurityAlgorithmSupport(securityAlgorithm, checkNull);
    }
}
