package com.meide.web.controller.system;

import cn.hutool.core.util.StrUtil;
import com.meide.common.core.domain.AjaxResult;
import com.meide.common.core.domain.MapResult;
import com.meide.common.core.domain.entity.SysMenu;
import com.meide.common.core.domain.entity.SysUser;
import com.meide.common.core.domain.model.LoginBody;
import com.meide.common.core.domain.model.LoginUser;
import com.meide.common.utils.PwdConfigUtil;
import com.meide.common.utils.SecurityUtils;
import com.meide.common.utils.ServletUtils;
import com.meide.framework.config.ServerConfig;
import com.meide.framework.web.service.SysLoginService;
import com.meide.framework.web.service.SysPermissionService;
import com.meide.framework.web.service.TokenService;
import com.meide.system.domain.SysConfig;
import com.meide.system.domain.result.LoginResult;
import com.meide.system.domain.vo.RouterVo;
import com.meide.system.domain.vo.ThemeVo;
import com.meide.system.domain.vo.UpdatePwdVo;
import com.meide.system.service.ISysConfigService;
import com.meide.system.service.ISysMenuService;
import com.meide.system.service.ISysThemeService;
import com.meide.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 登录验证
 *
 * @author jiay
 */
@RestController
@Api(tags = "登录验证控制器")
public class SysLoginController {
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysThemeService sysThemeService;


    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    @ApiOperation("登录")
    public LoginResult login(@RequestBody LoginBody loginBody) {
        // 生成令牌
        return loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
    }

    @PutMapping("/resetPwd")
    @ApiOperation("免登录修改密码")
    public AjaxResult<Object> resetPwd(@Validated @RequestBody UpdatePwdVo vo) {
        String userName = vo.getUsername();
        SysUser sysUser = userService.selectUserByUserName(userName);
        String password = sysUser.getPassword();
        String newPassword = vo.getNewPassword();
        String oldPassword = vo.getOldPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password)) {
            return AjaxResult.errorWithMsg("修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, password)) {
            return AjaxResult.errorWithMsg("新密码不能与旧密码相同");
        }
        //验证码校验
        loginService.captchaCheck(userName,vo.getCode(),vo.getUuid(),false);

        //校验密码规则
        String configStr = configService.selectConfigByKey("pwd.security.rules");
        String matcher = PwdConfigUtil.init(configStr).matcher(newPassword);
        if (StrUtil.isNotBlank(matcher)) {
            return AjaxResult.errorWithMsg(matcher);
        }

        if (userService.resetUserPwd(userName, SecurityUtils.encryptPassword(newPassword)) > 0) {
            return AjaxResult.success();
        }
        return AjaxResult.errorWithMsg("修改密码异常，请联系管理员");

    }


    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public MapResult getInfo() {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        Long parentId = user.getDept().getParentId();
        Long deptId = user.getDeptId();
        Integer type = userService.getDeptType(deptId);
        String s = type.toString() + parentId;
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        MapResult ajax = MapResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        ajax.put("serverUrl", serverConfig.getUrl());
        ajax.put("deptName",s);
        return ajax;
    }

    /**
     * 程序通用信息配置
     *
     * @return
     */
    @ApiOperation("获取通用配置")
    @GetMapping("prepareConfig")
    public MapResult prepareConfig() {
        MapResult ajax = MapResult.success();
        ajax.put("serverUrl", serverConfig.getUrl());
        List<SysConfig> sysConfigs = new ArrayList<>();
        List<SysConfig> pageConfigList = configService.selectConfigByLeftLikeKey("page.");
        List<SysConfig> pwdConfigList = configService.selectConfigByLeftLikeKey("pwd.");
        List<ThemeVo> sysThemes = sysThemeService.prepareConfig();
        if (null != pageConfigList) {
            sysConfigs.addAll(pageConfigList);
        }
        if (null != pwdConfigList) {
            sysConfigs.addAll(pwdConfigList);
        }
        ajax.put("sysConfigs", sysConfigs);
        ajax.put("sysThemes",sysThemes);
        return ajax;
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult<List<RouterVo>> getRouters() {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        // 用户信息
        SysUser user = loginUser.getUser();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(user.getUserId());
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
