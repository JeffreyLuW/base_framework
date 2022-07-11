package com.meide.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meide.demo.domain.PO.GroupDeviceBind;
import com.meide.demo.mapper.GroupDeviceBindMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupDeviceBindService {

    final GroupDeviceBindMapper mapper;

    public void editGroup(int groupId, List<Integer> deviceList, String deviceType){
        // todo 允许设备没有group么? 一个设备可以归属于多个group么?(no)

        // 遍历删除关联关系并组装出GroupDeviceBindList
        List<GroupDeviceBind> collect = deviceList.stream().map(id -> {

            LambdaQueryWrapper<GroupDeviceBind> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(GroupDeviceBind::getDeviceType, deviceType)
                    .eq(GroupDeviceBind::getDeviceId, id)
                    .eq(GroupDeviceBind::getGroupId, groupId);
            mapper.delete(wrapper);


            return GroupDeviceBind.builder().deviceId(id).groupId(groupId).deviceType(deviceType).build();
        }).collect(Collectors.toList());


        mapper.insertBatchSomeColumn(collect);

    }

}
