package cn.bbwres.biscuit.web.filter;

import cn.bbwres.biscuit.context.UserInfoContext;
import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.web.utils.WebFrameworkUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 用户信息处理服务拦截器
 *
 * @author zhanglinfeng
 */
public class UserInfoFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoFilter.class);


    /**
     * 执行拦截处理
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        UserBaseInfo requestUser = WebFrameworkUtils.getRequestUser();
        LOGGER.debug("当前请求放入线程上下文的参数为:[{}]", requestUser);
        UserInfoContext.setCurrentContext(requestUser);
        try {
            filterChain.doFilter(request, response);
        } finally {
            UserInfoContext.clearCurrentContext();
        }

    }
}
