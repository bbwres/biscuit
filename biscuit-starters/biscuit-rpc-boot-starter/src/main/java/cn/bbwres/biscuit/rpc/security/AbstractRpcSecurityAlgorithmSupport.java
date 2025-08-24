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
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 远程调用安全密钥计算
 *
 * @author zhanglinfeng
 */
@Slf4j
public abstract class AbstractRpcSecurityAlgorithmSupport implements RpcSecurityAlgorithmSupport {

    protected final RpcSecurityProperties rpcSecurityProperties;

    protected final Environment environment;

    protected AbstractRpcSecurityAlgorithmSupport(RpcSecurityProperties rpcSecurityProperties, Environment environment) {
        this.rpcSecurityProperties = rpcSecurityProperties;
        this.environment = environment;
    }


    /**
     * 初始化Metadata 数据
     *
     * @return
     */
    @Override
    public Map<String, String> initSecurityMetadata() {
        Map<String, String> metadata = new HashMap<>(16);
        metadata.put(RpcConstants.SERVICE_SECURITY_ALGORITHM, rpcSecurityProperties.getSecurityAlgorithm());
        addMetadata(metadata);
        return metadata;
    }

    /**
     * 请求头中放入认证参数
     *
     * @param instance   服务实例
     * @param requestUrl 请求路径
     * @return a {@link Map} object
     */
    @Override
    public Map<String, List<String>> putHeaderAuthorizationInfo(ServiceInstance instance, String requestUrl) {
        log.debug("当前计算安全信息请求路径:[{}]", requestUrl);
        String sourceSystem = environment.getProperty("spring.application.name");
        return putHeaderAuthorization(sourceSystem, instance, requestUrl);
    }


    /**
     * 检查hash值是否符合
     *
     * @param instance   服务实例
     * @param headerMap  请求头参数
     * @param requestUrl 请求路径
     * @return true 匹配，false 不匹配
     */
    @Override
    public boolean checkDataInfo(ServiceInstance instance, Map<String, String> headerMap, String requestUrl) {
        try {
            String currentTimeMillis = headerMap.get(RpcConstants.CLIENT_TIME_HEADER_NAME);
            if (rpcSecurityProperties.isEnableReplayCheck()) {
                if (ObjectUtils.isEmpty(currentTimeMillis)) {
                    log.debug("当前请求校验失败！未传入:[{}]", RpcConstants.CLIENT_TIME_HEADER_NAME);
                    return false;
                }
                long currentTimeMillisLong = Long.parseLong(currentTimeMillis);
                long now = System.currentTimeMillis();
                if (Math.abs(now - currentTimeMillisLong) > rpcSecurityProperties.getReplayCheckTime()) {
                    log.debug("当前请求校验！调用时间超过指定的延迟时间");
                    return false;
                }
            }
            return checkRequestDataInfo(instance, headerMap, currentTimeMillis, requestUrl);
        } catch (Exception e) {
            log.warn("安全检查异常!", e);
            return false;
        }

    }

    /**
     * 设置认证参数
     *
     * @param sourceSystem 来源系统
     * @param instance     服务实例
     * @param requestUrl   请求地址
     * @return
     */
    protected abstract Map<String, List<String>> putHeaderAuthorization(String sourceSystem, ServiceInstance instance, String requestUrl);

    /**
     * 增加metadata数据
     *
     * @param metadata 服务元数据
     */
    protected abstract void addMetadata(Map<String, String> metadata);

    /**
     * 检查请求参数
     *
     * @param instance          服务实例
     * @param headerMap         请求头参数
     * @param currentTimeMillis 当前请求时间戳
     * @param requestUrl        请求地址
     * @return true 匹配，false 不匹配
     */
    protected abstract boolean checkRequestDataInfo(ServiceInstance instance, Map<String, String> headerMap, String currentTimeMillis, String requestUrl);


}
