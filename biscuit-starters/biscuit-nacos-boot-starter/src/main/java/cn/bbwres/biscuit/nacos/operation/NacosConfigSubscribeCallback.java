package cn.bbwres.biscuit.nacos.operation;

/**
 * nacos 配置订阅
 *
 * @author zhanglinfeng
 */
public interface NacosConfigSubscribeCallback {

    /**
     * 订阅回调
     *
     * @param config
     */
    void callback(String config);
}