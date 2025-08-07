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

package cn.bbwres.biscuit.gateway;

import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 网关 配置文件
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Data
@ConfigurationProperties("biscuit.gateway")
public class GatewayProperties {


    /**
     * 固定无需鉴权的uri
     * /
     * /favicon.ico
     * /swagger-ui.html
     * /csrf
     * /swagger-resources/**
     * /webjars/**
     * /auth/oauth/token
     */
    private String[] noAuthUris;
    /**
     * 固定登录鉴权的url
     */
    private String[] loginAuthUris;

    /**
     * 无权访问返回的错误码
     */
    private String accessDeniedCode = GlobalErrorCodeConstants.FORBIDDEN.getCode();


    /**
     * 认证失败返回的错误码
     */
    private String authFailCode = GlobalErrorCodeConstants.UNAUTHORIZED.getCode();

    /**
     * 系统异常返回的错误码
     */
    private String systemErrCode = GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode();


    /**
     * 认证通过后 后续的用户token 放入请求头的名字
     */
    private String userTokenHeader = "X-User-Token";
    /**
     * 用户信息
     */
    private String userInfoHeader = "X-User-Info";

    /**
     * 是否使用xss过滤器
     */
    private Boolean useXssFilter = false;
    /**
     * 本地缓存资源信息
     */
    private Boolean cacheResource = true;

    /**
     * 本地缓存资源有效期 单位为秒
     */
    private Integer localCacheResourceTime = 5 * 60;

    /**
     * 是否使用session。 true 使用session， false 不使用。默认不使用
     */
    private Boolean session;

    /**
     * 是否使用jwtToken 默认使用
     */
    private Boolean useJwtToken = true;

    /**
     * 根据传参的state 跳转到登录的url
     */
    private Map<String, String> loginStateUris = new HashMap<>(8);

    /**
     * 关闭Csrf
     */
    private Boolean disableCsrf = false;

    /**
     * 关闭cors
     */
    private Boolean disableCors = false;

    /**
     * 是否开启动态路由
     */
    private Boolean dynamicRouteEnabled = true;

    /**
     * 动态路由分组
     */
    private String dynamicRouteGroup = "DEFAULT_GROUP";
    /**
     * 动态路由配置的DataId
     */
    private String dynamicRouteDataId;

}
