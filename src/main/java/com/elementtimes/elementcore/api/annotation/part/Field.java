package com.elementtimes.elementcore.api.annotation.part;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 代表一个静态变量。只限定所在类和变量名，其他需求详见使用到的地方的注释
 * @author luqin2007
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {
    /**
     * 变量所在类
     * @return 所在类
     */
    Class<?> container();

    /**
     * 变量名
     * @return 变量名
     */
    String name();
}
