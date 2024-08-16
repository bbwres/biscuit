package cn.bbwres.biscuit.operation.log.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * 操作日志配置
 *
 * @author zhanglinfeng
 */
@Data
@ConfigurationProperties("operation.log")
public class OperationLogProperties implements Serializable {
    private static final long serialVersionUID = 5147806521514857L;


    /**
     * 获取当前用户的el表达式 必须是静态方法
     * 类全路径@方法
     */
    private String getUserEl = "cn.bbwres.biscuit.web.utils.WebFrameworkUtils@getRequestUser";

    /**
     * 获取 当前接入方的el表达式 必须是静态方法
     * 类全路径@方法
     */
    private String getAccessEl = "cn.bbwres.biscuit.web.utils.WebFrameworkUtils@getAccessInfo";

    /**
     * 是否使用mq发送日志
     */
    private boolean useMq = true;

    /**
     * 业务日志消息队列
     */
    private String messageType = "OPERATION_LOG_INFO";

    /**
     * mqtopic
     */
    private String topic = "OPERATION_LOG_INFO";

    /**
     * 生产者组 app
     */
    private String producerGroup = "OPERATION_LOG_INFO_PRODUCER_GROUP";

    /**
     * 消息队列ns
     */
    private String nameServerAddress;

}
