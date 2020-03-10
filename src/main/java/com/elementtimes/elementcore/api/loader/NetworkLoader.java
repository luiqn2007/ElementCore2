package com.elementtimes.elementcore.api.loader;

import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.annotation.ModEventNetwork;
import com.elementtimes.elementcore.api.annotation.ModSimpleNetwork;
import com.elementtimes.elementcore.api.annotation.result.SimpleMessageWrapper;
import com.elementtimes.elementcore.api.helper.ObjHelper;
import com.elementtimes.elementcore.api.helper.RefHelper;
import com.elementtimes.elementcore.api.interfaces.invoker.Invoker;
import com.elementtimes.elementcore.api.interfaces.invoker.VoidInvoker;
import com.elementtimes.elementcore.api.utils.ReflectUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;

import java.util.Map;
import java.util.function.Supplier;

public class NetworkLoader {

    public static void load(ECModElements elements) {
        if (elements.simpleChannel != null) {
            loadSimple(elements);
        }
        if (elements.eventChannel != null) {
            loadEvent(elements);
        }
    }

    private static void loadSimple(ECModElements elements) {
        ObjHelper.stream(elements, ModSimpleNetwork.class).forEach(data -> {
            ObjHelper.findClass(elements, data.getClassType()).ifPresent(aClass -> {
                Map<String, Object> map = data.getAnnotationData();
                VoidInvoker encoder = RefHelper.invoker(elements, map.get("encoder"), Object.class, PacketBuffer.class);
                Invoker<PacketBuffer> decoder = RefHelper.invoker(elements, map.get("decoder"), (a) -> new PacketBuffer(Unpooled.buffer()), Object.class);
                VoidInvoker handler = RefHelper.invoker(elements, map.get("handler"), Object.class, Supplier.class);
                elements.netSimples.add(new SimpleMessageWrapper(aClass, encoder, decoder, handler));
            });
        });
    }

    private static void loadEvent(ECModElements elements) {
        ObjHelper.stream(elements, ModEventNetwork.class).forEach(data -> {
            ObjHelper.findClass(elements, data.getClassType()).ifPresent(aClass -> {
                if (ObjHelper.getDefault(data, false)) {
                    elements.netEvents.add(ReflectUtils.create(aClass, aClass, elements));
                } else {
                    elements.netEvents.add(aClass);
                }
            });
        });
    }
}
