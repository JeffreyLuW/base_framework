package com.meide.utils.valid.rules;

import com.meide.utils.valid.OnValidateResult;
import com.meide.utils.valid.ValidatorResult;

/**
 * 满足指定的表达式
 */
public class MatchRegex implements ValidatorRule<Object> {

    String regex;
    String message;

    public MatchRegex(String regex, String message) {
        this.regex = regex;
        this.message = message;
    }

    @Override
    public void validate(Object value, OnValidateResult result) {
        if (null == value) {
            result.onComplete(ValidatorResult.OK);
            return;
        }
        String s = String.valueOf(value);
        if (s.matches(regex)) {
            result.onComplete(ValidatorResult.OK);
        } else {
            result.onComplete(new ValidatorResult(message, false));
        }
    }
}