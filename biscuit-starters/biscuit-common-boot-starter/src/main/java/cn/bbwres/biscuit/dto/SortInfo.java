package cn.bbwres.biscuit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * 排序信息
 *
 * @author zhanglinfeng12
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

    public SortInfo() {
    }

    public SortInfo(String sortField, Sort sort) {
        this.sortField = sortField;
        this.sort = sort;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }
}
