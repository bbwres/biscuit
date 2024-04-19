package cn.bbwres.biscuit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * 排序信息
 *
 * @author zhanglinfeng12
 * @version $Id: $Id
 */
@Schema(description = "排序信息")
public class SortInfo implements Serializable {
    private static final long serialVersionUID = -4981916565823419803L;

    /**
     * 排序的字段
     */
    @Schema(description = "排序的字段")
    private String sortField;

    /**
     * 排序类型
     */
    @Schema(description = "排序类型")
    private Sort sort;

    /**
     * <p>Constructor for SortInfo.</p>
     */
    public SortInfo() {
    }

    /**
     * <p>Constructor for SortInfo.</p>
     *
     * @param sortField a {@link java.lang.String} object
     * @param sort a {@link cn.bbwres.biscuit.dto.Sort} object
     */
    public SortInfo(String sortField, Sort sort) {
        this.sortField = sortField;
        this.sort = sort;
    }

    /**
     * <p>Getter for the field <code>sortField</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getSortField() {
        return sortField;
    }

    /**
     * <p>Setter for the field <code>sortField</code>.</p>
     *
     * @param sortField a {@link java.lang.String} object
     */
    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    /**
     * <p>Getter for the field <code>sort</code>.</p>
     *
     * @return a {@link cn.bbwres.biscuit.dto.Sort} object
     */
    public Sort getSort() {
        return sort;
    }

    /**
     * <p>Setter for the field <code>sort</code>.</p>
     *
     * @param sort a {@link cn.bbwres.biscuit.dto.Sort} object
     */
    public void setSort(Sort sort) {
        this.sort = sort;
    }
}
