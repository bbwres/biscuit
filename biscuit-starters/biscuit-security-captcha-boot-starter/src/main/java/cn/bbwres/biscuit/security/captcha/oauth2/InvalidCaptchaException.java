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

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * 无效的验证码
 *
 * @author zhanglinfeng
 */
public class InvalidCaptchaException extends OAuth2Exception {
    private static final long serialVersionUID = 1932118196169742922L;

    public InvalidCaptchaException(String msg, Throwable t) {
        super(msg, t);
    }

    public InvalidCaptchaException(String msg) {
        super(msg);
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "invalid_captcha";
    }
}
