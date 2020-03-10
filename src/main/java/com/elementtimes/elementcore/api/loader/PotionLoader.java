package com.elementtimes.elementcore.api.loader;

import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.annotation.ModPotion;
import com.elementtimes.elementcore.api.annotation.part.Parts;
import com.elementtimes.elementcore.api.helper.FindOptions;
import com.elementtimes.elementcore.api.helper.ObjHelper;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;

import java.lang.annotation.ElementType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author luqin2007
 */
public class PotionLoader {

    public static void load(ECModElements elements) {
        loadPotion(elements);
    }

    private static void loadPotion(ECModElements elements) {
        ObjHelper.stream(elements, ModPotion.class).forEach(data -> {
            FindOptions options = new FindOptions().withReturns(Potion.class).withTypes(ElementType.FIELD, ElementType.TYPE)
                    .addParameterObjects(() -> {
                        Map<String, Object> map = data.getAnnotationData();
                        String name1 = (String) map.getOrDefault("name", ObjHelper.getMemberName(data));
                        List<Map<String, Object>> effectMaps = (List<Map<String, Object>>) map.getOrDefault("effects", Collections.emptyList());
                        EffectInstance[] effects = effectMaps.stream()
                                .map(em -> Parts.effectInstance(em, elements))
                                .filter(Objects::nonNull)
                                .toArray(EffectInstance[]::new);
                        return new Object[] {name1, effects};
                    });
            ObjHelper.find(elements, data, options).ifPresent(obj -> {
                Potion potion = (Potion) obj;
                String name = ObjHelper.getDefault(data);
                ObjHelper.setRegisterName(potion, name, data, elements);
                elements.potions.add(potion);
            });
        });
    }
}
