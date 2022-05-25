package com.meide.system.domain.param.sysFilelink;

import com.meide.common.core.page.PageDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 业务——文件关联表(SysFilelink)分页查询条件
 *
 * @author jiay
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "业务——文件关联表分页条件查询类")
public class SysFilelinkPageParam extends PageDomain {

    @ApiModelProperty(value = "业务名/文件组名")
    private String group;
}
