package com.meide.framework.web.exception;

import com.meide.common.constant.HttpStatus;
import com.meide.common.core.domain.AjaxResult;
import com.meide.common.exception.BaseException;
import com.meide.common.exception.CustomException;
import com.meide.common.exception.DemoModeException;
import com.meide.common.utils.StringUtils;
import com.meide.framework.renren.ApiException;
import com.meide.framework.renren.RenException;
import com.meide.framework.renren.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * 全局异常处理器
 *
 * @author jiay
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${digital.showUnknownException}")
    private Boolean showUnknownException;

    /**
     * 基础异常
     */
    @ExceptionHandler(BaseException.class)
    public AjaxResult baseException(BaseException e) {
        return AjaxResult.errorWithMsg(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(CustomException.class)
    public AjaxResult businessException(CustomException e) {
        if (StringUtils.isNull(e.getCode())) {
            return AjaxResult.errorWithMsg(e.getMessage());
        }
        return AjaxResult.error(e.getCode(), e.getMessage());
    }
    @ExceptionHandler(IllegalStateException.class)
    public AjaxResult illegalStateException(IllegalStateException e) {
        return AjaxResult.errorWithMsg(e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
    public AjaxResult handlerNoFoundException(Exception e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error(HttpStatus.NOT_FOUND, "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.FORBIDDEN)
    public AjaxResult handleAuthorizationException(AccessDeniedException e) {
        log.error(e.getMessage());
        return AjaxResult.error(HttpStatus.FORBIDDEN, "没有权限，请联系管理员授权");
    }

    @ExceptionHandler(AccountExpiredException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.UNAUTHORIZED)
    public AjaxResult handleAccountExpiredException(AccountExpiredException e) {
        log.error(e.getMessage(), e);
        return AjaxResult.errorWithMsg(e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.UNAUTHORIZED)
    public AjaxResult handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error(e.getMessage(), e);
        return AjaxResult.errorWithMsg(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxResult handleException(Exception e) {
        log.error(e.getMessage(), e);
        StackTraceElement[] stackTrace = e.getStackTrace();
        List<String> list = null;
        //展示未知异常堆栈信息
        if (showUnknownException) {
            list = new ArrayList<>();
            list.add(e.toString());
            for (int i = 0; i < stackTrace.length; i++) {
                if (i < 5) {
                    list.add(stackTrace[i].toString());
                } else if (stackTrace[i].getClassName().startsWith("com.meide")) {
                    list.add(stackTrace[i].toString());
                }
            }
        }
        return AjaxResult.errorWithMsg("服务器异常，请稍候再试").ex(list);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult validatedBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return AjaxResult.errorWithMsg(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object validExceptionHandler(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return AjaxResult.errorWithMsg(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Object constraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        String message = e.getConstraintViolations().iterator().next().getMessage();
        return AjaxResult.errorWithMsg(message);
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public AjaxResult demoModeException(DemoModeException e) {
        return AjaxResult.errorWithMsg("演示模式，不允许操作");
    }

    /**
     * 自定义验证异常（renrne框架兼容）
     */
    @ExceptionHandler(RenException.class)
    public Result renException(RenException e) {
        log.error(e.getMessage(), e);
        return new Result().error(e.getCode(), e.getMsg());
    }

    /**
     * 自定义验证异常（renrne框架兼容）
     */
    @ExceptionHandler(ApiException.class)
    public Result apiException(ApiException e) {
        log.error(e.getMessage(), e);
        return new Result().error(e.getCode(), e.getMsg());
    }
}
