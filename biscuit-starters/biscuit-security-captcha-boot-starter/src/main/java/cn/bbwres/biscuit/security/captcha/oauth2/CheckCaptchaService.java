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
package cn.bbwres.biscuit.security.captcha.oauth2;

/**
 * 检查验证码
 *
 * @author zhanglinfeng
 */
public interface CheckCaptchaService {

    /**
     * 校验验证码
     *
     * @param grantType
     * @param code
     * @param codeKey
     * @return true  验证码验证通过，false 验证码验证不通过
     */
    boolean check(String grantType, String code, String codeKey);


}
