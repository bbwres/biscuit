package cn.bbwres.biscuit.operation.log.service;

import cn.bbwres.biscuit.operation.log.annotation.OperationLog;
import cn.bbwres.biscuit.operation.log.entity.OperationLogEntity;

/**
 * 操作日志 处理类
 *
 * @author zhanglinfeng
 */
public interface OperationLogSaveService {

    /**
     * 保存操作日志信息
     *
     * @param loggerMsg   操作日志内容
     * @param businessLog 注解信息
     */
    void saveLoggerMsg(OperationLogEntity loggerMsg, OperationLog businessLog);


}
