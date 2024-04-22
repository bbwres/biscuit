package cn.bbwres.biscuit.entity;

/**
 * 租户基础类
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public class BaseTenantEntity extends BaseEntity {

    private static final long serialVersionUID = -6563481654761263760L;
    /**
     * 租户id
     */
    private String tenantId;


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
