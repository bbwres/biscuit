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

package cn.bbwres.biscuit.gateway.service;

import java.util.List;
import java.util.Map;

/**
 * 获取认证的url资源
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public interface ResourceService {

    /**
     * 检查并解析token
     *
     * @param token a {@link java.lang.String} object
     * @return a {@link java.util.Map} object
     */
    Map<String, Object> checkToken(String token);


    /**
     * 获取仅需要登陆认证的资源地址
     *
     * @return a {@link java.util.List} object
     */
    List<String> getLoginAuthResource();


    /**
     * 根据角色信息获取出当前角色拥有的资源信息
     *
     * @param roleId 角色id
     * @return a {@link java.util.List} object
     */
    List<String> getResourceByRole(String roleId);


    /**
     * 获取登陆地址
     *
     * @return a {@link java.lang.String} object
     */
    default String getLoginUrl() {
        return null;
    }

    /**
     * 获取登陆地址
     *
     * @param state a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    default String getLoginUrlBuildState(String state) {
        return getLoginUrl();
    }


}
