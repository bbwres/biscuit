package cn.bbwres.biscuit.utils.okhttp.intercept;

import okhttp3.Interceptor;
import org.springframework.core.Ordered;

/**
 *
 * 有顺序的拦截器
 *
 * @author zlf
 * @version 1.0
 * @since 2025-12-31  17:45
 */
public interface OrderInterceptor extends Interceptor, Ordered {
}
