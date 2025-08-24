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

package cn.bbwres.biscuit.rpc.security;

import cn.bbwres.biscuit.rpc.constants.RpcConstants;
import cn.bbwres.biscuit.rpc.properties.RpcSecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * md5算法支持
 *
 * @author zhanglinfeng
 */
@Slf4j
public abstract class AbstractRpcSecurityAlgorithmHashSupport extends AbstractRpcSecurityAlgorithmSupport {


    protected AbstractRpcSecurityAlgorithmHashSupport(RpcSecurityProperties rpcSecurityProperties,
                                                      Environment environment) {
        super(rpcSecurityProperties, environment);
    }

    /**
     * 检查hash值是否符合
     *
     * @param instance          服务实例
     * @param headerMap         请求头参数
     * @param currentTimeMillis 时间戳
     * @param requestUrl        请求地址
     * @return true 匹配，false 不匹配
     */
    @Override
    protected boolean checkRequestDataInfo(ServiceInstance instance, Map<String, String> headerMap, String currentTimeMillis, String requestUrl) {
        String authorization = headerMap.get(RpcConstants.AUTHORIZATION_HEADER_NAME);
        String sourceSystem = headerMap.get(RpcConstants.CLIENT_SYSTEM_NAME_HEADER_NAME);
        String serviceSecurityKey = instance.getMetadata().get(RpcConstants.CLIENT_PASSWORD);
        String serviceName = instance.getServiceId();
        String waitSignStr = buildWaitSignStr(serviceName, currentTimeMillis, serviceSecurityKey, sourceSystem, requestUrl);
        String newAuthorization = hash(waitSignStr);
        return newAuthorization.equals(authorization);

    }

    /**
     * 请求头中放入认证参数
     *
     * @param sourceSystem 服务实例
     * @param instance     服务实例
     * @param requestUrl   请求路径
     * @return a {@link Map} object
     */
    @Override
    protected Map<String, List<String>> putHeaderAuthorization(String sourceSystem, ServiceInstance instance, String requestUrl) {

        Map<String, List<String>> headers = new HashMap<>(16);
        Map<String, String> metadata = instance.getMetadata();
        String serviceName = instance.getServiceId();
        String serviceSecurityKey = metadata.get(RpcConstants.CLIENT_PASSWORD);
        //加密算法的实现
        String currentTimeMillis = System.currentTimeMillis() + "";
        String waitSignStr = buildWaitSignStr(serviceName, currentTimeMillis, serviceSecurityKey, sourceSystem, requestUrl);
        String authorization = hash(waitSignStr);
        headers.put(RpcConstants.AUTHORIZATION_HEADER_NAME, List.of(authorization));
        headers.put(RpcConstants.CLIENT_TIME_HEADER_NAME, List.of(currentTimeMillis));
        headers.put(RpcConstants.CLIENT_SYSTEM_NAME_HEADER_NAME, List.of(sourceSystem));
        return headers;
    }

    /**
     * 加载签名字符串
     *
     * @param serviceName        服务名称
     * @param currentTimeMillis  当前时间戳
     * @param serviceSecurityKey 服务密钥
     * @param sourceSystem       来源系统
     * @param requestUrl         请求地址
     * @return 待签名字符串
     */
    protected String buildWaitSignStr(String serviceName, String currentTimeMillis, String serviceSecurityKey, String sourceSystem, String requestUrl) {
        return hash(serviceName + currentTimeMillis + sourceSystem + requestUrl) + serviceSecurityKey;
    }


    /**
     * hash 数据生成
     *
     * @param waitSignStr 待hash字符串
     * @return
     */
    protected abstract String hash(String waitSignStr);
}
