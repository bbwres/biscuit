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

package cn.bbwres.biscuit.rpc.filter;

import cn.bbwres.biscuit.rpc.properties.RpcProperties;
import cn.bbwres.biscuit.rpc.properties.SecurityProperties;
import cn.bbwres.biscuit.rpc.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequestTransformer;
import org.springframework.http.HttpRequest;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 负载均衡请求参数增强
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
public class RpcLoadBalancerRequestTransformer implements LoadBalancerRequestTransformer {

    private final RpcProperties rpcProperties;

    public RpcLoadBalancerRequestTransformer(RpcProperties rpcProperties) {
        this.rpcProperties = rpcProperties;
    }


    /** {@inheritDoc} */
    @Override
    public HttpRequest transformRequest(HttpRequest request, ServiceInstance instance) {
        if (Objects.isNull(instance)) {
            return request;
        }
        SecurityProperties securityProperties = rpcProperties.getSecurity();
        if (Objects.isNull(securityProperties) || !securityProperties.isEnable()) {
            log.debug("当前远程调用安全配置未开启!");
            return request;
        }
        Map<String, List<String>> stringListMap = SecurityUtils.putHeaderAuthorizationInfo(instance);
        if (!CollectionUtils.isEmpty(stringListMap)) {
            for (String headerName : stringListMap.keySet()) {
                request.getHeaders().put(headerName, stringListMap.get(headerName));
            }
        }
        return request;
    }
}
