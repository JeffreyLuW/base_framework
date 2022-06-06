package com.meide.utils.valid.rules;

/**
 * 验证输入为手机号
 */
public class IsPhone extends MatchRegex {


    public IsPhone() {
        this("请输入有效的手机号码");
    }

    public IsPhone(String message) {
        super("^1[3|4|5|7|8][0-9]{9}$", message);
    }

}