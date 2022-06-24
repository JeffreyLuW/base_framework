package com.meide.framework.web.service;

import cn.hutool.core.collection.CollUtil;
import com.meide.common.constant.Constants;
import com.meide.common.constant.HttpStatus;
import com.meide.common.core.cashe.CacheUtil;
import com.meide.common.core.domain.entity.SysRole;
import com.meide.common.core.domain.model.LoginUser;
import com.meide.common.enums.CacheType;
import com.meide.common.exception.CustomException;
import com.meide.common.exception.user.CaptchaException;
import com.meide.common.exception.user.CaptchaExpireException;
import com.meide.common.exception.user.UserPasswordNotMatchException;
import com.meide.common.utils.MessageUtils;
import com.meide.framework.manager.AsyncManager;
import com.meide.framework.manager.factory.AsyncFactory;
import com.meide.system.domain.result.LoginResult;
import com.meide.system.mapper.SysRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 登录校验方法
 *
 * @author jiay
 */
@Component
public class SysLoginService {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysRoleMapper roleMapper;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private CacheUtil cacheUtil;

    @Value("${digital.captcha.enable}")
    private Boolean captchaEnable;

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public LoginResult login(String username, String password, String code, String uuid) {
        //开发环境跳过验证码检验
        captchaCheck(username, code, uuid, true);
        // 用户验证
        Authentication authentication = null;
        try {
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new CustomException(e.getMessage());
            }
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 生成token
        Boolean bigscreen = false;
        String key = "bigscreen";
        List<SysRole> roleList = roleMapper.selectRolePermissionByUserId(loginUser.getUser().getUserId());
        if (CollUtil.isNotEmpty(roleList)) {
            for (SysRole role : roleList) {
                if (null != role) {
                    if (key.equals(role.getRoleKey())) {
                        bigscreen = true;
                        break;
                    }
                }
            }
        }
        return LoginResult.builder().code(HttpStatus.SUCCESS).msg("操作成功")
                .token(tokenService.createToken(loginUser))
                .bigscreen(bigscreen)
                .build();

    }

    /**
     * 校验图形验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     验证码唯一id
     * @param login    是否是登录过程，会记录登录失败的日志
     */
    public void captchaCheck(String username, String code, String uuid, boolean login) {
        if (captchaEnable) {
            String captcha = cacheUtil.getCacheObject(CacheType.PICTURE_CAPTCHA_CODE, uuid);
            cacheUtil.deleteObject(CacheType.PICTURE_CAPTCHA_CODE, uuid);
            if (captcha == null) {
                if (login) {
                    AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
                }
                throw new CaptchaExpireException();
            }
            if (!code.equalsIgnoreCase(captcha)) {
                if (login) {
                    AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
                }
                throw new CaptchaException();
            }
        }
    }
}
