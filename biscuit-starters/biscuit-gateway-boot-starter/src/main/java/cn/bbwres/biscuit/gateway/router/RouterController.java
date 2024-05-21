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

package cn.bbwres.biscuit.gateway.router;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

/**
 * 路由配置信息
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/router")
public class RouterController {

    private final RouteLocator routeLocator;


    @Value("${spring.application.name}")
    private String appName;


    /**
     * <p>appNames.</p>
     *
     * @return a {@link java.util.Set} object
     */
    @GetMapping(value = "/appNames")
    public Set<String> appNames() {
        Set<String> appNames = new HashSet<>(8);
        // 获取网关中配置的路由
        routeLocator.getRoutes().filter(route -> route.getUri().getHost() != null)
                .filter(route -> !appName.equals(route.getUri().getHost()))
                .subscribe(route -> appNames.add(route.getUri().getHost()));

        return appNames;
    }

}
