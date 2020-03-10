package com.elementtimes.elementcore.api.annotation;

import com.elementtimes.elementcore.api.annotation.enums.ValueType;
import com.elementtimes.elementcore.api.annotation.part.Color;
import com.elementtimes.elementcore.api.annotation.part.Getter;
import com.elementtimes.elementcore.api.annotation.part.Method;
import com.elementtimes.elementcore.api.annotation.part.block.Feature;
import com.elementtimes.elementcore.api.annotation.part.item.Properties;
import com.elementtimes.elementcore.api.annotation.tools.Tooltips;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记注册 Block
 * 该注解可用于一个静态对象或一个继承自 Block 的类，或者该类的构造函数。
 * 若注解一个类，优先使用无参构造实例化 Block，否则使用 block 属性生成 Block.Properties 对象，使用带有一个 Block.Properties 参数的构造函数
 * @author luqin2007
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.CONSTRUCTOR})
public @interface ModBlock {

    /**
     * RegisterName，代表方块注册名
     * 当该注解注解 Field 且方块 registerName 与变量名相同（忽略大小写，使用 toLowerCase 处理）时，可省略
     * @return registerName
     */
    String value() default "";

    /**
     * 为 Block 注册 Item。
     * ValueType.NONE
     *  无物品
     * ValueType.OBJECT
     *  使用 itemObj 属性，获取一个 Item 对象，否则等效 ValueType.NONE
     * 其他
     *  使用 item 属性创建 {@link net.minecraft.item.BlockItem}
     */
    ValueType itemType() default ValueType.CONST;

    /**
     * itemType 属性为 ValueType.CONST 时，使用该属性创建 {@link net.minecraft.item.BlockItem}
     */
    Properties item() default @Properties;

    /**
     * itemType 属性非 ValueType.CONST 或 ValueType.NONE 时，使用该属性获取 {@link net.minecraft.item.Item}
     */
    Getter itemObj() default @Getter;

    com.elementtimes.elementcore.api.annotation.part.block.Properties block() default @com.elementtimes.elementcore.api.annotation.part.block.Properties;

    /**
     * 世界生成
     */
    Feature[] features() default {};

    /**
     * 方块对应物品染色
     */
    Color itemColor() default @Color;

    /**
     * 方块染色
     */
    Color blockColor() default @Color;

    /**
     * Tooltips
     */
    Tooltips tooltips() default @Tooltips;
}