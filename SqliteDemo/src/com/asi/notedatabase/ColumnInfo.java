package com.asi.notedatabase;

import java.lang.reflect.Field;

/**
 * 列信息
 * field        :对应数据结构中相应字段信息
 * type         :标识field的数据类型，是一个缓存数值，性能考虑
 * notNull      :是否为空
 * primaryKey   :是否为主键
 * Created by asizhou on 2015/4/24.
 */
public class ColumnInfo {
    public Field    field;
    public String   name;
    public int      type;
    public boolean  notNull;
    public boolean  primaryKey;

    public ColumnInfo(Field f, String n, boolean nn, boolean pk, int t) {
        field       = f;
        name        = n;
        notNull     = nn;
        primaryKey  = pk;
        type		= t;
        field.setAccessible(true);
    }
}
