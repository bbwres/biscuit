package cn.bbwres.biscuit.web.i18n;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;

/**
 * 框架底层国际化配置
 *
 * @author zhanglinfeng
 */
public class BiscuitWebMessageSource extends ResourceBundleMessageSource {
    public BiscuitWebMessageSource() {
        setBasename("cn.bbwres.biscuit.web.i18n.messages");
        setDefaultEncoding(StandardCharsets.UTF_8.name());
    }

    public static MessageSourceAccessor getAccessor() {
        return new MessageSourceAccessor(new BiscuitWebMessageSource());
    }
}
