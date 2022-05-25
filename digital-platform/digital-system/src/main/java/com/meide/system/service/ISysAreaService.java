package com.meide.system.service;

import com.meide.common.core.domain.TreeSelect;
import com.meide.common.core.domain.entity.SysArea;
import com.meide.common.core.domain.param.SysAreaParam;

import java.util.List;

/**
 * @author jiay
 */
public interface ISysAreaService {

    List<SysArea> selectAreaList(SysAreaParam dept);

    List<TreeSelect> buildAreaTreeSelect(List<SysArea> depts);

    /**
     * 根据省市曲线镇村的id,查询其中文名
     */
    String getNameById(String id);
}
