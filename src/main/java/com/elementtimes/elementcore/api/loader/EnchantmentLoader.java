package com.elementtimes.elementcore.api.loader;

import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.annotation.ModEnchantment;
import com.elementtimes.elementcore.api.annotation.enums.EnchantmentBook;
import com.elementtimes.elementcore.api.annotation.result.EnchantmentBookWrapper;
import com.elementtimes.elementcore.api.helper.FindOptions;
import com.elementtimes.elementcore.api.helper.ObjHelper;
import com.elementtimes.elementcore.api.helper.RefHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemGroup;

public class EnchantmentLoader {

    public static void load(ECModElements elements) {
        loadEnchantment(elements);
        loadBook(elements);
    }

    private static void loadEnchantment(ECModElements elements) {
        ObjHelper.stream(elements, ModEnchantment.class).forEach(data -> {
            FindOptions option = new FindOptions().withReturns(Enchantment.class).addParameterObjects(() -> new Object[0]);
            ObjHelper.find(elements, data, option).ifPresent(obj -> {
                Enchantment enchantment = (Enchantment) obj;
                String regName = ObjHelper.getDefault(data);
                ObjHelper.setRegisterName(enchantment, regName, data, elements);
                elements.enchantments.add(enchantment);
            });
        });
    }

    private static void loadBook(ECModElements elements) {
        ObjHelper.stream(elements, ModEnchantment.Book.class).forEach(data -> {
            ObjHelper.find(elements, data, new FindOptions().withReturns(Enchantment.class)).ifPresent(enchantment -> {
                EnchantmentBook book = ObjHelper.getEnum(EnchantmentBook.class, ObjHelper.getDefault(data), EnchantmentBook.ALL);
                RefHelper.get(elements, data.getAnnotationData().get("groups"), ItemGroup.class)
                        .ifPresent(tab -> elements.enchantmentBooks.add(new EnchantmentBookWrapper(tab, book, (Enchantment) enchantment)));
            });
        });
    }
}
