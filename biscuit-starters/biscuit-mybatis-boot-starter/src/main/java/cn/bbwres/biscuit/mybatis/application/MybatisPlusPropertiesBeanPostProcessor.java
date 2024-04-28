package cn.bbwres.biscuit.mybatis.application;

import cn.bbwres.biscuit.mybatis.handler.BiscuitMybatisEnumTypeHandler;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.handlers.CompositeEnumTypeHandler;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * mybatis plus 参数配置
 *
 * @author zhanglinfeng
 */
public class MybatisPlusPropertiesBeanPostProcessor implements BeanPostProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(MybatisPlusPropertiesBeanPostProcessor.class);

    /**
     * 初始化
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof MybatisPlusProperties) {
            MybatisPlusProperties mybatisPlusProperties = (MybatisPlusProperties) bean;
            TypeHandler<?> typeHandler = mybatisPlusProperties.getConfiguration().getTypeHandlerRegistry().getTypeHandler(Enum.class);
            LOG.info("MybatisConfigurationInitializer:old: typeHandler = {}", typeHandler);
            if (typeHandler == null || EnumTypeHandler.class.equals(typeHandler.getClass())
                    || CompositeEnumTypeHandler.class.equals(typeHandler.getClass())) {
                //默认的枚举处理类为空，或者为默认的枚举处理类，则替换为自定义的枚举处理类
                mybatisPlusProperties.getConfiguration().getTypeHandlerRegistry().setDefaultEnumTypeHandler(BiscuitMybatisEnumTypeHandler.class);
                LOG.info("MybatisConfigurationInitializer:new: typeHandler = {}", BiscuitMybatisEnumTypeHandler.class);
            }
            return bean;
        }
        return bean;
    }
}
