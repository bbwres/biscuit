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

import cn.bbwres.biscuit.rpc.constants.RpcConstants;
import cn.bbwres.biscuit.rpc.properties.RpcSecurityProperties;
import cn.bbwres.biscuit.rpc.security.RpcSecurityAlgorithmContainer;
import cn.bbwres.biscuit.rpc.security.RpcSecurityAlgorithmSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequestTransformer;
import org.springframework.http.HttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

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

    private final RpcSecurityProperties rpcSecurityProperties;


    private final RpcSecurityAlgorithmContainer rpcSecurityAlgorithmContainer;

    public RpcLoadBalancerRequestTransformer(RpcSecurityProperties rpcSecurityProperties,
                                             RpcSecurityAlgorithmContainer rpcSecurityAlgorithmContainer) {
        this.rpcSecurityProperties = rpcSecurityProperties;
        this.rpcSecurityAlgorithmContainer = rpcSecurityAlgorithmContainer;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public HttpRequest transformRequest(HttpRequest request, ServiceInstance instance) {
        if (Objects.isNull(instance)) {
            return request;
        }
        String securityAlgorithm = instance.getMetadata().get(RpcConstants.SERVICE_SECURITY_ALGORITHM);
        if (ObjectUtils.isEmpty(securityAlgorithm)) {
            log.debug("当前请求的服务端没有设置安全信息!请求服务端:[{}]", instance.getInstanceId());
            return request;
        }
        RpcSecurityAlgorithmSupport rpcSecurityAlgorithmSupport = rpcSecurityAlgorithmContainer.getRpcSecurityAlgorithmSupport(securityAlgorithm, true);

        Map<String, List<String>> stringListMap = rpcSecurityAlgorithmSupport.putHeaderAuthorizationInfo(instance, request.getURI().getPath());
        if (!CollectionUtils.isEmpty(stringListMap)) {
            for (String headerName : stringListMap.keySet()) {
                request.getHeaders().put(headerName, stringListMap.get(headerName));
            }
        }
        return request;
    }
}
