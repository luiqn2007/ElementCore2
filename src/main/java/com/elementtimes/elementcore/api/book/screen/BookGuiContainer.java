package com.elementtimes.elementcore.api.book.screen;

import com.elementtimes.elementcore.ElementCore;
import com.elementtimes.elementcore.api.book.BookContainer;
import com.elementtimes.elementcore.api.book.ItemBook;
import com.elementtimes.elementcore.api.book.Page;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

/**
 * blit 参数一览
 * int x, int y, int z, int width, int height, TextureAtlasSprite sprite
 * int x, int y, int u, int v, int width, int height
 * int x, int y, int z, float u, float v, int width, int height, int textureHeight, int textureWidth
 * int x, int y, int width, int height, float u, float v, int uWidth, int vHeight, int textureWidth, int textureHeight
 * int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight
 * @author luqin2007
 */
@OnlyIn(Dist.CLIENT)
public class BookGuiContainer extends ContainerScreen<BookContainer> {

    public static ResourceLocation BACKGROUND = new ResourceLocation(ElementCore.CONTAINER.id(), "textures/gui/book.png");

    protected BookContainer mContainer;

    protected Button prev, next;
    protected int xStart, yStart, xSpace, ySpace, xLength, yLast, yLength;

    public BookGuiContainer(Container container, PlayerInventory inventory, ITextComponent text) {
        super((BookContainer) container, inventory, text);
        mContainer = (BookContainer) container;
        xSize = 300;
        ySize = 232;
        draw(DrawStage.CONSTRUCTOR);
    }

    @Override
    protected void init() {
        super.init();
        xStart = guiLeft + 43;
        yStart = guiTop + 18;
        xLength = 215;
        yLength = 197;
        xSpace = xLength;
        ySpace = yLength;
        yLast = yStart;
        prev = new ImageButton(  8 + guiLeft, 202 + guiTop, 16, 17,  0, 240, 0, BACKGROUND, 512, 256, btn -> mContainer.prevPage());
        next = new ImageButton(280 + guiLeft, 202 + guiTop, 16, 16, 16, 240, 0, BACKGROUND, 512, 256, btn -> mContainer.nextPage());
        addButton(prev);
        addButton(next);
        draw(DrawStage.INIT);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color3f(1f, 1f, 1f);
        Minecraft.getInstance().getTextureManager().bindTexture(BACKGROUND);
        blit(guiLeft, guiTop, 0, 0, xSize, ySize, 512, 256);
        draw(DrawStage.BACKGROUND, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        if (mouseY > next.y && mouseY < next.y + next.getHeight()) {
            if (mouseX > next.x && mouseX < next.x + next.getWidth()) {
                renderTooltip("Next Page", mouseX - guiLeft, mouseY - guiTop);
            } else if (mouseX > prev.x && mouseX < prev.x + prev.getWidth()) {
                renderTooltip("Previous Page", mouseX - guiLeft, mouseY - guiTop);
            }
        }
        draw(DrawStage.FOREGROUND, mouseX, mouseY);
    }

    public void setLastY(int lastY) {
        yLast = lastY;
    }

    protected void draw(DrawStage stage, int mouseX, int mouseY) {
        yLast = yStart;
        ySpace = yLength;
        Page page = mContainer.getPage();
        List<IContent> contents = page.getContents(stage);
        Iterator<IContent> iterator = contents.iterator();
        boolean draw = true;
        while (iterator.hasNext()) {
            IContent content = iterator.next();
            content.setGuiContainer(this);
            content.setContainer(mContainer);
            if (draw) {
                // draw
                int newY = content.draw(xStart, yLast, xSpace, ySpace, mouseX, mouseY);
                int height = newY - yLast;
                ySpace -= height;
                yLast = newY;
                // replace
                if (ySpace <= 0) {
                    draw = false;
                    mContainer.newPage().addAll(content.split());
                }
                IContent afterDisplay = content.replaceAfterDisplay();
                if (afterDisplay == null) {
                    iterator.remove();
                } else {
                    page.replace(content, afterDisplay);
                }
            } else {
                mContainer.newPage().add(content);
            }
        }
    }

    protected void draw(DrawStage stage) {
        draw(stage, (int) Minecraft.getInstance().mouseHelper.getMouseX(), (int) Minecraft.getInstance().mouseHelper.getMouseY());
    }

    @Nonnull
    @Override
    public <T extends Widget> T addButton(T button) {
        return super.addButton(button);
    }
}
