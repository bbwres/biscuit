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

/**
 * 参数校验异常
 *
 * @author zhanglinfeng
 */
public class ParamsCheckRuntimeException extends SystemRuntimeException {

    public static final String DEFAULT_ERROR_CODE = GlobalErrorCodeConstants.BAD_REQUEST.getCode();
    private static final long serialVersionUID = -6668831091899992624L;

    /**
     * 初始化异常
     */
    public ParamsCheckRuntimeException() {
        super(DEFAULT_ERROR_CODE, null);
    }

    /**
     * 初始化异常
     *
     * @param message
     */
    public ParamsCheckRuntimeException(String message) {
        super(DEFAULT_ERROR_CODE, message);
    }

    public ParamsCheckRuntimeException(String errorCode, String message) {
        super(errorCode, message);
    }
}
