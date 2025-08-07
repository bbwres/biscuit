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

import cn.bbwres.biscuit.gateway.route.entity.GatewayRouteInfoEntity;

import java.util.List;

/**
 * 网关路由更新
 *
 * @author zhanglinfeng
 */
public interface GatewayRoute {

    /**
     * 增加路由信息
     * @param gatewayRouteInfoEntity
     */
    void add(GatewayRouteInfoEntity gatewayRouteInfoEntity);

    /**
     * 修改路由信息
     * @param gatewayRouteInfoEntity
     */
    void modify(GatewayRouteInfoEntity gatewayRouteInfoEntity);

    /**
     * 删除路由信息
     * @param routeId
     */
    void delete(String routeId);
    

    /**
     * 更新数据
     *
     * @param gatewayRouteEntityList
     */
    void updateAll(List<GatewayRouteInfoEntity> gatewayRouteEntityList);

    /**
     * 更新数据
     *
     * @param gatewayStrategyRouteConfig
     */
    void updateAll(String gatewayStrategyRouteConfig);
}
