package cn.bbwres.biscuit.rpc.metadata;

import cn.bbwres.biscuit.rpc.constants.RpcConstants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.client.serviceregistry.Registration;

import java.util.Map;
import java.util.UUID;

/**
 * 服务注册处理
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public class RegistrationBeanPostProcessor implements BeanPostProcessor {

    /**
     * {@inheritDoc}
     *
     * 初始化
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Registration) {
            Map<String, String> metadata = ((Registration) bean).getMetadata();
            metadata.put(RpcConstants.CLIENT_PASSWORD, UUID.randomUUID().toString());
        }
        return bean;
    }
}
