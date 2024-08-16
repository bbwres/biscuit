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

package cn.bbwres.biscuit.operation.log.service.impl;

import cn.bbwres.biscuit.operation.log.annotation.OperationLog;
import cn.bbwres.biscuit.operation.log.constants.OperationLogConstant;
import cn.bbwres.biscuit.operation.log.entity.OperationLogEntity;
import cn.bbwres.biscuit.operation.log.service.EnhanceOperationLogService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * 补充支持springEl的参数
 *
 * @author zhanglinfeng
 */
@Order(300)
@RequiredArgsConstructor
public class EnhanceOperationLogSpringElServiceImpl implements EnhanceOperationLogService {

    private final ExpressionParser parser;

    /**
     * 扩展补充操作日志参数
     *
     * @param loggerMsg  操作日志对象
     * @param operateLog 注解
     * @param joinPoint  切入点
     * @param response   执行响应
     * @param exception  异常信息
     */
    @Override
    public void enhance(OperationLogEntity loggerMsg, OperationLog operateLog, ProceedingJoinPoint joinPoint, Object response,
                        Throwable exception) {
        // 获取方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();

        if (StringUtils.isNotBlank(operateLog.businessId())) {
            //获取业务id
            loggerMsg.setBusinessId(springElParser(parameters, joinPoint.getArgs(), operateLog.businessId()));
        }
        if (StringUtils.isNotBlank(operateLog.content()) && StringUtils.isBlank(loggerMsg.getContent())) {
            //获取业务内容
            loggerMsg.setContent(springElParser(parameters, joinPoint.getArgs(), operateLog.content()));
        }
    }

    /**
     * spel 解析
     *
     * @param parameters
     * @param args
     * @param springEl
     * @return
     */
    protected String springElParser(Parameter[] parameters, Object[] args, String springEl) {
        if (!(springEl.contains(OperationLogConstant.EL_1) || springEl.contains(OperationLogConstant.EL_2))) {
            return springEl;
        }
        //SpringEL解析器
        StandardEvaluationContext context = new StandardEvaluationContext();
        if (ArrayUtils.isNotEmpty(parameters)) {
            for (int i = 0; i < parameters.length; i++) {
                context.setVariable(parameters[i].getName(), args[i]);
            }
        }
        Expression expression = parser.parseExpression(springEl);
        Object result = expression.getValue(context);
        return Objects.nonNull(result) ? result.toString() : null;
    }
}
