package com.elementtimes.elementcore.api.loader;

import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.annotation.ModEntity;
import com.elementtimes.elementcore.api.annotation.result.EntityRendererWrapper;
import com.elementtimes.elementcore.api.annotation.result.EntityTypeWrapper;
import com.elementtimes.elementcore.api.annotation.part.Parts;
import com.elementtimes.elementcore.api.helper.FindOptions;
import com.elementtimes.elementcore.api.helper.ObjHelper;
import com.elementtimes.elementcore.api.helper.RefHelper;
import com.elementtimes.elementcore.api.interfaces.invoker.Invoker;
import com.elementtimes.elementcore.api.utils.CommonUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.forgespi.language.ModFileScanData;

import java.lang.annotation.ElementType;
import java.util.List;
import java.util.Map;

/**
 * @author luqin2007
 */
public class EntityLoader {

    public static void load(ECModElements elements) {
        loadEntity(elements);
        loadEntityType(elements);
        loadEggs(elements);
        loadSpawn(elements);
    }

    private static void loadEntity(ECModElements elements) {
        ObjHelper.stream(elements, ModEntity.class).forEach(data -> {
            loadEntityType(elements, data);
            if (CommonUtils.isClient()) {
                loadEntityRenderer(elements, data);
            }
        });
    }

    private static void loadEntityType(ECModElements elements, ModFileScanData.AnnotationData data) {
        Map<String, Object> objectMap = data.getAnnotationData();
        Invoker<Entity> invoker = RefHelper.invoker(elements, objectMap.get("create"), (a) -> null, EntityType.class, World.class);
        EntityClassification classification = ObjHelper.getEnum(EntityClassification.class, objectMap.get("classification"), EntityClassification.CREATURE);
        EntityType.Builder<Entity> builder = EntityType.Builder.create((type, world) -> invoker.invoke(type, world), classification);
        if ((boolean) objectMap.getOrDefault("disableSummoning", false)) {
            builder.disableSummoning();
        }
        if ((boolean) objectMap.getOrDefault("disableSerialization", false)) {
            builder.disableSerialization();
        }
        if ((boolean) objectMap.getOrDefault("immuneToFire", false)) {
            builder.immuneToFire();
        }
        builder.setShouldReceiveVelocityUpdates((boolean) objectMap.getOrDefault("velocityUpdates", true));
        int trackingRange = (int) objectMap.getOrDefault("trackerRange", -1);
        if (trackingRange >= 0) {
            builder.setTrackingRange(trackingRange);
        }
        int updateInterval = (int) objectMap.getOrDefault("updateInterval", -1);
        if (updateInterval >= 0) {
            builder.setUpdateInterval(updateInterval);
        }
        float width = (float) objectMap.getOrDefault("width", 0.6f);
        float height = (float) objectMap.getOrDefault("height", 1.8f);
        builder.size(width, height);
        Invoker<Entity> factory = RefHelper.invoker(elements, objectMap.get("clientFactory"), (a) -> null, FMLPlayMessages.SpawnEntity.class, World.class);
        builder.setCustomClientFactory((spawnEntity, world) -> factory.invoke(spawnEntity, world));
        EntityType<Entity> type = builder.build((String) objectMap.getOrDefault("id", elements.container.id() + "." + ObjHelper.getMemberName(data)));
        ObjHelper.setRegisterName(type, (String) objectMap.get("id"), data, elements);
        elements.entities.add(type);
        elements.entityWrappers.add(new EntityTypeWrapper(type, data));
    }

    @OnlyIn(Dist.CLIENT)
    private static void loadEntityRenderer(ECModElements elements, ModFileScanData.AnnotationData data) {
        ObjHelper.findClass(elements, data.getClassType()).ifPresent(entityClass -> {
            Class<?> manager = net.minecraft.client.renderer.entity.EntityRendererManager.class;
            Invoker<net.minecraft.client.renderer.entity.EntityRenderer<?>> empty = Invoker.empty();
            Invoker<net.minecraft.client.renderer.entity.EntityRenderer<?>> invoker =
                    RefHelper.invoker(elements, data.getAnnotationData().get("renderer"), empty, manager);
            if (invoker != empty) {
                elements.entityRenders.add(new EntityRendererWrapper(invoker::invoke, entityClass));
            }
        });
    }

    private static void loadEntityType(ECModElements elements) {
        ObjHelper.stream(elements, ModEntity.Type.class).forEach(data -> {
            FindOptions options = new FindOptions().withReturns(EntityType.class).withTypes(ElementType.FIELD);
            ObjHelper.find(elements, data, options).ifPresent(type -> {
                EntityType<?> t = (EntityType<?>) type;
                ObjHelper.setRegisterName(t, ObjHelper.getDefault(data), data, elements);
                elements.entities.add(t);
                elements.entityWrappers.add(new EntityTypeWrapper(t, data));
            });
        });
    }

    private static void loadEggs(ECModElements elements) {
        ObjHelper.stream(elements, ModEntity.Egg.class).forEach(data -> {
            EntityType<?> type = findEntityType(elements, data, "getter");
            if (type != null) {
                Map<String, Object> map = data.getAnnotationData();
                int primaryColor = (int) map.getOrDefault("primaryColor", 0x00000000);
                int secondaryColor = (int) map.getOrDefault("secondaryColor", 0x00000000);
                Item.Properties properties = Parts.propertiesItem((Map<String, Object>) map.get("properties"), elements);
                SpawnEggItem egg = new SpawnEggItem(type, primaryColor, secondaryColor, properties);
                elements.entityEggs.add(egg);
            }
        });
    }

    private static void loadSpawn(ECModElements elements) {
        ObjHelper.stream(elements, ModEntity.Spawn.class).forEach(data -> {
            EntityType<?> type = findEntityType(elements, data, "getter");
            if (type != null) {
                List<Map<String, Object>> spawners = ObjHelper.getDefault(data);
                for (Map<String, Object> spawner : spawners) {
                    elements.entitySpawns.add(Parts.entitySpawn(spawner, type, elements));
                }
            }
        });
    }

    private static EntityType<?> findEntityType(ECModElements elements, ModFileScanData.AnnotationData data, String getter) {
        EntityType<?> type = null;
        for (EntityTypeWrapper wrapper : elements.entityWrappers) {
            if (wrapper.match(data)) {
                type = wrapper.getEntityType();
                break;
            }
        }
        if (type == null) {
            type = RefHelper.get(elements, data.getAnnotationData().get(getter), EntityType.class).orElse(null);
        }
        return type;
    }
}
