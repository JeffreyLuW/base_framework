package com.meide.system.service;

import com.meide.system.domain.entity.SysFilelink;
import com.meide.system.domain.param.sysFilelink.SysFileLinkAddParam;
import com.meide.system.domain.param.sysFilelink.SysFilelinkEditParam;

import java.util.List;

/**
 * 业务——文件关联表(SysFilelink)表服务接口
 *
 * @author jiay
 */
public interface ISysFilelinkService {

    /**
     * 修改文件组数据
     * @param param 参数
     */
    void edit(SysFilelinkEditParam param);

    /**
     * 查询文件组下文件uri
     * @param fileGroup 文件组名
     * @return 文件组包含文件
     */
    List<SysFilelink> selectUriByGroup(String fileGroup);

    /**
     * 上传文件
     * @param fileLink
     */
    void upload(SysFilelink fileLink);

    /**
     * 建立关联关系
     * @param param
     */
    String add(SysFileLinkAddParam param);

    /**
     * 定时删除问阿金
     */
    void dealFile();

    /**
     * 查询单个文件信息
     * @param id
     * @return
     */
    SysFilelink selectFileInfoById(String id);

    /**
     * 根据表名和待更改的表的id，更改对应的文件
     * @param ids 表的主键
     * @param table 表名
     */
    void updateFilesSignCount(String[] ids, String table, String fileName);

    /**
     * 根据分组id，更改对应的文件
     * @param groupIds 关联文件的id
     */
    void updateFilesSignCountByGroupIds(List<String> groupIds);
}
