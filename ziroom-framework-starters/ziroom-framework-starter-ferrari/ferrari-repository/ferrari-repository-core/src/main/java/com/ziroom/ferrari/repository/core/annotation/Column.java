package com.ziroom.ferrari.repository.core.annotation;

import java.lang.annotation.*;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:10
 * @Version 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    //对应的列名
    String value() default "";

    //是否持久化
    boolean isTransient() default true;
}
