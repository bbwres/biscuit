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

import org.springframework.cloud.client.ServiceInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 远程调用安全处理服务
 *
 * @author zhanglinfeng
 */
public interface RpcSecurityAlgorithmSupport {


    /**
     * 匹配的安全算法
     *
     * @return
     */
    String securityAlgorithm();


    /**
     * 初始化Metadata 数据
     *
     * @return
     */
    default Map<String, String> initSecurityMetadata() {
        return new HashMap<>(8);
    }


    /**
     * 检查hash值是否符合
     *
     * @param instance   服务实例
     * @param headerMap  请求头参数
     * @param requestUrl 请求路径
     * @return true 匹配，false 不匹配
     */
    boolean checkDataInfo(ServiceInstance instance, Map<String, String> headerMap, String requestUrl);

    /**
     * 请求头中放入认证参数
     *
     * @param instance   服务实例
     * @param requestUrl 请求路径
     * @return a {@link java.util.Map} object
     */
    Map<String, List<String>> putHeaderAuthorizationInfo(ServiceInstance instance, String requestUrl);

}
