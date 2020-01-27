package com.elementtimes.elementcore.api.annotation.part;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 代表一组燃烧时间的值
 * @author luqin2007
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface BurnTime {
    /**
     * 匹配 metadata 值。当该值为空时，匹配所有 metadata
     * @return metadata/damage
     */
    int[] metadata() default {};

    /**
     * 燃烧时间。
     * 当该值小于 0 时忽略，且应当正确设置 method 属性
     * @return 燃烧时间
     */
    int burnTime() default -1;

    /**
     * 当 burnTime 返回 -1 时会调用此属性代表的方法。
     * 该方法应当返回一个 int，作为燃烧时间
     * 当该方法返回值小于 0 时，返回 getItemBurnTime 默认值
     * @return 燃烧时间方法
     */
    Method method() default @Method(container = Object.class, name = "");
}
