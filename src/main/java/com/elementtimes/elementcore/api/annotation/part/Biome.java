package com.elementtimes.elementcore.api.annotation.part;

import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.Vanilla;
import com.elementtimes.elementcore.api.annotation.enums.ValueType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;

/**
 * 代表一个方块材质
 * 该注解返回一个 {@link java.util.function.Supplier<net.minecraft.world.biome.Biome>}
 * @see net.minecraft.world.biome.Biome
 * @see Parts#biome(Object, Object, Supplier, ECModElements)
 * @author luqin2007
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Biome {

    ValueType type() default ValueType.VALUE;

    Getter object() default @Getter;

    /**
     * 参数
     *  {@link Object} 被注解对象，可能为 Entity 或 Block，也可能为 Class
     * 返回值
     *  {@link net.minecraft.world.biome.Biome}
     */
    Method method() default @Method;

    /**
     * 由此创建 ResourceLocation 寻找 Biome
     * @see net.minecraftforge.registries.ForgeRegistries#BIOMES
     */
    String value() default "";
}
