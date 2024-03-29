package cn.bbwres.biscuit.dto;


import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.List;

/**
 * 简化的page分页数据
 *
 * @author zhanglinfeng
 */
@Schema(description = "分页对象")
public class Page<R, Q> {

    /**
     * 查询数据列表
     */
    @Schema(description = "查询数据列表")
    protected List<R> records = Collections.emptyList();

    /**
     * 查询字段
     */
    @Schema(description = "查询参数")
    protected Q query;

    /**
     * 总数
     */
    @Schema(description = "总条数")
    protected long total = 0;
    /**
     * 每页显示条数，默认 10
     */
    @Schema(description = "每页显示条数")
    protected long size = 10;

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    protected long current = 1;
    /**
     * 总页数
     */
    @Schema(description = "总页数")
    protected long pages = 0L;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段")
    private List<SortInfo> sortInfos;

    /**
     * 是否存在上一页
     *
     * @return true / false
     */
    public boolean hasPrevious() {
        return this.current > 1;
    }

    /**
     * 是否存在下一页
     *
     * @return true / false
     */
    public boolean hasNext() {
        return this.current < this.getPages();
    }

    /**
     * 获取总页数
     */
    public void calculationPages() {
        if (getSize() == 0) {
            this.setPages(0L);
            return;
        }
        long pages = getTotal() / getSize();
        if (getTotal() % getSize() != 0) {
            pages++;
        }
        this.setPages(pages);
    }


    public List<R> getRecords() {
        return records;
    }

    public void setRecords(List<R> records) {
        this.records = records;
    }

    public Q getQuery() {
        return query;
    }

    public void setQuery(Q query) {
        this.query = query;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public List<SortInfo> getSortInfos() {
        return sortInfos;
    }

    public void setSortInfos(List<SortInfo> sortInfos) {
        this.sortInfos = sortInfos;
    }
}
