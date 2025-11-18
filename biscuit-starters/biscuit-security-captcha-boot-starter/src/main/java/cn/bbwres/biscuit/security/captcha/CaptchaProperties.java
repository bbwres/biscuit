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

package cn.bbwres.biscuit.security.captcha;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 验证码参数
 *
 * @author zhanglinfeng
 */
@Data
@ConfigurationProperties("biscuit.captcha")
public class CaptchaProperties {

    /**
     * 验证码值的请求参数名称
     */
    private String captchaCodeValueName = "captcha_verification";
    /**
     * 验证码key的请求参数名称
     */
    private String captchaCodeKeyName = "captcha_code_key";

    /**
     * 当用户连续失败次数达到该值时，触发验证码校验
     */
    private int loginFailureCaptchaThreshold = 2;

    /**
     * 验证码资源目录
     * key 为验证码的类型
     * value 为验证码的资源目录
     */
    private Map<String, String[]> captchaResource;


}
