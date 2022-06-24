package com.meide.system.domain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 业务——文件关联表(SysFilelink)实体类
 *
 * @author jiay
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "业务——文件关联表实体类")
@Table(name = "sys_file")
public class SysFilelink implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @ApiModelProperty("业务——文件关联表主键")
    private String id;
    @ApiModelProperty("关联id（分组id）")
    private String groupId;
    @ApiModelProperty("文件地址")
    private String path;
    @ApiModelProperty("文件类型")
    private String fileType;
    @ApiModelProperty("文件名")
    private String fileName;
    @ApiModelProperty("引用次数 0待删除 1已删除")
    private int signCount;
    @ApiModelProperty("创建人")
    private String creater;
    @ApiModelProperty("创建时间")
    private Date createTime;
}
