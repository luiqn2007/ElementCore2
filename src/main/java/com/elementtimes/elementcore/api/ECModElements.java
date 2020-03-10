package com.elementtimes.elementcore.api;

import com.elementtimes.elementcore.api.annotation.result.*;
import com.elementtimes.elementcore.api.event.FMLEvent;
import com.elementtimes.elementcore.api.event.ForgeEvent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.network.NetworkManager;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.spi.AbstractLogger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.*;

/**
 * 总入口，用于注册所有事件，收集注解产物
 * @author luqin2007
 */
public class ECModElements extends AbstractLogger {

    public boolean isLoaded = false;

    /**
     * Class
     */
    public final Map<String, Class<?>> classes = new HashMap<>();

    /**
     * Block
     */
    public final List<Block> blocks = new ArrayList<>();
    public final Map<Block, Item> blockItems = new HashMap<>();
    public final List<TileEntityType<?>> teTypes = new ArrayList<>();
    public final List<TerWrapper> ters = new ArrayList<>();
    public final List<FeatureWrapper> features = new ArrayList<>();
    public final List<BlockColorWrapper> blockColors = new ArrayList<>();

    /**
     * Item
     */
    public final List<Item> items = new ArrayList<>();
    public final List<ItemColorWrapper> itemColors = new ArrayList<>();
    public final Map<String, List<Item>> itemOreNames = new HashMap<>();

    /**
     * Fluid
     */
    public final List<Fluid> fluids = new ArrayList<>();
    public final List<Supplier<Item>> fluidBuckets = new ArrayList<>();
    public final List<Supplier<Block>> fluidBlocks = new ArrayList<>();

    /**
     * Capability
     */
    public final List<CapabilityData> capabilities = new ArrayList<>();

    /**
     * Network
     */
    public final List<SimpleMessageWrapper> netSimples = new ArrayList<>();
    public final List<Object> netEvents = new ArrayList<>();
    public SimpleChannel simpleChannel;
    public EventNetworkChannel eventChannel;
    public ResourceLocation simpleChannelName, eventChannelName;

    /**
     * Enchantment
     */
    public final List<Enchantment> enchantments = new ArrayList<>();
    public final List<EnchantmentBookWrapper> enchantmentBooks = new ArrayList<>();

    /**
     * Potion
     */
    public final List<Potion> potions = new ArrayList<>();

    /**
     * GUI
     */
    public final List<ContainerType<?>> containerTypes = new ArrayList<>();
    public final List<ScreenWrapper> containerScreens = new ArrayList<>();

    /**
     * Command
     */
    public final List<CommandNode<CommandSource>> commands = new ArrayList<>();
    public final List<LiteralArgumentBuilder<CommandSource>> commandBuilders = new ArrayList<>();

    /**
     * Entity
     */
    public final List<EntityType<?>> entities = new ArrayList<>();
    public final List<EntityTypeWrapper> entityWrappers = new ArrayList<>();
    public final List<EntityRendererWrapper> entityRenders = new ArrayList<>();
    public final List<SpawnEggItem> entityEggs = new ArrayList<>();
    public final List<EntitySpawner> entitySpawns = new ArrayList<>();

    /**
     * Key
     */
    public final List<KeyWrapper> keys = new ArrayList<>();

    /**
     * Tools
     */
    public final Map<Object, ToIntFunction<ItemStack>> burnTimes = new HashMap<>();
    public final List<TooltipsWrapper> tooltips = new ArrayList<>();
    public Config config;
    public ECModContainer container;

