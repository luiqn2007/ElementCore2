package com.elementtimes.elementcore.api.misc;

import net.minecraft.entity.EntityClassification;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * 用于注册实体生成
 * @author luqin2007
 */
public class EntitySpawnWrapper {

    private final Biome.SpawnListEntry mSpawnListEntry;
    private final Supplier<Optional<Biome>> mBiome;
    private final EntityClassification mClassification;

    public EntitySpawnWrapper(Biome.SpawnListEntry entry, Supplier<Optional<Biome>> biome, EntityClassification classification) {
        mSpawnListEntry = entry;
        mBiome = biome;
        mClassification = classification;
    }

    public void apply() {
        mBiome.get().ifPresent(biome -> {
            biome.getSpawns(mClassification).add(mSpawnListEntry);
        });
    }

    public EntityClassification getClassification() {
        return mClassification;
    }

    public Biome.SpawnListEntry getSpawnListEntry() {
        return mSpawnListEntry;
    }
}
