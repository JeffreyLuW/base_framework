package com.meide.demo.mapper;


import com.meide.demo.domain.PO.T;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaosTestMapper extends MyBaseMapper<T> {
    List<T> testList();
}
