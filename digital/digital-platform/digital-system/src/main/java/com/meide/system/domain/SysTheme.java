package com.meide.system.domain;

import com.meide.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * 主题(SysTheme)实体类
 *
 * @author jiay
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "主题实体类")
public class SysTheme extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @ApiModelProperty(value = "主键",required = true)
    @Column(name = "theme_key")
    private String key;
    @ApiModelProperty(value = "主题名称",required = true)
    private String name;
    @ApiModelProperty(value = "主题变量",required = true)
    private String vars;
    @ApiModelProperty("备注说明")
    private String remark;
    @ApiModelProperty(value = "状态 sys_normal_disable",required = true)
    private String status;
    @Transient
    private String statusLabel;
}