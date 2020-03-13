package com.elementtimes.elementcore.api.annotation;

import com.elementtimes.elementcore.api.annotation.part.Getter;
import com.elementtimes.elementcore.api.annotation.tools.ModBurnTime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 流体注册
 * 注册到 Fluid 类型对象上
 * 若流体继承自 FlowingFluid，只需要应用于 Flowing 和 Source 类型的任意一个流体对象即可
 * @author luqin2007
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ModFluid {
    /**
     * 非 FlowingFluid，或 source 类型流体
     */
    String name() default "";

    /**
     * flowing 类型流体
     */
    String flowingName() default "";

    /**
     * 不注册流体桶
     * 当没有流体桶或手动注册流体桶时，该值应设为 true
     */
    boolean noBucket() default false;

    /**
     * 流体方块注册
     */
    Getter block() default @Getter;

    ModBurnTime burnTime() default @ModBurnTime;
}
