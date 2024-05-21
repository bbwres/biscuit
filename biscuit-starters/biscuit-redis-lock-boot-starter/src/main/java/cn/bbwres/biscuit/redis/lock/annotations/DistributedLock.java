package cn.bbwres.biscuit.redis.lock.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 *
 * @author zhanglinfeng
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /**
     * 锁超时时间，为0则表示永不超时
     * 默认为30s
     *
     * @return
     */
    long timeout() default 30L;

    /**
     * 时间单位
     *
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 加锁的key
     *
     * @return
     */
    String key() default "#methodName";

    /**
     * key解析使用spel 表达式
     *
     * @return
     */
    boolean keyUseSpEL() default true;


}
