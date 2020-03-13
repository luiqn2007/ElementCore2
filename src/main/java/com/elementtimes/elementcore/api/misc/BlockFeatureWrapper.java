package com.elementtimes.elementcore.api.misc;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import java.util.Optional;
import java.util.function.Supplier;

public class BlockFeatureWrapper {

    private final Supplier<Optional<Biome>> mBiome;
    private final Supplier<ConfiguredFeature[]> mFeature;
    private final GenerationStage.Decoration mDecoration;

    public BlockFeatureWrapper(Supplier<Optional<Biome>> biome, GenerationStage.Decoration decoration, Supplier<ConfiguredFeature[]> feature) {
        mBiome = biome;
        mFeature = feature;
        mDecoration = decoration;
    }

    public void apply() {
        mBiome.get().ifPresent(biome -> {
            for (ConfiguredFeature feature : mFeature.get()) {
                biome.addFeature(mDecoration, feature);
            }
        });
    }
}
