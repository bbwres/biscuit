package cn.bbwres.biscuit.mybatis.config;

import cn.bbwres.biscuit.mybatis.handler.DefaultDataFieldFillHandler;
import cn.bbwres.biscuit.mybatis.handler.DefaultTenantLineHandler;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * mybatis-plus 自动装配
 *
 * @author zhanglinfeng
 */
@AutoConfiguration
@EnableTransactionManagement
@EnableConfigurationProperties({MybatisProperties.class, MybatisTenantProperties.class})
@MapperScan(value = "${mybatis-plus.mapper.base-packages}", annotationClass = Mapper.class)
public class MybatisPlusAutoConfigure {


    /**
     * 配置分页插件
     *
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    /**
     * 元数据填充
     *
     * @param mybatisTenantProperties
     * @param mybatisProperties
     * @return
     */
    @Bean
    public MetaObjectHandler metaObjectHandler(MybatisTenantProperties mybatisTenantProperties,
                                               MybatisProperties mybatisProperties) {
        return new DefaultDataFieldFillHandler(mybatisProperties, mybatisTenantProperties);
    }


    /**
     * 租户插件
     * 默认为不启用
     *
     * @param mybatisTenantProperties
     * @param mybatisProperties
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "mybatis-plus.tenant", name = "enabled", havingValue = "true")
    public TenantLineHandler defaultTenantLineHandler(MybatisTenantProperties mybatisTenantProperties,
                                                      MybatisProperties mybatisProperties) {
        return new DefaultTenantLineHandler(mybatisTenantProperties, mybatisProperties);
    }
}
