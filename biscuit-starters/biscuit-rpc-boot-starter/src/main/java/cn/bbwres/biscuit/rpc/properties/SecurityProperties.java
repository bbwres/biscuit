package cn.bbwres.biscuit.rpc.properties;

import lombok.Data;

/**
 * 安全配置项
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Data
public class SecurityProperties {

    /**
     * 是否启用安全配置
     * true -启用
     * false-不启用
     */
    private boolean enable = true;



    /**
     * 白名单配置信息
     */
    private String[] whiteListUri;


}
