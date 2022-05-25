package com.meide.register.service.impl;

import com.meide.register.domain.DataApiQueryBean;
import com.meide.register.domain.SysTable;
import com.meide.register.domain.SysTableColumn;
import com.meide.register.mapper.DataApiMapper;
import com.meide.register.service.IDataApiService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;

/**
 * 数据库表名 业务层处理
 *
 * @author jiay
 */

public class DataApiServiceImpl implements IDataApiService {

    @Autowired
    private DataApiMapper dataApiMapper;

    /**
     * 根据数据库类型 查询数据库所有表名
     *
     * @param ignoreTableName 屏蔽表名
     * @return 数据库表名
     */
    @Override
    public List<SysTable> selectTableList(String ignoreTableName) {
        List<SysTable> sysTables = dataApiMapper.selectTableList(ignoreTableName);
        return sysTables;

    }

    @Override
    public List<SysTableColumn> selectColumnListByTableName(String tableName) {

        List<SysTableColumn> column = dataApiMapper.selectColumnListByTableName(tableName);

        return column;
    }

    /**
     * 根据指定列名 精确查询数据
     *
     * @param dataApiQueryBean 数据操作参数
     * @return 数据库表查询结果
     */
    @Override
    public List<HashMap> selectByColumn(DataApiQueryBean dataApiQueryBean) {
        List<HashMap>  listByColumn = dataApiMapper.selectByColumn(dataApiQueryBean );
        return listByColumn;
    }
}
