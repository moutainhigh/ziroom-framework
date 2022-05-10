
package com.ziroom.framework.module.distributedlock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

/**
 * <pre>
 * 下面的例子会把account取出来当做锁的名称
 * lockNameExpression：spel表达式
 * 可取值如下
 * 静态字符串: 'abcd', 需要单引号，
 * 动态：需要加#
 *      参数: 可通过参数名或者param1，param2累加的方式表示参数。  支持级联，如user.name或param1.name
 *      public String test(User user) {}
 *
 *      其他可取值：
 *      applicationName：应用名称，通过spring.application.name设置， 若无，则为''
 *      className： 当前类名
 *      method： 当前方法名
 *
 * e.g.
 * 字符串类型 ： @DistributedLock("'aa'")
 * 动态参数型 ： @DistributedLock("#user.name")
 * 字符串+参数： @DistributedLock("'aa' + #user.name")
 * 类名+方法名+参数： @DistributedLock("#className+ #method + 'aa' + #user.name")
 * <pre>
 * @author liangrk
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /**
     * 分布式锁名称
     */
    @AliasFor("value")
    String lockNameExpression() default "";

    @AliasFor("lockNameExpression")
    String value() default "";


}
