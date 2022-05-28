package com.meide.utils.valid.rules;


import com.meide.utils.valid.OnValidateResult;

public interface ValidatorRule<T> {
    void validate(T value, OnValidateResult onValidatorResult);
}