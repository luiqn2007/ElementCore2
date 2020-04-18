package com.elementtimes.elementcore.api;

import com.elementtimes.elementcore.ElementCore;
import com.elementtimes.elementcore.api.annotation.tools.ModBook;
import com.elementtimes.elementcore.api.book.Book;
import com.elementtimes.elementcore.api.book.ItemBook;
import com.elementtimes.elementcore.api.book.screen.Text;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;

@ModBook
public class TestBook extends Book {

    public TestBook() {
        super(new ResourceLocation(ElementCore.CONTAINER.id(), "guidebook"));
        addPage(new Text("哈哈哈哈")).addContent(new Text("呵呵呵呵"));
        setItemCreator(book -> new ItemBook(book, new Item.Properties().group(ItemGroup.MISC)));
    }
}
