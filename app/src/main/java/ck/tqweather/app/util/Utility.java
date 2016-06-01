package ck.tqweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ck.tqweather.app.db.TqWeatherDB;
import ck.tqweather.app.model.City;
import ck.tqweather.app.model.County;
import ck.tqweather.app.model.Province;

/**
 * Created by ck on 2016/5/20.
 */
public class Utility {
    /*
    解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(
            TqWeatherDB tqWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (null != allProvinces && 0 < allProvinces.length) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    tqWeatherDB.saveprovince(province);
                }
                return true;
            }
        }
        return false;
    }

    /*
   解析和处理服务器返回的市级数据
    */
    public synchronized static boolean handleCitiesResponse(
            TqWeatherDB tqWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (null != allCities && 0 < allCities.length) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setProvinceId(provinceId);
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    tqWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /*
  解析和处理服务器返回的县级数据
   */
    public synchronized static boolean handleCountiesResponse(
            TqWeatherDB tqWeatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (null != allCounties && 0 < allCounties.length) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCityId(cityId);
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    tqWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("data");
            String cityName = weatherInfo.getString("city");
            String wendu = weatherInfo.getString("wendu");
            String weatherState = weatherInfo.getString("ganmao");
            JSONArray forecast = weatherInfo.getJSONArray("forecast");
            JSONObject today = (JSONObject) forecast.opt(0);
            String fengxiang = today.getString("fengxiang");
            String fengli = today.getString("fengli");
            String high = today.getString("high");
            String type = today.getString("type");
            String low = today.getString("low");
            String date = today.getString("date");
            JSONObject tomorrow = (JSONObject) forecast.opt(1);
            String tomorrowfengxiang = tomorrow.getString("fengxiang");
            String tomorrowfengli = tomorrow.getString("fengli");
            String tomorrowhigh = tomorrow.getString("high");
            String tomorrowtype = tomorrow.getString("type");
            String tomorrowlow = tomorrow.getString("low");
            String tomorrowdate = tomorrow.getString("date");

            JSONObject thirdday = (JSONObject) forecast.opt(2);
            String thirddayfengxiang = thirdday.getString("fengxiang");
            String thirddayfengli = thirdday.getString("fengli");
            String thirddayhigh = thirdday.getString("high");
            String thirddaytype = thirdday.getString("type");
            String thirddaylow = thirdday.getString("low");
            String thirddaydate = thirdday.getString("date");

            JSONObject yesterday = weatherInfo.getJSONObject("yesterday");
            String yesterdayfengxiang = yesterday.getString("fx");
            String yesterdayfengli = yesterday.getString("fl");
            String yesterdayhigh = yesterday.getString("high");
            String yesterdaytype = yesterday.getString("type");
            String yesterdaylow = yesterday.getString("low");
            String yesterdaydate = yesterday.getString("date");

            saveWeatherInfo(context, cityName, wendu, weatherState, fengxiang, fengli, high, type, low, date,
                    tomorrowhigh, tomorrowtype, tomorrowlow, tomorrowdate,
                    thirddayhigh, thirddaytype, thirddaylow, thirddaydate,
                    yesterdayhigh, yesterdaytype, yesterdaylow, yesterdaydate,
                    tomorrowfengxiang, tomorrowfengli, thirddayfengxiang, thirddayfengli, yesterdayfengxiang, yesterdayfengli);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    将服务器返回的所有天气信息存储到sharedPreference
     */

    private static void saveWeatherInfo(Context context, String cityName, String wendu, String weatherState, String fengxiang, String fengli, String high, String type, String low, String date,
                                        String tomorrowhigh, String tomorrowtype, String tomorrowlow, String tomorrowdate,
                                        String thirddayhigh, String thirddaytype, String thirddaylow, String thirddaydate,
                                        String yesterdayhigh, String yesterdaytype, String yesterdaylow, String yesterdaydate,
                                        String tomorrowfengxiang, String tomorrowfengli, String thirddayfengxiang, String thirddayfengli,
                                        String yesterdayfengxiang, String yesterdayfengli) {


        SharedPreferences sp = context.getSharedPreferences("weatherInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("city_selected", true);
        editor.putString("cityName", cityName);
        editor.putString("wendu", wendu);
        editor.putString("weatherState", weatherState);
        editor.putString("fengxiang", fengxiang);
        editor.putString("fengli", fengli);
        editor.putString("high", high);
        editor.putString("low", low);
        editor.putString("type", type);
        editor.putString("date", date);
        editor.putString("tomorrowhigh", tomorrowhigh);
        editor.putString("tomorrowtype", tomorrowtype);
        editor.putString("tomorrowlow", tomorrowlow);
        editor.putString("tomorrowdate", tomorrowdate);
        editor.putString("thirddayhigh", thirddayhigh);
        editor.putString("thirddaytype", thirddaytype);
        editor.putString("thirddaylow", thirddaylow);
        editor.putString("thirddaydate", thirddaydate);
        editor.putString("yesterdayhigh", yesterdayhigh);
        editor.putString("yesterdaytype", yesterdaytype);
        editor.putString("yesterdaylow", yesterdaylow);
        editor.putString("yesterdaydate", yesterdaydate);
        editor.putString("tomorrowfengxiang", tomorrowfengxiang);
        editor.putString("tomorrowfengli", tomorrowfengli);
        editor.putString("thirddayfengxiang", thirddayfengxiang);
        editor.putString("thirddayfengli", thirddayfengli);
        editor.putString("yesterdayfengxiang", yesterdayfengxiang);
        editor.putString("yesterdayfengli", yesterdayfengli);
        editor.commit();
    }

}
