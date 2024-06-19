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

import java.io.Serializable;

/**
 * 错误描述
 *
 * @author zhanglinfeng
 */
public class ErrorMessageInfo implements Serializable {
    private static final long serialVersionUID = -8071875532333309320L;

    /**
     * 错误描述
     */
    private String message;


    /**
     * 是否需要i18n处理
     */
    private Boolean i18nHandler;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getI18nHandler() {
        return i18nHandler;
    }

    public void setI18nHandler(Boolean i18nHandler) {
        this.i18nHandler = i18nHandler;
    }

    public ErrorMessageInfo() {
    }

    public ErrorMessageInfo(String message, Boolean i18nHandler) {
        this.message = message;
        this.i18nHandler = i18nHandler;
    }
}
