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

package cn.bbwres.biscuit.security.oauth2.grant;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.authentication.AuthenticationConverter;

/**
 * 自定义的CustomAuthenticationConverter
 *
 * @author zhanglinfeng
 */
public interface CustomAuthenticationGrant {


    /**
     * 获取自定义的 AuthenticationConverter
     *
     * @return AuthenticationConverter
     */
    AuthenticationConverter getCustomAuthenticationConverter();


    /**
     * 获取自定义的  AuthenticationProvider
     *
     * @return AuthenticationProvider
     */
    AuthenticationProvider getCustomAuthenticationProvider();

}
