package com.elementtimes.elementcore.api.common.helper;

import com.elementtimes.elementcore.api.common.ECModContainer;
import com.elementtimes.elementcore.api.common.ECModElements;
import com.elementtimes.elementcore.api.common.ECUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 有关注解对象与注册对象的辅助类
 * @author luqin2007
 */
@SuppressWarnings("WeakerAccess")
public class ObjHelper {

    public static <T> Optional<? extends T> find(@Nonnull ECModElements elements, @Nonnull Class<? extends T> type, ASMDataTable.ASMData data) {
        return findClass(elements, data.getClassName()).flatMap(aClass -> ECUtils.reflect.get(aClass, data.getObjectName(), null, type, elements));
    }

    public static <T> Optional<? extends T> findOrNew(@Nonnull ECModElements elements, @Nonnull Class<? extends T> type, ASMDataTable.ASMData data) {
        String className = data.getClassName();
        Optional<? extends T> optional;
        if (StringUtils.isNullOrEmpty(data.getObjectName())) {
            optional = ECUtils.reflect.create(className, type, elements);
        } else {
            optional = ObjHelper.find(elements, type, data);
        }
        return optional;
    }

    public static Optional<CreativeTabs> findTab(@Nonnull ECModElements elements, String key) {
        CreativeTabs creativeTabs = null;
        if (!StringUtils.isNullOrEmpty(key)) {
            creativeTabs = elements.tabs.get(key);
            if (creativeTabs == null) {
                for (ECModContainer mod : ECModContainer.MODS.values()) {
                    creativeTabs = mod.elements.tabs.get(key);
                    if (creativeTabs != null) {
                        break;
                    }
                }
            }
        }
        return Optional.ofNullable(creativeTabs);
    }

    public static <T> Optional<Class<? extends T>> findClass(@Nonnull ECModElements elements, @Nonnull String className) {
        boolean skip = true;
        for (String packageName : elements.packages) {
            if (className.startsWith(packageName)) {
                skip = false;
                break;
            }
        }
        if (skip) {
            return Optional.empty();
        }
        Class<? extends T> clazz = (Class<? extends T>) elements.classes.get(className);
        if (clazz == null) {
            try {
                clazz = (Class<? extends T>) Class.forName(className);
            } catch (ClassNotFoundException e) {
                elements.warn("Can't find class: {}", className);
            }
            if (clazz != null) {
                elements.classes.put(className, clazz);
            }
        }
        return Optional.ofNullable(clazz);
    }

    public static Stream<ASMDataTable.ASMData> stream(@Nonnull ECModElements elements, Class<? extends Annotation> annotation) {
        Set<ASMDataTable.ASMData> all = elements.asm.getAll(annotation.getName());
        if (all == null) {
            return Stream.empty();
        }
        return all.stream();
    }

    public static <T> T getDefault(ASMDataTable.ASMData data) {
        return (T) data.getAnnotationInfo().get("value");
    }

    public static <T> T getDefault(ASMDataTable.ASMData data, T defVal) {
        return (T) data.getAnnotationInfo().getOrDefault("value", defVal);
    }

    public static int getDefault(ASMDataTable.ASMData data, int defVal) {
        return (int) data.getAnnotationInfo().getOrDefault("value", defVal);
    }

    public static boolean getDefault(ASMDataTable.ASMData data, boolean defVal) {
        return (boolean) data.getAnnotationInfo().getOrDefault("value", defVal);
    }

    public static double getDefault(ASMDataTable.ASMData data, double defVal) {
        return (double) data.getAnnotationInfo().getOrDefault("value", defVal);
    }

    @Nullable
    public static Map<String, Object> getAnnotationMap(Object object) {
        Map<String, Object> map;
        if (object instanceof Map) {
            map = (Map<String, Object>) object;
        } else if (object instanceof ASMDataTable.ASMData) {
            map = ((ASMDataTable.ASMData) object).getAnnotationInfo();
        } else {
            return null;
        }
        if (map.isEmpty()) {
            return null;
        }
        return map;
    }
}