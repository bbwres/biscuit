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

package cn.bbwres.biscuit.security.oauth2.service.redis.repository;

import cn.bbwres.biscuit.security.oauth2.service.redis.pojo.OAuth2AuthorizationGrantAuthorization;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * The following listing shows the OAuth2AuthorizationGrantAuthorizationRepository,
 * which is able to find an OAuth2AuthorizationGrantAuthorization by the id field as well as by state,
 * authorizationCode, accessToken, refreshToken, idToken, deviceState, userCode and deviceCode values.
 *
 * @author zhanglinfeng
 */
@Repository
public interface OAuth2AuthorizationGrantAuthorizationRepository extends CrudRepository<OAuth2AuthorizationGrantAuthorization, String> {

    /**
     * 根据state 查询数据
     *
     * @param state state
     * @return OAuth2AuthorizationGrantAuthorization
     */
    OAuth2AuthorizationGrantAuthorization findByState(String state);


    /**
     * 根据授权码的token查询数据
     *
     * @param authorizationCode authorizationCode
     * @return OAuth2AuthorizationGrantAuthorization
     */
    OAuth2AuthorizationGrantAuthorization findByAuthorizationCodeTokenValue(String authorizationCode);

    /**
     * 根据state或者授权码的code查询数据
     *
     * @param state             state
     * @param authorizationCode authorizationCode
     * @return OAuth2AuthorizationGrantAuthorization
     */
    OAuth2AuthorizationGrantAuthorization findByStateOrAuthorizationCodeTokenValue(String state, String authorizationCode);

    /**
     * 根据accessToken 查询数据
     *
     * @param accessToken accessToken
     * @return OAuth2AuthorizationGrantAuthorization
     */
    OAuth2AuthorizationGrantAuthorization findByAccessTokenValue(String accessToken);

    /**
     * 根据refreshToken 查询数据
     *
     * @param refreshToken refreshToken
     * @return OAuth2AuthorizationGrantAuthorization
     */
    OAuth2AuthorizationGrantAuthorization findByRefreshTokenValue(String refreshToken);

    /**
     * 根据accessToken 或者refreshToken 查询数据
     *
     * @param accessToken  accessToken
     * @param refreshToken refreshToken
     * @return OAuth2AuthorizationGrantAuthorization
     */
    OAuth2AuthorizationGrantAuthorization findByAccessTokenValueOrRefreshTokenValue(String accessToken, String refreshToken);

    /**
     * 根据 idToken 查询数据
     *
     * @param idToken idToken
     * @return OAuth2AuthorizationGrantAuthorization
     */
    OAuth2AuthorizationGrantAuthorization findByIdTokenTokenValue(String idToken);

    /**
     * 根据 deviceState 查询数据
     *
     * @param deviceState deviceState
     * @return OAuth2AuthorizationGrantAuthorization
     */
    OAuth2AuthorizationGrantAuthorization findByDeviceState(String deviceState);

    /**
     * 根据 deviceState 查询数据
     *
     * @param deviceCode deviceState
     * @return OAuth2AuthorizationGrantAuthorization
     */
    OAuth2AuthorizationGrantAuthorization findByDeviceCodeTokenValue(String deviceCode);
    /**
     * 根据 userCode 查询数据
     *
     * @param userCode userCode
     * @return OAuth2AuthorizationGrantAuthorization
     */
    OAuth2AuthorizationGrantAuthorization findByUserCodeTokenValue(String userCode);

    /**
     * 根据 deviceState 查询数据
     *
     * @param deviceState deviceState
     * @param deviceCode deviceCode
     * @param userCode userCode
     * @return OAuth2AuthorizationGrantAuthorization
     */
    OAuth2AuthorizationGrantAuthorization findByDeviceStateOrDeviceCodeTokenValueOrUserCodeTokenValue(String deviceState, String deviceCode, String userCode);

}
