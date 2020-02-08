package com.elementtimes.elementcore.api.common.event;

import com.elementtimes.elementcore.api.common.ECModElements;
import com.elementtimes.elementcore.api.common.ECUtils;
import com.elementtimes.elementcore.common.block.EnergyBox;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 注解注册
 *
 * @author luqin2007
 */
public class ForgeRegister {
    private static Map<ResourceLocation, Class<? extends TileEntity>> tileEntities = new HashMap<>();

    private ECModElements mElements;

    public ForgeRegister(ECModElements elements) {
        mElements = elements;
    }

    @SubscribeEvent
    public void registerBlock(RegistryEvent.Register<Block> event) {
        ECUtils.common.runWithModActive(mElements.container.mod, () -> {
            IForgeRegistry<Block> registry = event.getRegistry();
            if (mElements.blocks != null) {
                mElements.blocks.values().forEach(registry::register);
            }
            if (mElements.fluidBlocks != null) {
                mElements.fluidBlocks.keySet().forEach(fluid -> registry.register(fluid.getBlock()));
            }
        }, event);
    }

    @SubscribeEvent
    public void registerItem(RegistryEvent.Register<Item> event) {
        ECUtils.common.runWithModActive(mElements.container.mod, () -> {
            IForgeRegistry<Item> registry = event.getRegistry();
            mElements.items.values().forEach(registry::register);
            mElements.blocks.values().forEach(block -> {
                ItemBlock itemBlock = new ItemBlock(block);
                //noinspection ConstantConditions
                itemBlock.setRegistryName(block.getRegistryName());
                registry.register(itemBlock);
                if (mElements.blockTileEntities != null && mElements.blockTileEntities.containsKey(block)) {
                    ResourceLocation res =
                            new ResourceLocation(mElements.container.id(), mElements.blockTileEntities.get(block).left);
                    Class<? extends TileEntity> te = mElements.blockTileEntities.get(block).right;
                    if (!tileEntities.values().contains(te)) {
                        tileEntities.put(res, te);
                        GameRegistry.registerTileEntity(te, res);
                    }
                }
            });

            mElements.blockOreDictionaries.forEach((oreName, blocks) -> {
                for (Block block : blocks) {
                    mElements.container.warn("[Block]OreName: " + block.getRegistryName() + " = " + oreName);
                    OreDictionary.registerOre(oreName, block);
                }
            });

            mElements.itemOreDictionaries.forEach((oreName, items) -> {
                for (Item item : items) {
                    mElements.container.warn("[Item]OreName: " + item.getRegistryName() + " = " + oreName);
                    OreDictionary.registerOre(oreName, item);
                }
            });
        }, event);
    }

    @SubscribeEvent
    public void registerRecipe(RegistryEvent.Register<IRecipe> event) {
        ECUtils.common.runWithModActive(mElements.container.mod, () -> {
            IForgeRegistry<IRecipe> registry = event.getRegistry();
            mElements.recipes.forEach(getter ->
                    Arrays.stream(getter.get()).filter(Objects::nonNull).forEach(registry::register));
        }, event);
    }

    @SubscribeEvent
    public void registerEnchantment(RegistryEvent.Register<Enchantment> event) {
        ECUtils.common.runWithModActive(mElements.container.mod, () -> {
            IForgeRegistry<Enchantment> registry = event.getRegistry();
            for (Enchantment enchantment : mElements.enchantments) {
                registry.register(enchantment);
            }
        }, event);
    }

    @SubscribeEvent
    public void registerPotion(RegistryEvent.Register<Potion> event) {
        ECUtils.common.runWithModActive(mElements.container.mod, () -> {
            IForgeRegistry<Potion> registry = event.getRegistry();
            for (Potion potion : mElements.potions) {
                registry.register(potion);
            }
        }, event);
    }
}
