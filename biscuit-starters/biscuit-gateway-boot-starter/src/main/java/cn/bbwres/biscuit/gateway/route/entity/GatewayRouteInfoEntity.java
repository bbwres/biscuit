package cn.bbwres.biscuit.gateway.route.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.*;

/**
 * @author zhanglinfeng
 */
@Data
@EqualsAndHashCode
public class GatewayRouteInfoEntity implements Serializable {
    private static final long serialVersionUID = 8006129440015626673L;

    /**
     * id
     */
    private String id;
    /**
     * uri
     */
    private String uri;
    /**
     * 断言信息
     */
    private List<String> predicates = new ArrayList<>();
    /**
     * 用户断言信息
     */
    private List<Predicate> userPredicates = new ArrayList<>();
    /**
     * 过滤器信息
     */
    private List<String> filters = new ArrayList<>();
    /**
     * 用户过滤器信息
     */
    private List<Filter> userFilters = new ArrayList<>();
    /**
     * 顺序
     */
    private int order = 0;
    /**
     * 元数据信息
     */
    private Map<String, Object> metadata = new HashMap<>();


    /**
     * 断言信息配置
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Predicate extends Clause {

        private static final long serialVersionUID = -9036062391460206476L;
    }

    /**
     * 过滤器配置
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Filter extends Clause {

        private static final long serialVersionUID = 8102213103104986156L;
    }

    /**
     *基础信息
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Clause implements Serializable {

        private static final long serialVersionUID = -879919541829050104L;
        /**
         * 名称
         */
        private String name;

        /**
         * 参数
         */
        private Map<String, String> args = new LinkedHashMap<>();

    }

}