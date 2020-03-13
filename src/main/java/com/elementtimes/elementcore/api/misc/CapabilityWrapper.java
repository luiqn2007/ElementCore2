package com.elementtimes.elementcore.api.misc;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.function.Supplier;

public class CapabilityWrapper {
    public final Class typeInterface;
    public final Capability.IStorage storage;
    public final Supplier<Object> factory;

    public Object factory() {
        return factory.get();
    }

    public CapabilityWrapper(Class<?> t, Capability.IStorage s, Supplier<Object> f) {
        typeInterface = t;
        storage = s;
        factory = f;
    }

    public void apply() {
        CapabilityManager.INSTANCE.register(typeInterface, storage, this::factory);
    }

    @Override
    public String toString() {
        return "CapabilityData{" + "type=" + typeInterface + ", storage=" + storage + '}';
    }
}
