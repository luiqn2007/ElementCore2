package com.elementtimes.elementcore.api.annotation.part.block;

import com.elementtimes.elementcore.api.Vanilla;
import com.elementtimes.elementcore.api.annotation.enums.ValueType;
import com.elementtimes.elementcore.api.annotation.part.Biome;
import com.elementtimes.elementcore.api.annotation.part.Getter;
import com.elementtimes.elementcore.api.annotation.part.Method;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 代表一个方块材质
 * @author luqin2007
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Feature {

    Biome biome() default @Biome;

    GenerationStage.Decoration decoration() default GenerationStage.Decoration.UNDERGROUND_ORES;

    /**
     * ValueType.OBJECT
     *  通过 getter 属性获取一个 ConfiguredFeature 对象
     *  {@link net.minecraft.world.gen.feature.ConfiguredFeature}
     * ValueType.METHOD
     *  通过 method 属性获取一个 ConfiguredFeature 对象
     *  {@link net.minecraft.world.biome.Biome#createDecoratedFeature(net.minecraft.world.gen.feature.Feature, IFeatureConfig, Placement, IPlacementConfig)}
     *  参数
     *      Block
     *  返回值
     *      ConfiguredFeature 对象
     * 其他
     *  根据注解其他参数，通过 Biome.createDecoratedFeature 方法创建
     */
    ValueType type() default ValueType.CONST;

    /**
     * 获取一个 ConfiguredFeature 对象
     */
    Getter getter() default @Getter;

    /**
     * 参数
     *     Block
     * 返回值
     *     ConfiguredFeature 对象
     */
    Method method() default @Method;

    /**
     * 生成类型
     */
    Getter feature() default @Getter(value = Vanilla.Features.class, name = "ORE");

    /**
     * 生成方法
     * 参数
     *  Block
     * 返回值
     *  IFeatureConfig
     * 默认值为主世界铁矿生成
     */
    Method featureConfig() default @Method(value = Vanilla.Features.class, name = "featureConfig");

    /**
     * 生成配置
     */
    Getter placement() default @Getter(value = Vanilla.Placements.class, name = "COUNT_RANGE");

    /**
     * 配置值
     * 参数
     *  Block, Placements
     * 返回值
     *  IPlacementConfig
     * 默认值为主世界铁矿生成
     */
    Method placementConfig() default @Method(value = Vanilla.Placements.class, name = "placementConfig");
}
