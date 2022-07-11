package com.meide.demo.mapper;


import com.meide.demo.domain.PO.CollectionData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaosMapper extends MyBaseMapper<CollectionData> {

    public int insertDataBatch(List<CollectionData> list);

    public int insertDataBatchInterval5min(List<CollectionData> list);

}
