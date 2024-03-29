package cn.bbwres.biscuit.scheduler.annotation;


import java.lang.annotation.*;

/**
 * 定时批处理任务定义
 *
 * @author zhanglinfeng
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface JobBatchDefinition {
    /**
     * 批处理名称
     *
     * @return
     */
    String batchName();

    /**
     * 分组
     *
     * @return
     */
    String group() default "DEFAULT";

    /**
     * 初始化执行的方法
     *
     * @return
     */
    String initMethod();

    /**
     * 初始化的 的cron 表达式
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


}
