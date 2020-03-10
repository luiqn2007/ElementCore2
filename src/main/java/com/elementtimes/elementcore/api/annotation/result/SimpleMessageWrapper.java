package com.elementtimes.elementcore.api.annotation.result;

import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.interfaces.invoker.Invoker;
import com.elementtimes.elementcore.api.interfaces.invoker.VoidInvoker;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SimpleMessageWrapper {

    public static int nextIndex = 0;

    public final Class message;
    public final VoidInvoker encoder, handler;
    public final Invoker<PacketBuffer> decoder;

    public SimpleMessageWrapper(Class<?> message, VoidInvoker encoder, Invoker<PacketBuffer> decoder, VoidInvoker handler) {
        this.message = message;
        this.encoder = encoder;
        this.decoder = decoder;
        this.handler = handler;
    }

    private void encode(Object message, PacketBuffer buffer) {
        encoder.invoke(message, buffer);
    }

    private PacketBuffer decode(Object message) {
        return decoder.invoke(message);
    }

    private void handler(Object message, Supplier<NetworkEvent.Context> contextSupplier) {
        handler.invoke(message, contextSupplier);
    }

    public void apply(ECModElements elements) {
        elements.simpleChannel.registerMessage(nextIndex++, message, this::encode, this::decode, this::handler);
    }
}
