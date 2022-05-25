package com.meide.framework.web.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.meide.common.core.domain.entity.SysUser;
import com.meide.common.core.domain.model.LoginUser;
import com.meide.common.enums.UserStatus;
import com.meide.common.exception.BaseException;
import com.meide.common.utils.PwdConfigUtil;
import com.meide.common.utils.StringUtils;
import com.meide.system.service.ISysConfigService;
import com.meide.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 用户验证处理
 *
 * @author jiay
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private ISysConfigService configService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.selectUserByUserName(username);
        if (StringUtils.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new UsernameNotFoundException("登录用户：" + username + " 不存在");
        } else if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            log.info("登录用户：{} 已被删除.", username);
            throw new BaseException("对不起，您的账号：" + username + " 已被删除");
        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new BaseException("对不起，您的账号：" + username + " 已停用");
        }
        //校验是否强制修改密码 ，配置格式 e.g: type=wordCase|num&len=6-18&expire=365
        String configStr = configService.selectConfigByKey("pwd.security.rules");
        if (StrUtil.isNotBlank(configStr)) {
            //取到强制修改密码的天数配置
            Integer expire = PwdConfigUtil.init(configStr).getExpire();
            if (expire != null && expire > 0) {
                Date now = new Date();
                Date pwdUpdateTime = user.getPwdUpdateTime();
                long between = DateUtil.between(pwdUpdateTime, now, DateUnit.DAY);
                if (between >= expire) {
                    throw new BaseException(StrUtil.format("对不起，您的账号：{} ,已经{}天未修改密码，为了您的账号安全，请修改密码后再次登录", username, between));
                }
            }

        }
        return createLoginUser(user);
    }

    public UserDetails createLoginUser(SysUser user) {
        return new LoginUser(user, permissionService.getMenuPermission(user));
    }
}
