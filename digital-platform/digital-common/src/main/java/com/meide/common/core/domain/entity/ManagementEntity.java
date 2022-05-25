package com.meide.common.core.domain.entity;

import lombok.*;

import java.math.BigDecimal;

/**
 * 功能描述 管理机构详细信息表-Entity
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ManagementEntity {
	private static final long serialVersionUID = 1L;

	private Long id;
	/**
	 * 所属名称
	 */
	private String igationName;
	/**
	 * 所属编码
	 */
	private String igationCode;
	/**
	 * 机构编码
	 */
	private String MOrganCode;
	/**
	 * 机构名称
	 */
	private String organName;
	/**
	 * 拼音
	 */
	private String organPy;
	/**
	 * 详细位置
	 */
	private String detailLocation;
	/**
	 * 男职工人数（单位 人）
	 */
	private Integer maleEmployeeNumber;
	/**
	 * 女职工人数（单位 人）
	 */
	private Integer femaleEmployeeNumber;
	/**
	 * 高工人数（单位 人）
	 */
	private Integer seniorEngineer;
	/**
	 * 工程师人数（单位 人）
	 */
	private Integer engineerNumber;
	/**
	 * 大专以上人数（单位 人）
	 */
	private Integer upperMajor;
	/**
	 * 职工月平均工资（单位 元）
	 */
	private BigDecimal averagePay;
	/**
	 * 上级机构名称
	 */
	private String upperOrganName;
	/**
	 * 图片地址
	 */
	private String photoAddress;

    /**
     * 机构介绍
     */
    private String organIntroduce;

}
