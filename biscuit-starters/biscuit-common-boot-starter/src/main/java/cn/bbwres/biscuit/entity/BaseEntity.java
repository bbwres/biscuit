package cn.bbwres.biscuit.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 375884071908914508L;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 最后更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新人
     */
    private String updater;

    /**
     * 租户id
     */
    private String tenantId;


    /**
     * <p>Getter for the field <code>createTime</code>.</p>
     *
     * @return a {@link java.time.LocalDateTime} object
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * <p>Setter for the field <code>createTime</code>.</p>
     *
     * @param createTime a {@link java.time.LocalDateTime} object
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * <p>Getter for the field <code>updateTime</code>.</p>
     *
     * @return a {@link java.time.LocalDateTime} object
     */
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * <p>Setter for the field <code>updateTime</code>.</p>
     *
     * @param updateTime a {@link java.time.LocalDateTime} object
     */
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * <p>Getter for the field <code>creator</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getCreator() {
        return creator;
    }

    /**
     * <p>Setter for the field <code>creator</code>.</p>
     *
     * @param creator a {@link java.lang.String} object
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * <p>Getter for the field <code>updater</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getUpdater() {
        return updater;
    }

    /**
     * <p>Setter for the field <code>updater</code>.</p>
     *
     * @param updater a {@link java.lang.String} object
     */
    public void setUpdater(String updater) {
        this.updater = updater;
    }

    /**
     * <p>Getter for the field <code>tenantId</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * <p>Setter for the field <code>tenantId</code>.</p>
     *
     * @param tenantId a {@link java.lang.String} object
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
