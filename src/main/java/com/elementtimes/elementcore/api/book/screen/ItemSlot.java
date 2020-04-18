package com.elementtimes.elementcore.api.book.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 * 添加物品槽
 * @author luqin2007
 */
public class ItemSlot extends BaseContent {

    protected Slot mSlot;

    public ItemSlot(Slot slot) {
        mSlot = slot;
    }

    public ItemSlot(IInventory inventory, int index, int offsetX, int offsetY) {
        mSlot = new Slot(inventory, index, offsetX, offsetY);
    }

    public ItemSlot(IItemHandler handler, int index, int offsetX, int offsetY) {
        mSlot = new SlotItemHandler(handler, index, offsetX, offsetY);
    }

    public ItemSlot(ItemStack stack, int offsetX, int offsetY) {
        IInventory inventory = new IInventory() {
            private final ItemStack mStack = stack.copy();
            @Override public int getSizeInventory() { return 1; }
            @Override public boolean isEmpty() { return mStack.isEmpty(); }
            @Override public ItemStack getStackInSlot(int index) { return mStack.copy(); }
            @Override public void setInventorySlotContents(int index,@Nonnull ItemStack stack) { }
            @Override public int getInventoryStackLimit() { return mStack.getMaxStackSize(); }
            @Override public void markDirty() { }
            @Override public boolean isUsableByPlayer(@Nonnull PlayerEntity player) { return false; }
            @Override public void openInventory(@Nonnull PlayerEntity player) { }
            @Override public void closeInventory(@Nonnull PlayerEntity player) { }
            @Override public boolean isItemValidForSlot(int index,@Nonnull ItemStack stack) { return false; }
            @Override public void clear() { }
            @Nonnull @Override public ItemStack decrStackSize(int index, int count) { return ItemStack.EMPTY; }
            @Nonnull @Override public ItemStack removeStackFromSlot(int index) { return ItemStack.EMPTY; }
        };
        mSlot = new Slot(inventory, 0, offsetX, offsetY);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int draw(int x, int y, int spaceX, int spaceY, int mouseX, int mouseY) {
        getContainer().addSlot(mSlot);
        return y + 18;
    }

    @Override
    public DrawStage getStage() {
        return DrawStage.CONTAINER;
    }

    @Override
    public boolean isTemp() {
        return false;
    }
}
