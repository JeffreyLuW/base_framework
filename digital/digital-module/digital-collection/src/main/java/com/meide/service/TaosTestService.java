package com.meide.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meide.common.DataSourceContextHolder;
import com.meide.mapper.TaosTestMapper;
import com.meide.model.PO.T;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaosTestService {


    final TaosTestMapper taosTestMapper;

    public Object test(){
        DataSourceContextHolder.setDB(DataSourceContextHolder.TAOSDB);
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<T>();

        List<T> ts = taosTestMapper.selectList(wrapper);
        return ts;
    }

}
