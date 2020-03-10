package com.elementtimes.elementcore.api.event;

import com.elementtimes.elementcore.api.ECModContainer;
import com.elementtimes.elementcore.api.ECModElements;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author luqin2007
 */
public class ForgeEvent {

    private ECModContainer mContainer;

    public ForgeEvent(ECModContainer container) {
        mContainer = container;
    }

    private ECModElements elements() {
        return mContainer.elements();
    }

    @SubscribeEvent
    public void onBlock(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        elements().blocks.forEach(registry::register);
        elements().fluidBlocks.stream().map(Supplier::get).filter(Objects::nonNull).forEach(registry::register);
    }

    @SubscribeEvent
    public void onTileEntityType(RegistryEvent.Register<TileEntityType<?>> event) {
        elements().teTypes.forEach(event.getRegistry()::register);
    }

    @SubscribeEvent
    public void onItem(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        elements().items.forEach(registry::register);
        elements().blockItems.values().forEach(registry::register);
        elements().entityEggs.forEach(registry::register);
        elements().fluidBuckets.stream().map(Supplier::get).filter(Objects::nonNull).forEach(registry::register);
    }

    @SubscribeEvent
    public void onEnchantment(RegistryEvent.Register<Enchantment> event) {
        elements().enchantments.forEach(event.getRegistry()::register);
    }

    @SubscribeEvent
    public void onEntity(RegistryEvent.Register<EntityType<?>> event) {
        elements().entities.forEach(event.getRegistry()::register);
    }

    @SubscribeEvent
    public void onFluid(RegistryEvent.Register<Fluid> event) {
        elements().fluids.forEach(event.getRegistry()::register);
    }

    @SubscribeEvent
    public void onContainerType(RegistryEvent.Register<ContainerType<?>> event) {
        elements().containerTypes.forEach(event.getRegistry()::register);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
        elements().commands.forEach(dispatcher.getRoot()::addChild);
        elements().commandBuilders.forEach(dispatcher::register);
    }
}
