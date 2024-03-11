package cn.bbwres.biscuit.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;


/**
 * 容器初始化 扩展处理
 *
 * @author zhanglinfeng
 */
public abstract class AbstractBiscuitApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractBiscuitApplicationContextInitializer.class);

    /**
     * 容器初始化
     *
     * @param applicationContext
     */
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.getBeanFactory().addBeanPostProcessor(new InstantiationAwareBeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                return afterInitialization(applicationContext, bean, beanName);
            }
        });
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    /**
     * 后续处理
     *
     * @param applicationContext
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    protected abstract Object afterInitialization(ConfigurableApplicationContext applicationContext,
                                                  Object bean, String beanName) throws BeansException;
}
