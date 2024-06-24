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

import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.spring.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.autoconfiguration.ImageCaptchaAutoConfiguration;
import cloud.tianai.captcha.spring.plugins.secondary.SecondaryVerificationApplication;
import cn.bbwres.biscuit.security.captcha.endpoint.CaptchaEndpoint;
import cn.bbwres.biscuit.security.captcha.oauth2.CaptchaResourceStore;
import cn.bbwres.biscuit.security.captcha.oauth2.CaptchaTokenGranter;
import cn.bbwres.biscuit.security.captcha.oauth2.CheckCaptchaService;
import cn.bbwres.biscuit.security.captcha.oauth2.CheckCaptchaServiceImpl;
import cn.bbwres.biscuit.security.oauth2.properties.BiscuitSecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 验证码配置类
 *
 * @author zhanglinfeng
 */
@AutoConfiguration
@AutoConfigureBefore(ImageCaptchaAutoConfiguration.class)
@EnableConfigurationProperties(CaptchaProperties.class)
public class CaptchaAutoConfigure {


    /**
     * 验证码端点
     *
     * @param imageCaptchaApplication 验证码上下文
     * @return
     */
    @Bean
    public CaptchaEndpoint captchaEndpoint(ImageCaptchaApplication imageCaptchaApplication) {
        return new CaptchaEndpoint(imageCaptchaApplication);
    }

    /**
     * 检查验证码服务
     *
     * @param imageCaptchaApplication 验证码上下文
     * @return
     */
    @Bean
    public CheckCaptchaService checkCaptchaService(ImageCaptchaApplication imageCaptchaApplication) {
        return new CheckCaptchaServiceImpl(imageCaptchaApplication);
    }

    @Bean("captchaTokenGranter")
    public CaptchaTokenGranter captchaTokenGranter(CheckCaptchaService checkCaptchaService,
                                                   BiscuitSecurityProperties biscuitSecurityProperties) {
        return new CaptchaTokenGranter(checkCaptchaService, biscuitSecurityProperties);
    }

    /**
     * 验证码资源信息
     *
     * @return
     */
    @Bean
    public ResourceStore captchaResourceStore(CaptchaProperties captchaProperties) {
        return new CaptchaResourceStore(captchaProperties);
    }
}
