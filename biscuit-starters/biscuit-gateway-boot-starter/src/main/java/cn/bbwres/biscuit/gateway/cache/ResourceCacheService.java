package cn.bbwres.biscuit.gateway.cache;

import cn.bbwres.biscuit.gateway.GatewayProperties;
import cn.bbwres.biscuit.gateway.service.ResourceService;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 获取认证的url资源 的缓存服务
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public class ResourceCacheService {

    /**
     * 登陆认证
     */
    public static final String LOGIN_AUTH_RESOURCE = "LOGIN_AUTH_RESOURCE";

    private LoadingCache<String, List<String>> resourceNoUserCache;

    private LoadingCache<String, List<String>> resourceRoleCache;

    private final GatewayProperties gatewayProperties;

    private final ResourceService resourceService;

    /**
     * <p>Constructor for ResourceCacheService.</p>
     *
     * @param gatewayProperties a {@link cn.bbwres.biscuit.gateway.GatewayProperties} object
     * @param resourceService a {@link cn.bbwres.biscuit.gateway.service.ResourceService} object
     */
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
                    .build(resourceService::getResourceByRole);
        }
    }


    /**
     * 获取仅需要登陆认证的资源地址
     *
     * @return a {@link java.util.List} object
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
     * @param roleId 角色id
     * @return a {@link java.util.List} object
     */
    public List<String> getResourceByRole(String roleId) {
        if (gatewayProperties.getCacheResource()) {
            return resourceRoleCache.get(roleId);
        }
        return resourceService.getResourceByRole(roleId);
    }


    /**
     * 获取登陆地址
     *
     * @param state a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    public String getLoginUrlBuildState(String state) {
        return resourceService.getLoginUrlBuildState(state);
    }


}
