package com.meide.utils.valid.rules;


import com.meide.utils.valid.OnValidateResult;
import com.meide.utils.valid.ValidatorResult;

/**
 * 必填规则
 */
public class LimitLength implements ValidatorRule<Object> {
    Integer min;
    Integer max;
    String message;

    public LimitLength(Integer min, Integer max) {
        this(min, max, null);
    }

    public LimitLength(Integer min, Integer max, String message) {
        this.max = max;
        this.min = min;
        this.message = message;

        if (null == this.message) {
            if (null != min && null != max) {
                if (min.equals(max)) {
                    this.message = "只能输入" + min + "个字符";
                } else {
                    this.message = String.format("需要%s-%s个字符", min, max);
                }
            } else if (null != min && null == max) {
                this.message = String.format("至少%s个字符", min);
            } else if (null == min && null != max) {
                this.message = String.format("最多%s个字符", max);
            }
        }
    }

    @Override
    public void validate(Object value, OnValidateResult result) {
        if (null == value || (min == null && max == null)) {
            result.onComplete(ValidatorResult.OK);
            return;
        }
        String s = String.valueOf(value);

        if (null != this.min && s.length() < this.min
                || null != this.max & s.length() > max) {
            result.onComplete(new ValidatorResult(message, false));
        } else {
            result.onComplete(ValidatorResult.OK);
        }
    }
}