package com.meide.web.controller.common;

import com.meide.common.constant.HttpStatus;
import com.meide.common.core.cashe.CacheUtil;
import com.meide.system.domain.result.CaptchaResult;
import com.meide.common.enums.CacheType;
import com.meide.common.utils.sign.Base64;
import com.meide.common.utils.uuid.IdUtils;
import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 验证码操作处理
 *
 * @author jiay
 */
@RestController
@Api(tags = "验证码控制器")
public class CaptchaController
{
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    private CacheUtil cacheUtil;

    // 验证码类型
    @Value("${digital.captcha.type}")
    private String captchaType;

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    @ApiOperation(value = "生成验证码")
    public CaptchaResult getCode(HttpServletResponse response) throws IOException
    {
        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();

        String capStr = null, code = null;
        BufferedImage image = null;

        // 生成验证码
        if ("math".equals(captchaType))
        {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        }
        else if ("char".equals(captchaType))
        {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }

        //缓存验证码
        cacheUtil.setCacheObject(CacheType.PICTURE_CAPTCHA_CODE,uuid,code);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try
        {
            ImageIO.write(image, "jpg", os);
        }
        catch (IOException e)
        {
            return CaptchaResult.builder().code(HttpStatus.ERROR).msg(e.getMessage()).build();
        }

        return CaptchaResult.builder().code(HttpStatus.SUCCESS).msg("操作成功").uuid(uuid).img(Base64.encode(os.toByteArray())).build();
    }
}
