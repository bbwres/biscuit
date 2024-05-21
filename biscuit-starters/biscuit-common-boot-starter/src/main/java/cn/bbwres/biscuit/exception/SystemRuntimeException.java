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

package cn.bbwres.biscuit.exception;


import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import org.springframework.util.ObjectUtils;

/**
 * 系统运行时异常
 *
 * @author zhanglinfeng
 */
public class SystemRuntimeException extends RuntimeException {

    public static final String DEFAULT_ERROR_CODE = GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode();

    private static final long serialVersionUID = -6668831091899992624L;

    private String errorCode;

    /**
     * 初始化异常
     */
    public SystemRuntimeException() {
        this(null, null);
    }

    /**
     * 初始化异常
     *
     * @param message
     */
    public SystemRuntimeException(String message) {
        this(null, message);
    }


    public SystemRuntimeException(String errorCode, String message) {
        super(message);
        this.errorCode = ObjectUtils.isEmpty(errorCode) ? DEFAULT_ERROR_CODE : errorCode;
    }


    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }


}
