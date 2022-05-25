package com.meide.web.controller.system;

import com.meide.common.config.DigitalConfig;
import com.meide.common.constant.HttpStatus;
import com.meide.common.core.controller.BaseController;
import com.meide.common.core.domain.AjaxResult;
import com.meide.common.exception.file.FileSizeLimitExceededException;
import com.meide.common.utils.SecurityUtils;
import com.meide.common.utils.file.FileUploadUtils;
import com.meide.common.utils.uuid.UUID;
import com.meide.framework.config.ServerConfig;
import com.meide.system.domain.entity.SysFilelink;
import com.meide.system.domain.param.sysFilelink.SysFileLinkAddParam;
import com.meide.system.domain.param.sysFilelink.SysFilelinkEditParam;
import com.meide.system.domain.result.FileUploadResult;
import com.meide.system.service.ISysFilelinkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.meide.common.utils.file.FileUploadUtils.DEFAULT_MAX_SIZE;

/**
 * 业务——文件关联表(SysFilelink)表控制层
 *
 * @author jiay
 */
@Api(tags = "文件关联控制器")
@RestController
@RequestMapping("sysFilelink")
public class SysFilelinkController extends BaseController {

    @Autowired
    private ServerConfig serverConfig;
    @Resource
    private ISysFilelinkService sysFilelinkService;

    @PostMapping("/upload")
    @ApiOperation("上传文件")
    public FileUploadResult uploadFile(@ApiParam(value = "文件流",required = true) MultipartFile file) throws Exception {
        long size = file.getSize();
        if (DEFAULT_MAX_SIZE != -1 && size > DEFAULT_MAX_SIZE) {
            throw new FileSizeLimitExceededException(DEFAULT_MAX_SIZE / 1024 / 1024);
        }

        try {
            // 上传文件路径
            String filePath = DigitalConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            SysFilelink fileLink = SysFilelink.builder()
                    .id(UUID.randomUUID().toString())
                    .path(fileName)
                    .fileType(fileName.substring(fileName.lastIndexOf(".") + 1))
                    .fileName(fileName.substring(fileName.lastIndexOf("__") + 2))
                    //标记为1的不会删除，0的可以会被定时任务删除
                    .signCount(1)
                    .creater(String.valueOf(SecurityUtils.getLoginUser().getUser().getUserId()))
                    .createTime(new Date())
                    .build();
            sysFilelinkService.upload(fileLink);
            String url = serverConfig.getUrl() + fileName;
            return FileUploadResult.builder().code(HttpStatus.SUCCESS).msg("操作成功").fileName(fileName.substring(fileName.lastIndexOf("__") + 2)).url(url).id(fileLink.getId()).build();
        } catch (Exception e) {
            return FileUploadResult.builder().code(HttpStatus.ERROR).msg(e.getMessage()).build();
        }
    }

    @PutMapping("/add")
    @ApiOperation("文件关联表新增")
    public AjaxResult add(@Validated @RequestBody SysFileLinkAddParam param){
        return AjaxResult.success(sysFilelinkService.add(param));
    }

    @GetMapping("/group/{groupId}")
    @ApiOperation("根据文件关联id查找文件信息")
    public AjaxResult<List<SysFilelink>> info(@ApiParam(value = "文件关联id",required = true) @PathVariable String groupId) {
        return AjaxResult.success(sysFilelinkService.selectUriByGroup(groupId));
    }

    @GetMapping("/{id}")
    @ApiOperation("根据文件id查找文件信息")
    public AjaxResult<SysFilelink> get(@ApiParam(value = "主键",required = true) @PathVariable String id) {
        return AjaxResult.success(sysFilelinkService.selectFileInfoById(id));
    }

    @PutMapping("/group")
    @ApiOperation("文件关联表修改（覆盖）")
    public AjaxResult edit(@Validated @RequestBody SysFilelinkEditParam param){
        sysFilelinkService.edit(param);
        return success();
    }

}
