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

package cn.bbwres.biscuit.security.oauth2.granter;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.TokenRequest;

/**
 * 扩展的TokenGranter
 *
 * @author zhanglinfeng
 */
public interface EnhancerTokenGranter {


    /**
     * 装载认证token信息
     *
     * @param client
     * @param tokenRequest
     * @return
     */
    Authentication buildAuthentication(ClientDetails client, TokenRequest tokenRequest);


    /**
     * 可以处理的gentType
     *
     * @return
     */
    String grantType();


    /**
     * 父类的TokenGranter
     * 存在该值时，除了调用buildAuthentication方法，还会调用父类的getOAuth2Authentication方法
     *
     * @return
     */
    Class<?> parentClazz();

}
