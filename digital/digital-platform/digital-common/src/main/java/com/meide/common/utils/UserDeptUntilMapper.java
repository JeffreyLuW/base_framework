package com.meide.common.utils;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jiay
 */
@Repository
@Mapper
public interface UserDeptUntilMapper {

    /**
     * 查询所有部门编码
     * @return
     */
   List<String> selectAllDept();

    /**
     * 查询管理站下面的单位
     * @param deptId
     * @return
     */
    List<String> selectManDept(Long deptId);

    /**
     * 查询所属单位
     * @param deptId
     * @return
     */
    List<String> selectDept(Long deptId);

    /**
     * 获取上级id
     * @param id
     * @return
     */
    Long getParentId(Long id);
}
