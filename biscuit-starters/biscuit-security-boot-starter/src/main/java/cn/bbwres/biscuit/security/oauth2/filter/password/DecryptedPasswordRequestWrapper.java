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

package cn.bbwres.biscuit.security.oauth2.filter.password;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * 包装 HttpServletRequest，替换指定参数（如 password）的取值。
 * <p>
 * OAuth2 password grant 走 form 表单，需要通过这个 wrapper 把解密后的真实密码透传给后续的认证流程。
 *
 * @author zhanglinfeng
 */
public class DecryptedPasswordRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String[]> overrideParams;

    public DecryptedPasswordRequestWrapper(HttpServletRequest request, String paramName, String paramValue) {
        super(request);
        this.overrideParams = new HashMap<>();
        this.overrideParams.put(paramName, new String[]{paramValue});
    }

    @Override
    public String getParameter(String name) {
        String[] values = overrideParams.get(name);
        if (values != null && values.length > 0) {
            return values[0];
        }
        return super.getParameter(name);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = overrideParams.get(name);
        if (values != null) {
            return values;
        }
        return super.getParameterValues(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> merged = new HashMap<>(super.getParameterMap());
        merged.putAll(overrideParams);
        return merged;
    }
}
