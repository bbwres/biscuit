package cn.bbwres.biscuit.scheduler.annotation;


import java.lang.annotation.*;

/**
 * job 定时任务定义
 *
 * @author zhanglinfeng
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface JobDefinition {
    /**
     * job名称
     *
     * @return
     */
    String jobName();

    /**
     * 分组
     * @return
     */
    String group() default "DEFAULT";

    /**
     * 执行的方法
     * @return
     */
    String targetMethod();

    /**
     * job 的cron 表达式
     *
     * @return
     */
    String cron();


    /**
     * 任务描述
     *
     * @return
     */
    String description() default "";

    /**
     * 是否并行处理
     * false 则为串行处理
     *
     * @return
     */
    boolean concurrent() default true;

    /**
     * 任务执行时是否需要传入上下文
     * 为true 时，执行方法需要接收JobExecutionContext类型的参数
     * @return
     */
    boolean useContext() default false;

    /**
     * 任务参数
     *
     * @return
     */
    JobDataMap[] arguments() default {};



}
