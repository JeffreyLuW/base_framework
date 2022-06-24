package com.meide.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.meide.demo.mapper.IDimArea;
import com.meide.demo.domain.DimArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jiay
 * @date 2022/6/15
 */
@Service
public class DimService {
    @Autowired
    private IDimArea dimAreaMapper;

    public void query(){
        dimAreaMapper.selectCount(new QueryWrapper<DimArea>().eq("id",1));
    }
}
