package com.meide.versioncontrol.mapper;

import com.meide.versioncontrol.domain.entity.AppVersionInfoEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * app版本控制
 */
@Repository
public interface AppVersionInfoMapper {

    /**
     * 增加版本信息
     *
     * @param entity
     * @return
     */
    int insertVersion(@Param("entity") AppVersionInfoEntity entity);

    /**
     * 更新版本信息
     *
     * @param entity
     * @return
     */
    int updateVersion(@Param("entity") AppVersionInfoEntity entity);

    /**
     * 删除版本信息
     *
     * @param ids
     * @return
     */
    int deleteVersion(@Param("list") List<Long> ids);

    /**
     * 查询版本信息列表
     *
     * @param entity
     * @return
     */
    List<AppVersionInfoEntity> selectVersionList(@Param("entity") AppVersionInfoEntity entity);

    /**
     * 根据主键查询版本信息
     *
     * @param versionId
     * @return
     */
    AppVersionInfoEntity selectVersionById(Long versionId);
}
