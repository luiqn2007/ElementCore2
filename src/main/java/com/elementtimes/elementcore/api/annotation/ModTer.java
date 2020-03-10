package com.elementtimes.elementcore.api.annotation;

import com.elementtimes.elementcore.api.annotation.part.Getter2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author luqin2007
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.CONSTRUCTOR})
public @interface ModTer {

    /**
     * 为该 TileEntity 注册一个 TileEntityRenderer
     * 默认使用 TileEntityRendererAnimation 即ASM动画
     * @return TileEntityRenderer 类
     */
    Getter2 value() default @Getter2(value = "net.minecraftforge.client.model.animation.TileEntityRendererAnimation");
}
