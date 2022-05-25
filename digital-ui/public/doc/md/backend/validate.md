# Validate

validate是Spring提供的参数验证，如果引入了`spring-boot-start-web`启动包，会自动引入validate的启动包，所以不必额外引入依赖。

在`digatal-admin`模块`com.meide.demo`包中提供了简单的例子

## 启动校验的三种方法

### 1. 接受Body参数

在接收参数的实体类前添加`@Validated`注解，可以通过实体类中参数上的校验规则，进行参数校验。

```
public void xxx(@Validate @RequestBody User user){}
```



### 2. 接受Param参数

直接通过Param接收Get请求参数的时候，可以直接在参数前添加校验规则注解。

需要注意的是，这样使用的时候必须在当前Controller上添加`@Validate`注解。

```
public void xxx(@Max(value="3",message="数字不能大于三") @RequestParam Integer num){}
```



### 3. 手动校验

在任何地方，进行手动校验。

可以参考`SysUserServiceImpl#importUser`方法

```java
try (
	ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()
) {
    //获取校验对象
	Validator validator = validatorFactory.getValidator();
    //对user对象进行校验，校验结果为Set<ConstraintViolation<?>>
    Set<ConstraintViolation<SysUser>> validateSet = validator.validate(user);
}
```

在获取校验结果之后，可以直接抛出`ConstraintViolationException(validateSet)`，进行全局异常统一处理。

也可以手动拼接错误信息，返回友好提示

```java
if (validateSet != null && validateSet.size() > 0) {
    String messages = validateSet.stream()
        .map(ConstraintViolation::getMessage)
        .reduce((m1, m2) -> m1 + "；" + m2)
        .orElse("参数输入有误！");
}
```

## 常用校验规则注解

加在实体类字段或对应Getter方法上，或者加载Controller参数上

- @AssertFalse 校验false
- @AssertTrue 校验true
- @DecimalMax(value=,inclusive=) 小于value，inclusive是否包含等于
- @DecimalMin(value=,inclusive=) 大于value，inclusive是否包含等于
- @Max(value=) 小于等于value
- @Min(value=) 大于等于value
- @NotNull  检查Null
- @Past  检查日期
- @Pattern(regex=,flag=)  正则
- @Size(min=, max=)  字符串，集合，map限制大小

这些注解都有message参数，用于校验不通过时对前端进行友好提示，建议必填。

## 根据业务执行不同的校验规则
有的时候，同一个实体类会被多种业务场景作为参数接受的容器，我们需要根据业务场景不同，指定不同的校验规则

比如，示例代码中，新增和修改都使用同一个`NoteInfoParam`接收参数。

新增的时候不需要传id，因为还没有生成；而修改的时候必须传id。

根据这种场景我们定义了两个空接口`Insert`和`Update`

我们在`NoteInfoParam`指定规则的注解中使用`group`参数，指定`Insert`或者`Update`，代表在某个场景下起作用。

最后，我们在Controller中的`@Validate`注解中也指定group为`Insert`或者`Update`，这样的话，它会根据你指定的场景，只根据相同场景的条件注解来对参数进行校验。

具体可以查看`DemoNoteController`相关代码

### 自定义校验规则注解

#### 1. 定义注解

```java
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
//用于校验手机号的逻辑类
@Constraint(validatedBy = PhoneValidator.class)
public @interface Phone {
    //手机号的校验格式
    String regexp() default "^[1][3-9][0-9]{9}$";

    //出现错误返回的信息
    String message() default "手机号格式错误";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        Phone[] value();
    }
}
```

#### 2. 创建校验注解的类

```java
//校验注解的类实现ConstraintValidator，第一个泛型是注解，第二个泛型是校验参数的类型（手机号是String类型）
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private String regexp;

    //初始化方法
    @Override
    public void initialize(Phone constraintAnnotation) {
        //获取校验的手机号的格式
        this.regexp = constraintAnnotation.regexp();
    }

    //value是@Phone注解所注解的字段值
    //校验，返回true则通过校验，返回false则校验失败，错误信息为注解中的message
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null){
            return true;
        }

        return value.matches(regexp);
    }
}
```

#### 3. 使用

```java
@RestController
@Validated
public class PhoneController {

    @GetMapping("/sendPhone")
    public JsonResult sendPhone(@Phone @NotNull(message = "手机号不能为空") String phone){
        return JsonResult.success("正确的手机号");
    }
}
```

## 全局异常捕获

需要全局捕获的异常有

- BindException
- MethodArgumentNotValidException
- ConstraintViolationException

如果要自定义处理流程，修改`com.meide.framework.web.exception.GlobalExceptionHandler`