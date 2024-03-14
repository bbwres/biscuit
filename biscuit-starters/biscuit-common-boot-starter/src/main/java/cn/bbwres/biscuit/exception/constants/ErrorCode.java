package cn.bbwres.biscuit.exception.constants;

import java.io.Serializable;

/**
 * 错误码
 *
 * @author zhanglinfeng
 */
public class ErrorCode implements Serializable {
    private static final long serialVersionUID = 9022572900152622888L;

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误信息
     */
    private String message;

    public ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
