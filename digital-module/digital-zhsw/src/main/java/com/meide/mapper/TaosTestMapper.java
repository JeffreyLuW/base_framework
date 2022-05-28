package com.meide.mapper;


import com.meide.common.base.MyBaseMapper;
import com.meide.model.PO.T;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaosTestMapper extends MyBaseMapper<T> {
    List<T> testList();
}
