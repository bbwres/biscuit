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

package cn.bbwres.biscuit.dto;


import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.List;

/**
 * 简化的page分页数据
 *
 * @author zhanglinfeng
 * @version $Id: $Id
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


    /**
     * <p>Getter for the field <code>records</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<R> getRecords() {
        return records;
    }

    /**
     * <p>Setter for the field <code>records</code>.</p>
     *
     * @param records a {@link java.util.List} object
     */
    public void setRecords(List<R> records) {
        this.records = records;
    }

    /**
     * <p>Getter for the field <code>query</code>.</p>
     *
     * @return a Q object
     */
    public Q getQuery() {
        return query;
    }

    /**
     * <p>Setter for the field <code>query</code>.</p>
     *
     * @param query a Q object
     */
    public void setQuery(Q query) {
        this.query = query;
    }

    /**
     * <p>Getter for the field <code>total</code>.</p>
     *
     * @return a long
     */
    public long getTotal() {
        return total;
    }

    /**
     * <p>Setter for the field <code>total</code>.</p>
     *
     * @param total a long
     */
    public void setTotal(long total) {
        this.total = total;
    }

    /**
     * <p>Getter for the field <code>size</code>.</p>
     *
     * @return a long
     */
    public long getSize() {
        return size;
    }

    /**
     * <p>Setter for the field <code>size</code>.</p>
     *
     * @param size a long
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * <p>Getter for the field <code>current</code>.</p>
     *
     * @return a long
     */
    public long getCurrent() {
        return current;
    }

    /**
     * <p>Setter for the field <code>current</code>.</p>
     *
     * @param current a long
     */
    public void setCurrent(long current) {
        this.current = current;
    }

    /**
     * <p>Getter for the field <code>pages</code>.</p>
     *
     * @return a long
     */
    public long getPages() {
        return pages;
    }

    /**
     * <p>Setter for the field <code>pages</code>.</p>
     *
     * @param pages a long
     */
    public void setPages(long pages) {
        this.pages = pages;
    }

    /**
     * <p>Getter for the field <code>sortInfos</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<SortInfo> getSortInfos() {
        return sortInfos;
    }

    /**
     * <p>Setter for the field <code>sortInfos</code>.</p>
     *
     * @param sortInfos a {@link java.util.List} object
     */
    public void setSortInfos(List<SortInfo> sortInfos) {
        this.sortInfos = sortInfos;
    }
}
