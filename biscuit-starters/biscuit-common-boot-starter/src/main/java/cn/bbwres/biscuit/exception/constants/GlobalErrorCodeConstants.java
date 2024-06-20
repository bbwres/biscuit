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

package cn.bbwres.biscuit.exception.constants;

/**
 * 错误码常量
 *
 * @author zhanglinfeng
 */
public interface GlobalErrorCodeConstants {

    //################################ 通用错误码 ###############################

    /**
     * 成功
     */
    ErrorCode SUCCESS = new ErrorCode("0", "success");
    /**
     * 请求参数不正确
     */
    ErrorCode GLOBAL_HTTP_CODE_PREFIX = new ErrorCode("100000", "http状态码前缀");

    ErrorCode BAD_REQUEST = new ErrorCode("100000400", "global.bad_request");
    ErrorCode UNAUTHORIZED = new ErrorCode("100000401", "global.unauthorized");
    ErrorCode FORBIDDEN = new ErrorCode("100000403", "global.forbidden");
    ErrorCode NOT_FOUND = new ErrorCode("100000404", "global.not_found");
    ErrorCode METHOD_NOT_ALLOWED = new ErrorCode("100000405", "global.method_not_allowed");
    ErrorCode LOCKED = new ErrorCode("100000423", "global.locked");
    ErrorCode TOO_MANY_REQUESTS = new ErrorCode("100000429", "global.too_many_requests");
    ErrorCode INTERNAL_SERVER_ERROR = new ErrorCode("100000500", "global.internal_server_error");


    //############################## 登录相关错误 ##########################################

    ErrorCode INVALID_CAPTCHA = new ErrorCode("101000100", "global.invalid_captcha");
    ErrorCode INVALID_TOKEN = new ErrorCode("101000101", "global.invalid_token");

    // ############################### 业务错误码 ##############################

    /**
     * 通用的业务处理失败状态码
     * 000 系统
     * 000 业务
     * 000 异常明细
     */
    ErrorCode BUSINESS_ERROR = new ErrorCode("200000001", "global.business_error");
}
