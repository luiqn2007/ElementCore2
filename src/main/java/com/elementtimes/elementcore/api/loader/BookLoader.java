package com.elementtimes.elementcore.api.loader;

import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.annotation.tools.ModBook;
import com.elementtimes.elementcore.api.book.IBook;
import com.elementtimes.elementcore.api.book.ItemBook;
import com.elementtimes.elementcore.api.helper.FindOptions;
import com.elementtimes.elementcore.api.helper.ObjHelper;

import java.lang.annotation.ElementType;

public class BookLoader {

    public static void load(ECModElements elements) {
        ObjHelper.stream(elements, ModBook.class).forEach(data -> {
            ObjHelper.find(elements, data, new FindOptions<>(IBook.class, ElementType.TYPE, ElementType.FIELD)).ifPresent(book -> {
                ItemBook item = book.createItem();
                if (item.getRegistryName() == null) {
                    item.setRegistryName(book.getId());
                }
                elements.items.add(item);
                elements.warn("[Book]book: {}", book.getId());
            });
        });
    }
}
