package com.meide.utils.valid.rules;

/**
 * 验证输入为汉字。
 */
public class IsChinese extends MatchRegex {


    public IsChinese() {
        this("只能输入汉字");
    }

    public IsChinese(String message) {
        super("^[\\u0391-\\uFFE5]+$", message);
    }

}