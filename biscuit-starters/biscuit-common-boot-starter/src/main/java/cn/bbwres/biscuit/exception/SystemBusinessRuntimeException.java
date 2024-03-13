package cn.bbwres.biscuit.exception;


/**
 * 系统运行时业务异常
 *
 * @author zhanglinfeng
 */
public class SystemBusinessRuntimeException extends SystemRuntimeException {

    /**
     * 000 系统
     * 000 业务
     * 000 异常明细
     */
    public static final String DEFAULT_ERROR_CODE = "100000001";
    private static final long serialVersionUID = -6668831091899992624L;

    /**
     * 初始化异常
     */
    public SystemBusinessRuntimeException() {
        super(DEFAULT_ERROR_CODE, null);
    }

    /**
     * 初始化异常
     *
     * @param message
     */
    public SystemBusinessRuntimeException(String message) {
        super(DEFAULT_ERROR_CODE, message);
    }


    public SystemBusinessRuntimeException(String errorCode, String message) {
        super(errorCode, message);
    }
}
