package cn.bbwres.biscuit.operation.log.service;

import cn.bbwres.biscuit.operation.log.annotation.OperationLog;
import cn.bbwres.biscuit.operation.log.entity.OperationLogEntity;

/**
 * 操作日志 处理类
 *
 * @author zhanglinfeng
 */
public interface OperationLogService {

    /**
     * 保存日志信息
     *
     * @param loggerMsg
     * @param businessLog
     */
    void saveLoggerMsg(OperationLogEntity loggerMsg, OperationLog businessLog);


}
