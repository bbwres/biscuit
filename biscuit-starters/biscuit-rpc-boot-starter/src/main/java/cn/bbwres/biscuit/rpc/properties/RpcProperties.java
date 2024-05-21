package cn.bbwres.biscuit.rpc.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * rpc 参数配置类
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Data
@ConfigurationProperties("biscuit.rpc")
public class RpcProperties {


    /**
     * 安全配置项
     */
    private SecurityProperties security = new SecurityProperties();


}
