package com.elementtimes.elementcore.api.annotation.part.block;

import com.elementtimes.elementcore.api.Vanilla;
import com.elementtimes.elementcore.api.annotation.enums.ValueType;
import com.elementtimes.elementcore.api.annotation.part.Getter;
import com.elementtimes.elementcore.api.annotation.part.Method;
import com.elementtimes.elementcore.api.annotation.part.item.ToolType;
import net.minecraft.block.Block;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于创建一个 {@link net.minecraft.block.Block.Properties}
 * @author luqin2007
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Properties {

    /**
     * ValueType.OBJECT
     *  使用 object 属性，可接受一个 Block 或 Block.Properties 对象
     * ValueType.METHOD
     *  使用 method 属性
     *  参数：无
     *  返回值：Block.Properties 或 Block 对象
     * 其他
     *  根据其他参数，创建 Block.Properties 对象
     */
    ValueType type() default ValueType.OBJECT;

    /**
     * 当 type == ValueType.OBJECT 时调用，返回一个 Block 或 Block.Properties 对象
     * 当返回 Block 对象时，使用 {@link net.minecraft.block.Block.Properties#from(Block)} 方法获取
     *  当该值无效时，使用 Stone 的 Properties
     */
    Getter object() default @Getter(value = Vanilla.Properties.class, name = "()defaultBlock");

    /**
     * 当 type == ValueType.METHOD 时调用，返回一个 Block 或 Block.Properties 对象
     * 当返回 Block 对象时，使用 {@link net.minecraft.block.Block.Properties#from(Block)} 方法获取
     *  当该值无效时，使用 Stone 的 Properties
     */
    Method method() default @Method(value = Vanilla.Properties.class, name = "defaultBlock");

    Material material() default @Material;

    int colorIndex() default -1;

    int colorDye() default -1;

    boolean doesNotBlockMovement() default false;

    float slipperiness() default 0.6f;

    Getter soundType() default @Getter(value = Vanilla.Sounds.class, name = "STONE");

    int lightValue() default 0;

    float hardness() default 0f;

    float resistance() default 0f;

    boolean ticksRandomly() default false;

    boolean variableOpacity() default false;

    ToolType harvest() default @ToolType(tool = @Getter, level = -1);

    boolean noDrops() default false;

    /**
     * 返回一个 Block 对象，使用该 Block 的掉落物
     */
    Getter loot() default @Getter;
}
