package cn.bbwres.biscuit.operation.log.entity;

import cn.bbwres.biscuit.utils.JsonUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * 日志消息
 *
 * @author zhanglinfeng
 */
@Getter
@Setter
@Accessors(chain = true)
public class OperationLogEntity implements Serializable {
    private static final long serialVersionUID = -8484099318738763805L;

    /**
     * 链路id 待生成
     */
    private String traceId;


    /**
     * 归属系统
     */
    private String system;


    /**
     * 业务模块
     */
    private String module;

    /**
     * 业务类型
     */
    private String business;


    /**
     * 日志内容
     */
    private String content;


    /**
     * 操作类型
     */
    private String operation;

    /**
     * 业务id
     */
    private String businessId;


    /**
     * 操作人/接入方
     */
    private String operationUser;
    /**
     * 操作人/接入方 名称
     */
    private String operationUserName;


    /**
     * 请求参数
     */
    private String requestMsg;

    /**
     * 响应参数
     */
    private String responseMsg;


    /**
     * 日志生成时间
     */
    private String createTime;


    /**
     * 执行时长，单位：毫秒
     */
    private Long duration;

    /**
     * 异常日志
     */
    private String exceptionMsg;

    /**
     * 日志级别
     */
    private String loggerLevel;

    /**
     * 是否接口请求
     */
    private boolean accessRequest;


    /**
     * 拓展字段
     */
    private Map<String, Object> exts;


    /**
     * 请求方法 get post
     */
    private String requestMethod;

    /**
     * 请求路径地址
     */
    private String requestUrl;

    /**
     * 用户 IP
     */
    private String userIp;

    /**
     * 浏览器 UserAgent
     */
    private String userAgent;


    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
