package com.elementtimes.elementcore.api.annotation.result;

import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public class EntityRendererWrapper {

    public final Function<Object, Object> render;
    public final Class<?> entityClass;

    public EntityRendererWrapper(Function<Object, Object> render, Class<?> entityClass) {
        this.render = render;
        this.entityClass = entityClass;
    }

    @OnlyIn(Dist.CLIENT)
    public void apply() {
        net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler((Class<? extends Entity>) entityClass,
                (net.minecraftforge.fml.client.registry.IRenderFactory<Entity>) manager -> (net.minecraft.client.renderer.entity.EntityRenderer) render.apply(manager));
    }
}
