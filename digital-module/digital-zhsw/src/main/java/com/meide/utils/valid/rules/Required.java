package com.meide.utils.valid.rules;

import com.meide.utils.valid.OnValidateResult;
import com.meide.utils.valid.ValidatorResult;

/**
 * 必填规则.
 */
public class Required implements ValidatorRule<Object> {
    String errorMessage;

    public Required() {
        this("不能为空");
    }

    public Required(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public void validate(Object value, OnValidateResult result) {
        if (null == value || String.valueOf(value).trim().isEmpty()) {
            result.onComplete(new ValidatorResult(errorMessage, false));
        } else {
            result.onComplete(ValidatorResult.OK);
        }
    }
}