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

import cn.bbwres.biscuit.context.UserInfoContext;
import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.rpc.constants.RpcConstants;
import cn.bbwres.biscuit.rpc.properties.RpcProperties;
import cn.bbwres.biscuit.rpc.properties.RpcSecurityProperties;
import cn.bbwres.biscuit.rpc.security.RpcSecurityAlgorithmContainer;
import cn.bbwres.biscuit.rpc.security.RpcSecurityAlgorithmSupport;
import cn.bbwres.biscuit.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerClientRequestTransformer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.ClientRequest;

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
public class RpcLoadBalancerClientRequestTransformer implements LoadBalancerClientRequestTransformer {

    private final RpcSecurityProperties rpcSecurityProperties;

    private final RpcProperties rpcProperties;


    private final RpcSecurityAlgorithmContainer rpcSecurityAlgorithmContainer;

    public RpcLoadBalancerClientRequestTransformer(RpcSecurityProperties rpcSecurityProperties, RpcProperties rpcProperties,
                                                   RpcSecurityAlgorithmContainer rpcSecurityAlgorithmContainer) {
        this.rpcSecurityProperties = rpcSecurityProperties;
        this.rpcProperties = rpcProperties;
        this.rpcSecurityAlgorithmContainer = rpcSecurityAlgorithmContainer;
    }


    @Override
    public ClientRequest transformRequest(ClientRequest request, ServiceInstance instance) {
        if (rpcProperties.isTransmitUserInfo()) {
            //透传用户信息
            UserBaseInfo userBaseInfo = UserInfoContext.getCurrentContext();
            if ((!ObjectUtils.isEmpty(userBaseInfo))&&!request.headers().containsKey(rpcProperties.getUserInfoHeaderName())) {
                ClientRequest.Builder clientRequestBuilder = ClientRequest.from(request);
                clientRequestBuilder.header(rpcProperties.getUserInfoHeaderName(), JsonUtil.toJsonBase64(userBaseInfo, true));
                request = clientRequestBuilder.build();
            }
        }

        if (Objects.isNull(instance)) {
            return request;
        }
        String securityAlgorithm = instance.getMetadata().get(RpcConstants.SERVICE_SECURITY_ALGORITHM);
        if (ObjectUtils.isEmpty(securityAlgorithm)) {
            log.debug("当前请求的服务端没有设置安全信息!请求服务端:[{}]", instance.getInstanceId());
            return request;
        }
        RpcSecurityAlgorithmSupport rpcSecurityAlgorithmSupport = rpcSecurityAlgorithmContainer.getRpcSecurityAlgorithmSupport(securityAlgorithm, true);
        Map<String, List<String>> stringListMap = rpcSecurityAlgorithmSupport.putHeaderAuthorizationInfo(instance, request.url().getPath());
        ClientRequest.Builder clientRequestBuilder = ClientRequest.from(request);
        if (!CollectionUtils.isEmpty(stringListMap)) {
            for (String headerName : stringListMap.keySet()) {
                clientRequestBuilder.headers(header -> header.addAll(headerName, stringListMap.get(headerName)));
            }
        }

        return clientRequestBuilder.build();
    }
}
