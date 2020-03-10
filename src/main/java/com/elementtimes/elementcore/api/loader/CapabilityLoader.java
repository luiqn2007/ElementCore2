package com.elementtimes.elementcore.api.loader;

import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.annotation.ModCapability;
import com.elementtimes.elementcore.api.annotation.result.CapabilityData;
import com.elementtimes.elementcore.api.helper.ObjHelper;
import com.elementtimes.elementcore.api.helper.RefHelper;
import com.elementtimes.elementcore.api.interfaces.invoker.Invoker;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import org.objectweb.asm.Type;

import java.util.Map;
import java.util.Optional;

public class CapabilityLoader {

    public static void load(ECModElements elements) {
        ObjHelper.stream(elements, ModCapability.class).forEach(data -> {
            Map<String, Object> info = data.getAnnotationData();
            Type type = (Type) info.get("type");
            Map<String, Object> factory = (Map<String, Object>) info.get("typeFactory");
            Map<String, Object> storage = (Map<String, Object>) info.get("storage");
            Optional<Class<?>> typeClassOpt = ObjHelper.findClass(elements, type.getClassName());
            Optional<? extends IStorage> storageOpt = RefHelper.get(elements, storage, IStorage.class);
            Invoker<Object> factoryFunc = RefHelper.invoker(elements, factory, Invoker.empty());
            if (typeClassOpt.isPresent() && factory != null && storageOpt.isPresent()) {
                CapabilityData capability = new CapabilityData(typeClassOpt.get(), storageOpt.get(), factoryFunc::invoke);
                elements.warn("[ModCapability]{}", capability);
                elements.capabilities.add(capability);
            }
        });
    }
}
