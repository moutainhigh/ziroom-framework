package com.ziroom.framework.autoconfigure.web;

import java.lang.reflect.Method;
import java.util.Objects;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

/**
 * Created by liangrk on 2022/5/5.
 */
public class WeblogPointcut extends StaticMethodMatcherPointcut {

    WebLogProperties webLogProperties;

    public WeblogPointcut(WebLogProperties webLogProperties) {
        this.webLogProperties = webLogProperties;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {

        Package aPackage = targetClass.getPackage();

        // todo 支持ant
        for (String basePackage : webLogProperties.getBasePackages()) {
            if (Objects.equals(basePackage, aPackage.getName())) {
                return true;
            }
        }

        return false;
    }
}
