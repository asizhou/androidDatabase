package com.asi.notedatabase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 表信息
 * tableName    ：表名
 * version      ：表版本号
 * Created by asizhou on 2015/4/24.
 */
public class TableInfo {
    private static String TAG = "TableInfo";
    //表名
    public String tableName;

    //表数据结构
    public Class<?> type;

    //表版本号
    public int version;

    //表字段信息
    public HashMap<String, ColumnInfo> columnInfos;

    public TableInfo(Class<?> t, String name, int v, HashMap<String, ColumnInfo> infos) {
        tableName   = name;
        version     = v;
        columnInfos = infos;
        type        = t;
    }

	/**
	 * 获取主键列:暂只支持单主键
	 */
    public ColumnInfo getPrimaryColumn() {
        Iterator iter = columnInfos.values().iterator();
        while (iter.hasNext()) {
            ColumnInfo info = (ColumnInfo)iter.next();
            if (info.primaryKey) {
                DbUtils.d(TAG, "getPrimaryColumn" + info.name);
                return info;
            }
        }

        DbUtils.e(TAG, "getPrimaryColumn e:null");
        return null;
    }

	/**
	 * 通过列名获取列信息
	 */
    public ColumnInfo getColumn(String name) {
        if (name != null) {
            return columnInfos.get(name);
        }

        return null;
    }
}
