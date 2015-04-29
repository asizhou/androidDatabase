package com.asi.notedatabase;

import android.database.Cursor;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Db工具类
 * Created by asizhou on 2015/4/24.
 */
public class DbUtils {
    public static String TAG = "DbUtils";

    /**
     * 是否输出log
     */
    private static boolean LOG = false;
    public static void d(String tag, String msg) {
    	if (LOG) {
			Log.d(tag, msg);
		}
    }
    
    public static void e(String tag, String msg) {
    	if (LOG) {
    		Log.e(tag, msg);
		}
    }
    
    //为提高效率，建一个数据类型缓存
    public static final String  S_byte			= "byte"; //4
    public static final String  S_Byte			= "Byte"; //4
    public static final String  S_Boolean		= "Boolean";//7
    public static final String  S_boolean		= "boolean";//7
    public static final String  S_short			= "short";//5
    public static final String  S_Short			= "Short";//5
    public static final String  S_char			= "char";//4
    public static final String  S_Character		= "Character";//9
    public static final String  S_int			= "int";//3
    public static final String  S_Integer		= "Integer";//7
    public static final String  S_long			= "long";//4
    public static final String  S_Long			= "Long";//4
    public static final String  S_double		= "double";//6
    public static final String  S_Double		= "double";//6
    public static final String  S_float			= "float";//5
    public static final String  S_Float			= "Float";//5
    public static final String  S_String		= "String";//6
    
    //为提高效率，再建一个数据类型缓存，用于提高类型比对效率，String比对效率太低
    public static final short  I_byte			= 0; 
    public static final short  I_Byte			= 1;
    public static final short  I_Boolean		= 2;
    public static final short  I_boolean		= 3;
    public static final short  I_short			= 4;
    public static final short  I_Short			= 5;
    public static final short  I_char			= 6;
    public static final short  I_Character		= 7;
    public static final short  I_int			= 8;
    public static final short  I_Integer		= 9;
    public static final short  I_long			= 10;
    public static final short  I_Long			= 11;
    public static final short  I_double			= 12;
    public static final short  I_Double			= 13;
    public static final short  I_float			= 14;
    public static final short  I_Float			= 15;
    public static final short  I_String			= 16;
    
    /**
     * class的类型转换成int存储，提高比对效率
     * @param type
     * @return int
     */
    public static int typeString2int(char[] type) {
    	int ret = -1;
    	if (type != null) {
    		switch (type.length) {
    		//int
    		case 3:
    			ret = I_int;
    			break;
    		//byte,Byte,char,long,Long
			case 4:
			{
				if (type[0] == 'l') {
					ret = I_long;
				}
				else if (type[0] == 'L') {
					ret = I_Long;
				}
				else if (type[0] == 'c') {
					ret = I_char;
				}
				else if (type[0] == 'B') {
					ret = I_Byte;
				}
				else if (type[0] == 'b') {
					ret = I_byte;
				}
			}break;
			//short,Short,float,Float
			case 5:		
			{
				if (type[0] == 's') {
					ret = I_short;
				}
				else if (type[0] == 'S') {
					ret = I_Short;
				}
				else if (type[0] == 'f') {
					ret = I_float;
				}
				else if (type[0] == 'F') {
					ret = I_Float;
				}
			}break;
			//double,Double.String
			case 6:				
			{
				if (type[0] == 'S') {
					ret = I_String;
				}
				else if (type[0] == 'D') {
					ret = I_Double;
				}
				else if (type[0] == 'd') {
					ret = I_double;
				}
			}break;
			//Boolean,boolean,Integer
			case 7:	
			{
				if (type[0] == 'b') {
					ret = I_boolean;
				}
				else if (type[0] == 'I') {
					ret = I_Integer;
				}
				else if (type[0] == 'B') {
					ret = I_Boolean;
				}
			}break;
			//Character
			case 9:
				ret = I_Character;
				break;
			}
		}
    	//DbUtils.d(TAG, "typeString2int type:"+ new String(type) + " int:" + ret);
    	return ret;
    }
    
    /**
     * 获取表信息
     * 如果自定义了表名，则是使用自定义值，否则使用类简称作为表名
     * @param list
     * @return TableInfo
     */
    public static TableInfo getTableInfo(List<?> list) {
        if (list != null && list.size() > 0) {
            Class<?> cl = list.get(0).getClass();
            return getTableInfo(cl);
        }
        return null;
    }

