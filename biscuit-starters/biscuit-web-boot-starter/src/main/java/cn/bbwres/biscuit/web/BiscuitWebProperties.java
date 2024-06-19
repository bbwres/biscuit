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

package cn.bbwres.biscuit.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * web 配置参数
 *
 * @author zhanglinfeng
 */
@ConfigurationProperties("biscuit.web")
public class BiscuitWebProperties {

    /**
     * web 传输时的默认日期 时间格式化
     */
    private String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * 传输时的默认日期格式化
     */
    private String dateFormat = "yyyy-MM-dd";

    /**
     * 传输时的默认时间格式化
     */
    private String timeFormat = "HH:mm:ss";

    /**
     * 用户信息的header名称
     */
    private String userInfoHeaderName = "X-User-Info";

    /**
     * 参数校验是否开启快速失败
     */
    private Boolean validatorFailFast = true;

    /**
     * 是否开启参数验证国际化
     */
    private Boolean validatorI18nEnable = false;
    /**
     * 参数验证国际化配置
     */
    private String validatorI18nBaseName = "i18n/validatorMessages";

    /**
     * 参数验证国际化配置缓存时间
     */
    private Integer validatorI18nCacheSeconds = 240;


    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getUserInfoHeaderName() {
        return userInfoHeaderName;
    }

    public void setUserInfoHeaderName(String userInfoHeaderName) {
        this.userInfoHeaderName = userInfoHeaderName;
    }

    public Boolean getValidatorFailFast() {
        return validatorFailFast;
    }

    public void setValidatorFailFast(Boolean validatorFailFast) {
        this.validatorFailFast = validatorFailFast;
    }

    public String getValidatorI18nBaseName() {
        return validatorI18nBaseName;
    }

    public void setValidatorI18nBaseName(String validatorI18nBaseName) {
        this.validatorI18nBaseName = validatorI18nBaseName;
    }

    public Integer getValidatorI18nCacheSeconds() {
        return validatorI18nCacheSeconds;
    }

    public void setValidatorI18nCacheSeconds(Integer validatorI18nCacheSeconds) {
        this.validatorI18nCacheSeconds = validatorI18nCacheSeconds;
    }

    public Boolean getValidatorI18nEnable() {
        return validatorI18nEnable;
    }

    public void setValidatorI18nEnable(Boolean validatorI18nEnable) {
        this.validatorI18nEnable = validatorI18nEnable;
    }
}
