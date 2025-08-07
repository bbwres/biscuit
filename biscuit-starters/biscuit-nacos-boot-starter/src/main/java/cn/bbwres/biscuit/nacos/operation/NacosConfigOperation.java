package cn.bbwres.biscuit.nacos.operation;


import cn.bbwres.biscuit.nacos.constant.NacosConstant;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.Executor;

/**
 * nacos 操作类
 *
 * @author zhanglinfeng
 */
public class NacosConfigOperation implements DisposableBean {
    private static final Logger LOG = LoggerFactory.getLogger(NacosConfigOperation.class);

    private final ConfigService nacosConfigService;

    private final Environment environment;

    public NacosConfigOperation(ConfigService nacosConfigService, Environment environment) {
        this.nacosConfigService = nacosConfigService;
        this.environment = environment;
    }

    /**
     * 获取nacos配置
     *
     * @param group
     * @param serviceId
     * @return
     * @throws NacosException
     */
    public String getConfig(String group, String serviceId) throws NacosException {
        String timeout = environment.getProperty(NacosConstant.NACOS_PLUGIN_TIMEOUT);
        if (ObjectUtils.isEmpty(timeout)) {
            timeout = environment.getProperty(NacosConstant.SPRING_CLOUD_NACOS_CONFIG_TIMEOUT);
        }

        return nacosConfigService.getConfig(serviceId, group, !ObjectUtils.isEmpty(timeout) ? Long.parseLong(timeout) : NacosConstant.NACOS_DEFAULT_TIMEOUT);
    }

    /**
     * 删除配置
     *
     * @param group
     * @param serviceId
     * @return
     * @throws NacosException
     */
    public boolean removeConfig(String group, String serviceId) throws NacosException {
        return nacosConfigService.removeConfig(serviceId, group);
    }

    /**
     * 推送配置信息
     *
     * @param group
     * @param serviceId
     * @param config
     * @return
     * @throws NacosException
     */
    public boolean publishConfig(String group, String serviceId, String config) throws NacosException {
        return nacosConfigService.publishConfig(serviceId, group, config);
    }

    /**
     * 推送配置信息
     *
     * @param group
     * @param serviceId
     * @param config
     * @param configType
     * @return
     * @throws NacosException
     */
    public boolean publishConfig(String group, String serviceId, String config, ConfigType configType) throws NacosException {
        return nacosConfigService.publishConfig(serviceId, group, config, configType.getType());
    }

    /**
     * 订阅配置
     *
     * @param group
     * @param serviceId
     * @param executor
     * @param nacosConfigSubscribeCallback
     * @return
     * @throws NacosException
     */
    public Listener subscribeConfig(String group, String serviceId, Executor executor, NacosConfigSubscribeCallback nacosConfigSubscribeCallback) throws NacosException {
        Listener listener = new Listener() {
            @Override
            public void receiveConfigInfo(String config) {
                nacosConfigSubscribeCallback.callback(config);
            }

            @Override
            public Executor getExecutor() {
                return executor;
            }
        };

        nacosConfigService.addListener(serviceId, group, listener);

        return listener;
    }

    /**
     * 解除订阅
     *
     * @param group
     * @param serviceId
     * @param listener
     */
    public void unsubscribeConfig(String group, String serviceId, Listener listener) {
        nacosConfigService.removeListener(serviceId, group, listener);
    }

    @Override
    public void destroy() throws Exception {
        nacosConfigService.shutDown();

        LOG.info("Shutting down Nacos config service...");
    }
}