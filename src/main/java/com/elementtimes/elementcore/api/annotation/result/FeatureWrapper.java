package com.elementtimes.elementcore.api.annotation.result;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import java.util.function.Supplier;

public class FeatureWrapper {

    private final Supplier<Biome> mBiome;
    private final Supplier<ConfiguredFeature> mFeature;
    private final GenerationStage.Decoration mDecoration;

    public FeatureWrapper(Supplier<Biome> biome, GenerationStage.Decoration decoration, Supplier<ConfiguredFeature> feature) {
        mBiome = biome;
        mFeature = feature;
        mDecoration = decoration;
    }

    public void apply() {
        Biome biome = mBiome.get();
        if (biome != null) {
            biome.addFeature(mDecoration, mFeature.get());
        }
    }
}
