package com.meide.system.mapper;

import com.meide.common.core.domain.entity.SysArea;
import com.meide.common.core.domain.param.SysAreaParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jiay
 */
@Repository
public interface SysAreaMapper {

    List<SysArea> selectAreaList(SysAreaParam area);

    /**
     * 根据id和级别查询信息
     * @param id
     * @param level
     * @return
     */
    SysArea selectOneById(@Param("id") String id, @Param("level") int level);
}
