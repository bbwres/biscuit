package cn.bbwres.biscuit.gateway.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ResourceServiceImpl implements ResourceService {
    /**
     * 检查并解析token
     *
     * @param token
     * @return
     */
    @Override
    public Map<String, Object> checkToken(String token) {
        return null;
    }

    /**
     * 获取仅需要登陆认证的资源地址
     *
     * @return
     */
    @Override
    public List<String> getLoginAuthResource() {
        return null;
    }

    /**
     * 根据角色信息获取出当前角色拥有的资源信息
     *
     * @param roleId 角色id
     * @return
     */
    @Override
    public List<String> getResourceByRole(String roleId) {
        return List.of("/userEx");
    }

    /**
     * 获取登陆地址
     *
     * @return
     */
    @Override
    public String getLoginUrl() {
        return ResourceService.super.getLoginUrl();
    }

    /**
     * 获取登陆地址
     *
     * @param state
     * @return
     */
    @Override
    public String getLoginUrlBuildState(String state) {
        return ResourceService.super.getLoginUrlBuildState(state);
    }


}
