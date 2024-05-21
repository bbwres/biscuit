/*
 *
 *  * Copyright 2024 bbwres
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package cn.bbwres.biscuit.redis.lock.aop;

import cn.bbwres.biscuit.redis.lock.annotations.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * redis 分布式锁切面
 *
 * @author zhanglinfeng
 */
@Order(-12)
@Aspect
@RequiredArgsConstructor
public class RedisLockAspect {

    private final RedissonClient redissonClient;

    private final ExpressionParser parser;

    private static final String DISTRIBUTED_LOCK_KEY = "'DISTRIBUTED_LOCK_KEY:'";

    /**
     * 处理锁
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("execution(public * *(..)) && @annotation(cn.bbwres.biscuit.redis.lock.annotations.DistributedLock)")
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        //获取参数
        Parameter[] parameters = method.getParameters();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
        String key = distributedLock.key();
        if (distributedLock.keyUseSpEL()) {
            Object[] args = pjp.getArgs();
            key = spelParser(parameters, args, distributedLock.key(), pjp.getTarget(), method);
        }

        RLock rLock = redissonClient.getLock(key);
        Object result;
        try {
            if (distributedLock.timeout() > 0) {
                rLock.lock(distributedLock.timeout(), distributedLock.timeUnit());
            } else {
                rLock.lock();
            }
            result = pjp.proceed();
        } finally {
            rLock.unlockAsync();
        }
        return result;

    }

    /**
     * spel 解析
     *
     * @param parameters
     * @param args
     * @param spel
     * @return
     */
    public String spelParser(Parameter[] parameters, Object[] args, String spel, Object targetObject, Method method) {
        //测试SpringEL解析器
        Expression expression = parser.parseExpression(DISTRIBUTED_LOCK_KEY + "+" + spel);
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameters.length; i++) {
            context.setVariable(parameters[i].getName(), args[i]);
        }
        context.setVariable("targetClass", targetObject.getClass());
        context.setVariable("methodName", targetObject.getClass().getName() + "." + method.getName());
        context.setVariable("method", method);
        return expression.getValue(context).toString();
    }


}
