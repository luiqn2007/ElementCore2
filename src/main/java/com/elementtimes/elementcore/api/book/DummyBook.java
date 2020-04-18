package com.elementtimes.elementcore.api.book;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DummyBook implements IBook {

    public static IBook INSTANCE = new DummyBook();

    private final ResourceLocation id = new ResourceLocation("", "");
    private final ITextComponent name = new StringTextComponent("");
    private final List<ITextComponent> tooltips = Collections.emptyList();
    private final ItemBook book = new ItemBook(this, new Item.Properties());
    private final ArrayList<Page> page = new ArrayList<>();

    @Nonnull
    @Override
    public ResourceLocation getId() { return id; }

    @Nullable
    @Override
    public ITextComponent getBookName(ItemStack bookStack) { return name; }

    @Nullable
    @Override
    public List<ITextComponent> getTooltips(ItemStack bookStack) { return tooltips; }

    @Nonnull
    @Override
    public ItemBook createItem() { return book; }

    @Override
    public ArrayList<Page> getPages() { return page; }
}
