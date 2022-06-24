package com.meide.system.mapper;

import com.meide.system.domain.entity.SysFilelink;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 业务——文件关联表(SysFilelink)表数据库访问层
 *
 * @author jiay
 */
@Repository
public interface SysFilelinkMapper extends Mapper<SysFilelink> {

    /**
     * 批量插入数据
     * @param list 对象集合
     * @return 插入条数
     */
    int insertAllGroup(@Param("list") List<SysFilelink> list);

    /**
     * 根据文件组查询数据
     *
     * @param fileGroup 文件组名
     * @return 文件组集合
     */
    List<SysFilelink> selectUriByGroup(String fileGroup);

    /**
     * 根据主键查询信息
     *
     * @param id
     * @return
     */
    SysFilelink selectById(String id);

    /**
     * 根据分组id，对引用次数进行降级
     *
     * @param groupId
     */
    void updateByGroup(String groupId);

    /**
     * 上传文件
     * @param fileLink
     */
    void upload(SysFilelink fileLink);

    /**
     * 更新groupid根据id数组
     * @param param
     */
    void updateGroupByIds(@Param("list") List<String> list, @Param("groupId") String groupId);

    /**
     * 根据groupId更改引用数字
     * @param groupId
     */
    void updateSignByGroupId(String groupId);

    /**
     * 查询待删除的文件路径
     * @return
     */
    List<String> selectFilePathForDelete();

    /**
     * 删除该删除的文件
     */
    void deleteFileForNeed();

    /**
     * 根据分组id，更改对应的文件
     * @param asList
     */
    void updateFilesSignCountByGroupIds(@Param("list") List<String> list);

    /**
     * 根据表名和待更改的表的id，更改对应的文件
     * @param list
     * @param table
     */
    void updateFilesSignCount(@Param("list")List<String> list, @Param("table")String table, @Param("fileName") String fileName);
}
