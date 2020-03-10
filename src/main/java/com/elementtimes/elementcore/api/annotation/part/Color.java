package com.elementtimes.elementcore.api.annotation.part;

import com.elementtimes.elementcore.api.annotation.enums.ValueType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解到任意 Item/Block 对象中，作为方块/物品染色
 * @author luqin2007
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Color {

    /**
     * 染色方法
     *  ValueType.CONST
     *      选取 color 属性值
     *  ValueType.METHOD
     *      选取 method 属性值
     *  ValueType.OBJECT
     *      选取 obj 属性值
     *  ValueType.NONE
     *      忽略
     * @return 染色方法
     */
    ValueType type() default ValueType.NONE;

    /**
     * 当 type == ValueType.CONST 时，选取此值
     * @return 染色值
     */
    int color() default 0;

    /**
     * 当 type == ValueType.METHOD 时，选取此值
     * 若该染色器应用于物品，则
     *  参数
     *      ItemStack, int
     *  返回值
     *      int
     * 若该染色器应用于方块，则
     *  参数
     *      BlockState, IEnviromentBlockReader, BlockPos, int
     *  返回值
     *      int
     * @return 染色方法
     */
    Method2 method() default @Method2;

    /**
     * 当 type == ValueType.OBJECT 时，选取此值
     * 若该染色器应用于物品，则
     *  返回 {@link net.minecraft.client.renderer.color.IItemColor}
     * 若该染色器应用于方块，则
     *  返回 {@link net.minecraft.client.renderer.color.IBlockColor}
     * @return 染色对象
     */
    Getter2 obj() default @Getter2;
}
