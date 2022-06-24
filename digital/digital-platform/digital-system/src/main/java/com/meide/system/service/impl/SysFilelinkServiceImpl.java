package com.meide.system.service.impl;

import com.meide.common.utils.uuid.UUID;
import com.meide.system.domain.entity.SysFilelink;
import com.meide.system.domain.param.sysFilelink.SysFileLinkAddParam;
import com.meide.system.domain.param.sysFilelink.SysFilelinkEditParam;
import com.meide.system.mapper.SysFilelinkMapper;
import com.meide.system.service.ISysFilelinkService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 业务——文件关联表(SysFilelink)表服务实现类
 *
 * @author jiay
 */
@Service("sysFilelinkService")
public class SysFilelinkServiceImpl implements ISysFilelinkService {
    @Resource
    private SysFilelinkMapper sysFilelinkMapper;

    @Resource
    private Environment environment;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(SysFilelinkEditParam param) {

        // 1.将所有的group关联的数据降级，将groupId置空
        sysFilelinkMapper.updateSignByGroupId(param.getGroupId());
        // 2.将ids关联的数据升级，赋值groupId
        if(0 != param.getIds().length) {
            sysFilelinkMapper.updateGroupByIds(Arrays.asList(param.getIds()),param.getGroupId());
        }
    }

    @Override
    public List<SysFilelink> selectUriByGroup(String fileGroup) {
        return sysFilelinkMapper.selectUriByGroup(fileGroup);
    }

    @Override
    public void upload(SysFilelink fileLink) {
        sysFilelinkMapper.upload(fileLink);
    }

    @Override
    public String add(SysFileLinkAddParam param) {
        String groupId = null;
        if (Optional.ofNullable(param.getIds()).isPresent()) {
            groupId = UUID.randomUUID().toString();
            sysFilelinkMapper.updateGroupByIds(Arrays.asList(param.getIds()), groupId);
        }
        return groupId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dealFile() {
        try {
            // 1.查询所有待删除的文件
            List<String> fileList = sysFilelinkMapper.selectFilePathForDelete();
            // 2.删除数据库的数据，先执行sql有错误可以回滚
            this.sysFilelinkMapper.deleteFileForNeed();
            // 3.循环删除问阿金
            for (String path : fileList) {
                this.deleteFile(path);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SysFilelink selectFileInfoById(String id) {
        return this.sysFilelinkMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateFilesSignCount(String[] ids, String table, String fileName) {
        sysFilelinkMapper.updateFilesSignCount(Arrays.asList(ids), table, fileName);
    }

    @Override
    public void updateFilesSignCountByGroupIds(List<String> groupIds) {
        sysFilelinkMapper.updateFilesSignCountByGroupIds(groupIds);
    }

    /**
     * 删除文件
     * @param pathname
     * @return
     * @throws IOException
     */
    private void deleteFile(String pathname) throws IOException {
        String fileFolder = environment.getProperty("digital.profile");
        String path = pathname.replaceFirst("profile",fileFolder);
        String dir = System.getProperty("user.dir");
        File file = new File(dir + path);
        if (file.isFile() && file.exists()) {
            file.delete();
            System.out.println(path + "文件已经被成功删除");
        }
    }

}
