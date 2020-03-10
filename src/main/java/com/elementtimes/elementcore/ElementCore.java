package com.elementtimes.elementcore;

import com.elementtimes.elementcore.api.ECModContainer;
import com.elementtimes.elementcore.api.ECModElements;
import net.minecraftforge.fml.common.Mod;

/**
 * 元素核心
 * TODO
 *  0 Command: 客户端命令
 *  1 Tag: 代码注册
 *  2 BaseScreen: 获取流体材质
 *  3 EventNetwork: post 方法
 *  4 Item: 渲染
 *  5 BurnTime: 物品/方块/流体燃烧时间
 *  6 RecipeLoader: 注册配方 Ser
 * @author luqin2007
 */
@Mod(ElementCore.MODID)
public class ElementCore {
    public static ElementCore INSTANCE = null;

    static final String MODID = "elementcore";

    public static ECModElements.Builder builder() {
        return ECModElements.builder();
    }

    public ECModContainer container;

    public ElementCore() {
        INSTANCE = this;
        container = builder().useSimpleNetwork().build();
    }
}
