package com.meide.common.core.domain;

import com.meide.common.constant.HttpStatus;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;

import java.util.List;

/**
 * 操作消息提醒
 *
 * @author jiay
 */
@Getter
@ApiOperation("标准返回对象")
public class AjaxResult<T> {
    @ApiModelProperty("状态码")
    private Integer code;
    @ApiModelProperty("描述信息")
    private String msg;
    @ApiModelProperty("返回数据")
    private T data;
    @ApiModelProperty("未知错误堆栈")
    private List<String> ex;

    public AjaxResult<T> ex(List<String> ex) {
        this.ex = ex;
        return this;
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     */
    public AjaxResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     * @param data 数据对象
     */
    public AjaxResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static <T> AjaxResult<T> success() {
        return AjaxResult.successWithMsg("操作成功");
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static <T> AjaxResult <T> success(T data) {
        return AjaxResult.success("操作成功", data);
    }

    /**
     * 返回成功消息
     * 废弃，请不要在msg返回信息
     * @param msg 返回内容
     * @return 成功消息
     */
    @Deprecated
    public static <T> AjaxResult<T> successWithMsg(String msg) {
        return AjaxResult.success(msg, null);
    }

    /**
     * 返回成功消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static <T> AjaxResult<T> success(String msg, T data) {
        return new AjaxResult<>(HttpStatus.SUCCESS, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @return
     */
    public static <T> AjaxResult<T> error() {
        return AjaxResult.errorWithMsg("操作失败");
    }

    /**
     * 返回错误消息
     */
    public static <T> AjaxResult<T> errorWithMsg() {
        return AjaxResult.error("关键字段缺失", null);
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static <T> AjaxResult<T> errorWithMsg(String msg) {
        return AjaxResult.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static <T> AjaxResult<T> error(String msg, T data) {
        return new AjaxResult<>(HttpStatus.ERROR, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @return 警告消息
     */
    public static <T> AjaxResult<T> error(int code, String msg) {
        return new AjaxResult<>(code, msg, null);
    }
}