    /**
     * 获取表信息
     * 如果自定义了表名，则是使用自定义值，否则使用类简称作为表名
     * @param ob
     * @return TableInfo
     */
    public static TableInfo getTableInfo(Object ob) {
        if (ob != null) {
            return getTableInfo(ob.getClass());
        }
        return null;
    }

    /**
     * 获取表信息
     * 如果自定义了表名，则是使用自定义值，否则使用类简称作为表名
     * @param cl
     * @return TableInfo
     */
    public static TableInfo getTableInfo(Class<?>  cl) {
        TableInfo table = null;
        if (cl != null) {
            String tableName = null;
            int tableVersion = 0;
            InTable inTable = (InTable)cl.getAnnotation(InTable.class);
            if (inTable != null) {
                tableName       = inTable.name();
                tableVersion    = inTable.version();
            }

            //如果自定义了表名，则是使用自定义值，否则使用类简称作为表名
            if (tableName == null || tableName.length() == 0) {
                tableName = cl.getSimpleName();
            }

            //获取类的字段信息
            HashMap<String, ColumnInfo> infos = getColumnInfo(cl);
            table = new TableInfo(cl, tableName, tableVersion, infos);
        }

        return table;
    }

    /**
     * 获取类的字段信息
     * @param cl
     * @return
     */
    public static HashMap<String, ColumnInfo> getColumnInfo(Class<?>  cl) {
        DbUtils.d(TAG, "getColumnInfo cl:" + cl.getName());
        HashMap<String, ColumnInfo> infos = null;
        if (cl != null) {
            infos = new HashMap<String, ColumnInfo>();
            Field[] fs = cl.getDeclaredFields();
            for (Field f:fs) {
                Column co = f.getAnnotation(Column.class);
                if (co != null) {
                	String fullName = f.getType().getName();
                	char[] name = getSimpleName(fullName);   
                	
                    int type = typeString2int(name);
                    //DbUtils.d(TAG, "getColumnInfo name:" + fullName + " type:" + type);
                    String fname = f.getName();
                    infos.put(fname, new ColumnInfo(f, fname, co.notNull(), co.primaryKey(), type));
                    /*DbUtils.d(TAG, "getColumnInfo type:" + type
                            + " name:" + f.getName()
                            + " notNull:" + co.notNull()
                            + " primaryKey:" + co.primaryKey());*/
                }
            }
        }

        return infos;
    }

    /**
     * 实现类似于class的getSimpleName
     * @param name
     * @return
     */
    public static char[] getSimpleName(String name) {
    	DbUtils.d(TAG, "getSimpleName:" + name);
    	if (name != null) {
			char[] chars = name.toCharArray();
			int len = chars.length;
			int get = 0;

            //查找'.'位置，计算需要复制的部分
			for(int i = len -1; i >= 0; i--) {
				if (chars[i] == '.') {
					break;
				}
				get++;//需要复制字符
			}

            //如果name不存在'.'，则全复制
            if (get == len) {
                return chars;
            }

            //复制'.'后部分
			char[] ret = new char[get];
			for(int i = 1 ; i <= get; i++) {
				ret[get - i] = chars[len - i];
			}
			return ret;
			
		}
    	return null;
    }

    /**
     * 对变量的某个字段赋值
     * @param field
     * @param dest
     * @param value
     */
    public static void setValue(Field field, int t, Object dest, Object value) {
        //DbUtils.d(TAG, "setValue  field.name:" + field.getName());
        try {
            /**需要根据字段不同数据类型调用不同的方法进行赋值*/
            //Object类，考虑到使用频率，独立出来放最前面
        	if(t == I_String) {
                //DbUtils.d(TAG, "setValue  set");
                field.set(dest, value);
            }
            else if (t == I_int) {
                //DbUtils.d(TAG, "setValue  setInt");
                field.setInt(dest, ((Integer)value).intValue());
            }
        	else if (t == I_short) {
                //DbUtils.d(TAG, "setValue  setShort");
                field.setShort(dest, ((Short)value).shortValue());
            }
        	else if (t == I_long) {
                //DbUtils.d(TAG, "setValue  setLong");
                field.setLong(dest, ((Long)value).longValue());
            }
            else if (t == I_boolean) {
                boolean bool = ((Boolean)value).booleanValue();
                //DbUtils.d(TAG, "setValue  setBoolean:"+bool);
                field.setBoolean(dest, bool);
            }            
            else if (t == I_byte) {
                 //DbUtils.d(TAG, "setValue  setByte");
                 field.setByte(dest, ((Byte)value).byteValue());
            }
            else if (t == I_char) {
                //DbUtils.d(TAG, "setValue  setChar");
                field.setChar(dest, ((Character)value).charValue());
            }
            else if (t == I_double) {
                //DbUtils.d(TAG, "setValue  setDouble");
                field.setDouble(dest, ((Double)value).doubleValue());
            }
            else if(t == I_float) {
                DbUtils.d(TAG, "setValue  setFloat");
                field.setFloat(dest, ((Float)value).floatValue());
            }
            //Object类，String也是Object，考虑到使用频率，独立出来放最前面
            else /*if(t == I_Byte
                ||  t == I_Boolean
                ||  t == I_Short
                ||  t == I_Character
                ||  t == I_Integer
                ||  t == I_Long
                ||  t == I_Double
                ||  t == I_Float)*/ {
                //DbUtils.d(TAG, "setValue  set");
                field.set(dest, value);
            }
        } catch (IllegalAccessException e) {
        	DbUtils.e(TAG, "setValue  e:" + e.toString());
        }
    }


