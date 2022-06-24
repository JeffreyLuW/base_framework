package com.meide.common.enums;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

public enum CacheType {
    //图片验证码 有效期2分钟
    PICTURE_CAPTCHA_CODE("captcha_codes",2,TimeUnit.MINUTES),
    //登录用户token 有效期30分钟
    LOGIN_TOKEN("login_tokens",30,TimeUnit.MINUTES),
    //防止重复提交 有效期10秒
    REPEAT_SUBMIT("repeat_submit",10,TimeUnit.SECONDS),
    //系统配置 永久有效
    SYS_CONFIG("sys_config",-1,TimeUnit.SECONDS),
    //字典表缓存 永久有效
    SYS_DICT("sys_dict",-1,TimeUnit.SECONDS),
    //用于DictUtil的所有字典项缓存 永久有效
    SYS_DICT_DETAILS("sys_dict_details",-1,TimeUnit.SECONDS),
    //主题表缓存 永久有效
    SYS_THEME("sys_theme",-1,TimeUnit.SECONDS)
    ;

    //缓存前缀
    private String perfix;
    //失效时间数字
    private Integer num;
    //失效时间单位
    private TimeUnit unit;

    CacheType(String perfix,Integer num, TimeUnit unit) {
        this.perfix = perfix;
        this.num = num;
        this.unit = unit;
    }

    public String getPerfix() {
        return perfix;
    }

    public Integer getNum() {
        return num;
    }

    public TimeUnit getUnit() {
        return unit;
    }


}
