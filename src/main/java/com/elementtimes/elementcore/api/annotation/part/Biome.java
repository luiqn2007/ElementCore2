package com.elementtimes.elementcore.api.annotation.part;

import com.elementtimes.elementcore.api.Vanilla;
import com.elementtimes.elementcore.api.annotation.enums.ValueType;
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
public @interface Biome {

    /**
     * METHOD
     *  通过 method 属性获取
     * OBJECT
     *  通过 getter 属性获取
     * 其他
     *  通过 name 属性构建 ResourceLocation 获取
     *  {@link net.minecraftforge.registries.ForgeRegistries#BIOMES}
     */
    ValueType type() default ValueType.CONST;

    Getter getter() default @Getter(value = Vanilla.Biomes.class, name = "PLAINS");

    /**
     * 参数
     *  无
     * 返回值
     *  Biome
     */
    Method method() default @Method(value = Vanilla.Biomes.class, name = "plains");

    String name() default "plains";
}
