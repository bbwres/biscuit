package cn.bbwres.biscuit.mybatis.config;

import java.io.Serializable;

/**
 * 自动扫描的mapper包
 *
 * @author zhanglinfeng
 */
public class MapperBasePackage implements Serializable {
    private static final long serialVersionUID = -3218296198348042946L;

    /**
     * 要扫描的mapper包路径，多个路径时用逗号分隔
     */
    private String basePackages;

    public String getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(String basePackages) {
        this.basePackages = basePackages;
    }
}
