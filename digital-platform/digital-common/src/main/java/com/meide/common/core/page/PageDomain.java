package com.meide.common.core.page;

import com.meide.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 分页数据
 *
 * @author jiay
 */
@ApiModel(description = "分页数据")
@Data
public class PageDomain {
    /**
     * 当前记录起始索引
     */
    @ApiModelProperty("页数")
    private Integer pageNum = 1;

    /**
     * 每页显示记录数
     */
    @ApiModelProperty("每页数据数量")
    private Integer pageSize = 10;

    /**
     * 排序列
     */
    @ApiModelProperty("排序的列名")
    private String orderByColumn;

    /**
     * 排序的方向desc或者asc
     */
    @ApiModelProperty("排序的方向")
    private String isAsc = "asc";

    @ApiIgnore
    @ApiModelProperty(hidden = true)
    public String getOrderBy() {
        if (StringUtils.isEmpty(orderByColumn)) {
            return "";
        }
        return StringUtils.toUnderScoreCase(orderByColumn) + " " + isAsc;
    }
}
