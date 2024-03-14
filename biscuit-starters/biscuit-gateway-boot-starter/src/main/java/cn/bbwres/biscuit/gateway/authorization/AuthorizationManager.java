package cn.bbwres.biscuit.gateway.authorization;


import cn.bbwres.biscuit.gateway.GatewayProperties;
import cn.bbwres.biscuit.gateway.cache.ResourceCacheService;
import cn.bbwres.biscuit.gateway.constants.GatewayConstant;
import cn.bbwres.biscuit.gateway.entity.UserRole;
import cn.bbwres.biscuit.gateway.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;

/**
 * 认证配置信息
 *
 * @author zhanglinfeng
 */
@RequiredArgsConstructor
@Slf4j
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {


    private final GatewayProperties gatewayProperties;

    private final ResourceCacheService resourceCacheService;

    private final PathMatcher pathMatcher;

    /**
     * 检查权限
     * @param mono the Authentication to check
     * @param authorizationContext the object to check
     * @return
     */
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        String path = request.getURI().getPath();
        //是否是已经认证
        Mono<Authentication> authenticationMono = mono.filter(Authentication::isAuthenticated);
        //获取当前用户的角色和拥有的资源
        Mono<Set<String>> resourceMono = authenticationMono.map(authentication -> {
            Set<String> resources = new HashSet<>();
            //获取只需要登陆就可以拿到的资源
            List<String> noAuthResource = resourceCacheService.getLoginAuthResource();
            if (!CollectionUtils.isEmpty(noAuthResource)) {
                resources.addAll(noAuthResource);
            }
            String clientId = authentication.getDetails().toString();

            //设置用户的信息
            authorizationContext.getExchange().getAttributes().put(GatewayConstant.USER_INFO, authentication.getPrincipal());

            //获取用户角色信息
            authentication.getAuthorities().stream()
                    .map((Function<GrantedAuthority, UserRole>) grantedAuthority -> {
                        UserRole userRole = new UserRole();
                        BeanUtils.copyProperties(grantedAuthority, userRole);
                        if (ObjectUtils.isEmpty(userRole.getRoleCode())) {
                            userRole.setRoleCode(grantedAuthority.getAuthority());
                        }
                        return userRole;
                    }).filter(userRole -> clientId.equals(userRole.getClientId()))
                    .map(authority -> resourceCacheService.getResourceByRole(authority.getRoleCode(),
                            authority.getClientId(), authority.getTenantId()))
                    .filter(Objects::nonNull).forEach(resources::addAll);

            return resources;
        });

        return resourceMono.map(resources -> AuthUtils.checkAuth(new ArrayList<>(resources), path, pathMatcher))
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }


}