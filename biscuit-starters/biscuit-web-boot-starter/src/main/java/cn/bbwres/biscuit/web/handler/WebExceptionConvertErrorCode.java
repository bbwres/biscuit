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

import cn.bbwres.biscuit.exception.ErrorMessageInfo;
import cn.bbwres.biscuit.exception.ExceptionConvertErrorCode;
import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ValidationException;

/**
 * web的异常转换为错误码
 *
 * @author zhanglinfeng
 */
public class WebExceptionConvertErrorCode implements ExceptionConvertErrorCode {
    /**
     * 异常信息转换为错误码
     *
     * @param ex
     * @return
     */
    @Override
    public String exceptionConvertErrorCode(Exception ex) {
        if (ex instanceof MissingServletRequestParameterException
                || ex instanceof IllegalArgumentException
                || ex instanceof MethodArgumentTypeMismatchException
                || ex instanceof BindException
                || ex instanceof ValidationException) {
            return GlobalErrorCodeConstants.BAD_REQUEST.getCode();
        }
        if (ex instanceof NoHandlerFoundException) {
            return GlobalErrorCodeConstants.NOT_FOUND.getCode();
        }
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            return GlobalErrorCodeConstants.METHOD_NOT_ALLOWED.getCode();
        }
        return null;
    }

    /**
     * 异常描述信息转换
     *
     * @param ex
     * @return
     */
    @Override
    public ErrorMessageInfo exceptionConvertErrorMessage(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException) {
            StringBuilder sb = new StringBuilder();
            BindingResult bindingResult = ((MethodArgumentNotValidException) ex).getBindingResult();
            for (ObjectError error : bindingResult.getAllErrors()) {
                sb.append('[').append(error.getDefaultMessage()).append("] ");
            }
            ErrorMessageInfo errorMessageInfo = new ErrorMessageInfo();
            errorMessageInfo.setMessage(sb.toString());
            errorMessageInfo.setI18nHandler(false);
            return errorMessageInfo;
        }
        return null;
    }
}
