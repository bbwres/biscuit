/*
 *
 *  * Copyright 2024 bbwres
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package cn.bbwres.biscuit.security.oauth2.endpoint;

import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import cn.bbwres.biscuit.security.oauth2.constants.Oauth2SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 用户信息的端点
 *
 * @author zhanglinfeng
 */
@Slf4j
@RestController
@RequestMapping("/rpc-api/auth")
public class UserInfoEndpoint {

    private final OAuth2AuthorizationService oauth2AuthorizationService;

    private final ResourceService resourceService;

    public UserInfoEndpoint(OAuth2AuthorizationService oauth2AuthorizationService,
                            ResourceService resourceService) {
        this.oauth2AuthorizationService = oauth2AuthorizationService;
        this.resourceService = resourceService;
    }

    /**
     * 检查用户的token
     *
     * @param token 用户token
     * @return 用户信息
     */
    @PostMapping("/check_token")
    public Result<UserBaseInfo> checkToken(@RequestBody String token) {
        OAuth2Authorization oauth2Authorization = oauth2AuthorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        if (ObjectUtils.isEmpty(oauth2Authorization)) {
            return Result.error(GlobalErrorCodeConstants.INVALID_TOKEN);
        }
        UserBaseInfo userBaseInfo = new UserBaseInfo();
        userBaseInfo.setClientId(oauth2Authorization.getRegisteredClientId());
        userBaseInfo.setUsername(oauth2Authorization.getPrincipalName());
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = oauth2Authorization.getAccessToken();
        if (accessToken.isInvalidated() || accessToken.isExpired()) {
            log.info("accessToken is invalidated or expired。token:{}", token);
            return Result.error(GlobalErrorCodeConstants.INVALID_TOKEN);
        }
        Map<String, Object> claims = accessToken.getClaims();
        if (CollectionUtils.isEmpty(claims)) {
            userBaseInfo.setAuthorities(new ArrayList<>(oauth2Authorization.getAuthorizedScopes()));
            return Result.success(userBaseInfo);
        }
        userBaseInfo.setUserId(Objects.nonNull(claims.get(Oauth2SystemConstants.CUSTOM_CLAIMS_PREFIX_USER_ID)) ? claims.get(Oauth2SystemConstants.CUSTOM_CLAIMS_PREFIX_USER_ID).toString() : null);
        userBaseInfo.setTenantId(Objects.nonNull(claims.get(Oauth2SystemConstants.CUSTOM_CLAIMS_PREFIX_TENANT_ID)) ? claims.get(Oauth2SystemConstants.CUSTOM_CLAIMS_PREFIX_TENANT_ID).toString() : null);
        userBaseInfo.setUserInfo(claims);
        userBaseInfo.setZhName(Objects.nonNull(claims.get(Oauth2SystemConstants.CUSTOM_CLAIMS_PREFIX_ZH_NAME)) ? claims.get(Oauth2SystemConstants.CUSTOM_CLAIMS_PREFIX_ZH_NAME).toString() : null);

        Object roles = claims.get(Oauth2SystemConstants.CUSTOM_CLAIMS_PREFIX_ROLES);
        if (Objects.nonNull(roles)) {
            Set<String> roles1 = (Set<String>) roles;
            userBaseInfo.setAuthorities(new ArrayList<>(roles1));
        }
        return Result.success(userBaseInfo);
    }


    /**
     * 获取登录就可以访问的地址
     *
     * @return
     */
    @GetMapping("/login_auth_resource")
    public Result<List<String>> loginAuthResource() {
        return Result.success(resourceService.getLoginAuthResource());
    }


    /**
     * 获取登录就可以访问的地址
     *
     * @return
     */
    @PostMapping("/resource_by_role")
    public Result<List<String>> resourceByRole(@RequestBody Set<String> rules) {
        return Result.success(resourceService.getResourceByRole(rules));
    }


}
