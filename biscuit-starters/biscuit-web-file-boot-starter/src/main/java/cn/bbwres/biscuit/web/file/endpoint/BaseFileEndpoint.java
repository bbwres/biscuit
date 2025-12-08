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

package cn.bbwres.biscuit.web.file.endpoint;

import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.exception.constants.ErrorCode;
import cn.bbwres.biscuit.utils.JsonUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 文件操作的基础controller
 *
 * @author zhanglinfeng
 */
@Slf4j
public class BaseFileEndpoint {


    /**
     * 处理错误响应
     *
     * @param response   response
     * @param httpStatus httpStatus
     * @param errorCode  errorCode
     */
    protected void sendErrorResponse(HttpServletResponse response, HttpStatus httpStatus, ErrorCode errorCode) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());
        try {
            String respBody = JsonUtil.toJson(Result.error(errorCode));
            response.getWriter().write(respBody == null ? "" : respBody);
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("输出错误信息时发生异常", e);
        }

    }
}
