package com.meide.service;

import com.meide.common.DataSourceContextHolder;
import com.meide.mapper.TaosTestMapper;
import com.meide.model.PO.T;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaosTestService {


    final TaosTestMapper taosTestMapper;

    public Object test(){
        DataSourceContextHolder.setDB(DataSourceContextHolder.TAOSDB);
        List<T> ts = taosTestMapper.testList();
        return ts;
    }

}
