package com.elementtimes.elementcore.api.annotation.part;

import net.minecraft.entity.EntityClassification;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 实体生成相关
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
