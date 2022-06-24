package com.meide.register.mapper;

import com.meide.register.domain.DataApiQueryBean;
import com.meide.register.domain.SysTable;
import com.meide.register.domain.SysTableColumn;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * 读取数据库表元素
 *
 * @author jiay
 */
public interface DataApiMapper
{

    /**
     * 根据数据库类型 查询数据库所有表名
     *
     * @param ignoreTableName 屏蔽表名
     * @return 数据库表名
     */
    public List<SysTable> selectTableList(@Param("ignoreTableName") String ignoreTableName);

    /**
     * 根据指定列名 精确查询数据
     *
     * @param dataApiQueryBean 数据操作参数
     * @return 数据库表查询结果
     */
    List<HashMap> selectByColumn(DataApiQueryBean dataApiQueryBean);

    List<SysTableColumn> selectColumnListByTableName(String tableName);
}
