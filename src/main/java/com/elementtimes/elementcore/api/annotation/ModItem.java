package com.elementtimes.elementcore.api.annotation;

import com.elementtimes.elementcore.api.annotation.part.Color;
import com.elementtimes.elementcore.api.annotation.part.item.Properties;
import com.elementtimes.elementcore.api.annotation.tools.Tooltips;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 物品
 * 当应用于一个类时，该类应当继承自 Item，且存在一个无参或只有一个 Item.Properties 参数的构造函数
 * 此时，使用 properties 参数创建 Properties
 * @see net.minecraft.item.Item
 * @see net.minecraft.item.Item.Properties
 * @author luqin2007
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ModItem {

    /**
     * RegistryName
     */
    String value() default "";

    Properties properties() default @Properties;

    Color color() default @Color;

    Tooltips tooltips() default @Tooltips;
}