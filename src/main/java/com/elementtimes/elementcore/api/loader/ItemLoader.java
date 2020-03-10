package com.elementtimes.elementcore.api.loader;

import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.annotation.ModItem;
import com.elementtimes.elementcore.api.annotation.part.Parts;
import com.elementtimes.elementcore.api.annotation.result.ItemColorWrapper;
import com.elementtimes.elementcore.api.annotation.result.TooltipsWrapper;
import com.elementtimes.elementcore.api.helper.FindOptions;
import com.elementtimes.elementcore.api.helper.ObjHelper;
import com.elementtimes.elementcore.api.utils.CommonUtils;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import java.lang.annotation.ElementType;
import java.util.Map;
import java.util.function.Predicate;

public class ItemLoader {

    public static void load(ECModElements elements) {
        loadItem(elements);
    }

    private static void loadItem(ECModElements elements) {
        ObjHelper.stream(elements, ModItem.class).forEach(data -> {
            Map<String, Object> map = data.getAnnotationData();
            FindOptions options = new FindOptions().withReturns(Item.class).withTypes(ElementType.FIELD, ElementType.TYPE)
                    .addParameterObjects(() -> new Object[0])
                    .addParameterObjects(() -> new Object[] {
                            Parts.propertiesItem((Map<String, Object>) map.get("properties"), elements)
                    }, Item.Properties.class);
            ObjHelper.find(elements, data, options).ifPresent(obj -> {
                Item item = (Item) obj;
                String name = ObjHelper.getDefault(data);
                ObjHelper.setRegisterName(item, name, data, elements);
                elements.items.add(item);
                loadItemTooltips(elements, item, map);
                if (CommonUtils.isClient()) {
                    loadItemColor(elements, item, map);
                }
            });
        });
    }

    @OnlyIn(Dist.CLIENT)
    private static void loadItemColor(ECModElements elements, Item item, Map<String, Object> dataMap) {
        Object color = Parts.color((Map<String, Object>) dataMap.get("color"), true, elements);
        elements.itemColors.add(new ItemColorWrapper(item, color));
    }

    private static void loadItemTooltips(ECModElements elements, Item item, Map<String, Object> dataMap) {
        Predicate<ItemTooltipEvent> p = event -> event.getItemStack().getItem().asItem() == item.asItem();
        TooltipsWrapper tooltips = Parts.tooltips((Map<String, Object>) dataMap.get("tooltips"), p, elements);
        if (tooltips != null) {
            elements.tooltips.add(tooltips);
        }
    }
}
