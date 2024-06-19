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

package cn.bbwres.biscuit.i18n;

import cn.bbwres.biscuit.i18n.support.SupportMessageSource;
import cn.bbwres.biscuit.i18n.support.SystemMessageSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.List;

/**
 * i18n自动配置类
 *
 * @author zhanglinfeng
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(I18nProperties.class)
public class I18nAutoConfigure {


    /**
     * 系统默认的messageSource
     *
     * @return
     */
    @Bean("systemMessageSource")
    public SystemMessageSource systemMessageSource(I18nProperties i18nProperties) {
        return new SystemMessageSource(i18nProperties);
    }


    /**
     * 自定义的messageSource
     *
     * @return
     */
    @Bean
    public SupportMessageSource messageSource(List<MessageSource> messageSources) {
        return new SupportMessageSource(messageSources);
    }


    /**
     * 国际化处理
     *
     * @param supportMessageSource
     * @return
     */
    @Bean
    public MessageSourceAccessor messageSourceAccessor(SupportMessageSource supportMessageSource) {
        return new MessageSourceAccessor(supportMessageSource);
    }


}
