package cn.bbwres.biscuit.mybatis.application;

import cn.bbwres.biscuit.application.AbstractBiscuitApplicationContextInitializer;
import cn.bbwres.biscuit.mybatis.handler.BiscuitMybatisEnumTypeHandler;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 修改Mybatis的初始化配置
 *
 * @author zhanglinfeng
 */
public class MybatisConfigurationInitializer extends AbstractBiscuitApplicationContextInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(MybatisConfigurationInitializer.class);

    /**
     * 后续处理
     *
     * @param applicationContext
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    protected Object afterInitialization(ConfigurableApplicationContext applicationContext, Object bean, String beanName) throws BeansException {
        if (bean instanceof MybatisConfiguration) {
            MybatisConfiguration configuration = (MybatisConfiguration) bean;
            TypeHandler<?> typeHandler = configuration.getTypeHandlerRegistry().getTypeHandler(Enum.class);
            LOG.info("MybatisConfigurationInitializer:old: typeHandler = {}", typeHandler);
            if (typeHandler == null || EnumTypeHandler.class.equals(typeHandler.getClass())) {
                //默认的枚举处理类为空，或者为默认的枚举处理类，则替换为自定义的枚举处理类
                configuration.getTypeHandlerRegistry().setDefaultEnumTypeHandler(BiscuitMybatisEnumTypeHandler.class);
                LOG.info("MybatisConfigurationInitializer:new: typeHandler = {}", BiscuitMybatisEnumTypeHandler.class);
            }
            return bean;
        }
        return bean;

    }
}
