package com.meide.framework.renren;


/**
 *功能描述 自定义-异常
 *
 * @author jiay
 */
public class ApiException extends RuntimeException {
	private static final long serialVersionUID = 1L;

    private int code;
	private String msg;

	public ApiException(String msg) {
		super(msg);
		this.code = ErrorCode.RETURN_CODE;
		this.msg = msg;
	}

	public ApiException(String msg, Throwable e) {
		super(msg, e);
		this.code = ErrorCode.INTERNAL_SERVER_ERROR;
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}