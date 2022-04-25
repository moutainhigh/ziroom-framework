package com.ziroom.framework.ferrari.repository.core.annotation;

import com.ziroom.framework.ferrari.repository.core.constant.OrmFrameEnum;

import java.lang.annotation.*;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:27
 * @Version 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DaoDescription {
    /**
     * 标注使用的底层orm框架
     *
     * @return
     */
    OrmFrameEnum ormFrame() default OrmFrameEnum.MYBATIS;

    /**
     * 标注dao设置对象的bean
     *
     * @return
     */
    String settingBean() default "masterSqlSessionTemplate";
}
