package com.elementtimes.elementcore.api.annotation.tools;

import com.elementtimes.elementcore.api.annotation.enums.ValueType;
import com.elementtimes.elementcore.api.annotation.part.Method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注册一个 ItemStack 的 Tooltip
 * 若该注解单独使用，会对所有 ItemStack 进行处理
 * 否则，会根据所在注解特殊处理
 * @author luqin2007
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Tooltips {

    /**
     * 当其为 ValueType.METHOD 时，使用 method 属性对应的方法；否则，使用 value 属性对应的值
     * @return 值类型
     */
    ValueType type() default ValueType.CONST;

    /**
     * 简单的 Tooltips
     * @return Tooltips
     */
    String[] value() default {};

    /**
     * 对物品栈 Tooltips 的详细设定
     * 参数
     *  ItemStack：要附加的物品栈
     *  List<String>：所有附加在该物品上的 Tooltips，可操作该列表修改 Tooltip
     * 返回值
     *  无
     * @return Tooltip 获取器
     */
    Method method() default @Method;
}
