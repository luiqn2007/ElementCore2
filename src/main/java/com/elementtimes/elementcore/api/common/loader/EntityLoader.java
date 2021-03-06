package com.elementtimes.elementcore.api.common.loader;

import com.elementtimes.elementcore.api.annotation.ModEntity;
import com.elementtimes.elementcore.api.common.ECModElements;
import com.elementtimes.elementcore.api.common.helper.ObjHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import java.util.*;

/**
 * @author luqin2007
 */
public class EntityLoader {

    public static void load(ECModElements elements) {
        ObjHelper.stream(elements, ModEntity.class).forEach(data -> {
            ObjHelper.findClass(elements, data.getClassName()).ifPresent(aClass -> {
                EntityData entityData = new EntityData((Class<? extends Entity>) aClass, data.getAnnotationInfo());
                elements.warn("[ModEntity]{}", entityData);
                elements.entities.add(entityData);
            });
        });
    }

    public static class EntityData {
        public final int network;
        public final String id;
        public final String name;
        public final int trackerRange;
        public final int updateFrequency;
        public final boolean sendVelocityUpdate;
        public final boolean hasEgg;
        public final int eggColorPrimary;
        public final int eggColorSecondary;
        public final boolean canSpawn;
        public final EnumCreatureType spawnType;
        public final int spawnWeight;
        public final int spawnMin;
        public final int spawnMax;
        public final Class<? extends Entity> entityClass;
        public final List<String> biomeIds;

        private EntityEntry mEntry = null;

        public EntityData(Class<? extends Entity> entityClass, Map<String, Object> data) {
            this((int) data.getOrDefault("network", 2), entityClass, (String) data.get("id"), (String) data.get("name"),
                    (int) data.getOrDefault("trackerRange", 64), (int) data.getOrDefault("updateFrequency", 3), (boolean) data.getOrDefault("sendVelocityUpdate", true),
                    (boolean) data.getOrDefault("hasEgg", true), (int) data.getOrDefault("eggColorPrimary", 0x000000), (int) data.getOrDefault("eggColorSecondary", 0x000000),
                    (boolean) data.getOrDefault("canSpawn", false), EnumCreatureType.valueOf((String) data.getOrDefault("spawnType", "CREATURE")),
                    (int) data.getOrDefault("spawnWeight", 0), (int) data.getOrDefault("spawnMin", 0), (int) data.getOrDefault("spawnMax", 0),
                    (List<String>) data.getOrDefault("biomeIds", Collections.singletonList("plains")));
        }

        public EntityData(int network, Class<? extends Entity> entityClass, String id, String name,
                          int trackerRange, int updateFrequency, boolean sendVelocityUpdate,
                          boolean hasEgg, int eggColorPrimary, int eggColorSecondary,
                          boolean canSpawn, EnumCreatureType spawnType, int spawnWeight, int spawnMin, int spawnMax, List<String> biomeIds) {
            this.network = network;
            this.entityClass = entityClass;
            this.id = id;
            this.name = name;
            this.trackerRange = trackerRange;
            this.updateFrequency = updateFrequency;
            this.sendVelocityUpdate = sendVelocityUpdate;
            this.hasEgg = hasEgg;
            this.eggColorPrimary = eggColorPrimary;
            this.eggColorSecondary = eggColorSecondary;
            this.canSpawn = canSpawn;
            this.spawnType = spawnType;
            this.spawnWeight = spawnWeight;
            this.spawnMin = spawnMin;
            this.spawnMax = spawnMax;
            this.biomeIds = biomeIds;
        }

        public EntityEntry toEntry(String modId) {
            if (mEntry == null) {
                List<Biome> biomes = new ArrayList<>();
                for (String biomeId : biomeIds) {
                    Biome biome;
                    if (biomeId.contains(":")) {
                        biome = Biome.REGISTRY.getObject(new ResourceLocation(biomeId));
                    } else {
                        biome = Biome.REGISTRY.getObject(new ResourceLocation(modId, biomeId));
                        if (biome == null) {
                            Biome.REGISTRY.getObject(new ResourceLocation(biomeId));
                        }
                    }
                    if (biome != null) {
                        biomes.add(biome);
                    }
                }
                EntityEntryBuilder<Entity> builder = EntityEntryBuilder.create()
                        .id(id, network)
                        .name(name)
                        .entity(entityClass)
                        .tracker(trackerRange, updateFrequency, sendVelocityUpdate);
                if (hasEgg) {
                    builder.egg(eggColorPrimary, eggColorSecondary);
                }
                if (canSpawn) {
                    builder.spawn(spawnType, spawnWeight, spawnMin, spawnMax, biomes);
                }
                mEntry = builder.build();
            }
            return mEntry;
        }

        @Override
        public String toString() {
            String egg = hasEgg ? "eggColorPrimary=" + eggColorPrimary + ", eggColorSecondary=" + eggColorSecondary : "No Egg";
            String spawn = canSpawn ? "spawnType=" + spawnType + ", spawnWeight=" + spawnWeight + ", spawnCount=[" + spawnMin + ", " + spawnMax + "], biomeIds=" + Arrays.toString(biomeIds.toArray(new String[0])) : "No Spawn";
            return "EntityData " + entityClass.getSimpleName() + "{" +
                    "network=" + network +
                    ", id=" + id +
                    ", name=" + name +
                    ", trackerRange=" + trackerRange +
                    ", updateFrequency=" + updateFrequency +
                    ", sendVelocityUpdate=" + sendVelocityUpdate +
                    ", \n\tegg=" + egg +
                    ", \n\tspawn=" + spawn +
                    '}';
        }
    }
}
