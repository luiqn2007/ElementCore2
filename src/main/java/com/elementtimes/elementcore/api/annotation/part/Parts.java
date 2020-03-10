package com.elementtimes.elementcore.api.annotation.part;

import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.Vanilla;
import com.elementtimes.elementcore.api.annotation.enums.ValueType;
import com.elementtimes.elementcore.api.annotation.result.Config;
import com.elementtimes.elementcore.api.annotation.result.EntitySpawner;
import com.elementtimes.elementcore.api.annotation.result.FeatureWrapper;
import com.elementtimes.elementcore.api.annotation.result.TooltipsWrapper;
import com.elementtimes.elementcore.api.helper.ObjHelper;
import com.elementtimes.elementcore.api.helper.RefHelper;
import com.elementtimes.elementcore.api.interfaces.invoker.IntInvoker;
import com.elementtimes.elementcore.api.interfaces.invoker.Invoker;
import com.elementtimes.elementcore.api.interfaces.invoker.VoidInvoker;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * 除 Getter, Method 外其他类的处理方法
 * @author luqin2007
 */
public class Parts {

    public static Supplier<Biome> biome(Map<String, Object> biomeMap, ECModElements elements) {
        if (biomeMap == null || biomeMap.isEmpty()) {
            return () -> Vanilla.Biomes.PLAINS;
        }
        switch (ObjHelper.getEnum(ValueType.class, biomeMap.get("type"), ValueType.CONST)) {
            case OBJECT:
                Supplier<Biome> getter = RefHelper.getter(elements, biomeMap.get("getter"), Biome.class);
                return () -> {
                    Biome biome = getter.get();
                    if (biome == null) {
                        biome = Vanilla.Biomes.PLAINS;
                    }
                    return biome;
                };
            case METHOD:
                Invoker<Biome> invoker = RefHelper.invoker(elements, biomeMap.get("method"), (a) -> Vanilla.Biomes.PLAINS);
                return invoker::invoke;
            default:
                String name = (String) biomeMap.getOrDefault("name", "plains");
                ResourceLocation location = new ResourceLocation(name);
                return () -> ForgeRegistries.BIOMES.getValue(location);
        }
    }

    public static Material material(Map<String, Object> materialMap, ECModElements elements) {
        if (materialMap == null || materialMap.isEmpty()) {
            return Vanilla.Materials.ROCK;
        }
        switch (ObjHelper.getEnum(ValueType.class, materialMap.get("value"), ValueType.OBJECT)) {
            case OBJECT:
                Optional<? extends Material> material = RefHelper.get(elements, materialMap.get("material"), Material.class);
                return material.isPresent() ? material.get() : Material.ROCK;
            case METHOD:
                Optional<? extends Material> material2 = RefHelper.invoke(elements, materialMap.get("material2"), Material.class, new Object[0]);
                return material2.isPresent() ? material2.get() : Material.ROCK;
            default:
                MaterialColor color = MaterialColor.COLORS[MathHelper.clamp((int) materialMap.getOrDefault("colorIndex", 11), 0, 63)];
                boolean isLiquid = (boolean) materialMap.getOrDefault("isLiquid", false);
                boolean isSolid = (boolean) materialMap.getOrDefault("isSolid", true);
                boolean isBlockMovement = (boolean) materialMap.getOrDefault("isBlockMovement", true);
                boolean isOpaque = (boolean) materialMap.getOrDefault("isOpaque", true);
                boolean requiresTool = (boolean) materialMap.getOrDefault("requiresTool", false);
                boolean flammable = (boolean) materialMap.getOrDefault("flammable", false);
                boolean replaceable = (boolean) materialMap.getOrDefault("replaceable", false);
                PushReaction pushReaction = ObjHelper.getEnum(PushReaction.class, materialMap.get("pushReaction"), PushReaction.NORMAL);
                return new Material(color, isLiquid, isSolid, isBlockMovement, isOpaque, !requiresTool, flammable, replaceable, pushReaction);
        }
    }

