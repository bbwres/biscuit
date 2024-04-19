package cn.bbwres.biscuit.mongodb.config;

import lombok.Data;

/**
 * Mongo 连接参数
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Data
public class MongodbParams {

    /**
     * Mongo url mongodb://127.0.0.1:27017
     */
    private String connectionString;


}
