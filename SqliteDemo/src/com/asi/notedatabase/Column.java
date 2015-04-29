package com.asi.notedatabase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段注解，标识该字段是否需要加入数据库
 *
 * primaryKey:boolean 是否为主键
 * notNull   :boolean 是否不为空
 * Created by asizhou on 2015/4/24.
 */
@Target(ElementType.FIELD)          //作用于类成员变量
@Retention(RetentionPolicy.RUNTIME) //编译后，运行时可用
public @interface Column {
    public boolean primaryKey() default false;
    public boolean notNull() default false;
}
