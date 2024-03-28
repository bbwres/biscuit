package cn.bbwres.biscuit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * 通用返回对象
 *
 * @author zhanglinfeng
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


    public Result() {
    }

    public Result(String resultCode, String resultMsg, T data) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.data = data;
    }

    public Result(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    public Result(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
