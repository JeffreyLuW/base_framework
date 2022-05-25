package com.meide.system.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 消息通知所选部门
 * @author jiay
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SysNoticeDept {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 通知id
     */
    private Long noticeId;

    /**
     * 部门id
     */
    private Long deptId;
}