    ECModElements(boolean debugEnable,
                  boolean netSimple, ResourceLocation netSimpleName, Supplier<String> simpleNetVersion, Predicate<String> simpleClientVersions, Predicate<String> simpleServerVersions,
                  boolean netEvent, ResourceLocation netEventName, Supplier<String> eventNetVersion, Predicate<String> eventClientVersions, Predicate<String> eventServerVersions,
                  Config config, ModContainer modContainer, Logger logger) {
        this.container = new ECModContainer(modContainer, this, debugEnable, logger);
        this.config = config;
        simpleChannelName = netSimple ? netSimpleName == null ? new ResourceLocation(modContainer.getModId(), "SimpleChannelRegisterByElementCore") : netSimpleName : null;
        simpleChannel = netSimple ? NetworkRegistry.newSimpleChannel(simpleChannelName, simpleNetVersion, simpleClientVersions, simpleServerVersions) : null;
        eventChannelName = netEvent ? netEventName == null ? new ResourceLocation(modContainer.getModId(), "EventChannelRegisterByElementCore") : netEventName : null;
        eventChannel = netEvent ? NetworkRegistry.newEventChannel(eventChannelName, eventNetVersion, eventClientVersions, eventServerVersions) : null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private boolean debugEnable = false, netSimple = true, netEvent = false;
        private Logger logger = null;
        private ResourceLocation netSimpleName = null, netEventName = null;
        private Supplier<String> simpleNetVersion = () -> "", eventNetVersion = () -> "";
        private Predicate<String> simpleClientVersions = s -> true, eventClientVersions = s -> true;
        private Predicate<String> simpleServerVersions = s -> true, eventServerVersions = s -> true;
        private Config config = new Config();

        public Builder enableDebugMessage() {
            debugEnable = true;
            return this;
        }
        public Builder disableDebugMessage() {
            debugEnable = false;
            return this;
        }
        public Builder setLogger(Logger logger) {
            this.logger = logger;
            return this;
        }
        public Builder useSimpleNetwork() {
            netSimple = true;
            return this;
        }
        public Builder noSimpleNetwork() {
            netSimple = false;
            return this;
        }
        public Builder setSimpleChannelName(ResourceLocation name) {
            netSimpleName = name;
            return this;
        }
        public Builder setSimpleChannelName(String namespace, String name) {
            netSimpleName = new ResourceLocation(namespace, name);
            return this;
        }
        public Builder setSimpleNetProtocolVersion(String version) {
            simpleNetVersion = () -> version;
            return this;
        }
        public Builder setSimpleNetProtocolVersion(Supplier<String> version) {
            simpleNetVersion = version;
            return this;
        }
        public Builder setSimpleNetClientAcceptedVersion(Predicate<String> acceptedVersion) {
            this.simpleClientVersions = acceptedVersion;
            return this;
        }
        public Builder setSimpleNetClientAcceptedVersion(String... versions) {
            this.simpleClientVersions = s -> ArrayUtils.contains(versions, s);
            return this;
        }
        public Builder useEventNetwork() {
            netEvent = true;
            return this;
        }
        public Builder noEventNetwork() {
            netEvent = false;
            return this;
        }
        public Builder setEventChannelName(ResourceLocation name) {
            netEventName = name;
            return this;
        }
        public Builder setEventChannelName(String namespace, String name) {
            netEventName = new ResourceLocation(namespace, name);
            return this;
        }
        public Builder setEventNetProtocolVersion(String version) {
            eventNetVersion = () -> version;
            return this;
        }
        public Builder setEventNetProtocolVersion(Supplier<String> version) {
            eventNetVersion = version;
            return this;
        }
        public Builder setEventNetClientAcceptedVersion(Predicate<String> acceptedVersion) {
            this.eventClientVersions = acceptedVersion;
            return this;
        }
        public Builder setEventNetClientAcceptedVersion(String... versions) {
            this.eventClientVersions = s -> ArrayUtils.contains(versions, s);
            return this;
        }
        public Builder enableOBJModel() {
            config.useOBJ = true;
            return this;
        }
        public Builder enableB3DModel() {
            config.useB3D = true;
            return this;
        }

        public ECModContainer build() {
            // newInstance
            FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();
            ModContainer container = ModLoadingContext.get().getActiveContainer();
            ECModElements elements = new ECModElements(debugEnable,
                    netSimple, netSimpleName, simpleNetVersion, simpleClientVersions, simpleServerVersions,
                    netEvent, netEventName, eventNetVersion, eventClientVersions, eventServerVersions,
                    config, container, logger);
            ECModContainer.MODS.put(container.getModId(), elements.container);
            // event
            new FMLEvent(elements.container).apply(context.getModEventBus());
            MinecraftForge.EVENT_BUS.register(new ForgeEvent(elements.container));
            return elements.container;
        }
    }

    public boolean sendTo(Object message, NetworkManager net, NetworkDirection direction) {
        if (simpleChannel == null) {
            return false;
        }
        simpleChannel.sendTo(message, net, direction);
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean sendToServer(Object message) {
        if (simpleChannel == null) {
            return false;
        }
        simpleChannel.sendToServer(message);
        return true;
    }

    public boolean sendTo(Object message, ServerPlayerEntity player) {
        return sendTo(message, player.connection.netManager, NetworkDirection.PLAY_TO_SERVER);
    }

    @OnlyIn(Dist.CLIENT)
    @Deprecated
    public boolean postTo(Consumer<ByteBuf> bufWriter) {
        return false;
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, Message message, Throwable t) {
        return container.isDebugMessageEnable();
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, CharSequence message, Throwable t) {
        return container.isDebugMessageEnable();
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Throwable t) {
        return container.isDebugMessageEnable();
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message) {
        return container.isDebugMessageEnable();
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, Object message, Throwable t) {
        return container.isDebugMessageEnable();
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object... params) {
        return container.isDebugMessageEnable();
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0) {
        return container.isDebugMessageEnable();
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1) {
        return container.isDebugMessageEnable();
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2) {
        return container.isDebugMessageEnable();
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
        return container.isDebugMessageEnable();
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
        return container.isDebugMessageEnable();
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        return container.isDebugMessageEnable();
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        return container.isDebugMessageEnable();
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        return container.isDebugMessageEnable();
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        return container.isDebugMessageEnable();
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        return container.isDebugMessageEnable();
    }

    @Override
    public void logMessage(String fqcn, Level level, Marker marker, Message message, Throwable t) {
        container.logger.log(level, marker, message, t);
    }

    @Override
    public Level getLevel() {
        return container.logger.getLevel();
    }
}
