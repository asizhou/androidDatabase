package com.asi.notedatabase;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

/**
 * Created by asizhou on 2015/4/24.
 */
@SuppressWarnings({"unused", "rawtypes"})
public class DbManager {
    public static String TAG = "DbManager";

    /** 单例*/
    private static DbManager instance = null;

    /** 创建单例*/
    public static void createInstance(Context ctx) {
        if (instance == null) {
            synchronized (DbManager.class) {
                if (instance == null) {
                    instance = new DbManager(ctx.getApplicationContext());
                }
            }
        }
    }

    /** 获取单例*/
    public static DbManager getInstance() {
        return instance;
    }

    /** 私有*/
    private DbManager(Context ctx) {
        context = ctx;
        DbHelper helper = new DbHelper(context);
        db = helper.getWritableDatabase();
    }

    private Context   context = null;
    private SQLiteDatabase db = null;
    private HashMap<String, TableDao> daos = new HashMap<String, TableDao>();
	
	
	/** 获取缓存的dao*/
    private TableDao getDao(String key) {
    	TableDao dao = null;
    	String log = "getDao key:" +key + " null";
        if (key != null) {
            synchronized (daos) {
            	dao = daos.get(key);
            }
            if (dao != null) {
            	log = "getDao key:"+key + " version:" + dao.getVersion();
            }
        }
        DbUtils.d(TAG, log);
        return dao;
    }

	/** 增加缓存的dao*/
    private void addDao(String key, TableDao dao) {
    	DbUtils.d(TAG, "addDao key:"+key + " version:" + dao != null?Integer.toString(dao.getVersion()): "null");
        if (key != null && dao != null) {
            synchronized (daos) {
                daos.put(key, dao);
            }
        }
    }

    /** 删除缓存的dao*/
	private void removeDao(TableDao dao) {
		DbUtils.d(TAG, "removeDao name:"+dao.getName() + " version:" + dao.getVersion());
        if (dao != null) {
            synchronized (daos) {
                daos.remove(dao);
            }
        }
    }

	/** 数据库插入*/
    public void insert(List list) {
    	DbUtils.d(TAG, "insert list size:"+list.size());
        TableInfo info = DbUtils.getTableInfo(list);

        //检查是否已创建相应的dao，如果不存在则创建
        checkTableDao(info);

        TableDao  dao  = getDao(info.tableName);
        if (dao != null) {
            dao.insert(list);
        }
        else {
        	DbUtils.e(TAG, "insert dao null");
        }
    }

	/** 数据库查询*/
	public List queryAll(Class<?> cl) {
		DbUtils.d(TAG, "queryAll Class:"+cl.getName());

        //根据Class信息获取相应的表信息
        TableInfo info = DbUtils.getTableInfo(cl);

        //检查是否已创建相应的dao，如果不存在则创建
        checkTableDao(info);

        TableDao  dao  = getDao(info.tableName);
        if (dao != null) {
            return dao.queryAll();
        }
        else {
        	DbUtils.e(TAG, "queryAll dao null");
		}
        return null;
    }

	/** 检查是否有相应的dao，没有则创建*/
    private void checkTableDao(TableInfo info) {
    	if (info != null) {
    		DbUtils.d(TAG, "checkTableDao dao:"+info.tableName);
    		TableDao  dao  = getDao(info.tableName);
    		if (dao == null) {
                //没有则创建
    			dao = new TableDao(context, db, info);

                //加入缓存
    			addDao(info.tableName, dao);
    		}			
		}
    	else {
    		DbUtils.e(TAG, "checkTableDao info null");
		}
    }
}
