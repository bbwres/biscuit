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
