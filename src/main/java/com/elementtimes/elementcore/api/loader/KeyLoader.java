package com.elementtimes.elementcore.api.loader;

import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.annotation.ModKey;
import com.elementtimes.elementcore.api.annotation.result.KeyWrapper;
import com.elementtimes.elementcore.api.helper.FindOptions;
import com.elementtimes.elementcore.api.helper.ObjHelper;
import com.elementtimes.elementcore.api.helper.RefHelper;
import com.elementtimes.elementcore.api.interfaces.invoker.VoidInvoker;
import com.elementtimes.elementcore.api.utils.CommonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.annotation.ElementType;

public class KeyLoader {

    public static void load(ECModElements elements) {
        if (CommonUtils.isClient()) {
            loadKey(elements);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void loadKey(ECModElements elements) {
        Class<?> keyClass = net.minecraft.client.settings.KeyBinding.class;
        Class<?> keyEventClass = net.minecraftforge.client.event.InputEvent.KeyInputEvent.class;
        ObjHelper.stream(elements, ModKey.class).forEach(data -> {
            ObjHelper.find(elements, data, new FindOptions().withTypes(ElementType.FIELD).withReturns(keyClass)).ifPresent(key -> {
                Object aDefault = ObjHelper.getDefault(data);
                VoidInvoker invoker = RefHelper.invokerNullable(elements, aDefault, keyEventClass, keyClass);
                elements.keys.add(new KeyWrapper(key, invoker));
            });
        });
    }
}
