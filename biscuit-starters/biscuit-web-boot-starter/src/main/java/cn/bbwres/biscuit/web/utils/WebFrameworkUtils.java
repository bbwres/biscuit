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
import cn.bbwres.biscuit.web.BiscuitWebProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 获取请求的用户信息
 *
 * @author zhanglinfeng
 */
public class WebFrameworkUtils {

    private BiscuitWebProperties biscuitWebProperties;


    @Autowired
    public void setBiscuitWebProperties(BiscuitWebProperties biscuitWebProperties) {
        this.biscuitWebProperties = biscuitWebProperties;
    }

    private static WebFrameworkUtils webFrameworkUtils;


    @PostConstruct
    public void initialize() {
        webFrameworkUtils = this;
        webFrameworkUtils.biscuitWebProperties = biscuitWebProperties;
    }

    /**
     * 用户客户端ip
     */
    private static final String CLIENT_IP_HTTP_KEY = "X-Real-IP";


    /**
     * 获取当前登陆用户信息
     * 用户信息为json格式，包含用户id、用户名等信息,获取出的请求头为base64
     * 因此需要先反序列化
     *
     * @return
     */
    public static UserBaseInfo<?> getRequestUser() {
        String userInfoHeaderName = webFrameworkUtils.biscuitWebProperties.getUserInfoHeaderName();
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
     * @return
     */
    public static String getClientIp() {
        return getHeader(CLIENT_IP_HTTP_KEY);
    }


    /**
     * 获取请求头信息
     *
     * @param name
     * @return
     */
    public static String getHeader(String name) {
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (Objects.isNull(servletRequestAttributes)) {
            return null;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return request.getHeader(name);
    }


}
