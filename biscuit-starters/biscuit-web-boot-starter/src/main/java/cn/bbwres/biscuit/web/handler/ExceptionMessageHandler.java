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
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 异常信息转换处理
 *
 * @author zhanglinfeng
 */
public class ExceptionMessageHandler {


    private final List<ExceptionConvertErrorCode> exceptionConvertErrorCodes;

    public ExceptionMessageHandler(List<ExceptionConvertErrorCode> exceptionConvertErrorCodes) {
        this.exceptionConvertErrorCodes = exceptionConvertErrorCodes;
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
            return new Result<>(errorCode, errorMessageInfo.getMessage(),true);
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
        if (errorMessageInfo.getI18nHandler() != null && errorMessageInfo.getI18nHandler()) {
            return new Result<>(errorCode, errorMessageInfo.getMessage(),true);
        }
        return new Result<>(errorCode, errorMessageInfo.getMessage(), false);
    }

}
