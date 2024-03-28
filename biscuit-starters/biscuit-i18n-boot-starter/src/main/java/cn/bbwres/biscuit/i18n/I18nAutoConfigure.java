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
    public SystemMessageSource systemMessageSource() {
        return new SystemMessageSource();
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
