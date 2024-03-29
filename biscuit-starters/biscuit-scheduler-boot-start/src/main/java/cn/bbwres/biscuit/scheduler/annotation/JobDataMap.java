package cn.bbwres.biscuit.scheduler.annotation;

import java.lang.annotation.*;

/**
 * job 参数
 *
 * @author zhanglinfeng
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface JobDataMap {

    /**
     * 参数key
     * @return
     */
    String key();

    /**
     * 参数值
     * @return
     */
    String value();
}
