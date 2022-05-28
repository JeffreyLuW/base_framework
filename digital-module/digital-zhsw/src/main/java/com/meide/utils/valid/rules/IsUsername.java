package com.meide.utils.valid.rules;

/**
 * 用户名校验 字母开头 数字字母下划线减号 共4-20位
 */
public class IsUsername extends MatchRegex {

    public IsUsername() {
        this("用户名格式错误");
    }

    public IsUsername(String message) {
        super("^[a-zA-Z]([-_a-zA-Z0-9]{3,19})+$", message);
    }
}
