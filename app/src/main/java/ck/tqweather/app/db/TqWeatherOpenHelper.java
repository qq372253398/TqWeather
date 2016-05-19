package ck.tqweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ck on 2016/5/19.
 */
public class TqWeatherOpenHelper extends SQLiteOpenHelper {
    /*
    创建升级表 primary_name 省的名字
              primary_code 省的编号
     */
    public static final String CREATE_PROVINCE = "create table Province ("
            + "id integer primary key autoincrement"
            + "primary_name text"
            + "primary_code text)";
    /*
   创建升级表 city_name 市的名字
             city_code 市的编号
             province_id 关联city表关联province表的外键
    */
    public static final String CREATE_CITY = "create table City ("
            + "id integer primary key autoincrement"
            + "city_name text"
            + "city_code text"
            + "province_id integer)";
    /*
   创建升级表 county_name 县的名字
             county_code 县的编号
             city_id 关联county表关联city表的外键
    */
    public static final String CREATE_COUNTY = "create table County ("
            + "id integer primary key autoincrement"
            + "county_name text"
            + "county_code text"
            + "city_id integer)";

    public TqWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PROVINCE);
        sqLiteDatabase.execSQL(CREATE_CITY);
        sqLiteDatabase.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
