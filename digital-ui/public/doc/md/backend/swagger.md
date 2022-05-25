# swagger

swagger是一个API文档规范，它允许你使用JSON或YAML描述API的属性；SpringFox是该规范的Java实现，可以配合Spring框架动态读取接口信息。

项目使用的knife4j在此基础上对ui和功能进行增强，配置更加方便

> https://doc.xiaominfo.com/guide/

只有配置文件中`swagger.enabled=true`的时候，swagger才会生效。

可以在项目`系统工具-系统接口`查看接口

## 注解使用方法

### @Api

该注解标注在Controller类声明上方，声明该类是swagger的资源

参数：

- tags：说明该类的作用，参数是数组，可以填写多个
- value：在UI界面不展示（不常用）
- description：进一步描述

### @ApiOperation

标注在方法声明上方

参数：

- value：方法的用途和作用
- notes：方法的注意事项和备注
- tags：方法的作用，参数为数组（不常用）

### @ApiParam

如果在方法参数上直接接收基本类型数据，使用该注解对参数进行标注

- value：参数用途
- defaultValue：默认值
- required：是否必填，默认false

### @ApiModel

有些model类用于方法参数的接收或返回值封装，在类的声明上标注它们

- value：该entity类型(不常用)
- description：描述(推荐使用这个参数)

### @ApiModelProperty

标注在entity的字段上，或字段的getter方法上

- value：参数的含义
- required：是否必填，默认false

## 调用方法

选中某个接口后点击`调试`页面，输入相关的参数；

其中，请求头的授权信息可以手动填写，也可以通过右侧感叹号按钮自动填充token；

填写完毕后，点击`发送`按钮即可对接口进行调用。