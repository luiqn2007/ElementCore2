package com.elementtimes.elementcore.api.book;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBook extends Item implements INamedContainerProvider {

    protected IBook mBook;
    public static ItemStack ITEM;

    public ItemBook(IBook book, Properties properties) {
        super(properties);
        mBook = book;
    }

    public IBook getBook() {
        return mBook;
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        ITextComponent bookName = mBook.getBookName(stack);
        return bookName == null ? super.getDisplayName(stack) : bookName;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, net.minecraft.client.util.ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        List<ITextComponent> tooltips = mBook.getTooltips(stack);
        if (tooltips != null && !tooltips.isEmpty()) {
            tooltip.addAll(tooltips);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (playerIn instanceof ServerPlayerEntity) {
            ITEM = playerIn.getHeldItem(handIn);
            NetworkHooks.openGui((ServerPlayerEntity) playerIn, this);
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public ITextComponent getDisplayName() {
        return mBook.getBookName(ITEM);
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new BookContainer(id, mBook);
    }
}
