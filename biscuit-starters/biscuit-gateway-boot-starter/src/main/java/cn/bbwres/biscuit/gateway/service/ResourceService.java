package cn.bbwres.biscuit.gateway.service;

import cn.bbwres.biscuit.gateway.entity.UserRole;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
     * @param roleCode 角色编码
     * @param clientId 客户端id
     * @param tenantId 租户id
     * @return
     */
    List<String> getResourceByRole(String roleCode, String clientId, String tenantId);


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


    /**
     * 获取用户角色信息
     * 默认从当前用户的token 中获取
     *
     * @param userId
     * @param clientId
     * @param authorities
     * @return
     */
    default List<UserRole> getUserRoles(String userId, String clientId, List<Map<String, Object>> authorities) {
        if (CollectionUtils.isEmpty(authorities)) {
            return null;
        }
        List<UserRole> list = new ArrayList<>();
        for (Map<String, Object> authority : authorities) {
            list.add(new UserRole()
                    .setClientId(clientId)
                    .setRoleCode((String) authority.get("roleCode")));

        }
        return list;
    }

}
