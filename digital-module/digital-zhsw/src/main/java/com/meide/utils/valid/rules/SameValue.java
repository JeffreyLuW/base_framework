package com.meide.utils.valid.rules;


import com.meide.utils.valid.OnValidateResult;
import com.meide.utils.valid.ValidatorResult;

/**
 * 两个值相同
 */
public class SameValue implements ValidatorRule<Object> {
    Object targetValue;
    String message;

    public SameValue(String targetValue) {
        this(targetValue, "两次输入不一致");
    }

    public SameValue(String targetValue, String message) {
        this.targetValue = targetValue;
        this.message = message;
    }

    @Override
    public void validate(Object value, OnValidateResult result) {
        if (null == value) {
            result.onComplete(ValidatorResult.OK);
            return;
        }
        if (!value.equals(targetValue)) {
            result.onComplete(new ValidatorResult(message, false));
        } else {
            result.onComplete(ValidatorResult.OK);
        }
    }
}