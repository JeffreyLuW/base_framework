package com.meide.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel第一行合并
 *
 * @author jiay
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExcelHeader {

    /**
     * 合并单元格的名称
     */
    public String name() default "";

    /**
     * 合并单元格的开始索引
     *
     * @return
     */
    public int startIndex() default 0;

    /**
     * 合并单元格的结束索引
     *
     * @return
     */
    public int endIndex() default 0;
}
