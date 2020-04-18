package com.elementtimes.elementcore.api.book.screen;

import java.util.function.Supplier;

/**
 * 按钮
 * 构造中传入一个 GuiButton 的创建方法，参数为 id 在创建时自动分配
 *
 * Button 构造参数一览
 * int x, int y, int width, int height, String text, IPressable onPress
 * ImageButton 构造参数一览
 * int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, IPressable onPress
 * int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, int textureWidth, int textureHeight, Button.IPressable onPress
 * int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, int textureWidth, int textureHeight, IPressable onPress, String text
 * @author luqin2007
 */
public class Button extends BaseContent {

    protected Supplier<Object> mButton;

    public Button(Supplier<Object> button) {
        mButton = button;
    }

    @Override
    public int draw(int x, int y, int spaceX, int spaceY, int mouseX, int mouseY) {
        return getGuiContainer()
                .addButton((net.minecraft.client.gui.widget.button.Button) mButton.get()).getHeight();
    }

    @Override
    public DrawStage getStage() {
        return DrawStage.INIT;
    }

    @Override
    public boolean isTemp() {
        return false;
    }
}
