package com.meide.utils.valid.rules;

/**
 * 验证输入为 数字字母下划线
 */
public class IsNormalWord extends MatchRegex {


    public IsNormalWord() {
        this("只能是数字字母下划线");
    }

    public IsNormalWord(String message) {
        super("^[0-9a-zA-Z_]+$", message);
    }

}