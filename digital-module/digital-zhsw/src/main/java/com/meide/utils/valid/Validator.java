package com.meide.utils.valid;


import com.meide.utils.valid.rules.IsPhone;
import com.meide.utils.valid.rules.Required;
import com.meide.utils.valid.rules.SameValue;
import com.meide.utils.valid.rules.ValidatorRule;

import java.util.ArrayList;

/**
 * 用于简化表单验证，可以通过指定规则 实现值的验证。
 * 可以自定义各种ValidatorRule的实现，同时支持异步验证。
 */
public class Validator {

    private ArrayList<ValueRules> valueRules = new ArrayList<>();
    private int currIndex = 0;

    public static Validator create() {
        return new Validator();
    }

    /**
     * 前面是值 后面是多个规则。
     *
     * @param value
     * @param rules
     * @return
     */
    public Validator validate(Object value, ValidatorRule... rules) {
        return this.validate(value, null, rules);
    }


    public Validator validate(Object value, OnValidateResult onValidate, ValidatorRule... rules) {
        valueRules.add(new ValueRules(value, rules).onValidate(onValidate));
        return this;
    }

    public void then(OnValidateResult result) {
        then(result, false);
    }

    ValidatorResult tempResult;

    //同步验证
    public ValidatorResult thenSync() {
        then(new OnValidateResult() {
            @Override
            public void onComplete(ValidatorResult result) {
                tempResult = result;
            }
        }, false);
        return tempResult;
    }

    public void then(OnValidateResult result, boolean validateAll) {
        currIndex = 0;
        if (validateAll) {
            doValidateAll(result);
        } else {
            doValidate(result);
        }
    }

    private void doValidateAll(OnValidateResult result) {
        ArrayList<ValidatorResult> allResult = new ArrayList<>();
        for (ValueRules rule : valueRules) {
            rule.validate(new OnValidateResult() {
                @Override
                public void onComplete(ValidatorResult currResult) {
                    allResult.add(currResult);
                    if (allResult.size() == valueRules.size()) {
                        //complete
                        boolean success = true;
                        StringBuilder errorMsgBuilder = new StringBuilder();
                        for (ValidatorResult rs : allResult) {
                            if (rs != null && !rs.isValid()) {
                                success = false;
                                if (errorMsgBuilder.length() > 0)
                                    errorMsgBuilder.append(";");
                                errorMsgBuilder.append(rs.getMessage());
                            }
                        }
                        result.onComplete(
                                success ? ValidatorResult.OK :
                                        new ValidatorResult(errorMsgBuilder.toString(), success)
                        );
                    }
                }
            });
        }
    }

    /**
     * 碰到错误就不再继续验证。
     *
     * @param result
     */
    private void doValidate(OnValidateResult result) {
        ValueRules rule = valueRules.get(currIndex);
        rule.validate(new OnValidateResult() {
            @Override
            public void onComplete(ValidatorResult currResult) {
                if (currResult != null && !currResult.isValid()) {
                    //error
                    result.onComplete(currResult);
                } else {
                    //right
                    if (currIndex == valueRules.size() - 1) {
                        result.onComplete(ValidatorResult.OK);
                    } else {
                        //go on
                        currIndex++;
                        doValidate(result);
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        String value = "13065023700";// "admin";
        String value2 = "123";

        Validator.create()
                .validate(value, new Required("用户名不能为空"), new IsPhone())
                .validate(value2, new Required("密码不能为空"), new SameValue("123", "密码不一致"))
                .then((validatorResult) -> {
                    System.out.println(validatorResult);
                });

    }


}