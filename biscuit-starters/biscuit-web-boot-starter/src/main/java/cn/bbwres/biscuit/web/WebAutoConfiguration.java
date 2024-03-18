package cn.bbwres.biscuit.web;

import cn.bbwres.biscuit.web.handler.BiscuitHandlerExceptionResolver;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
                                                                           ObjectProvider<MessageSourceAccessor> messagesProvider) {
        return new BiscuitHandlerExceptionResolver(objectMapper,messagesProvider);
    }

    /**
     * 请求处理
     *
     * @return
     */
    @Bean
    public WebFrameworkUtils httpRequestHandler() {
        return new WebFrameworkUtils();
    }


}
