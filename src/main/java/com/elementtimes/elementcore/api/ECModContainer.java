package com.elementtimes.elementcore.api;

import com.elementtimes.elementcore.api.loader.CommonLoader;
import net.minecraftforge.fml.ModContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 将当前 mod 的信息
 * @author luqin2007
 */
public class ECModContainer {
    public static final Map<String, ECModContainer> MODS = new LinkedHashMap<>();

    public final ModContainer mod;
    public Logger logger;
    public final ECModElements elements;
    private boolean debugEnable;

    public ECModContainer(ModContainer mod, ECModElements container, boolean debugEnable, Logger logger) {
        this.mod = mod;
        this.logger = logger == null ? LogManager.getLogger(mod.getModInfo().getDisplayName()) : logger;
        this.elements = container;
        this.debugEnable = debugEnable;
    }

    public String name() {
        return mod.getModInfo().getDisplayName();
    }

    public String id() {
        return mod.getModId();
    }

    public ArtifactVersion version() {
        return mod.getModInfo().getVersion();
    }

    public boolean isDebugMessageEnable() {
        return debugEnable;
    }

    public void enableDebugMessage() {
        debugEnable = true;
    }

    public void disableDebugMessage() {
        debugEnable = false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name(), id(), version());
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ECModContainer)
                && name().equals(((ECModContainer) obj).name())
                && id().equals(((ECModContainer) obj).id())
                && version().equals(((ECModContainer) obj).version());
    }

    public ECModElements elements() {
        if (!elements.isLoaded) {
            CommonLoader.load(elements);
            elements.isLoaded = true;
        }
        return elements;
    }
}
