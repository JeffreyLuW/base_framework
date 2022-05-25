package com.meide.web.controller.common;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.alibaba.fastjson.JSONObject;
import com.meide.common.config.DigitalConfig;
import com.meide.common.constant.Constants;
import com.meide.common.constant.HttpStatus;
import com.meide.common.core.domain.AjaxResult;
import com.meide.common.exception.CustomException;
import com.meide.common.utils.StringUtils;
import com.meide.common.utils.file.FileUploadUtils;
import com.meide.common.utils.file.FileUtils;
import com.meide.dbengine.utils.SwaggerUtil;
import com.meide.framework.config.ServerConfig;
import com.meide.system.domain.result.FileUploadResult;
import com.meide.web.domain.dto.ScanLoginDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.Swagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 通用请求处理
 *
 * @author jiay
 */
@RestController
@Api(tags = "通用接口控制器")
public class CommonController {
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private ServerConfig serverConfig;


    @Autowired
    private ApplicationContext applicationContext;


    /**
     * 通用下载请求
     *
     * @param fileName 文件名称
     * @param delete   是否删除
     */
    @GetMapping("common/download")
    @ApiOperation(value = "通用下载",notes = "不需要认证")
    public void fileDownload(
            @ApiParam(value = "文件名",required = true) String fileName, @ApiParam(value = "下载后是否删除文件",required = true) Boolean delete,
            @ApiIgnore HttpServletResponse response, @ApiIgnore HttpServletRequest request) {
        try {
            if (!FileUtils.isValidFilename(fileName)) {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            String filePath = DigitalConfig.getDownloadPath() + fileName;
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition",
                    "attachment;fileName=" + FileUtils.setFileDownloadHeader(request, fileName));
            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (delete) {
                FileUtils.deleteFile(filePath);
            }
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 通用上传请求
     */
    @PostMapping("/common/upload")
    @ApiOperation("通用上传")
    public FileUploadResult uploadFile(@ApiParam(value = "文件流",required = true) MultipartFile file) throws Exception {
        try {
            // 上传文件路径
            String filePath = DigitalConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            return FileUploadResult.builder().code(HttpStatus.SUCCESS).msg("操作成功").fileName(fileName).url(url).build();
        } catch (Exception e) {
            return FileUploadResult.builder().code(HttpStatus.ERROR).msg(e.getMessage()).build();
        }
    }

    /**
     * 本地资源通用下载
     */
    @GetMapping("/common/download/resource")
    @ApiOperation(value = "本地资源下载",notes = "不需要认证")
    public void resourceDownload(@ApiParam(value = "文件名") @RequestParam String name, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 本地资源路径
        String localPath = DigitalConfig.getAbsoluteProfilePath();
        // 数据库资源地址
        String downloadPath = localPath + StringUtils.substringAfter(name, Constants.RESOURCE_PREFIX);
        // 下载名称
        String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
        if (downloadName.contains("__")) {
            downloadName = downloadName.substring(downloadName.indexOf("__") + 2);
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition",
                "attachment;fileName=" + FileUtils.setFileDownloadHeader(request, downloadName));
        FileUtils.writeBytes(downloadPath, response.getOutputStream());
    }

    /**
     * 自定义的获取swagger文档数据。可以添加额外接口数据
     *
     * @param swaggerGroup
     * @param servletRequest
     * @return
     * @throws Exception
     */
    @RequestMapping("/swagger/v2/api-docs/ext")
    public Object swaggerExt(@RequestParam(value = "group", required = false) String swaggerGroup, HttpServletRequest servletRequest) throws Exception {
        Swagger swagger = SwaggerUtil.getSwagger(applicationContext, swaggerGroup, servletRequest);


//        DataApiSwagger.regist(swagger);

        return swagger;
    }

    @ApiOperation("sm2加密")
    @PostMapping("/sm2/encryptBcd")
    public AjaxResult<String> encryptBcd(@RequestBody Map<String, String> map) {
        if (null != map) {
            String str = map.get("str");
            if (StringUtils.isEmpty(str)) {
                throw new CustomException("参数不正确！");
            }
            ScanLoginDTO scanLoginDTO = JSONObject.parseObject(str, ScanLoginDTO.class);
            String clientId = scanLoginDTO.getClientId();
            String timestamp = String.valueOf(scanLoginDTO.getTimestamp());
            //采用拆分timestamp前7位后6位再拼接clientId进行混淆
            String encryptStr = timestamp.substring(0, 7) + clientId + timestamp.substring(timestamp.length() - 6);
            /*SM2 sm2 = SmUtil.sm2(Constants.PRIVATE_KEY, Constants.PUBLIC_KEY);
            // 公钥加密，私钥解密
            String encryptStr = sm2.encryptBcd(str, KeyType.PublicKey);*/
            return AjaxResult.success(encryptStr);
        } else {
            throw new CustomException("参数不能为空！");
        }

    }

    @PostMapping("/sm2/decryptBcd")
    public AjaxResult<String> decryptBcd(String encryptStr) {
        SM2 sm2 = SmUtil.sm2(Constants.PRIVATE_KEY, Constants.PUBLIC_KEY);
        String decryptStr = StrUtil.utf8Str(sm2.decryptFromBcd(encryptStr, KeyType.PrivateKey));
        return AjaxResult.success(decryptStr);
    }

}
