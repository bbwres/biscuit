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

package cn.bbwres.biscuit.web;

import cn.bbwres.biscuit.exception.ExceptionConvertErrorCode;
import cn.bbwres.biscuit.web.config.WebAppMvcConfigurer;
import cn.bbwres.biscuit.web.handler.BiscuitHandlerExceptionResolver;
import cn.bbwres.biscuit.web.handler.WebExceptionConvertErrorCode;
import cn.bbwres.biscuit.web.utils.WebFrameworkUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * web自动配置
 *
 * @author zhanglinfeng
 */
@AutoConfiguration
@EnableConfigurationProperties(BiscuitWebProperties.class)
public class WebAutoConfiguration {


    /**
     * json 配置
     *
     * @return
     */
    @Bean
    public ObjectMapper objectMapper(BiscuitWebProperties properties) {

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(properties.getDateTimeFormat())));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(properties.getDateFormat())));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(properties.getTimeFormat())));

        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(properties.getDateTimeFormat())));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(properties.getDateFormat())));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(properties.getTimeFormat())));


        ObjectMapper objectMapper = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat(properties.getDateTimeFormat())).registerModule(new JavaTimeModule());
        //设置不序列化为空的字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //反序列化未知字段不报错
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //序列化未知字段不报错
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return objectMapper;
    }

    /**
     * 配置json
     *
     * @param objectMapper
     * @return
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }


    /**
     * mvc 异常处理
     *
     * @param objectMapper
     * @return
     */
    @Bean
    public BiscuitHandlerExceptionResolver biscuitHandlerExceptionResolver(ObjectMapper objectMapper,
                                                                           ObjectProvider<MessageSourceAccessor> messagesProvider,
                                                                           List<ExceptionConvertErrorCode> exceptionConvertErrorCodes) {
        return new BiscuitHandlerExceptionResolver(objectMapper, messagesProvider, exceptionConvertErrorCodes);
    }


    /**
     * mvc 异常处理类
     *
     * @param biscuitHandlerExceptionResolver 异常处理类
     * @return WebAppMvcConfigurer
     */
    @Bean("webAppMvcConfigurer")
    public WebAppMvcConfigurer webAppMvcConfigurer(BiscuitHandlerExceptionResolver biscuitHandlerExceptionResolver) {
        return new WebAppMvcConfigurer(biscuitHandlerExceptionResolver);
    }


    /**
     * mvc 参数校验配置
     *
     * @return LocalValidatorFactoryBean
     */
    @Bean
    public LocalValidatorFactoryBean mvcValidator(BiscuitWebProperties biscuitWebProperties) {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        //参数校验快速失败
        localValidatorFactoryBean.getValidationPropertyMap().put("hibernate.validator.fail_fast", biscuitWebProperties.getValidatorFailFast().toString());
        if (biscuitWebProperties.getValidatorI18nEnable()) {
            ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
            messageSource.setBasename(biscuitWebProperties.getValidatorI18nBaseName());
            // 缓存时长
            messageSource.setCacheSeconds(biscuitWebProperties.getValidatorI18nCacheSeconds());
            //为Validator配置国际化
            localValidatorFactoryBean.setValidationMessageSource(messageSource);
        }
        return localValidatorFactoryBean;
    }

    /**
     * web 常用的异常处理
     *
     * @return WebExceptionConvertErrorCode
     */
    @Bean("webExceptionConvertErrorCode")
    public WebExceptionConvertErrorCode webExceptionConvertErrorCode() {
        return new WebExceptionConvertErrorCode();
    }

    /**
     * 请求处理
     *
     * @return
     */
    @Bean
    public WebFrameworkUtils webFrameworkUtils() {
        return new WebFrameworkUtils();
    }


}
