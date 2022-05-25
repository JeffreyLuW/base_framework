package com.meide.web.controller.system;

import com.meide.common.core.domain.AjaxResult;
import com.meide.common.core.domain.model.LoginUser;
import com.meide.framework.web.service.TokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

@Api(tags = "1开发环境接口")
@RestController
@RequestMapping("/dev/")
@ConditionalOnProperty(value = "spring.profiles.active", havingValue = "dev")
@Validated
public class DevController {
    @Resource
    private UserDetailsService userDetailsService;
    @Autowired
    private TokenService tokenService;

    @ApiOperation(value = "获取token", notes = "根据用户名获取token，不需要登录，仅在dev环境下使用")
    @GetMapping("getToken")

    public AjaxResult<String> getToken(
            @NotBlank(message = "username不能为空") @RequestParam(defaultValue = "admin") String username
    ) {
        LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(username);
        String token = tokenService.createToken(loginUser);
        return AjaxResult.success("Bearer " + token);
    }
}
