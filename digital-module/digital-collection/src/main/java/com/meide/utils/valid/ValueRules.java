package com.meide.utils.valid;

import com.meide.utils.valid.rules.ValidatorRule;

/**
 * 某个值的验证规则。
 */
public class ValueRules {
    Object value;
    ValidatorRule[] rules;
    int currentIndex = 0;

    OnValidateResult extraOnValidateResult;

    public ValueRules(Object value, ValidatorRule[] rules) {
        this.rules = rules;
        this.value = value;
    }

    //设置一个监听。当前值验证结束后的监听
    public ValueRules onValidate(OnValidateResult onValidate) {
        this.extraOnValidateResult = onValidate;
        return this;
    }

    public void validate(OnValidateResult result) {
        if (null == rules||rules.length==0) {
            result.onComplete(ValidatorResult.OK);
            return;
        }
        currentIndex = 0;
        doValidate(result);
    }

    private void doValidate(OnValidateResult result) {
        ValidatorRule rule = rules[currentIndex];
        rule.validate(value, new OnValidateResult() {
            @Override
            public void onComplete(ValidatorResult currResult) {
                if (currResult != null && !currResult.isValid()) {
                    //error   stop check
                    if (extraOnValidateResult != null) {
                        extraOnValidateResult.onComplete(currResult);
                    }
                    result.onComplete(currResult);
                } else {
                    //最后一个?
                    if (currentIndex == rules.length - 1) {
                        if (extraOnValidateResult != null) {
                            extraOnValidateResult.onComplete(ValidatorResult.OK);
                        }
                        result.onComplete(ValidatorResult.OK);
                    } else {
                        // go on
                        currentIndex++;
                        doValidate(result);
                    }
                }
            }
        });
    }
}