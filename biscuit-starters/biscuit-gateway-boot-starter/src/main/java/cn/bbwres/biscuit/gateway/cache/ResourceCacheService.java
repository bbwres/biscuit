package cn.bbwres.biscuit.gateway.cache;

import cn.bbwres.biscuit.gateway.GatewayProperties;
import cn.bbwres.biscuit.gateway.entity.UserRole;
import cn.bbwres.biscuit.gateway.service.ResourceService;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 获取认证的url资源 的缓存服务
 *
 * @author zhanglinfeng
 */
public class ResourceCacheService {

    /**
     * 登陆认证
     */
    public static final String LOGIN_AUTH_RESOURCE = "LOGIN_AUTH_RESOURCE";

    private LoadingCache<String, List<String>> resourceNoUserCache;

    private LoadingCache<UserRole, List<String>> resourceRoleCache;

    private final GatewayProperties gatewayProperties;

    private final ResourceService resourceService;

    public ResourceCacheService(GatewayProperties gatewayProperties, ResourceService resourceService) {
        this.gatewayProperties = gatewayProperties;
        this.resourceService = resourceService;
    }

    /**
     * 初始化缓存
     */
    @PostConstruct
    public void init() {
        if (gatewayProperties.getCacheResource()) {
            resourceNoUserCache = Caffeine.newBuilder()
                    .expireAfterWrite(gatewayProperties.getLocalCacheResourceTime(), TimeUnit.SECONDS)
                    .maximumSize(10)
                    .build(key -> resourceService.getLoginAuthResource());
            resourceRoleCache = Caffeine.newBuilder()
                    .expireAfterWrite(gatewayProperties.getLocalCacheResourceTime(), TimeUnit.SECONDS)
                    .maximumSize(2000)
                    .build(key -> resourceService.getResourceByRole(key.getRoleCode(), key.getClientId(), key.getTenantId()));
        }
    }


    /**
     * 获取仅需要登陆认证的资源地址
     *
     * @return
     */
    public List<String> getLoginAuthResource() {
        if (gatewayProperties.getCacheResource()) {
            return resourceNoUserCache.get(LOGIN_AUTH_RESOURCE);
        }
        return resourceService.getLoginAuthResource();
    }


    /**
     * 根据角色信息获取出当前角色拥有的资源信息
     *
     * @param roleCode 角色编码
     * @param clientId 客户端id
     * @param tenantId 租户id
     * @return
     */
    public List<String> getResourceByRole(String roleCode, String clientId, String tenantId) {
        if (gatewayProperties.getCacheResource()) {
            return resourceRoleCache.get(new UserRole(roleCode, clientId, tenantId));
        }
        return resourceService.getResourceByRole(roleCode, clientId, tenantId);
    }


    /**
     * 获取登陆地址
     *
     * @param state
     * @return
     */
    public String getLoginUrlBuildState(String state) {
        return resourceService.getLoginUrlBuildState(state);
    }


    /**
     * 获取用户角色信息
     * 默认从当前用户的token 中获取
     * TODO 是否加入缓存
     *
     * @param userId
     * @param clientId
     * @param authorities
     * @return
     */
    public List<UserRole> getUserRoles(String userId, String clientId, List<Map<String, Object>> authorities) {
        return resourceService.getUserRoles(userId, clientId, authorities);
    }


}
