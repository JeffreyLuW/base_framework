package com.meide.register.service;

import com.meide.register.domain.DataApiQueryBean;

import java.util.HashMap;
import java.util.List;

public interface BusTableService {

    List<HashMap> selectApi(DataApiQueryBean map);

    int deleteApi(HashMap dataApiBean);

    int updateApi(HashMap params);

    int updateMaxApi(HashMap params);

    int insertApi(HashMap params);

    HashMap selectOneApi(HashMap params);
}
