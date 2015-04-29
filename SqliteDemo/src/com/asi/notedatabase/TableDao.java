package com.asi.notedatabase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 数据库操作相关
 * Created by asizhou on 2015/4/24.
 */
@SuppressLint("NewApi")
@SuppressWarnings({"rawtypes", "unchecked"})
public class TableDao {
    public static String TAG = "TableDao";

    private TableInfo tableInfo = null;
    private SQLiteDatabase db   = null;
    private String primaryKey   = null;
    public TableDao(Context ctx, SQLiteDatabase db, TableInfo info) {
        if (db != null && info != null) {
            tableInfo   = info;
            this.db     = db;
        }
        else {
            DbUtils.e(TAG, "data is null");
        }

        //检查更新表信息
        checkTable(ctx);
    }


    /**
     * 检查更新表信息
     * @param ctx
     */
    private void checkTable(Context ctx) {
        //检查版本号
        SharedPreferences sp = ctx.getSharedPreferences(TAG, Context.MODE_MULTI_PROCESS);
        int version = sp.getInt(tableInfo.tableName, -1);
        DbUtils.d(TAG, "checkTable oldversion:"+version+" newversion:"+tableInfo.version);
        if (version != tableInfo.version) {
            //如果是不同版号
            //更新信息
            SharedPreferences.Editor ed = sp.edit();
            ed.putInt(tableInfo.tableName, version);
            ed.apply();

            //更新表结构
            dropTable();
            createTable();
        }
    }
    
	/**
	 * 获取表版本
	 */
    public int getVersion() {
    	return tableInfo.version;
    }
    
	/**
	 * 获取表名
	 */
    public String getName() {
    	return tableInfo.tableName;
    }

	/**
	 * 创建相应表
	 */
    public void createTable() {
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE IF NOT EXISTS ");

        //增加表名信息
        sql.append("'"+tableInfo.tableName+"' ");
        sql.append("(");

        ColumnInfo column   = null;
        String fieldName    = null;
        String type         = null;
        Iterator iter = tableInfo.columnInfos.values().iterator();
        while (iter.hasNext()) {
            column = (ColumnInfo)iter.next();
            //增加列信息
            fieldName = column.field.getName();
            type      = DbUtils.getTypeString(column.type);
            sql.append("'"+fieldName+"' " + type + " ");
            if (column.notNull) {
                sql.append("NOT NULL ");
            }

            //最后一个就不需要加了，没有下一个
            if (iter.hasNext())
                sql.append(", ");

            //增加主键信息，暂时只支持单主键
            if (column.primaryKey) {
                primaryKey = "PRIMARY KEY("+fieldName+")";
            }
        }

        //增加主键信息
        if (primaryKey != null) {
            sql.append(", ");
            sql.append(primaryKey);
        }
        sql.append(");");

        String sqlStr = sql.toString();
        //DbUtils.d(TAG, sqlStr);
        db.execSQL(sqlStr);
    }

    /**
     * 插入多行
     * @param list
     */
    public void insert(List<?> list) {
        if(list != null && list.size() > 0) {
            DbUtils.d(TAG, "insert list:" + list.size());
            //创建事务
            db.beginTransaction();
            boolean succ = false;
            for (Object ob:list) {
                succ = insert(ob);
                //如果插入错误，停止所有操作
                if (!succ) {
                    break;
                }
            }
            //如果成功，提交事务，否则回滚
            if (succ) {
                db.setTransactionSuccessful();
            }
            db.endTransaction();
        }
        else {
            DbUtils.e(TAG, "insert err list null or size 0!");
        }

    }

    /**
     * 插入一行
     * @param ob
     * @return
     */
    public boolean insert(Object ob) {
        //DbUtils.d(TAG, "insert ob:" + ob);
        if (ob != null) {
            try {
                StringBuffer sql = new StringBuffer();
                //StringBuilder sql = new StringBuilder();
                sql.append("INSERT OR REPLACE INTO " + tableInfo.tableName + " ");

                ColumnInfo column   = null;
                String fieldName    = null;

                StringBuffer args   = new StringBuffer();
                StringBuffer values = new StringBuffer();
                args.append("(");
                values.append("(");

                Iterator iter = tableInfo.columnInfos.values().iterator();
                while (iter.hasNext()) {
                    column = (ColumnInfo)iter.next();
                    //增加列信息
                    fieldName = column.field.getName();
                    args.append(fieldName);
                    Object res = DbUtils.changeValue(column.type, column.field.get(ob));
                    if (res == null) {
                        values.append("null");
                    } else {
                        values.append("'"+res+"'");
                    }

                    //如果是最后一个不需要+','
                    if (iter.hasNext()) {
                        args.append(",");
                        values.append(",");
                    }
                }

                args.append(")");
                values.append(")");
                sql.append(args.toString());
                sql.append(" VALUES ");
                sql.append(values.toString());
                sql.append(";");
                //DbUtils.d(TAG, sql.toString());
                db.execSQL(sql.toString());
                return true;
            } catch (Throwable e) {
                DbUtils.e(TAG, "insert " + e.toString());
            }
        }
        else {
            DbUtils.e(TAG, "insert ob null");
        }

        return false;
    }


