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

package cn.bbwres.biscuit.gateway.route;

import cn.bbwres.biscuit.gateway.GatewayProperties;
import cn.bbwres.biscuit.nacos.operation.AbstractNacosProcessor;
import cn.bbwres.biscuit.nacos.operation.NacosConfigOperation;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;

/**
 * 网关动态路由配置
 *
 * @author zhanglinfeng
 */
public class GatewayRouteNacosProcessor extends AbstractNacosProcessor {
    private static final String APP_NAME = "spring.application.name";
    private static final String DYNAMIC_ROUTE = "dynamic-route";

    private final DefaultGatewayRoute defaultGatewayRoute;
    private final GatewayProperties gatewayProperties;

    private final Environment environment;

    public GatewayRouteNacosProcessor(NacosConfigOperation nacosConfigOperation,
                                      DefaultGatewayRoute defaultGatewayRoute, GatewayProperties gatewayProperties,
                                      Environment environment) {
        super(nacosConfigOperation);
        this.defaultGatewayRoute = defaultGatewayRoute;
        this.gatewayProperties = gatewayProperties;
        this.environment = environment;
    }

    /**
     * 获取Group
     *
     * @return
     */
    @Override
    public String getGroup() {
        return gatewayProperties.getDynamicRouteGroup();
    }

    /**
     * 获取DataId
     *
     * @return
     */
    @Override
    public String getDataId() {
        if (!ObjectUtils.isEmpty(gatewayProperties.getDynamicRouteDataId())) {
            return gatewayProperties.getDynamicRouteDataId();
        }

        return environment.getProperty(APP_NAME) + DYNAMIC_ROUTE;
    }

    /**
     * 处理配置回调
     *
     * @param config
     */
    @Override
    public void callbackConfig(String config) {
        defaultGatewayRoute.updateAll(config);
    }
}
