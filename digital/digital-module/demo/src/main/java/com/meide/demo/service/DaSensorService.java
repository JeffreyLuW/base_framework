package com.meide.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meide.demo.domain.PO.DaSensor;
import com.meide.demo.mapper.DaSensorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DaSensorService {

    final DaSensorMapper mapper;

    public List<String> queryAllId(){
        List<String> res = mapper.queryAllId();
        return res;
    }

    public List<DaSensor> queryAll(){
        LambdaQueryWrapper<DaSensor> wrapper = new LambdaQueryWrapper<>();
        List<DaSensor> list = mapper.selectList(wrapper);
        return list;
    }


    public void insertOne(DaSensor sen){
        try{
            int insert = mapper.insert(sen);
        }catch(Exception e){
            e.printStackTrace();
            sen.setSensorId(sen.getSensorId()+1);
            insertOne(sen);
        }
    }


}
