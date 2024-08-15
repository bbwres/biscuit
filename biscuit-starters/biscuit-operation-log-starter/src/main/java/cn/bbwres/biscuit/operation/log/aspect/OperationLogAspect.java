package cn.bbwres.biscuit.operation.log.aspect;

import cn.bbwres.biscuit.operation.log.annotation.OperationLog;
import cn.bbwres.biscuit.operation.log.constants.LoggerConstant;
import cn.bbwres.biscuit.operation.log.entity.OperationLogEntity;
import cn.bbwres.biscuit.operation.log.service.EnhanceOperationLogService;
import cn.bbwres.biscuit.operation.log.service.OperationLogService;
import cn.bbwres.biscuit.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;

import java.io.OutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 业务日志切面
 *
 * @author zhanglinfeng
 */
@Order(-20)
@Slf4j
@Aspect
@RequiredArgsConstructor
public class OperationLogAspect {

    private static final Logger BUSINESS_LOG = LoggerFactory.getLogger(LoggerConstant.OPERATION_LOG_NAME);

    /**
     * 用于记录操作内容的上下文
     */
    private static final ThreadLocal<String> CONTENT = new ThreadLocal<>();

    /**
     * 用于记录拓展字段的上下文
     */
    private static final ThreadLocal<Map<String, Object>> EXT = new ThreadLocal<>();


    private final OperationLogService loggerMsgService;

    private final List<EnhanceOperationLogService> enhanceOperationLogServices;


    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");


    public static void addExt(String key, Object value) {
        if (EXT.get() == null) {
            EXT.set(new HashMap<>(8));
        }
        EXT.get().put(key, value);
    }

    public static void setContent(String content) {
        CONTENT.set(content);
    }

    public static void clearThreadLocal() {
        CONTENT.remove();
        EXT.remove();
    }

    /**
     * 切面
     *
     * @param joinPoint
     * @param operateLog
     * @return
     */
    @Around("@annotation(operateLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operateLog) throws Throwable {
        // 记录开始时间
        LocalDateTime startTime = LocalDateTime.now();
        try {
            // 执行原有方法
            Object result = joinPoint.proceed();
            // 记录正常执行时的操作日志
            buildLoggerMsg(joinPoint, result, startTime, null, operateLog);
            return result;
        } catch (Throwable exception) {
            buildLoggerMsg(joinPoint, null, startTime, exception, operateLog);
            throw exception;
        } finally {
            clearThreadLocal();
        }
    }


    /**
     * 设置 日志对象
     *
     * @param joinPoint 切入点
     * @param response  响应参数
     * @param startTime 开始执行时间
     * @param exception exception
     */
    private void buildLoggerMsg(ProceedingJoinPoint joinPoint, Object response, LocalDateTime startTime,
                                Throwable exception, OperationLog operateLog) {
        try {
            // 填充日志参数
            OperationLogEntity loggerMsg = new OperationLogEntity()
                    .setCreateTime(startTime.format(DATE_TIME_FORMATTER))
                    .setAccessRequest(operateLog.isAccessRequest())
                    .setLoggerLevel(LogLevel.INFO.name());
            //设置执行耗时
            loggerMsg.setDuration((Duration.between(startTime, LocalDateTime.now()).toMillis()));

            //获取请求参数
            if (operateLog.logArgs() && ArrayUtils.isNotEmpty(joinPoint.getArgs())) {
                List<Object> objectList = new ArrayList<>(16);
                for (int i = 0; i < joinPoint.getArgs().length; i++) {
                    if (!ArrayUtils.contains(operateLog.ignoreRequestParamsIdx(), i)) {
                        objectList.add(joinPoint.getArgs()[i]);
                    }
                }
                //获取忽略的数据
                //请求参数
                loggerMsg.setRequestMsg(JsonUtil.toJson(objectList));
            }

            //异常信息
            if (Objects.nonNull(exception)) {
                loggerMsg.setExceptionMsg(StringUtils.left(exception.getMessage(), 255));
                loggerMsg.setLoggerLevel(LogLevel.ERROR.name());
            }
            if (Objects.nonNull(response) && !(response instanceof OutputStream) && !operateLog.ignoreResponseParams()) {
                loggerMsg.setResponseMsg(JsonUtil.toJson(response));
            }

            if (!CollectionUtils.isEmpty(enhanceOperationLogServices)) {
                for (EnhanceOperationLogService enhanceOperationLogService : enhanceOperationLogServices) {
                    enhanceOperationLogService.enhance(loggerMsg, operateLog, joinPoint);
                }
            }
            //设置其他参数
            BUSINESS_LOG.info("{}", loggerMsg);
            //处理日志
            loggerMsgService.saveLoggerMsg(loggerMsg, operateLog);
        } catch (Exception e) {
            log.warn("日志处理异常![{}]", e.getMessage());
        }
    }


}
