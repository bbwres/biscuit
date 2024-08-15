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
import cn.bbwres.biscuit.operation.log.constants.LoggerConstant;
import cn.bbwres.biscuit.operation.log.entity.OperationLogEntity;
import cn.bbwres.biscuit.operation.log.service.EnhanceOperationLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;

/**
 * 补充操作日志的基本信息
 *
 * @author zhanglinfeng
 */
@RequiredArgsConstructor
public class EnhanceOperationLogBaseServiceImpl implements EnhanceOperationLogService {

    private final Environment environment;

    /**
     * 扩展补充操作日志参数
     *
     * @param loggerMsg  操作日志对象
     * @param operateLog 注解
     * @param joinPoint  切入点
     */
    @Override
    public void enhance(OperationLogEntity loggerMsg,
                        OperationLog operateLog, ProceedingJoinPoint joinPoint) {

        //补充系统名称
        if (!ObjectUtils.isEmpty(operateLog.system())) {
            loggerMsg.setSystem(operateLog.system());
        } else {
            loggerMsg.setSystem(environment.getProperty(LoggerConstant.APP_NAME_KEY));
        }
        //补充系统模块
        //补充操作类型
        loggerMsg.setBusiness(operateLog.business())
                .setModule(operateLog.module())
                .setOperation(operateLog.operation());

    }
}
