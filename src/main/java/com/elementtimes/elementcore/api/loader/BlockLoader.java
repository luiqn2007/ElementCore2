package com.elementtimes.elementcore.api.loader;

import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.annotation.ModBlock;
import com.elementtimes.elementcore.api.annotation.enums.ValueType;
import com.elementtimes.elementcore.api.annotation.part.Parts;
import com.elementtimes.elementcore.api.annotation.result.BlockColorWrapper;
import com.elementtimes.elementcore.api.annotation.result.TooltipsWrapper;
import com.elementtimes.elementcore.api.helper.FindOptions;
import com.elementtimes.elementcore.api.helper.ObjHelper;
import com.elementtimes.elementcore.api.helper.RefHelper;
import com.elementtimes.elementcore.api.utils.CommonUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author luqin2007
 */
public class BlockLoader {

    public static void load(ECModElements elements) {
        loadBlock(elements);
    }

    private static void loadBlock(ECModElements elements) {
        ObjHelper.stream(elements, ModBlock.class).forEach(data -> {
            String name = ObjHelper.getDefault(data);
            // block
            Map<String, Object> dataMap = data.getAnnotationData();
            FindOptions options = new FindOptions().withReturns(Block.class)
                    .addParameterObjects(() -> new Object[0])
                    .addParameterObjects(() ->
                            new Object[] {Parts.propertiesBlock((Map<String, Object>) dataMap.get("block"), elements)}, Block.Properties.class);
            Block block = (Block) ObjHelper.find(elements, data, options).orElseGet(() -> new Block(Block.Properties.create(Material.ROCK)));
            ObjHelper.setRegisterName(block, name, data, elements);
            elements.blocks.add(block);
            loadBlockItem(elements, block, dataMap);
            loadBlockFeature(elements, block, dataMap);
            loadBlockTooltips(elements, block, dataMap);
            if (CommonUtils.isClient()) {
                loadBlockColor(elements, block, dataMap);
            }
        });
    }

    private static void loadBlockItem(ECModElements elements, Block block, Map<String, Object> dataMap) {
        switch (ObjHelper.getEnum(ValueType.class, dataMap.get("itemType"), ValueType.CONST)) {
            case OBJECT:
                RefHelper.get(elements, dataMap.get("itemObj"), Item.class).ifPresent(item -> {
                    ObjHelper.setRegisterName(block, item);
                    elements.blockItems.put(block, item);
                });
                break;
            case NONE:
                break;
            default:
                Item.Properties properties = Parts.propertiesItem((Map<String, Object>) dataMap.get("item"), elements);
                BlockItem item = new BlockItem(block, properties);
                ObjHelper.setRegisterName(block, item);
                elements.blockItems.put(block, item);
        }
    }

    private static void loadBlockFeature(ECModElements elements, Block block, Map<String, Object> dataMap) {
        List<Map<String, Object>> features = (List<Map<String, Object>>) dataMap.getOrDefault("features", Collections.emptyList());
        for (Map<String, Object> feature : features) {
            elements.features.add(Parts.feature(feature, block, elements));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void loadBlockColor(ECModElements elements, Block block, Map<String, Object> dataMap) {
        Object blockColor = Parts.color((Map<String, Object>) dataMap.get("blockColor"), false, elements);
        Object itemColor = Parts.color((Map<String, Object>) dataMap.get("itemColor"), false, elements);
        if (blockColor != null || itemColor != null) {
            elements.blockColors.add(new BlockColorWrapper(block, itemColor, blockColor));
        }
    }

    private static void loadBlockTooltips(ECModElements elements, Block block, Map<String, Object> dataMap) {
        Predicate<ItemTooltipEvent> p = event -> event.getItemStack().getItem().asItem() == block.asItem();
        TooltipsWrapper tooltips = Parts.tooltips((Map<String, Object>) dataMap.get("tooltips"), p, elements);
        if (tooltips != null) {
            elements.tooltips.add(tooltips);
        }
    }
}
