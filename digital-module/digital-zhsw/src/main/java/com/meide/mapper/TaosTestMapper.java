package com.meide.mapper;


import com.meide.model.PO.T;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface TaosTestMapper {
    List<T> testList();
}
