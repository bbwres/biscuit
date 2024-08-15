package cn.bbwres.biscuit.operation.log.aspect;

import cn.bbwres.biscuit.operation.log.constants.LoggerConstant;
import cn.bbwres.biscuit.operation.log.entity.OperationLogEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * 业务日志记录工具类
 *
 * @author zhanglinfeng
 */
public class OperationLogUtils {
    private static final Logger OPERATION_LOG = LoggerFactory.getLogger(LoggerConstant.OPERATION_LOG_NAME);


    /**
     * 设置日志内容
     *
     * @param content 日志内容
     */
    public static void setContent(String content) {
        OperationLogAspect.setContent(content);
    }


    /**
     * 记录log信息
     */
    public static void log(OperationLogEntity msg) {
        OPERATION_LOG.info("{}", msg);
    }


    /**
     * 记录日志
     *
     * @param system            系统名称
     * @param module            业务模块
     * @param business          业务类型
     * @param content           日志内容
     * @param operation         操作类型
     * @param requestMsg        请求参数
     * @param responseMsg       响应参数
     * @param businessId        业务id
     * @param operationUser     操作人/接入方
     * @param operationUserName 操作人/接入方 名称
     * @param exceptionMsg      异常日志
     * @param loggerLevel       日志级别
     */
    public static void log(String system, String module, String business, String content, String operation, String requestMsg, String responseMsg, String businessId, String operationUser, String operationUserName, String exceptionMsg, String loggerLevel, boolean accessRequest) {
        OperationLogEntity msg = new OperationLogEntity()
                .setSystem(system)
                .setModule(module)
                .setBusiness(business)
                .setContent(content)
                .setOperation(operation)
                .setRequestMsg(requestMsg)
                .setResponseMsg(responseMsg)
                .setBusinessId(businessId)
                .setOperationUser(operationUser)
                .setOperationUserName(operationUserName)
                .setExceptionMsg(exceptionMsg)
                .setLoggerLevel(loggerLevel)
                .setAccessRequest(accessRequest)
                .setCreateTime(LocalDateTime.now().format(OperationLogAspect.DATE_TIME_FORMATTER));
        log(msg);
    }


}
