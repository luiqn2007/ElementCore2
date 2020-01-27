package com.elementtimes.elementcore.api.annotation.part;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 代表一个方法。只限定方法所在类和方法名，其他需求详见使用到的地方的注释
 * @author luqin2007
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Method {
    /**
     * 方法所在类
     * @return 类
     */
    Class<?> container();

    /**
     * 方法名
     * @return 方法名
     */
    String name();

    /**
     * 该方法是否被 static 修饰。
     * 如果该值为 false，则 containerObj 值应当指向一个有效的实例
     * @return 该方法是否为静态方法
     */
    boolean isStatic() default true;

    /**
     * 当 isStatic 值为 false 时，将通过该属性获取对应实例
     * @return 非静态方法的容器
     */
    Field containerObj() default @Field(container = Object.class, name = "");
}
