package ck.tqweather.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ck.tqweather.app.model.City;
import ck.tqweather.app.model.County;
import ck.tqweather.app.model.Province;

/**
 * Created by ck on 2016/5/19.
 */
public class TqWeatherDB {
    /*
    数据库名字
     */
    public static final String DB_NAME = "tq_weather";
    /*
    数据库版本
     */
    public static final int DB_VISION = 1;
    private static TqWeatherDB tqWeatherDB;
    private SQLiteDatabase db;

    /*
    将构造方法私有化
     */
    private TqWeatherDB(Context context) {
        TqWeatherOpenHelper dbHelper = new TqWeatherOpenHelper(context, DB_NAME, null, DB_VISION);
        db = dbHelper.getWritableDatabase();
    }

    /*
    获取TqWeatherDB实例
    synchronized一次只能一个线程进入这个方法，如果有其他线程正在使用，就只能等待
     */
    public synchronized static TqWeatherDB getInstance(Context context) {
        if (null == tqWeatherDB) {
            tqWeatherDB = new TqWeatherDB(context);
        }
        return tqWeatherDB;
    }

    /*
    将Province实例存储到数据库
     */
    public void saveprovince(Province province) {
        if (null != province) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    /*
    从数据库读取全国所有的省份信息
     */
    public List<Province> loadProvinces() {
        List<Province> provincelist = new ArrayList<Province>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                provincelist.add(province);
            } while (cursor.moveToNext());
        }
        if (null != cursor) {
            cursor.close();
        }
        return provincelist;
    }

    /*
    将City实例存储到数据库
     */
    public void saveCity(City city) {
        if (null != city) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            db.insert("City", null, values);
        }
    }

    /*
    将数据库读取某些省下所有城市的信息
     */
    public List<City> loadCities(int provinceId) {
        List<City> citylist = new ArrayList<City>();
        Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                citylist.add(city);
            } while (cursor.moveToNext());
        }
        if (null != cursor) {
            cursor.close();
        }
        return citylist;
    }

    /*
    将County实例存储到数据库
     */
    public void saveCounty(County county) {
        ContentValues values = new ContentValues();
        values.put("county_name", county.getCountyName());
        values.put("county_code", county.getCountyCode());
        values.put("city_id", county.getCityId());
        db.insert("County", null, values);
    }

    /*
   将数据库读取某些市下所有县的信息
    */
    public List<County> loadCounties(int cityId) {
        List<County> countylist = new ArrayList<County>();
        Cursor cursor = db.query("County", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                countylist.add(county);
            } while (cursor.moveToNext());
        }
        if (null != cursor) {
            cursor.close();
        }
        return countylist;
    }
}
