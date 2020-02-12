package com.elementtimes.elementcore.api.common.loader;

import com.elementtimes.elementcore.api.annotation.enums.GenType;
import com.elementtimes.elementcore.api.annotation.ModBlock;
import com.elementtimes.elementcore.api.common.ECModElements;
import com.elementtimes.elementcore.api.common.ECUtils;
import com.elementtimes.elementcore.api.common.helper.ObjHelper;
import com.elementtimes.elementcore.api.common.helper.RefHelper;
import com.elementtimes.elementcore.api.template.SimpleOreGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.discovery.asm.ModAnnotation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockLoader {

    public static void load(ECModElements elements) {
        loadBlock(elements);
        loadBlockTileEntity(elements);
        loadBlockHarvestLevel(elements);
        loadBlockWorldGenerator(elements);
    }

    private static void loadBlock(ECModElements elements) {
        ObjHelper.stream(elements, ModBlock.class).forEach(data -> {
            ObjHelper.findClass(elements, data.getClassName()).ifPresent(aClass -> {
                Map<String, Object> info = data.getAnnotationInfo();
                Block block = newBlock(elements, data.getClassName(), data.getObjectName(),
                        (String) info.get("registerName"), (String) info.get("unlocalizedName"));
                ObjHelper.findTab(elements, (String) info.get("creativeTabKey")).ifPresent(block::setCreativeTab);
            });
        });
    }

    private static void loadBlockTileEntity(ECModElements elements) {
        ObjHelper.stream(elements, ModBlock.TileEntity.class).forEach(data -> {
            ObjHelper.find(elements, Block.class, data).ifPresent(block -> {
                Map<String, Object> teInfo = data.getAnnotationInfo();
                String name2;
                try {
                    name2 = (String) teInfo.getOrDefault("name", block.getRegistryName().getResourcePath());
                } catch (NullPointerException e) {
                    name2 = data.getObjectName();
                }
                String name = name2;
                Type type = ObjHelper.getDefault(data);
                ObjHelper.<TileEntity>findClass(elements, type.getClassName())
                        .ifPresent(aClass -> elements.blockTileEntities.put(block, ImmutablePair.of(name, aClass)));
            });
        });
    }

    private static void loadBlockHarvestLevel(ECModElements elements) {
        ObjHelper.stream(elements, ModBlock.HarvestLevel.class).forEach(data -> {
            ObjHelper.find(elements, Block.class, data).ifPresent(block -> {
                Map<String, Object> info = data.getAnnotationInfo();
                ModAnnotation.EnumHolder toolClass = (ModAnnotation.EnumHolder) info.getOrDefault("toolClass", "pickaxe");
                int level = (int) info.getOrDefault("level", 2);
                block.setHarvestLevel(toolClass.getValue(), level);
            });
        });
    }

    private static void loadBlockWorldGenerator(ECModElements elements) {
        ObjHelper.stream(elements, ModBlock.WorldGen.class).forEach(data -> {
            ObjHelper.find(elements, Block.class, data).ifPresent(block -> {
                Map<String, Object> info = data.getAnnotationInfo();
                int yRange = (int) info.getOrDefault("YRange", 48);
                int yMin = (int) info.getOrDefault("YMin", 16);
                int count = (int) info.getOrDefault("count", 8);
                int times = (int) info.getOrDefault("times", 6);
                float probability = (float) info.getOrDefault("probability", 0.6f);
                int[] dimBlackList = (int[]) info.getOrDefault("dimBlackList", new int[0]);
                int[] dimWhiteList = (int[]) info.getOrDefault("dimWhiteList", new int[0]);
                GenType type = (GenType) info.getOrDefault("type", GenType.Ore);
                List<WorldGenerator> worldGeneratorList = elements.blockWorldGen.getOrDefault(type, new ArrayList<>());
                worldGeneratorList.add(new SimpleOreGenerator(yRange, yMin, count, times, probability,
                        dimBlackList, dimWhiteList, block.getDefaultState()));
                elements.blockWorldGen.put(type, worldGeneratorList);
            });
        });
        ObjHelper.stream(elements, ModBlock.WorldGenObj.class).forEach(data -> {
            ObjHelper.find(elements, Block.class, data).ifPresent(block -> {
                ModAnnotation.EnumHolder typeValue = (ModAnnotation.EnumHolder) data.getAnnotationInfo().get("type");
                GenType type;
                if (typeValue == null) {
                    type = GenType.Ore;
                } else {
                    type = GenType.valueOf(typeValue.getValue());
                }
                RefHelper.get(elements, ObjHelper.getDefault(data), WorldGenerator.class).ifPresent(generator -> {
                    elements.blockWorldGen.computeIfAbsent(type, t -> new ArrayList<>()).add(generator);
                });
            });
        });
    }

    private static Block newBlock(ECModElements elements, String className, String objectName,
                                  String registerName, String unlocalizedName) {
        Block block = ObjHelper.findClass(elements, className).
                flatMap(aClass -> ECUtils.reflect.get(aClass, objectName, null, Block.class, elements)).
                orElseGet(() -> new Block(Material.ROCK));
        String register = StringUtils.isNullOrEmpty(registerName) ? objectName : registerName;
        if (block.getRegistryName() == null) {
            if (register.contains(":")) {
                block.setRegistryName(new ResourceLocation(register.toLowerCase()));
            } else {
                block.setRegistryName(new ResourceLocation(elements.container.id(), register.toLowerCase()));
            }
        }
        if ("tile.null".equals(block.getUnlocalizedName())) {
            if (StringUtils.isNullOrEmpty(unlocalizedName)) {
                block.setUnlocalizedName(elements.container.id() + "." + objectName.toLowerCase());
            } else {
                block.setUnlocalizedName(elements.container.id() + "." + unlocalizedName.toLowerCase());
            }
        }
        elements.blocks.add(block);
        return block;
    }
}