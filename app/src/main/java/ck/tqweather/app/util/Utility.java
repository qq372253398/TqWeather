package ck.tqweather.app.util;

import android.text.TextUtils;

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
}

