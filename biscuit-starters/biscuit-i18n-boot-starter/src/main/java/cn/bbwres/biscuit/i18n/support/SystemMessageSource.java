package cn.bbwres.biscuit.i18n.support;

import org.springframework.context.support.ResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;

/**
 * 系统国际化配置
 *
 * @author zhanglinfeng
 */
public class SystemMessageSource extends ResourceBundleMessageSource {

    public SystemMessageSource() {
        setBasename("cn.bbwres.biscuit.i18n.system_messages");
        setDefaultEncoding(StandardCharsets.UTF_8.name());
    }
}
