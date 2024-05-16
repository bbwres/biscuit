/*
 * Copyright 2013-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.openfeign.loadbalancer;

import cn.bbwres.biscuit.rpc.constants.RpcConstants;
import cn.bbwres.biscuit.rpc.utils.SecurityUtils;
import feign.Client;
import feign.Request;
import feign.Response;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.CompletionContext;
import org.springframework.cloud.client.loadbalancer.LoadBalancerLifecycle;
import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.cloud.client.loadbalancer.ResponseData;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * @author Olga Maciaszek-Sharma
 * <p>
 * A utility class for handling {@link LoadBalancerLifecycle} calls.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
final class LoadBalancerUtils {

    private LoadBalancerUtils() {
        throw new IllegalStateException("Can't instantiate a utility class");
    }

    static Response executeWithLoadBalancerLifecycleProcessing(Client feignClient, Request.Options options,
                                                               Request feignRequest, org.springframework.cloud.client.loadbalancer.Request lbRequest,
                                                               org.springframework.cloud.client.loadbalancer.Response<ServiceInstance> lbResponse,
                                                               Set<LoadBalancerLifecycle> supportedLifecycleProcessors, boolean loadBalanced, boolean useRawStatusCodes)
            throws IOException {
        supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onStartRequest(lbRequest, lbResponse));
        try {
            if (loadBalanced && lbResponse.hasServer()) {
                ServiceInstance serviceInstance = lbResponse.getServer();
                Map<String, String> metadata = serviceInstance.getMetadata();
                String clientName = serviceInstance.getServiceId();
                String clientPassword = metadata.get(RpcConstants.CLIENT_PASSWORD);
                //加密算法的实现
                String currentTimeMillis = System.currentTimeMillis() + "";
                String authorization = SecurityUtils.hashDataInfo(clientName, clientPassword, currentTimeMillis);
                feignRequest.headers().put(RpcConstants.AUTHORIZATION_HEADER_NAME, List.of(authorization));
                feignRequest.headers().put(RpcConstants.CLIENT_TIME_HEADER_NAME, List.of(currentTimeMillis));
            }

            Response response = feignClient.execute(feignRequest, options);
            if (loadBalanced) {
                supportedLifecycleProcessors.forEach(
                        lifecycle -> lifecycle.onComplete(new CompletionContext<>(CompletionContext.Status.SUCCESS,
                                lbRequest, lbResponse, buildResponseData(response, useRawStatusCodes))));
            }
            return response;
        } catch (Exception exception) {
            if (loadBalanced) {
                supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onComplete(
                        new CompletionContext<>(CompletionContext.Status.FAILED, exception, lbRequest, lbResponse)));
            }
            throw exception;
        }
    }

    static ResponseData buildResponseData(Response response, boolean useRawStatusCodes) {
        HttpHeaders responseHeaders = new HttpHeaders();
        response.headers().forEach((key, value) -> responseHeaders.put(key, new ArrayList<>(value)));
        if (useRawStatusCodes) {
            return new ResponseData(responseHeaders, null, buildRequestData(response.request()), response.status());
        }
        return new ResponseData(HttpStatus.resolve(response.status()), responseHeaders, null,
                buildRequestData(response.request()));
    }

    static RequestData buildRequestData(Request request) {
        HttpHeaders requestHeaders = new HttpHeaders();
        request.headers().forEach((key, value) -> requestHeaders.put(key, new ArrayList<>(value)));
        return new RequestData(HttpMethod.resolve(request.httpMethod().name()), URI.create(request.url()),
                requestHeaders, null, new HashMap<>(16));
    }

    static Response executeWithLoadBalancerLifecycleProcessing(Client feignClient, Request.Options options,
                                                               Request feignRequest, org.springframework.cloud.client.loadbalancer.Request lbRequest,
                                                               org.springframework.cloud.client.loadbalancer.Response<ServiceInstance> lbResponse,
                                                               Set<LoadBalancerLifecycle> supportedLifecycleProcessors, boolean useRawStatusCodes) throws IOException {
        return executeWithLoadBalancerLifecycleProcessing(feignClient, options, feignRequest, lbRequest, lbResponse,
                supportedLifecycleProcessors, true, useRawStatusCodes);
    }

}
