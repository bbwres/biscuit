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
    private String userInfoHeaderName="X-User-Info";






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
}
