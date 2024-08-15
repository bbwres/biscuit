package cn.bbwres.biscuit.operation.log.annotation;

import java.lang.annotation.*;

/**
 * 业务操作日志注解
 *
 * @author zhanglinfeng
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {


    /**
     * 当前日志归属的系统，为空则取spring.application.name
     */
    String system() default "";

    /**
     * 业务模块
     */
    String module() default "";

    /**
     * 业务类型
     */
    String business() default "";


    /**
     * 日志内容
     */
    String content() default "";


    /**
     * 操作类型
     * 增删改查
     */
    String operation() default "";


    /**
     * 业务id
     */
    String businessId() default "";

    /**
     * 是否接入方请求
     */
    boolean isAccessRequest() default false;

    /**
     * 是否记录方法参数
     */
    boolean logArgs() default true;

    /**
     * 忽略的请求参数序号,默认为不忽略
     */
    int[] ignoreRequestParamsIdx() default {-1};

    /**
     * 是否忽略响应参数 默认为不忽略
     */
    boolean ignoreResponseParams() default false;


}
