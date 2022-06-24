package com.meide.utils.valid.rules;

/**
 * 验证输入邮箱
 */
public class IsEmail extends MatchRegex {


    public IsEmail() {
        this("非法邮箱");
    }

    public IsEmail(String message) {
        super("^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,3}$", message);
    }

}