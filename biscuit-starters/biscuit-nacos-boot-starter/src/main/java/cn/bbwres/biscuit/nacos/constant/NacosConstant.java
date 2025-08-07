package cn.bbwres.biscuit.nacos.constant;


import com.alibaba.nacos.api.PropertyKeyConst;

/**
 * nacos 的属性常量
 *
 * @author zhanglinfeng
 */
public class NacosConstant extends PropertyKeyConst {

    public static final String NACOS_PLUGIN_TIMEOUT = "nacos.plugin.timout";

    public static final String SPRING_CLOUD_NACOS_CONFIG_TIMEOUT = "spring.cloud.nacos.config.timout";

    public static final long NACOS_DEFAULT_TIMEOUT = 30000;
}