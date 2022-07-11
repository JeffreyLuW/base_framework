package com.meide.demo.mapper;

import com.meide.demo.domain.PO.DaSensor;

import java.util.List;

public interface DaSensorMapper extends MyBaseMapper<DaSensor> {
    public List<String> queryAllId();
}
