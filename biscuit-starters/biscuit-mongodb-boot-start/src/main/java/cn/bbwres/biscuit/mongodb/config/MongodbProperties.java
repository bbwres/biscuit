package cn.bbwres.biscuit.mongodb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Mongo 配置
 * 多数据源的mongo配置
 *
 * @author zhanglinfeng
 */
@Data
@ConfigurationProperties("multiple.mongodb")
public class MongodbProperties {
    

    /**
     * 参数配置
     */
    private Map<String, MongodbParams> config;


}
