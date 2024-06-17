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

/**
 * 异常转换为错误码
 *
 * @author zhanglinfeng
 */
public interface ExceptionConvertErrorCode {


    /**
     * 异常信息转换为错误码
     *
     * @param ex
     * @return
     */
    String exceptionConvertErrorCode(Exception ex);


    /**
     * 异常描述信息转换
     * @param ex
     * @return
     */
    String exceptionConvertErrorMessage(Exception ex);

}
