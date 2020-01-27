package com.elementtimes.elementcore.api.annotation.tools;

import com.elementtimes.elementcore.api.annotation.part.Field;
import com.elementtimes.elementcore.api.annotation.part.Method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 物品栏物品检索编辑
 * 可注解到任何地方
 * @author luqin2007
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ModTabEditor {
    Field tab();

    /**
     * 该方法需要接收一个 NonNullList<ItemStack> 类型的变量，无返回值
     * @return 物品修改器
     */
    Method editor();
}
