package com.elementtimes.elementcore.api.annotation.part;

import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.annotation.enums.ValueType;
import com.elementtimes.elementcore.api.helper.ObjHelper;
import com.elementtimes.elementcore.api.helper.RefHelper;
import com.elementtimes.elementcore.api.interfaces.invoker.IntInvoker;
import com.elementtimes.elementcore.api.interfaces.invoker.Invoker;
import com.elementtimes.elementcore.api.misc.BlockFeatureWrapper;
import com.elementtimes.elementcore.api.misc.EntitySpawnWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.*;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Supplier;

/**
 * 除 Getter, Method 外其他类的处理方法
 * @author luqin2007
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class Parts {

    /**
     * @see com.elementtimes.elementcore.api.annotation.part.Biome
     */
    public static Supplier<Optional<Biome>> biome(Object biome, Object annotatedObj, ECModElements elements) {
        Map<String, Object> map = ObjHelper.getAnnotationMap(biome);
        if (map == null) {
            return Optional::empty;
        }
        switch (ObjHelper.getEnum(ValueType.class, map.get("type"), ValueType.VALUE)) {
            case OBJECT:
                Supplier<Biome> getter = RefHelper.getter(elements, map.get("object"), Biome.class);
                return () -> Optional.ofNullable(getter.get());
            case METHOD:
                Invoker<Biome> invoker = RefHelper.invoker(elements, map.get("method"), (a) -> null, Object.class);
                return () -> Optional.ofNullable(invoker.invoke(annotatedObj));
            case VALUE:
                String name = (String) map.getOrDefault("value", "");
                ResourceLocation location = new ResourceLocation(name);
                return () -> Optional.ofNullable(ForgeRegistries.BIOMES.getValue(location));
            default: return Optional::empty;
        }
    }

    /**
     * @see com.elementtimes.elementcore.api.annotation.part.Material
     */
    public static Optional<Material> material(Object material, ECModElements elements) {
        Map<String, Object> map = ObjHelper.getAnnotationMap(material);
        if (map == null) {
            return Optional.empty();
        }
        switch (ObjHelper.getEnum(ValueType.class, map.get("type"), ValueType.OBJECT)) {
            case OBJECT:
                return RefHelper.get(elements, map.get("object"), Material.class);
            case METHOD:
                return RefHelper.invoke(elements, map.get("method"), Material.class, new Object[0]);
            case VALUE:
                MaterialColor color = MaterialColor.COLORS[MathHelper.clamp((int) map.getOrDefault("colorIndex", 11), 0, 63)];
                boolean isLiquid = (boolean) map.getOrDefault("isLiquid", false);
                boolean isSolid = (boolean) map.getOrDefault("isSolid", true);
                boolean isBlockMovement = (boolean) map.getOrDefault("isBlockMovement", true);
                boolean isOpaque = (boolean) map.getOrDefault("isOpaque", true);
                boolean requiresTool = (boolean) map.getOrDefault("requiresTool", false);
                boolean flammable = (boolean) map.getOrDefault("flammable", false);
                boolean replaceable = (boolean) map.getOrDefault("replaceable", false);
                PushReaction pushReaction = ObjHelper.getEnum(PushReaction.class, map.get("pushReaction"), PushReaction.NORMAL);
                Material m = new Material(color, isLiquid, isSolid, isBlockMovement, isOpaque, !requiresTool, flammable, replaceable, pushReaction);
                return Optional.of(m);
            default:
                return Optional.empty();
        }
    }

    /**
     * @see BlockProps
     */
    public static Optional<Block.Properties> propertiesBlock(Object prop, ECModElements elements) {
        Map<String, Object> map = ObjHelper.getAnnotationMap(prop);
        if (map == null) {
            return Optional.empty();
        }
        switch (ObjHelper.getEnum(ValueType.class, map.get("type"), ValueType.OBJECT)) {
            case OBJECT:
                return RefHelper.get(elements, map.get("object"), Object.class)
                        .map(obj -> obj instanceof Block ? Block.Properties.from((Block) obj) : obj)
                        .filter(obj -> obj instanceof Block.Properties).map(o -> (Block.Properties) o);
            case METHOD:
                return RefHelper.invoke(elements, map.get("method"), Object.class, new Object[0])
                        .map(obj -> obj instanceof Block ? Block.Properties.from((Block) obj) : obj)
                        .filter(obj -> obj instanceof Block.Properties).map(o -> (Block.Properties) o);
            case VALUE:
                Optional<Material> materialOptional = material(map.get("material"), elements);
                if (materialOptional.isPresent()) {
                    Block.Properties properties;
                    if (map.containsKey("colorIndex")) {
                        int colorIndex = MathHelper.clamp((int) map.get("colorIndex"), 0, 63);
                        properties = Block.Properties.create(materialOptional.get(), MaterialColor.COLORS[colorIndex]);
                    } else if (map.containsKey("colorDye")) {
                        int colorDye = MathHelper.clamp((int) map.get("colorDye"), 0, 15);
                        properties = Block.Properties.create(materialOptional.get(), DyeColor.values()[colorDye]);
                    } else {
                        properties = Block.Properties.create(materialOptional.get());
                    }
                    if ((boolean) map.getOrDefault("doesNotBlockMovement", false)) {
                        properties.doesNotBlockMovement();
                    }
                    Float slipperiness = (Float) map.get("slipperiness");
                    if (slipperiness != null) {
                        properties.slipperiness(slipperiness);
                    }
                    if ((boolean) map.getOrDefault("doesNotBlockMovement", false)) {
                        properties.doesNotBlockMovement();
                    }
                    RefHelper.get(elements, map.get("soundType"), SoundType.class).ifPresent(properties::sound);
                    Integer lightValue = (Integer) map.get("lightValue");
                    if (lightValue != null) {
                        properties.lightValue(lightValue);
                    }
                    Float hardness = (Float) map.get("hardness");
                    Float resistance = (Float) map.get("resistance");
                    if (hardness != null || resistance != null) {
                        properties.hardnessAndResistance(hardness == null ? 0f : hardness, resistance == null ? 0f : resistance);
                    }
                    if ((boolean) map.getOrDefault("ticksRandomly", false)) {
                        properties.tickRandomly();
                    }
                    if ((boolean) map.getOrDefault("variableOpacity", false)) {
                        properties.variableOpacity();
                    }
                    if (map.containsKey("harvest")) {
                        Map<String, Object> harvestMap = (Map<String, Object>) map.get("harvest");
                        RefHelper.get(elements, harvestMap.get("tool"), ToolType.class).ifPresent(type -> {
                            properties.harvestTool(type);
                            properties.harvestLevel((int) harvestMap.getOrDefault("level", -1));
                        });
                    }
                    if ((boolean) map.getOrDefault("noDrops", false)) {
                        properties.noDrops();
                    }
                    RefHelper.get(elements, map.get("loot"), Block.class).ifPresent(properties::lootFrom);
                    return Optional.of(properties);
                }
                return Optional.empty();
            default:
                return Optional.empty();
        }
    }

    /**
     * @see Color
     */
    @OnlyIn(Dist.CLIENT)
    public static Optional<Object> color(Object color, boolean forItem, ECModElements elements) {
        Map<String, Object> map = ObjHelper.getAnnotationMap(color);
        if (map == null) {
            return Optional.empty();
        }
        switch (ObjHelper.getEnum(ValueType.class, map.get("type"), ValueType.NONE)) {
            case VALUE:
                int cl = (int) map.getOrDefault("value", 0);
                Object ov = forItem
                        ? (net.minecraft.client.renderer.color.IItemColor) (a, b) -> cl
                        : (net.minecraft.client.renderer.color.IBlockColor) (a, b, c, d) -> cl;
                return Optional.of(ov);
            case METHOD:
                Class<?>[] types = forItem
                        ? new Class<?>[] {ItemStack.class, int.class}
                        : new Class<?>[] {BlockState.class, IEnviromentBlockReader.class, BlockPos.class, int.class};
                IntInvoker invoker = RefHelper.invoker(elements, map.get("method"), 0, types);
                Object om = forItem
                        ? (net.minecraft.client.renderer.color.IItemColor) (a, b) -> invoker.invoke(a, b)
                        : (net.minecraft.client.renderer.color.IBlockColor) (a, b, c, d) -> invoker.invoke(a, b, c, d);
                return Optional.of(om);
            case OBJECT:
                Class<?> type = forItem
                        ? net.minecraft.client.renderer.color.IItemColor.class
                        : net.minecraft.client.renderer.color.IBlockColor.class;
                return RefHelper.get(elements, map.get("object"), type).map(o -> (Object) o);
            default: return Optional.empty();
        }
    }

    /**
     * @see Food
     */
    public static Optional<net.minecraft.item.Food> food(Object food, ECModElements elements) {
        Map<String, Object> map = ObjHelper.getAnnotationMap(food);
        if (map == null) {
            return Optional.empty();
        }
        int hunger = (int) map.getOrDefault("hunger", -1);
        if (hunger == -1) {
            return Optional.empty();
        }
        net.minecraft.item.Food.Builder foodBuilder =
                new net.minecraft.item.Food.Builder().hunger(hunger).saturation((float) map.getOrDefault("saturation", 0f));
        if ((boolean) map.getOrDefault("meat", false)) {
            foodBuilder.meat();
        }
        if ((boolean) map.getOrDefault("alwaysEdible", false)) {
            foodBuilder.setAlwaysEdible();
        }
        if ((boolean) map.getOrDefault("fastToEat", false)) {
            foodBuilder.fastToEat();
        }
        List<Map<String, Object>> effects = (List<Map<String, Object>>) map.getOrDefault("effect", Collections.emptyList());
        for (Map<String, Object> e : effects) {
            effectInstance(e.get("instance"), elements).ifPresent(effectInstance -> {
                float probability = (float) e.getOrDefault("probability", 1f);
                foodBuilder.effect(effectInstance, probability);
            });
        }
        return Optional.of(foodBuilder.build());
    }

    /**
     * @see ItemProps
     */
    public static Optional<Item.Properties> propertiesItem(Object prop, ECModElements elements) {
        Map<String, Object> map = ObjHelper.getAnnotationMap(prop);
        if (map == null) {
            return Optional.empty();
        }
        Item.Properties properties = new Item.Properties();
        Optional<net.minecraft.item.Food> food = RefHelper.get(elements, map.get("foodGetter"), net.minecraft.item.Food.class);
        if (food.isPresent()) {
            properties.food(food.get());
        } else {
            food(map.get("food"), elements).ifPresent(properties::food);
        }
        if (map.containsKey("maxDamage")) {
            int maxDamage = (int) map.get("maxDamage");
            if (maxDamage != -1) {
                properties.maxDamage(maxDamage);
            }
        }
        if (map.containsKey("containerItem")) {
            RefHelper.get(elements, map.get("containerItem"), Item.class).ifPresent(properties::containerItem);
        }
        RefHelper.get(elements, map.get("group"), ItemGroup.class).ifPresent(properties::group);
        if (map.containsKey("rarity")) {
            properties.rarity(ObjHelper.getEnum(Rarity.class, map.get("rarity"), Rarity.COMMON));
        }
        if (map.containsKey("noRepair") && (boolean) map.getOrDefault("noRepair", false)) {
            properties.setNoRepair();
        }
        if (map.containsKey("toolType")) {
            List<Map<String, Object>> types = (List<Map<String, Object>>) map.getOrDefault("toolType", Collections.emptyList());
            for (Map<String, Object> typeMap : types) {
                RefHelper.get(elements, typeMap.get("tool"), ToolType.class).ifPresent(type -> {
                    int level = (int) typeMap.getOrDefault("level", 0);
                    properties.addToolType(type, level);
                });
            }
        }
        if (map.containsKey("teisr")) {
            Supplier<Object> teisr = RefHelper.getterOrNull(elements, map.get("teisr"), Object.class);
            if (teisr != null) {
                properties.setTEISR(() -> () -> (net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer) teisr.get());
            }
        }
        return Optional.of(properties);
    }

    /**
     * @see com.elementtimes.elementcore.api.annotation.part.EffectInstance
     */
    public static Optional<EffectInstance> effectInstance(Object effect, ECModElements elements) {
        Map<String, Object> map = ObjHelper.getAnnotationMap(effect);
        if (map == null) {
            return Optional.empty();
        }
        Optional<? extends Effect> effectOpt = RefHelper.get(elements, map.get("effect"), Effect.class);
        if (effectOpt.isPresent()) {
            Effect e = effectOpt.get();
            int duration = (int) map.getOrDefault("duration", 0);
            int amplifier = (int) map.getOrDefault("amplifier", 0);
            boolean ambient = (boolean) map.getOrDefault("ambient", false);
            boolean showParticles = (boolean) map.getOrDefault("showParticles", true);
            EffectInstance instance = new EffectInstance(e, duration, amplifier, ambient, showParticles);
            return Optional.of(instance);
        }
        return Optional.empty();
    }

    /**
     * @see EntitySpawn
     */
    public static Optional<EntitySpawnWrapper> entitySpawn(Object spawn, Object annotatedObj, EntityType<?> type, Biome defBiome, ECModElements elements) {
        Map<String, Object> map = ObjHelper.getAnnotationMap(spawn);
        if (map == null || type == null) {
            return Optional.empty();
        }
        Supplier<Optional<Biome>> biome = biome(map.get("biome"), annotatedObj, elements);
        int weight = (int) map.getOrDefault("weight", 10);
        int min = (int) map.getOrDefault("minCount", 4);
        int max = (int) map.getOrDefault("maxCount", 4);
        Biome.SpawnListEntry entry = new Biome.SpawnListEntry(type, weight, min, max);
        EntityClassification classification = ObjHelper.getEnum(EntityClassification.class, map.get("classification"), EntityClassification.CREATURE);
        return Optional.of(new EntitySpawnWrapper(entry, biome, classification));
    }

    /**
     * @see com.elementtimes.elementcore.api.annotation.part.Feature
     */
    public static Optional<BlockFeatureWrapper> feature(Object feature, Object annotatedObj, ECModElements elements, Supplier<Collection<Block>> blocks) {
        Map<String, Object> map = ObjHelper.getAnnotationMap(feature);
        if (map == null) {
            return Optional.empty();
        }
        Supplier<Optional<Biome>> biome = biome(map.get("biome"), annotatedObj, elements);
        GenerationStage.Decoration decoration = ObjHelper.getEnum(GenerationStage.Decoration.class, map.get("decoration"), GenerationStage.Decoration.UNDERGROUND_ORES);
        switch (ObjHelper.getEnum(ValueType.class, map.get("type"), ValueType.VALUE)) {
            case OBJECT:
                Supplier<ConfiguredFeature> getterObj = RefHelper.getter(elements, map.get("getter"), ConfiguredFeature.class);
                Supplier<ConfiguredFeature[]> getterArr = RefHelper.getter(elements, map.get("getter"), ConfiguredFeature[].class);
                return Optional.of(new BlockFeatureWrapper(biome, decoration, () -> {
                    ConfiguredFeature<?> f = getterObj.get();
                    if (f != null) {
                        return new ConfiguredFeature[] {f};
                    } else {
                        return getterArr.get();
                    }
                }));
            case METHOD:
                Invoker<ConfiguredFeature<?>> invokerObj = RefHelper.invoker(elements, map.get("method"), (a) -> null, Object.class);
                Invoker<ConfiguredFeature<?>[]> invokerArr = RefHelper.invoker(elements, map.get("method"), (a) -> null, Object.class);
                return Optional.of(new BlockFeatureWrapper(biome, decoration, () -> {
                    ConfiguredFeature<?> f = invokerObj.invoke(annotatedObj);
                    if (f != null) {
                        return new ConfiguredFeature[] {f};
                    } else {
                        return invokerArr.invoke(annotatedObj);
                    }
                }));
            case VALUE:
                Supplier<Feature> featureSupplier = RefHelper.getter(elements, map.get("feature"), Feature.class);
                Invoker<IFeatureConfig> featureConfigInvoker = RefHelper.invoker(elements, map.get("featureConfig"), (a) -> null, Block.class, Feature.class);
                Supplier<Placement> placementSupplier = RefHelper.getter(elements, map.get("placement"), Placement.class);
                Invoker<IPlacementConfig> placementConfigInvoker = RefHelper.invoker(elements, map.get("placementConfig"), (a) -> null, Block.class, Placement.class);
                BlockFeatureWrapper wrapperFeature = new BlockFeatureWrapper(biome, decoration, () -> {
                    List<ConfiguredFeature> list = new ArrayList<>();
                    for (Block block : blocks.get()) {
                        Feature f = featureSupplier.get();
                        Placement placement = placementSupplier.get();
                        IFeatureConfig featureConfig = featureConfigInvoker.invoke(block, f);
                        IPlacementConfig placementConfig = placementConfigInvoker.invoke(block, placement);
                        list.add(Biome.createDecoratedFeature(f, featureConfig, placement, placementConfig));
                    }
                    return list.toArray(new ConfiguredFeature[0]);
                });
                return Optional.of(wrapperFeature);
            default: return Optional.empty();

        }
    }
}
