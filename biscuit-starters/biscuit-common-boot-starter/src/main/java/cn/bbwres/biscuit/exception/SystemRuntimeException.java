package cn.bbwres.biscuit.exception;


import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import org.springframework.util.ObjectUtils;

/**
 * 系统运行时异常
 *
 * @author zhanglinfeng
 */
public class SystemRuntimeException extends RuntimeException {

    public static final String DEFAULT_ERROR_CODE = GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode();

    private static final long serialVersionUID = -6668831091899992624L;

    private String errorCode;

    /**
     * 初始化异常
     */
    public SystemRuntimeException() {
        this(null, null);
    }

    /**
     * 初始化异常
     *
     * @param message
     */
    public SystemRuntimeException(String message) {
        this(null, message);
    }


    public SystemRuntimeException(String errorCode, String message) {
        super(message);
        this.errorCode = ObjectUtils.isEmpty(errorCode) ? DEFAULT_ERROR_CODE : errorCode;
    }


    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }


}
