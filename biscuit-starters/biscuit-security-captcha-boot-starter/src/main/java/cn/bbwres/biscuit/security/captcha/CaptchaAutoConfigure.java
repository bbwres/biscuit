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

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.spring.autoconfiguration.ImageCaptchaAutoConfiguration;
import cn.bbwres.biscuit.security.captcha.config.CaptchaResourceStore;
import cn.bbwres.biscuit.security.captcha.config.CheckCaptchaService;
import cn.bbwres.biscuit.security.captcha.config.CheckCaptchaServiceImpl;
import cn.bbwres.biscuit.security.captcha.endpoint.CaptchaEndpoint;
import cn.bbwres.biscuit.security.captcha.filter.CaptchaCodeFilter;
import cn.bbwres.biscuit.security.oauth2.service.redis.RedisCheckUserLockService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

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
     * @return CaptchaEndpoint
     */
    @Bean
    public CaptchaEndpoint captchaEndpoint(ImageCaptchaApplication imageCaptchaApplication) {
        return new CaptchaEndpoint(imageCaptchaApplication);
    }


    /**
     * 资源文件加载信息
     *
     * @param captchaProperties 资源配置信息
     * @return ResourceStore
     */
    @Bean
    public ResourceStore resourceStore(CaptchaProperties captchaProperties) {
        return new CaptchaResourceStore(captchaProperties);
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


    /**
     * 验证码校验过滤器
     *
     * @param authorizationServerSettings
     * @param checkCaptchaService
     * @param captchaProperties
     * @param redisCheckUserLockService
     * @return
     */
    @Bean("captchaCodeFilter")
    public CaptchaCodeFilter captchaCodeFilter(AuthorizationServerSettings authorizationServerSettings,
                                               CheckCaptchaService checkCaptchaService,
                                               CaptchaProperties captchaProperties,
                                               RedisCheckUserLockService redisCheckUserLockService) {
        return new CaptchaCodeFilter(authorizationServerSettings, checkCaptchaService, captchaProperties, redisCheckUserLockService);
    }

}
