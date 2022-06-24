package com.meide.register.service.impl;

import com.meide.register.domain.DataApiQueryBean;
import com.meide.register.service.BusTableService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * 数据库表名 业务层处理
 *
 * @author jiay
 */
@Service
public class BusTableServiceImpl implements BusTableService {

    @Autowired
    private SqlSession session;

    @Override
    public List<HashMap> selectApi(DataApiQueryBean bean) {

        String method = "com.meide.register.mapper." + bean.getTableName() + "Mapper.selectApi";



        if (bean.getSelectType() == null) {
            bean.setSelectType("equalSign");
        }

        if (bean.getColumnName() == null) {
            method = "com.meide.register.mapper." + bean.getTableName() + "Mapper.selectOneApi";
        }
        if (bean.getColumnName() == null && bean.getColumnValue() == null) {
            method = "com.meide.register.mapper." + bean.getTableName() + "Mapper.selectAllApi";
        }

        return session.selectList(method, bean);
    }


    @Override
    public HashMap selectOneApi(HashMap params) {

        params.put("columnValue", params.get("id"));

        String method = "com.meide.register.mapper." + params.get("tableNmae") + "Mapper.selectOneApi";

        return session.selectOne(method, params);
    }


    @Override
    public int deleteApi(HashMap params) {
        String method = "com.meide.register.mapper." + params.get("tableNmae") + "Mapper.deleteApi";
        return session.delete(method, params);
    }

    @Override
    public int updateApi(HashMap params) {
        String method = "com.meide.register.mapper." + params.get("tableNmae") + "Mapper.updateApi";
        return session.update(method, params);
    }


    @Override
    public int updateMaxApi(HashMap params) {
        String method = "com.meide.register.mapper." + params.get("tableNmae") + "Mapper.updateMaxApi";
        return session.update(method, params);
    }


    @Override
    public int insertApi(HashMap params) {
        String method = "com.meide.register.mapper." + params.get("tableNmae") + "Mapper.insertApi";
        return session.update(method, params);
    }


}
