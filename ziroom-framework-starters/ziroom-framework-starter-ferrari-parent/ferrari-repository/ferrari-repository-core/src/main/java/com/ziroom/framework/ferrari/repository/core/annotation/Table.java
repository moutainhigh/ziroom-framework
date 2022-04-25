package com.ziroom.framework.ferrari.repository.core.annotation;

import java.lang.annotation.*;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:11
 * @Version 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    /**
     * 表名
     *
     * @return
     */
    String value();

    /**
     * oracle表对应的sequence
     *
     * @return
     */
    String sequence() default "";
}
