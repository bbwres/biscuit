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

package cn.bbwres.biscuit.web.utils;

import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.utils.JsonUtil;
import cn.bbwres.biscuit.utils.NetworkUtil;
import cn.bbwres.biscuit.utils.SpringContextUtil;
import cn.bbwres.biscuit.web.BiscuitWebProperties;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 获取请求的用户信息
 *
 * @author zhanglinfeng
 */
public class WebFrameworkUtils {


    /**
     * 获取配置文件
     *
     * @return
     */
    private static BiscuitWebProperties getBiscuitWebProperties() {
        return SpringContextUtil.getBean(BiscuitWebProperties.class);
    }


    private static final String[] CLIENT_IP_HTTP_HEADER_NAME = new String[]{"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
    /**
     * 用户agent
     */
    private static final String USER_AGENT = "User-Agent";


    /**
     * 获取当前登陆用户信息
     * 用户信息为json格式，包含用户id、用户名等信息,获取出的请求头为base64
     * 因此需要先反序列化
     *
     * @return
     */
    public static UserBaseInfo<?> getRequestUser() {
        String userInfoHeaderName = getBiscuitWebProperties().getUserInfoHeaderName();
        String userInfoStr = getHeader(userInfoHeaderName);
        if (Objects.isNull(userInfoStr)) {
            return null;
        }
        return JsonUtil.toObjectByBase64Json(userInfoStr, UserBaseInfo.class);
    }


    /**
     * 获取当前登陆用户的信息
     *
     * @param checkNull 检查是否为空
     * @return
     */
    public static UserBaseInfo<?> getRequestUser(boolean checkNull) {
        UserBaseInfo<?> userBaseInfo = getRequestUser();
        if (checkNull) {
            Assert.notNull(userBaseInfo, "user not null");
        }
        return userBaseInfo;
    }

    /**
     * 获取客户端ip
     *
     * @param otherHeaderNames 其他请求头参数名称
     * @return ip
     */
    public static String getClientIp(String... otherHeaderNames) {
        String[] headers = CLIENT_IP_HTTP_HEADER_NAME;
        if (ArrayUtils.isNotEmpty(otherHeaderNames)) {
            headers = ArrayUtils.addAll(headers, otherHeaderNames);
        }

        return getClientIpByHeader(headers);
    }

    /**
     * 获取客户端ip
     *
     * @param headerNames 请求头参数名称
     * @return ip
     */
    public static String getClientIpByHeader(String... headerNames) {
        String ip;
        for (String headerName : headerNames) {
            ip = getHeader(headerName);
            if (!NetworkUtil.isUnknown(ip)) {
                return NetworkUtil.getMultistageReverseProxyIp(ip);
            }
        }
        HttpServletRequest request = getRequest();
        if (Objects.isNull(request)) {
            return null;
        }
        ip = request.getRemoteAddr();
        return NetworkUtil.getMultistageReverseProxyIp(ip);
    }

    /**
     * 获取请求头信息
     *
     * @param name
     * @return
     */
    public static String getHeader(String name) {
        HttpServletRequest request = getRequest();
        if (Objects.isNull(request)) {
            return null;
        }
        return request.getHeader(name);
    }

    /**
     * 获取http request
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (Objects.isNull(servletRequestAttributes)) {
            return null;
        }
        return servletRequestAttributes.getRequest();
    }


    /**
     * 获取agent
     *
     * @return ua
     */
    public static String getUserAgent() {
        String ua = getHeader(USER_AGENT);
        return ua != null ? ua : "";
    }

}
