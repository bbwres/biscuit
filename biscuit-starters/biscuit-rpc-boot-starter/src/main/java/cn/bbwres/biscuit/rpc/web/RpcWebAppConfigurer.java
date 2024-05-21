package cn.bbwres.biscuit.rpc.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * rpc webConfigurer 配置
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@RequiredArgsConstructor
public class RpcWebAppConfigurer implements WebMvcConfigurer {

    private final RpcServerHandlerInterceptorAdapter rpcServerHandlerInterceptorAdapter;


    /** {@inheritDoc} */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //增加rpc服务端拦截器
        registry.addInterceptor(rpcServerHandlerInterceptorAdapter).addPathPatterns("/**");
    }
}
