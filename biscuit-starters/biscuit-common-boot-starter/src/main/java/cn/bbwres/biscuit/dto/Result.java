package cn.bbwres.biscuit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

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
     * @param resultMsg a {@link java.lang.String} object
     * @param data a T object
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
     * @param resultMsg a {@link java.lang.String} object
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
