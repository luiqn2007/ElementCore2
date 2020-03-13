package com.elementtimes.elementcore.api.annotation.part;

import com.elementtimes.elementcore.api.ECModElements;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;

/**
 * 实体生成相关
 * @see Parts#entitySpawn(Object, Object, EntityType, Supplier, ECModElements)
 * @see com.elementtimes.elementcore.api.misc.EntitySpawnWrapper
 * @author luqin2007
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface EntitySpawn {
    Biome biome() default @Biome;
    EntityClassification classification() default EntityClassification.CREATURE;
    int weight() default 10;
    int minCount() default 4;
    int maxCount() default 4;
}
