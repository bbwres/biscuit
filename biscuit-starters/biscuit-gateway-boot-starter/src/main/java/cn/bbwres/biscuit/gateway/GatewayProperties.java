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
     * 认证服务的服务地址
     */
    private String authorizationServerName = "lb://auth";
    /**
     * 检查token的请求path
     */
    private String authorizationServerCheckTokenPath = "/rpc-api/auth/check_token";
    /**
     * 获取登录授权的请求path
     */
    private String authorizationServerLoginAuthResource = "/rpc-api/auth/login_auth_resource";
    /**
     * 根据角色获取资源的请求path
     */
    private String authorizationServerResourceByRole = "/rpc-api/auth/resource_by_role";


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
     * 请求黑名单
     */
    private String[] blackUris= new String[]{"/rpc-api/**"};

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
     * 认证通过后 后续的用户信息放入请求头的名字
     */
    private String userInfoHeader = "x-user-info";

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
    private Boolean useJwtToken = false;

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
    private Boolean dynamicRouteEnabled;

    /**
     * 动态路由分组
     */
    private String dynamicRouteGroup = "DEFAULT_GROUP";
    /**
     * 动态路由配置的DataId
     */
    private String dynamicRouteDataId;

}
