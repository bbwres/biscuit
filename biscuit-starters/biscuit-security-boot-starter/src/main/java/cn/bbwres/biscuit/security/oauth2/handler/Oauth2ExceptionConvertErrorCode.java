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

package cn.bbwres.biscuit.security.oauth2.handler;

import cn.bbwres.biscuit.exception.ErrorMessageInfo;
import cn.bbwres.biscuit.exception.ExceptionConvertErrorCode;
import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * oauth 的错误码
 *
 * @author zhanglinfeng
 */
public class Oauth2ExceptionConvertErrorCode implements ExceptionConvertErrorCode {
    /**
     * 异常信息转换为错误码
     *
     * @param ex
     * @return
     */
    @Override
    public String exceptionConvertErrorCode(Exception ex) {
        if (ex instanceof OAuth2Exception) {
            return GlobalErrorCodeConstants.GLOBAL_HTTP_CODE_PREFIX.getCode() + ((OAuth2Exception) ex).getHttpErrorCode();
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
        return null;
    }
}
