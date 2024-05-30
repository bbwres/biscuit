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

import cloud.tianai.captcha.spring.plugins.secondary.SecondaryVerificationApplication;
import org.springframework.util.ObjectUtils;

public class CheckCaptchaServiceImpl implements CheckCaptchaService {

    private final SecondaryVerificationApplication secondaryVerificationApplication;

    public CheckCaptchaServiceImpl(SecondaryVerificationApplication secondaryVerificationApplication) {
        this.secondaryVerificationApplication = secondaryVerificationApplication;
    }

    /**
     * 校验验证码
     *
     * @param grantType
     * @param code
     * @param codeKey
     * @return true  验证码验证通过，false 验证码验证不通过
     */
    @Override
    public boolean check(String grantType, String code, String codeKey) {
        if (ObjectUtils.isEmpty(code)) {
            return false;
        }
        return secondaryVerificationApplication.secondaryVerification(code);
    }
}
