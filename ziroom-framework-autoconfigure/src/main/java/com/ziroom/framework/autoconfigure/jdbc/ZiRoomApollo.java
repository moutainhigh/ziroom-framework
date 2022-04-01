package com.ziroom.framework.autoconfigure.jdbc;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * auto all properties by package path and class name
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface ZiRoomApollo {

    String value() default ConfigConsts.NAMESPACE_APPLICATION;
    /**
     * assign the config path
     * @return
     */
    String namespace() default "";
    /**
     * this prefix of configuration key
     * @return
     */
    String prefix() default "";
}
