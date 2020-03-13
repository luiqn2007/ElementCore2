package com.elementtimes.elementcore.api.annotation;

import com.elementtimes.elementcore.api.annotation.part.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记注册 Block
 * 该注解可用于一个静态对象或一个继承自 Block 的类。
 * 若注解一个类，优先使用无参构造实例化 Block，否则使用 block 属性生成 Block.Properties 对象，使用带有一个 Block.Properties 参数的构造函数
 * 使用该注解注册的方块，默认会添加一个方块对应的物品，若要设定该物品或者禁止生成，使用 @ModBlock.Item 进行详细设定
 * @author luqin2007
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ModBlock {

    /**
     * RegisterName
     */
    String value() default "";

    /**
     * 用于生成对应的 BlockItem
     */
    ItemProps item() default @ItemProps;

    /**
     * 用于实例化方块类
     */
    BlockProps prop() default @BlockProps;

    /**
     * 当该属性为 false 时，不会自动生成 BlockItem 对象
     */
    boolean noItem() default false;

    /**
     * 方块的世界生成
     * 若该注解应用到一个类上且没有与 ModBlock 同用，则会对所有继承自该类的方块统一增加对应物品
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.TYPE})
    @interface Features {
        /**
         * 世界生成
         */
        Feature[] value() default {};
    }

    /**
     * 方块及其对应物品的染色
     * 若该注解应用到一个类上且没有与 ModBlock 同用，则会对所有继承自该类的方块统一增加对应物品
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.TYPE})
    @interface Colors {

        /**
         * 方块对应物品染色
         */
        Color item() default @Color;

        /**
         * 方块染色
         */
        Color block() default @Color;
    }
}