package cn.bbwres.biscuit.gateway.i18n;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;

/**
 * 网关服务国际化配置
 *
 * @author zhanglinfeng
 */
public class GatewayMessageSource extends ResourceBundleMessageSource {

    public GatewayMessageSource() {
        setBasename("cn.bbwres.biscuit.gateway.i18n.messages");
        setDefaultEncoding(StandardCharsets.UTF_8.name());
    }

    public static MessageSourceAccessor getAccessor() {
        return new MessageSourceAccessor(new GatewayMessageSource());
    }
}
