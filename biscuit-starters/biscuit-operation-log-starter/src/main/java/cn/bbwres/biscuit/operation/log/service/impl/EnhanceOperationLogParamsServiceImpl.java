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
import cn.bbwres.biscuit.operation.log.entity.OperationLogEntity;
import cn.bbwres.biscuit.operation.log.service.EnhanceOperationLogService;
import cn.bbwres.biscuit.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.boot.logging.LogLevel;
import org.springframework.core.annotation.Order;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 补充操作日志的请求和响应参数信息
 *
 * @author zhanglinfeng
 */
@Order(100)
@RequiredArgsConstructor
public class EnhanceOperationLogParamsServiceImpl implements EnhanceOperationLogService {


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
    public void enhance(OperationLogEntity loggerMsg,
                        OperationLog operateLog, ProceedingJoinPoint joinPoint, Object response,
                        Throwable exception) {

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

    }
}
