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

package cn.bbwres.biscuit.dto;

import cn.bbwres.biscuit.exception.constants.ErrorCode;
import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;

/**
 * 通用返回对象
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Schema(description = "通用返回对象")
public class Result<T> implements Serializable {
    private static final long serialVersionUID = -6435479828255665882L;

    /**
     * 响应码
     */
    @Schema(description = "响应码")
    private String resultCode;

    /**
     * 响应描述
     */
    @Schema(description = "响应描述")
    private String resultMsg;

    /**
     * 响应的数据
     */
    @Schema(description = "响应的数据")
    private T data;


    /**
     * <p>Constructor for Result.</p>
     */
    public Result() {
    }

    /**
     * <p>Constructor for Result.</p>
     *
     * @param resultCode a {@link java.lang.String} object
     * @param resultMsg  a {@link java.lang.String} object
     * @param data       a T object
     */
    public Result(String resultCode, String resultMsg, T data) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.data = data;
    }

    /**
     * <p>Constructor for Result.</p>
     *
     * @param resultCode a {@link java.lang.String} object
     * @param resultMsg  a {@link java.lang.String} object
     */
    public Result(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    /**
     * <p>Constructor for Result.</p>
     *
     * @param resultCode a {@link java.lang.String} object
     */
    public Result(String resultCode) {
        this.resultCode = resultCode;
    }


    /**
     * 处理成功
     *
     * @param data 数据
     * @param <T>  返回当前数据
     * @return 返回处理成功的对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(GlobalErrorCodeConstants.SUCCESS.getCode(),
                GlobalErrorCodeConstants.SUCCESS.getMessage(), data);
    }

    /**
     * 处理失败
     *
     * @param errorCode 错误码
     * @param errorMsg  错误描述
     * @param <T>       数据对象
     * @return 处理失败的数据
     */
    public static <T> Result<T> error(ErrorCode errorCode, String errorMsg) {
        return new Result<>(errorCode.getCode(), ObjectUtils.isEmpty(errorMsg) ? errorCode.getMessage() : errorMsg);
    }

    /**
     * 检查是否为成功
     *
     * @return true 成功
     */
    public boolean checkSuccess() {
        return GlobalErrorCodeConstants.SUCCESS.getCode().equals(this.resultCode);
    }


    /**
     * 处理失败
     *
     * @param errorCode 错误码
     * @param <T>       数据对象
     * @return 处理失败的数据
     */
    public static <T> Result<T> error(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), null);
    }

    /**
     * <p>Getter for the field <code>resultCode</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * <p>Setter for the field <code>resultCode</code>.</p>
     *
     * @param resultCode a {@link java.lang.String} object
     */
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * <p>Getter for the field <code>resultMsg</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getResultMsg() {
        return resultMsg;
    }

    /**
     * <p>Setter for the field <code>resultMsg</code>.</p>
     *
     * @param resultMsg a {@link java.lang.String} object
     */
    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    /**
     * <p>Getter for the field <code>data</code>.</p>
     *
     * @return a T object
     */
    public T getData() {
        return data;
    }

    /**
     * <p>Setter for the field <code>data</code>.</p>
     *
     * @param data a T object
     */
    public void setData(T data) {
        this.data = data;
    }
}
