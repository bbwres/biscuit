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
import feign.Request;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.openfeign.loadbalancer.LoadBalancerFeignRequestTransformer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 负载均衡请求参数增强
 *
 * @author zhanglinfeng
 */
@Slf4j
public class RpcLoadBalancerFeignRequestTransformer implements LoadBalancerFeignRequestTransformer {

    private final RpcSecurityProperties rpcSecurityProperties;

    private final RpcProperties rpcProperties;


    private final RpcSecurityAlgorithmContainer rpcSecurityAlgorithmContainer;

    public RpcLoadBalancerFeignRequestTransformer(RpcSecurityProperties rpcSecurityProperties, RpcProperties rpcProperties,
                                                  RpcSecurityAlgorithmContainer rpcSecurityAlgorithmContainer) {
        this.rpcSecurityProperties = rpcSecurityProperties;
        this.rpcProperties = rpcProperties;
        this.rpcSecurityAlgorithmContainer = rpcSecurityAlgorithmContainer;
    }


    /**
     * Allows transforming load-balanced requests based on the provided
     * {@link ServiceInstance}.
     *
     * @param request  Original request.
     * @param instance ServiceInstance returned from LoadBalancer.
     * @return New request or original request
     */
    @Override
    public Request transformRequest(Request request, ServiceInstance instance) {
        if (rpcProperties.isTransmitUserInfo()) {
            //透传用户信息
            UserBaseInfo userBaseInfo = UserInfoContext.getCurrentContext();
            if (!ObjectUtils.isEmpty(userBaseInfo)) {
                RequestTemplate requestTemplate = request.requestTemplate();
                requestTemplate.header(rpcProperties.getUserInfoHeaderName(), JsonUtil.toJsonBase64(userBaseInfo, true));
                request = Request.create(request.httpMethod(), request.url(), requestTemplate.headers(), request.body(),
                        request.charset(), requestTemplate);
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

        RequestTemplate requestTemplate = request.requestTemplate();

        String path = requestTemplate.path();
        String targetPath = requestTemplate.feignTarget().url();

        Map<String, List<String>> stringListMap = rpcSecurityAlgorithmSupport.putHeaderAuthorizationInfo(instance, path.replaceFirst(targetPath,""));
        if (!CollectionUtils.isEmpty(stringListMap)) {
            for (String headerName : stringListMap.keySet()) {
                request.header(headerName, stringListMap.get(headerName));
                requestTemplate.header(headerName, stringListMap.get(headerName));
            }
        }
        return Request.create(request.httpMethod(), request.url(), requestTemplate.headers(), request.body(),
                request.charset(), requestTemplate);

    }
}
