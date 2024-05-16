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
 */
public class RegistrationBeanPostProcessor implements BeanPostProcessor {

    /**
     * 初始化
     *
     * @param bean     初始化的bean
     * @param beanName 初始化的beanName
     * @return 初始化的bean
     * @throws BeansException 异常信息o
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
