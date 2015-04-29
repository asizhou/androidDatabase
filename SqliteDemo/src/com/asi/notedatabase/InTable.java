package com.asi.notedatabase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类注解，标识该类加入数据库的信息
 *
 * name     :String 当前该类加入数据库的表名
 * version  :int    当前该类加入数据库时的版本号
 * Created by asizhou on 2015/4/24.
 */
@Target(ElementType.TYPE)            //作用于类
@Retention(RetentionPolicy.RUNTIME)  //编译后，运行时可用
public @interface InTable {
    public String name() default "";
    public int version() default 0;
}
