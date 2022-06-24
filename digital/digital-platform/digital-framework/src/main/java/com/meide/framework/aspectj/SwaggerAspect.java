package com.meide.framework.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 增强swagger接口数据
 */
@Aspect
@Component
public class SwaggerAspect {
    private static final Logger log = LoggerFactory.getLogger(SwaggerAspect.class);

    // 配置织入点
    @Pointcut("execution(public * springfox.documentation.swagger2.web.Swagger2Controller.getDocumentation(..))")
    public void pointCut() {
    }

    @Around(value = "pointCut()")
    public Object doAfterReturning(ProceedingJoinPoint point) throws Throwable {
        Object rs = point.proceed();
        System.out.println(rs);
        return rs;
    }

}