    /**数据库数据类型:浮点类型*/
    private static String TYPE_REAL     = "REAL";
    /**数据库数据类型:文本类型*/
    private static String TYPE_STR      = "TEXT";
    /**数据库数据类型:数字类型*/
    private static String TYPE_INT      = "INTEGER";

    /**
     * 返回数据类型的对应数据库数据类型
     * @param t
     * @return
     */
    public static String getTypeString(int t) {
        if(t == I_float || t == I_Float) {
            return TYPE_REAL;
        }
        else if(t == I_String) {
            return TYPE_STR;
        }
        /*else if (t == I_byte)
        ||  t == I_Byte)
        ||  t == I_boolean)
        ||  t == I_Boolean)
        ||  t == I_short)
        ||  t == I_Short)
        ||  t == I_char)
        ||  t == I_Character)
        ||  t == I_int)
        ||  t == I_Integer)
        ||  t == I_long)
        ||  t == I_Long)
        ||  t == I_double)
        ||  t == I_Double) {
            return TYPE_INT;
        }*/

        return TYPE_INT;
    }


    /**
     * java数据转对应数据库数据
     * 数据库不支持boolean，char，需要转成int
     * @param type
     * @param ob
     * @return
     */
    public static Object changeValue(int type, Object ob) {
        //DbUtils.e(TAG, "changeValue type:"+type);
    	try {
        if (ob != null) {
            if (type == I_Boolean || type == I_boolean) {
                if (ob.toString().equals("false")) {
                    return 0;
                }
                else if (ob.toString().equals("true")) {
                    return 1;
                }
            }
            else if (type == I_Character || type == I_char) {
                return (int)((Character)ob).charValue();
            }
            else if (type == I_Byte || type == I_byte) {
                return (int)((Byte)ob).byteValue();
            }
        }
    	}catch(Throwable t) {
    		t.printStackTrace();
    		DbUtils.e(TAG, "changeValue type:"+type + " ob:" + ob.getClass().getName());
    	}

        return ob;
    }

    /**
     * 从数据库游标取值
     * @param cursor
     * @param index
     * @param t
     * @return
     */
    public static Object getCursorValue(Cursor cursor, int index, int t) {
    	DbUtils.d(TAG, "getCursorValue type:"+t);
        if (cursor != null) {
            /**需要根据字段不同数据类型调用不同的方法进行取值*/
            if (t == I_byte ||  t == I_Byte) {
                return (byte)cursor.getInt(index);
            }
            //如果是boolean，需要转换值类型
            else if (t == I_boolean ||  t == I_Boolean) {
                int bool = cursor.getInt(index);
                if (bool == 0) {
                    return false;
                }
                else {
                    return true;
                }
            }
            else if (t == I_short || t == I_Short) {
                return cursor.getShort(index);
            }
            else if (t == I_char || t == I_Character) {
                //如果是char，需要getInt
                return (char)cursor.getInt(index);
            }
            else if (t == I_int || t == I_Integer) {
                return cursor.getInt(index);
            }
            else if (t == I_long || t == I_Long) {
                return cursor.getLong(index);
            }
            else if (t == I_double || t == I_Double) {
                return cursor.getDouble(index);
            }
            else if(t == I_float || t == I_Float) {
                return cursor.getFloat(index);
            }
            else if(t == I_String) {
                return cursor.getString(index);
            }
        }
        else {
        	DbUtils.e(TAG, "getCursorValue err: null");
        }

        return null;
    }
}
