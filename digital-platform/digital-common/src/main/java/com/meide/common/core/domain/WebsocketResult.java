package com.meide.common.core.domain;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 操作消息提醒
 *
 * @author jiay
 */
@Data
@ApiOperation("标准返回对象")
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class WebsocketResult<T> {
    @ApiModelProperty("状态码")
    private Integer code;
    @ApiModelProperty("描述信息")
    private String msg;
    @ApiModelProperty("业务类型")
    private String type;
    @ApiModelProperty("返回数据")
    private T data;
    @ApiModelProperty("未知错误堆栈")
    private List<String> ex;

    public WebsocketResult<T> ex(List<String> ex) {
        this.ex = ex;
        return this;
    }


}
