package cn.bbwres.biscuit.gateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 外部接口请求异常
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Getter
public class AccessRequestException extends RuntimeException {
    private static final long serialVersionUID = -3167373523458709045L;

    /**
     * http 状态码
     */
    private final HttpStatus status;

    /**
     * <p>Constructor for AccessRequestException.</p>
     *
     * @param status a {@link org.springframework.http.HttpStatus} object
     * @param message a {@link java.lang.String} object
     */
    public AccessRequestException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

}
