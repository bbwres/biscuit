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

package cn.bbwres.biscuit.security.oauth2.converter;

import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.endpoint.DefaultOAuth2AccessTokenResponseMapConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义的oauth2的accessToken响应参数
 * ---无用---
 *
 * @author zhanglinfeng
 */
public final class BiscuitOAuth2AccessTokenResponseMapConverter implements Converter<OAuth2AccessTokenResponse, Map<String, Object>> {

    private final Converter<OAuth2AccessTokenResponse, Map<String, Object>> accessTokenResponseParametersConverter = new DefaultOAuth2AccessTokenResponseMapConverter();

    /**
     * 转换响应参数
     *
     * @param source
     * @return
     */
    @Override
    public Map<String, Object> convert(OAuth2AccessTokenResponse source) {
        Map<String, Object> map = accessTokenResponseParametersConverter.convert(source);
        Map<String, Object> result = new HashMap<>(16);
        result.put(Result.RESULT_CODE_FIELD_NAME, GlobalErrorCodeConstants.SUCCESS.getCode());
        result.put(Result.RESULT_MSG_FIELD_NAME, GlobalErrorCodeConstants.SUCCESS.getMessage());
        result.put(Result.DATA_FIELD_NAME, map);
        return result;
    }
}
