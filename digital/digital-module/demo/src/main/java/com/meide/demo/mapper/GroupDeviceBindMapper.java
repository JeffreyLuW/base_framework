package com.meide.demo.mapper;

import com.meide.demo.domain.PO.GroupDeviceBind;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GroupDeviceBindMapper extends MyBaseMapper<GroupDeviceBind> {

    void insertBatch(List<GroupDeviceBind> list);

}