	/**
	 * 查询表所有行
	 */
	public List queryAll() {
		DbUtils.d(TAG, "queryAll");
        Cursor cursor = null;
        try {
            cursor = db.query(tableInfo.tableName, null, null, null, null, null, null);
            DbUtils.d(TAG, "queryAll query:"+tableInfo.tableName);
            if (cursor != null) {
                cursor.moveToFirst();
                int count = cursor.getCount();
                List list = null;
                DbUtils.d(TAG, "queryAll count:"+count);
                if (count > 0) {
                    list = new ArrayList(count);
                    Class<?> cl = tableInfo.type;
                    do {
                        //创建实例对象
                        Object instance = cl.newInstance();

                        /**对对象赋值*/
                        String[] names = cursor.getColumnNames();
                        //对对象的每一个字段赋值
                        for (String name : names) {
                            int index = cursor.getColumnIndex(name);
                            ColumnInfo info = tableInfo.getColumn(name);
                            if (info != null) {
                                Object value = DbUtils.getCursorValue(cursor, index, info.type);
                                if (value != null) {
                                    DbUtils.setValue(info.field, info.type, instance, value);
                                }
                            }
                        }

                        //加入输出队列
                        list.add(instance);
                    }
                    while (cursor.moveToNext());
                }

                cursor.close();
                cursor = null;

                if (list.size() > 0) {
                	DbUtils.d(TAG, "queryAll list:" + list.size());
                    return list;
                }
                else {
                	DbUtils.e(TAG, "queryAll list:0");
                }
            }
            else {
            	DbUtils.e(TAG, "queryAll cursor null");
            }
        } catch (Throwable e) {
        	DbUtils.e(TAG, "queryAll e:"+e.toString());
        }
        finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        
        DbUtils.e(TAG, "queryAll null");
        return null;
    }

    /**
     * 查询单个属性值，使用字段名+字段值
     * @param columnName :字段名
     * @param value      :字段值
     * @return
     */
    public Object query(String columnName, Object value) {
        DbUtils.d(TAG, "query name:" + columnName + " value:" + value);
        Cursor cursor = null;
        try {
            cursor = db.query(tableInfo.tableName, new String[]{columnName}, null, new String[]{value.toString()}, null, null, null);
            DbUtils.d(TAG, "query:"+tableInfo.tableName);
            if (cursor != null) {
                Class<?> cl = tableInfo.type;

                //创建实例对象
                Object instance = cl.newInstance();

                /**对对象赋值*/
                String[] names = cursor.getColumnNames();
                //对对象的每一个字段赋值
                for (String name : names) {
                    int index = cursor.getColumnIndex(name);
                    ColumnInfo info = tableInfo.getColumn(name);
                    if (info != null) {
                        Object va = DbUtils.getCursorValue(cursor, index, info.type);
                        if (va != null) {
                            DbUtils.setValue(info.field, info.type, instance, value);
                        }
                    }
                }
                cursor.close();
                cursor = null;

                return instance;
            }
            else {
                DbUtils.e(TAG, "queryAll cursor null");
            }
        } catch (Throwable e) {
            DbUtils.e(TAG, "query e:"+e.toString());
        }
        finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        DbUtils.e(TAG, "query null");
        return null;
    }

	/**
	 * 清空表，删除所有行
	 */
    public void deleteAll() {
        DbUtils.d(TAG, "deleteAll");
        try {
            String sqlStr = "TRUNCATE TABLE " + tableInfo.tableName + ";";
            //DbUtils.d(TAG, sqlStr);
            db.execSQL(sqlStr);
        } catch (Throwable e) {
            DbUtils.e(TAG, "deleteAll e:" + e.toString());
        }
    }

    /**
     * 删除一行
     * @param ob
     */
    public void delete(Object ob) {
        DbUtils.d(TAG, "delete");
        try {
            ColumnInfo info = tableInfo.getPrimaryColumn();
            String sqlStr = "DELETE FROM '" + tableInfo.tableName + "' WHERE " + primaryKey + "="+ info.field.get(ob)+ ";";
            //DbUtils.d(TAG, sqlStr);
            db.execSQL(sqlStr);
        } catch (Throwable e) {
            DbUtils.e(TAG, "delete e:" + e.toString());
        }
    }

	/**
	 * 删除表
	 */
    public void dropTable() {
        DbUtils.d(TAG, "dropTable");
        try {
            String sqlStr = "DROP TABLE IF EXISTS '" + tableInfo.tableName + "';";
            //DbUtils.d(TAG, sqlStr);
            db.execSQL(sqlStr);
        } catch (Throwable e) {
            DbUtils.e(TAG, "dropTable e:" + e.toString());
        }
    }
}
