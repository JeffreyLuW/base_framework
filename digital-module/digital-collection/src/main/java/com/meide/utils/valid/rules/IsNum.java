package com.meide.utils.valid.rules;

/**
 * 验证输入为数字
 */
public class IsNum extends MatchRegex {


    public IsNum() {
        this("只能输入数字");
    }

    public IsNum(String message) {
        super("^[0-9]+$", message);
    }

}