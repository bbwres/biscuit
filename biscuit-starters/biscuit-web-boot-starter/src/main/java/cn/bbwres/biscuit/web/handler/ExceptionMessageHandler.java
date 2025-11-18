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

package cn.bbwres.biscuit.web.handler;

import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.exception.ErrorMessageInfo;
import cn.bbwres.biscuit.exception.ExceptionConvertErrorCode;
import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 异常信息转换处理
 *
 * @author zhanglinfeng
 */
public class ExceptionMessageHandler {

    protected MessageSourceAccessor messages;

    private final List<ExceptionConvertErrorCode> exceptionConvertErrorCodes;

    public ExceptionMessageHandler(List<ExceptionConvertErrorCode> exceptionConvertErrorCodes,
                                   ObjectProvider<MessageSourceAccessor> messagesProvider) {
        this.exceptionConvertErrorCodes = exceptionConvertErrorCodes;
        this.messages = messagesProvider.getIfAvailable();
    }

    /**
     * 异常处理
     *
     * @param ex
     * @return
     */
    public Result<?> exceptionHandler(Exception ex) {
        ErrorMessageInfo errorMessageInfo = new ErrorMessageInfo();
        errorMessageInfo.setI18nHandler(true);
        String errorCode = null;

        if (ex instanceof SystemRuntimeException systemRuntimeException) {
            errorMessageInfo.setMessage(ObjectUtils.isEmpty(ex.getMessage()) ? GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getMessage() : ex.getMessage());
            errorCode = systemRuntimeException.getErrorCode();
            return result(errorCode, errorMessageInfo, messages);
        }

        for (ExceptionConvertErrorCode exceptionConvertErrorCode : exceptionConvertErrorCodes) {
            errorCode = exceptionConvertErrorCode.exceptionConvertErrorCode(ex);
            if (!ObjectUtils.isEmpty(errorCode)) {
                break;
            }
        }
        ErrorMessageInfo convert = null;
        for (ExceptionConvertErrorCode exceptionConvertErrorCode : exceptionConvertErrorCodes) {
            convert = exceptionConvertErrorCode.exceptionConvertErrorMessage(ex);
            if (!ObjectUtils.isEmpty(convert)) {
                break;
            }
        }
        errorMessageInfo.setMessage(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getMessage());
        errorMessageInfo = ObjectUtils.isEmpty(convert) ? errorMessageInfo : convert;
        errorCode = ObjectUtils.isEmpty(errorCode) ? GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode() : errorCode;
        return result(errorCode, errorMessageInfo, messages);
    }

    /**
     * 处理响应结果
     *
     * @param errorCode
     * @param errorMessageInfo
     * @param messages
     * @return
     */
    private Result<?> result(String errorCode, ErrorMessageInfo errorMessageInfo, MessageSourceAccessor messages) {
        if (!ObjectUtils.isEmpty(messages)
                && !ObjectUtils.isEmpty(errorMessageInfo.getI18nHandler())
                && errorMessageInfo.getI18nHandler()) {
            // 国际化处理
            errorMessageInfo.setMessage(messages.getMessage(errorMessageInfo.getMessage(), null, errorMessageInfo.getMessage()));
        }
        return new Result<>(errorCode, errorMessageInfo.getMessage());
    }
}
