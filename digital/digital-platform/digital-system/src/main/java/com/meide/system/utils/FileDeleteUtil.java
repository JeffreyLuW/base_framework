package com.meide.system.utils;

import com.meide.system.service.ISysFilelinkService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author jiay
 */
@Component
public class FileDeleteUtil {

    @Resource
    private ISysFilelinkService iSysFilelinkService;

    private static FileDeleteUtil fileDeleteUtil;

    @PostConstruct
    public void init(){
        fileDeleteUtil = this;
        fileDeleteUtil.iSysFilelinkService = this.iSysFilelinkService;
    }

    /**
     * 根据表名和待更改的表的id，更改对应的文件的引用标记，以备删除
     * @param ids 主键id
     * @param table 表名
     * @Param filename 对应的文件字段名
     */
    public static void updateFilesSignCount(String[] ids, String table,String fileName) {
        fileDeleteUtil.iSysFilelinkService.updateFilesSignCount(ids,table,fileName);
    }

    /**
     * 根据分组id，更改对应的文件，以备删除
     * @param groupIds
     */
    public static void updateFilesSignCount(List<String> groupIds) {
        fileDeleteUtil.iSysFilelinkService.updateFilesSignCountByGroupIds(groupIds);
    }
}
