package cn.bbwres.biscuit.exception;


/**
 * 参数校验异常
 *
 * @author zhanglinfeng
 */
public class ParamsCheckRuntimeException extends SystemRuntimeException {

    public static final String DEFAULT_ERROR_CODE = "400";
    private static final long serialVersionUID = -6668831091899992624L;

    /**
     * 初始化异常
     */
    public ParamsCheckRuntimeException() {
        super(DEFAULT_ERROR_CODE, null);
    }

    /**
     * 初始化异常
     *
     * @param message
     */
    public ParamsCheckRuntimeException(String message) {
        super(DEFAULT_ERROR_CODE, message);
    }

    public ParamsCheckRuntimeException(String errorCode, String message) {
        super(errorCode, message);
    }
}