package cn.bbwres.biscuit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BootstrapProfile 设置
 *
 * @author zhanglinfeng
 */
public class BootstrapProfile {

    private final static Logger log = LoggerFactory.getLogger(BootstrapProfile.class);

    private final static String PROFILE_NAME = "spring.profiles.active";

    /**
     * 设置启动的profile 文件
     */
    public static void setBootstrapProfile() {
        String profile = System.getProperty(PROFILE_NAME);
        log.info("当前启动参数中选择的环境文件为:[{}]", profile == null ? "默认开发" : profile);
        if (profile != null) {
            System.setProperty("spring.cloud.bootstrap.name", "bootstrap-" + profile);
        }
    }
}
