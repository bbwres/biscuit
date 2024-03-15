package cn.bbwres.biscuit.gateway.service;

import java.util.List;
import java.util.Map;

/**
 * 获取认证的url资源
 *
 * @author zhanglinfeng
 */
public interface ResourceService {

    /**
     * 检查并解析token
     *
     * @param token
     * @return
     */
    Map<String, Object> checkToken(String token);


    /**
     * 获取仅需要登陆认证的资源地址
     *
     * @return
     */
    List<String> getLoginAuthResource();


    /**
     * 根据角色信息获取出当前角色拥有的资源信息
     *
     * @param roleId 角色id
     * @return
     */
    List<String> getResourceByRole(String roleId);


    /**
     * 获取登陆地址
     *
     * @return
     */
    default String getLoginUrl() {
        return null;
    }

    /**
     * 获取登陆地址
     *
     * @param state
     * @return
     */
    default String getLoginUrlBuildState(String state) {
        return getLoginUrl();
    }


}
