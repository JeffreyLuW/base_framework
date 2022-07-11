package com.meide.demo.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meide.demo.domain.PO.CanvasSensorBind;
import com.meide.demo.mapper.CanvasSensorBindMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CanvasSensorBindService {

    final CanvasSensorBindMapper mapper;

    public List<CanvasSensorBind> queryAll(){
        LambdaQueryWrapper<CanvasSensorBind> wrapper = new LambdaQueryWrapper<>();
        List<CanvasSensorBind> binds = mapper.selectList(wrapper);
        return binds;
    }




}
