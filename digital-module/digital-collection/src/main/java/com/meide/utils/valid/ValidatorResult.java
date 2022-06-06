package com.meide.utils.valid;

/**
 * 验证结果
 */
public class ValidatorResult {
    private boolean valid = false;
    private String message = null;

    public static final ValidatorResult OK = new ValidatorResult(null, true);

    public ValidatorResult() {
    }

    public ValidatorResult(String message, boolean valid) {
        this.message = message;
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "ValidatorResult{" +
                "message='" + message + '\'' +
                ", valid=" + valid +
                '}';
    }
}