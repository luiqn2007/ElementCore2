package com.elementtimes.elementcore.api.annotation;

import com.elementtimes.elementcore.api.annotation.part.EffectInstance;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 药水
 * 注解到一个药水类的对象或类上
 * 若应用到药水类上，该类应当有一个接受 String, EffectInstance[] 类型的构造函数
 * @author luqin2007
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@SuppressWarnings("unused")
public @interface ModPotion {

    /**
     * RegistryName
     */
    String value() default "";

    /**
     * baseName
     */
    String name() default "";

    EffectInstance[] effects() default {};
}
