package cn.bbwres.biscuit.exception;


/**
 * 系统运行时业务异常
 *
 * @author zhanglinfeng
 */
public class SystemBusinessRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -6668831091899992624L;

    private String respType;


    public SystemBusinessRuntimeException(String respType, String message) {
        super(message);
        this.respType = respType;
    }

    public SystemBusinessRuntimeException(String message, Throwable cause, String respType) {
        super(message, cause);
        this.respType = respType;
    }

    public SystemBusinessRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String respType) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.respType = respType;
    }

    public String getRespType() {
        return respType;
    }

    public void setRespType(String respType) {
        this.respType = respType;
    }
}
