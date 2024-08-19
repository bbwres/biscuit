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
import cn.bbwres.biscuit.operation.log.service.OperationLogSaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 保存操作日志
 *
 * @author zhanglinfeng
 */
@Slf4j
@RequiredArgsConstructor
public class SaveOperationLogServiceImpl implements EnhanceOperationLogService {

    private final OperationLogSaveService operationLogSaveService;

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
        operationLogSaveService.saveLoggerMsg(loggerMsg, operateLog);
    }
}