    public static Block.Properties propertiesBlock(Map<String, Object> materialMap, ECModElements elements) {
        if (materialMap == null) {
            return Block.Properties.create(Vanilla.Materials.ROCK);
        }
        switch (ObjHelper.getEnum(ValueType.class, "type", ValueType.OBJECT)) {
            case OBJECT:
                return (Block.Properties) RefHelper.get(elements, materialMap.get("object"), Object.class)
                        .map(obj -> obj instanceof Block ? Block.Properties.from((Block) obj) : obj)
                        .map(obj -> obj instanceof Block.Properties ? obj : Vanilla.Properties.defaultBlock())
                        .get();
            case METHOD:
                return (Block.Properties) RefHelper.invoke(elements, materialMap.get("method"), Object.class, new Object[0])
                        .map(obj -> obj instanceof Block ? Block.Properties.from((Block) obj) : obj)
                        .map(obj -> obj instanceof Block.Properties ? obj : Vanilla.Properties.defaultBlock())
                        .get();
            default:
                Material material = material((Map<String, Object>) materialMap.get("material"), elements);
                Block.Properties properties;
                if (materialMap.containsKey("colorIndex")) {
                    int colorIndex = MathHelper.clamp((int) materialMap.get("colorIndex"), 0, 63);
                    properties = Block.Properties.create(material, MaterialColor.COLORS[colorIndex]);
                } else if (materialMap.containsKey("colorDye")) {
                    int colorDye = MathHelper.clamp((int) materialMap.get("colorDye"), 0, 15);
                    properties = Block.Properties.create(material, DyeColor.values()[colorDye]);
                } else {
                    properties = Block.Properties.create(material);
                }
                if ((boolean) materialMap.getOrDefault("doesNotBlockMovement", false)) {
                    properties.doesNotBlockMovement();
                }
                Float slipperiness = (Float) materialMap.get("slipperiness");
                if (slipperiness != null) {
                    properties.slipperiness(slipperiness);
                }
                if ((boolean) materialMap.getOrDefault("doesNotBlockMovement", false)) {
                    properties.doesNotBlockMovement();
                }
                RefHelper.get(elements, materialMap.get("soundType"), SoundType.class).ifPresent(properties::sound);
                Integer lightValue = (Integer) materialMap.get("lightValue");
                if (lightValue != null) {
                    properties.lightValue(lightValue);
                }
                Float hardness = (Float) materialMap.get("hardness");
                Float resistance = (Float) materialMap.get("resistance");
                if (hardness != null || resistance != null) {
                    properties.hardnessAndResistance(hardness == null ? 0f : hardness, resistance == null ? 0f : resistance);
                }
                if ((boolean) materialMap.getOrDefault("ticksRandomly", false)) {
                    properties.tickRandomly();
                }
                if ((boolean) materialMap.getOrDefault("variableOpacity", false)) {
                    properties.variableOpacity();
                }
                if (materialMap.containsKey("harvest")) {
                    Map<String, Object> harvestMap = (Map<String, Object>) materialMap.get("harvest");
                    RefHelper.get(elements, harvestMap.get("tool"), ToolType.class).ifPresent(type -> {
                        properties.harvestTool(type);
                        properties.harvestLevel((int) harvestMap.getOrDefault("level", -1));
                    });
                }
                if ((boolean) materialMap.getOrDefault("noDrops", false)) {
                    properties.noDrops();
                }
                RefHelper.get(elements, materialMap.get("loot"), Block.class).ifPresent(properties::lootFrom);
                return properties;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static Object color(Map<String, Object> colorMap, boolean forItem, ECModElements elements) {
        if (colorMap == null) {
            return null;
        }
        switch (ObjHelper.getEnum(ValueType.class, colorMap.get("type"), ValueType.NONE)) {
            case CONST:
                int color = (int) colorMap.getOrDefault("color", 0);
                return forItem
                        ? (net.minecraft.client.renderer.color.IItemColor) (a, b) -> color
                        : (net.minecraft.client.renderer.color.IBlockColor) (a, b, c, d) -> color;
            case METHOD:
                Class<?>[] types = forItem
                        ? new Class<?>[] {ItemStack.class, int.class}
                        : new Class<?>[] {BlockState.class, IEnviromentBlockReader.class, BlockPos.class, int.class};
                IntInvoker invoker = RefHelper.invoker(elements, colorMap.get("method"), 0, types);
                return forItem
                        ? (net.minecraft.client.renderer.color.IItemColor) (a, b) -> invoker.invoke(a, b)
                        : (net.minecraft.client.renderer.color.IBlockColor) (a, b, c, d) -> invoker.invoke(a, b, c, d);
            case OBJECT:
                Class<?> type = forItem
                        ? net.minecraft.client.renderer.color.IItemColor.class
                        : net.minecraft.client.renderer.color.IBlockColor.class;
                return RefHelper.get(elements, colorMap.get("obj"), type).orElse(null);
            default: return null;
        }
    }

    public static Food food(Map<String, Object> foodMap, ECModElements elements) {
        if (foodMap == null) {
            return null;
        }
        int hunger = (int) foodMap.getOrDefault("hunger", -1);
        if (hunger == -1) {
            return null;
        }
        Food.Builder foodBuilder = new Food.Builder().hunger(hunger).saturation((float) foodMap.getOrDefault("saturation", 0f));
        if ((boolean) foodMap.getOrDefault("meat", false)) {
            foodBuilder.meat();
        }
        if ((boolean) foodMap.getOrDefault("alwaysEdible", false)) {
            foodBuilder.setAlwaysEdible();
        }
        if ((boolean) foodMap.getOrDefault("fastToEat", false)) {
            foodBuilder.fastToEat();
        }
        List<Map<String, Object>> effects = (List<Map<String, Object>>) foodMap.getOrDefault("effect", Collections.emptyList());
        for (Map<String, Object> map : effects) {
            EffectInstance effectInstance = effectInstance((Map<String, Object>) map.get("instance"), elements);
            float probability = (float) map.getOrDefault("probability", 1f);
            foodBuilder.effect(effectInstance, probability);
        }
        return foodBuilder.build();
    }

    public static Item.Properties propertiesItem(Map<String, Object> propertiesMap, ECModElements elements) {
        Item.Properties properties = new Item.Properties();
        if (propertiesMap == null || propertiesMap.isEmpty()) {
            return properties;
        }
        Food food;
        Optional<? extends Food> foodOpt = RefHelper.get(elements, propertiesMap.get("foodGetter"), Food.class);
        if (foodOpt.isPresent()) {
            food = foodOpt.get();
        } else {
            food = food((Map<String, Object>) propertiesMap.get("food"), elements);
        }
        properties.food(food);
        if (propertiesMap.containsKey("maxStackSize")) {
            properties.maxStackSize((int) propertiesMap.get("maxStackSize"));
        }
        if (propertiesMap.containsKey("maxDamage")) {
            int maxDamage = (int) propertiesMap.get("maxDamage");
            if (maxDamage != -1) {
                properties.maxDamage(maxDamage);
            }
        }
        if (propertiesMap.containsKey("containerItem")) {
            RefHelper.get(elements, propertiesMap.get("containerItem"), Item.class).ifPresent(properties::containerItem);
        }
        RefHelper.get(elements, propertiesMap.get("group"), ItemGroup.class).ifPresent(properties::group);
        if (propertiesMap.containsKey("rarity")) {
            properties.rarity(ObjHelper.getEnum(Rarity.class, propertiesMap.get("rarity"), Rarity.COMMON));
        }
        if (propertiesMap.containsKey("noRepair") && (boolean) propertiesMap.getOrDefault("noRepair", false)) {
            properties.setNoRepair();
        }
        if (propertiesMap.containsKey("toolType")) {
            List<Map<String, Object>> types = (List<Map<String, Object>>) propertiesMap.getOrDefault("toolType", Collections.emptyList());
            for (Map<String, Object> typeMap : types) {
                RefHelper.get(elements, typeMap.get("tool"), ToolType.class).ifPresent(type -> {
                    int level = (int) typeMap.getOrDefault("level", 0);
                    properties.addToolType(type, level);
                });
            }
        }
        if (propertiesMap.containsKey("teisr")) {
            Supplier<Object> teisr = RefHelper.getterOrNull(elements, propertiesMap.get("teisr"), Object.class);
            if (teisr != null) {
                properties.setTEISR(() -> () -> (net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer) teisr.get());
            }
        }
        return properties;
    }

    public static ToIntFunction<ItemStack> burnTime(Map<String, Object> burnMap, Object applyTo, ECModElements elements) {
        int defValue = (int) burnMap.get("value");
        List<HashMap<String, Object>> subTimes = (List<HashMap<String, Object>>) burnMap.get("sub");
        boolean hasSubTime = subTimes != null && !subTimes.isEmpty();
        return stack -> {
            if (hasSubTime) {
                for (HashMap<String, Object> map : subTimes) {
                    int[] acceptMeta = (int[]) map.getOrDefault("damage", new int[0]);
                    if (acceptMeta.length == 0 || ArrayUtils.contains(acceptMeta, stack.getDamage())) {
                        int defBurnTime = (int) map.getOrDefault("value", -1);
                        if (defBurnTime < 0) {
                            HashMap<String, Object> methodMap = (HashMap<String, Object>) map.get("method");
                            return RefHelper.invoke(elements, methodMap, -1, new Object[]{stack}, ItemStack.class);
                        }
                        return defBurnTime;
                    }
                }
            }
            return defValue;
        };
    }

    public static EffectInstance effectInstance(Map<String, Object> effectMap, ECModElements elements) {
        Optional<? extends Effect> effectOpt = RefHelper.get(elements, effectMap.get("effect"), Effect.class);
        if (effectOpt.isPresent()) {
            Effect effect = effectOpt.get();
            int duration = (int) effectMap.getOrDefault("duration", 0);
            int amplifier = (int) effectMap.getOrDefault("amplifier", 0);
            boolean ambient = (boolean) effectMap.getOrDefault("ambient", false);
            boolean showParticles = (boolean) effectMap.getOrDefault("showParticles", true);
            return new EffectInstance(effect, duration, amplifier, ambient, showParticles);
        }
        return null;
    }

    public static Runnable invoker(Map<String, Object> invokerMap, Class<?> holder) {
        try {
            Method method = holder.getDeclaredMethod((String) invokerMap.get("value"));
            method.setAccessible(true);
            return () -> {
                try {
                    method.invoke(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        } catch (NoSuchMethodException e) {
            return () -> {};
        }
    }

    public static List<ResourceLocation> tag(Map<String, Object> tagMap, String namespace) {
        return ((List<String>) tagMap.getOrDefault("value", Collections.emptyList())).stream()
                .map(s -> s.contains(":") ? new ResourceLocation(s) : new ResourceLocation(namespace, s))
                .collect(Collectors.toList());
    }

    public static TooltipsWrapper tooltips(Map<String, Object> tooltipsMap, Predicate<ItemTooltipEvent> check, ECModElements elements) {
        if (tooltipsMap == null || tooltipsMap.isEmpty()) {
            return null;
        }
        Consumer<ItemTooltipEvent> tooltips;
        if (ObjHelper.getEnum(ValueType.class, tooltipsMap.get("type"), ValueType.CONST) == ValueType.METHOD) {
            VoidInvoker invoker = RefHelper.invoker(elements, tooltipsMap.get("method"), ItemTooltipEvent.class);
            tooltips = invoker::invoke;
        } else {
            List<String> tooltipList = (List<String>) tooltipsMap.getOrDefault("value", Collections.emptyList());
            List<TranslationTextComponent> list = tooltipList.stream().map(t -> new TranslationTextComponent(t)).collect(Collectors.toList());
            tooltips = event -> event.getToolTip().addAll(list);
        }
        return new TooltipsWrapper(tooltips, check);
    }

    public static EntitySpawner entitySpawn(Map<String, Object> spawnMap, EntityType<?> type, ECModElements elements) {
        int weight = (int) spawnMap.getOrDefault("weight", 10);
        int min = (int) spawnMap.getOrDefault("minCount", 4);
        int max = (int) spawnMap.getOrDefault("maxCount", 4);
        Biome.SpawnListEntry entry = new Biome.SpawnListEntry(type, weight, min, max);
        Supplier<Biome> biome = biome((Map<String, Object>) spawnMap.get("biome"), elements);
        EntityClassification classification = ObjHelper.getEnum(EntityClassification.class, "classification", EntityClassification.CREATURE);
        return new EntitySpawner(entry, biome, classification);
    }

    public static FeatureWrapper feature(Map<String, Object> featureMap, Block block, ECModElements elements) {
        if (featureMap == null || featureMap.isEmpty()) {
            return new FeatureWrapper(() -> null, null, null);
        }
        Supplier<Biome> biome = biome((Map<String, Object>) featureMap.get("biome"), elements);
        GenerationStage.Decoration decoration = ObjHelper.getEnum(GenerationStage.Decoration.class, featureMap.get("decoration"), GenerationStage.Decoration.UNDERGROUND_ORES);
        switch (ObjHelper.getEnum(ValueType.class, featureMap.get("type"), ValueType.CONST)) {
            case OBJECT:
                Supplier<ConfiguredFeature> getter = RefHelper.getter(elements, featureMap.get("getter"), ConfiguredFeature.class);
                return new FeatureWrapper(biome, decoration, getter);
            case METHOD:
                Invoker<ConfiguredFeature<?>> invoker = RefHelper.invoker(elements, featureMap.get("method"), (a) -> {
                    OreFeatureConfig featureConfig = (OreFeatureConfig) Vanilla.Features.featureConfig((Block) a[0]);
                    CountRangeConfig placementConfig = (CountRangeConfig) Vanilla.Placements.placementConfig((Block) a[0], Placement.COUNT_RANGE);
                    return Biome.createDecoratedFeature(Feature.ORE, featureConfig, Placement.COUNT_RANGE, placementConfig);
                }, Block.class);
                return new FeatureWrapper(biome, decoration, () -> invoker.invoke(block));
            default:
                Supplier<Feature> featureSupplier = RefHelper.getter(elements, featureMap.get("feature"), Feature.class);
                Invoker<IFeatureConfig> featureConfigInvoker = RefHelper.invoker(elements, featureMap.get("featureConfig"), (a) -> null, Block.class);
                Supplier<Placement> placementSupplier = RefHelper.getter(elements, featureMap.get("placement"), Placement.class);
                Invoker<IPlacementConfig> placementConfigInvoker = RefHelper.invoker(elements, featureMap.get("placementConfig"), (a) -> null, Block.class, Placement.class);
                return new FeatureWrapper(biome, decoration, () -> {
                    Feature feature = featureSupplier.get();
                    feature = feature == null ? Vanilla.Features.ORE : feature;
                    IFeatureConfig featureConfig = featureConfigInvoker.invoke(block);
                    featureConfig = featureConfig == null ? Vanilla.Features.featureConfig(block) : featureConfig;
                    Placement placement = placementSupplier.get();
                    placement = placement == null ? Vanilla.Placements.COUNT_RANGE : placement;
                    IPlacementConfig placementConfig = placementConfigInvoker.invoke(block, placement);
                    placementConfig = placementConfig == null ? Vanilla.Placements.placementConfig(block, placement) : placementConfig;
                    return Biome.createDecoratedFeature(feature, featureConfig, placement, placementConfig);
                });
        }
    }
}
