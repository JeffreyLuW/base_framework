package com.meide.demo.service;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meide.demo.domain.PO.DeviceGroup;
import com.meide.demo.domain.VO.DeviceGroupVo;
import com.meide.demo.mapper.DeviceGroupMapper;
import com.meide.demo.utils.DozerUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceGroupService {
    

    final DeviceGroupMapper mapper;

    /**
     * 增加device组（根/子）
     * */
    public int createGroup(DeviceGroup group){
        // 根据pid查询同级的目录数量
        LambdaQueryWrapper<DeviceGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceGroup::getGroupPid, Optional.ofNullable(group.getGroupPid()).orElse(0))
                .orderByDesc(DeviceGroup::getGroupSort);

        List<DeviceGroup> queryGroups = mapper.selectList(wrapper);
        if(CollectionUtils.isEmpty(queryGroups)){
            group.setGroupPid(0);
            group.setGroupSort(1);
            return mapper.insert(group);
        }else{
            DeviceGroup queryGroup = queryGroups.get(0);
            // 防止sort为零或重复
            group.setGroupPid(queryGroup.getGroupPid());
            group.setGroupSort(queryGroup.getGroupSort() + 1);
            return mapper.insert(group);
        }
    }

    /**
     * 删除device组
     * */
    public int dropGroupAndDevice(int groupId){
        return mapper.deleteById(groupId);

        // todo 要不要删除子级组以及组内的设备
    }

    /**
     * 查询device组列表
     * */
    public List<DeviceGroup> queryList(String type){
        LambdaQueryWrapper<DeviceGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceGroup::getGroupType, type);
        List<DeviceGroup> list = mapper.selectList(wrapper);
        return list;
    }

    /**
     * 查询device组树形列表
     * */
    public List<DeviceGroupVo> queryTreeList(String type){
        List<DeviceGroup> list = queryList(type);
        List<DeviceGroupVo> vos = DozerUtil.mapList(list, DeviceGroupVo.class);
        return restructureMenuTree(vos);
    }

    /**
     * 编辑组
     * */
    public int editById(DeviceGroup group){
        return mapper.updateById(group);
    }




    /*
     * 组装tree
     * */
    public List<DeviceGroupVo> restructureMenuTree(List<DeviceGroupVo> vos) {
        ArrayList<DeviceGroupVo> resList = new ArrayList<>();
        for(DeviceGroupVo vo: vos) {
            if(vo.getGroupPid() == 0)
                resList.add(renderTree(vo, vos));
        }
        // resList.sort((o1, o2) -> o1.getSort()- o2.getSort());
        resList.sort(Comparator.comparingInt(DeviceGroupVo::getGroupSort));
        return resList;
    }

    /*
     * 返回只有一个父节点的tree
     * */
    DeviceGroupVo renderTree(DeviceGroupVo parent, List<DeviceGroupVo> vos){
        ArrayList<DeviceGroupVo> resList = new ArrayList<>();
        vos.forEach(vo -> {
            if(parent.getGroupId().equals(vo.getGroupPid())){
                resList.add(vo);
                if(ObjectUtils.isEmpty(vo.getChildren()))
                    vo.setChildren(new ArrayList<>());
                vo.getChildren().add(renderTree(vo, vos));
            }
        });
        parent.setChildren(resList);
        return parent;
    }
    
    
    
}
