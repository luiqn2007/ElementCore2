package com.elementtimes.elementcore.api.annotation;

import com.elementtimes.elementcore.api.annotation.part.Method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 客户端与服务端通信的信息类
 * 注册到 SimpleChannel
 * 三个方法中 Object 参数为该类实例
 * @author luqin2007
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModSimpleNetwork {

    /**
     * 从 PacketBuffer 中读取数据
     * 参数
     *  Object, PacketBuffer
     * 返回值
     *  无
     */
    Method encoder();

    /**
     * 将数据写入 PacketBuffer
     * 参数
     *  Object
     * 返回值
     *  PacketBuffer
     * @see net.minecraft.network.PacketBuffer
     * @see io.netty.buffer.Unpooled#buffer()
     */
    Method decoder();

    /**
     * 处理数据
     * 参数
     *  Object, Supplier<NetworkEvent.Context>
     * 返回值
     *  无
     */
    Method handler();
}
