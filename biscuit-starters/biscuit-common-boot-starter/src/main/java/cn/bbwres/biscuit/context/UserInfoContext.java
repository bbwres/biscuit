package cn.bbwres.biscuit.context;

import cn.bbwres.biscuit.entity.UserBaseInfo;
import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 用户信息上下文内容
 * <p>
 * 整个应用线程持有该上下文，
 * 从该上下文中可以获取到当前请求配置的用户信息
 *
 * @author zhanglinfeng
 */
public class UserInfoContext {

    /**
     * 使用 ttl
     */
    private static final ThreadLocal<UserInfoContext> THREAD_LOCAL = TransmittableThreadLocal.withInitial(UserInfoContext::new);


    private UserBaseInfo userBaseInfo;

    /**
     * 获取上下文
     *
     * @return
     */
    public static UserBaseInfo getCurrentContext() {
        return THREAD_LOCAL.get().getUserBaseInfo();
    }

    /**
     * 设置用户
     *
     * @param userBaseInfo userBaseInfo
     */
    public static void setCurrentContext(UserBaseInfo userBaseInfo) {
        THREAD_LOCAL.get().setUserBaseInfo(userBaseInfo);
    }


    /**
     * 清除上下文
     */
    public static void clearCurrentContext() {
        THREAD_LOCAL.remove();
    }

    private UserBaseInfo getUserBaseInfo() {
        return userBaseInfo;
    }

    private void setUserBaseInfo(UserBaseInfo userBaseInfo) {
        this.userBaseInfo = userBaseInfo;
    }
}
