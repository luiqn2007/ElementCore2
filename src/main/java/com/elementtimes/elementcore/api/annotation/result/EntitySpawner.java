package com.elementtimes.elementcore.api.annotation.result;

import net.minecraft.entity.EntityClassification;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import java.util.function.Supplier;

/**
 * 用于注册实体生成
 * @author luqin2007
 */
public class EntitySpawner {

    private final Biome.SpawnListEntry mSpawnListEntry;
    private final Supplier<Biome> mBiome;
    private final EntityClassification mClassification;

    public EntitySpawner(Biome.SpawnListEntry entry, Supplier<Biome> biome, EntityClassification classification) {
        mSpawnListEntry = entry;
        mBiome = biome;
        mClassification = classification;
    }

    public void apply() {
        Biome biome = mBiome.get();
        if (biome != null) {
            biome.getSpawns(mClassification).add(mSpawnListEntry);
        }
    }

    public EntityClassification getClassification() {
        return mClassification;
    }

    public Biome.SpawnListEntry getSpawnListEntry() {
        return mSpawnListEntry;
    }
}
