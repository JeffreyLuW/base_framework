package com.meide.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meide.demo.domain.PO.DaEquipment;
import com.meide.demo.mapper.DaEquipmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DaEquipmentService {

    final DaEquipmentMapper mapper;

    public List<DaEquipment> queryAll(){
        LambdaQueryWrapper<DaEquipment> wrapper = new LambdaQueryWrapper<>();
        List<DaEquipment> list = mapper.selectList(wrapper);
        return list;
    }

}
