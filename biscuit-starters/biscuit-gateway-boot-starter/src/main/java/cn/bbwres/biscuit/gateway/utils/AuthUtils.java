package cn.bbwres.biscuit.gateway.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;

import java.util.List;
import java.util.Objects;

/**
 * 认证工具类
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
public class AuthUtils {


    /**
     * 鉴权检查
     *
     * @param noAuthResource a {@link java.util.List} object
     * @param path a {@link java.lang.String} object
     * @param pathMatcher a {@link org.springframework.util.PathMatcher} object
     * @return a boolean
     */
    public static boolean checkAuth(List<String> noAuthResource, String path, PathMatcher pathMatcher) {
        //获取出认证请求地址
        if (!CollectionUtils.isEmpty(noAuthResource)) {
            return noAuthResource.stream()
                    .filter(Objects::nonNull)
                    //根据前缀先过滤一遍
                    .anyMatch(resource -> {
                        boolean match = pathMatcher.match(resource, path);
                        if (match) {
                            log.info("当前请求路径:[{}]与地址:[{}]匹配成功", path, resource);
                        }
                        return match;
                    });
        }
        return false;
    }


}
