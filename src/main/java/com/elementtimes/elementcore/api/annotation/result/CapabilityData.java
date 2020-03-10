package com.elementtimes.elementcore.api.annotation.result;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.function.Supplier;

public class CapabilityData {
    public final Class typeInterface;
    public final Capability.IStorage storage;
    public final Supplier<Object> factory;

    public Object factory() {
        return factory.get();
    }

    public CapabilityData(Class<?> t, Capability.IStorage s, Supplier<Object> f) {
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
