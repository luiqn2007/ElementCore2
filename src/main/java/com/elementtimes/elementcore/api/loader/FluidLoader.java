package com.elementtimes.elementcore.api.loader;

import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.annotation.ModFluid;
import com.elementtimes.elementcore.api.helper.FindOptions;
import com.elementtimes.elementcore.api.helper.ObjHelper;
import com.elementtimes.elementcore.api.helper.RefHelper;
import net.minecraft.block.Block;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraftforge.forgespi.language.ModFileScanData;

import java.lang.annotation.ElementType;
import java.util.Map;
import java.util.function.Supplier;

public class FluidLoader {

    public static void load(ECModElements elements) {
        loadFluid(elements);
    }

    private static void loadFluid(ECModElements elements) {
        ObjHelper.stream(elements, ModFluid.class).forEach(data -> {
            FindOptions option = new FindOptions().withReturns(Fluid.class).withTypes(ElementType.FIELD);
            ObjHelper.find(elements, data, option).ifPresent(fluid -> {
                if (fluid instanceof FlowingFluid) {
                    FlowingFluid f = (FlowingFluid) fluid;
                    addFluid(data, elements, f.getStillFluid(), f.getFlowingFluid());
                } else {
                    addFluid(data, elements, (Fluid) fluid, null);
                }
                Supplier<Block> blockSupplier = RefHelper.getter(elements, data.getAnnotationData().get("block"), Block.class);
                elements.fluidBlocks.add(() -> {
                    Block block = blockSupplier.get();
                    if (block != null) {
                        ObjHelper.setRegisterName(block, (String) data.getAnnotationData().get("name"), data, elements);
                    }
                    return block;
                });
            });
        });
    }

    private static void addFluid(ModFileScanData.AnnotationData data, ECModElements elements, Fluid still, Fluid flowing) {
        Map<String, Object> map = data.getAnnotationData();
        String name = (String) map.get("name");
        ObjHelper.setRegisterName(still, name, data, elements);
        elements.fluids.add(still);
        if (flowing != null && flowing != still && flowing.getRegistryName() == null) {
            ObjHelper.setRegisterName(flowing, (String) map.get("flowingName"), still.getRegistryName().toString() + "_flowing", elements);
            elements.fluids.add(flowing);
        }
        if (!(boolean) map.getOrDefault("noBucket", false)) {
            Supplier<Item> bucket = () -> {
                Item filledBucket = still.getFilledBucket();
                if (filledBucket != null) {
                    ObjHelper.setRegisterName(filledBucket, name, data, elements);
                }
                return filledBucket;
            };
            elements.fluidBuckets.add(bucket);
        }
    }
}
