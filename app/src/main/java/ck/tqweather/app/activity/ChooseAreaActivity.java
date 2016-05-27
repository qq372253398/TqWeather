package ck.tqweather.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ck.tqweather.app.R;
import ck.tqweather.app.db.TqWeatherDB;
import ck.tqweather.app.model.City;
import ck.tqweather.app.model.County;
import ck.tqweather.app.model.Province;
import ck.tqweather.app.util.HttpCallbackListener;
import ck.tqweather.app.util.HttpUtil;
import ck.tqweather.app.util.Utility;

/**
 * Created by ck on 2016/5/20.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private List<Province> provincelist;
    private List<City> citylist;
    private List<County> countylist;
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;
    private TextView tv_title;
    private ListView lv_address;
    private ArrayAdapter<String> adapter;
    private TqWeatherDB tqWeatherDB;
    private List<String> datalist = new ArrayList<String>();
    //判断是否是从weatheractivity中跳转过来
    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area_activity);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        SharedPreferences sp = getSharedPreferences("weatherInfo", MODE_PRIVATE);
        if (sp.getBoolean("city_selected", false) && !isFromWeatherActivity) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            finish();
            return;
        }
        lv_address = (ListView) findViewById(R.id.address_list_view);
        tv_title = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datalist);
        lv_address.setAdapter(adapter);
        //获取数据库实例
        tqWeatherDB = TqWeatherDB.getInstance(this);
        lv_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (LEVEL_PROVINCE == currentLevel) {
                    selectedProvince = provincelist.get(position);
                    queryCities();
                } else if (LEVEL_CITY == currentLevel) {
                    selectedCity = citylist.get(position);
                    queryCounties();
                } else if (LEVEL_COUNTY == currentLevel) {
                    String countyCode = countylist.get(position).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("county_code", countyCode);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    finish();
                }
            }
        });
        //加载省级数据
        queryProvinces();
    }

    /*
    查询全国所有省，优先从数据库查，如果没有就去服务器上查
     */
    private void queryProvinces() {
        provincelist = tqWeatherDB.loadProvinces();
        if (0 < provincelist.size()) {
            datalist.clear();
            for (Province province : provincelist) {
                datalist.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            lv_address.setSelection(0);
            tv_title.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    /*
       查询全省所有市，优先从数据库查，如果没有就去服务器上查
        */
    private void queryCities() {
        citylist = tqWeatherDB.loadCities(selectedProvince.getId());
        if (0 < citylist.size()) {
            datalist.clear();
            for (City city : citylist) {
                datalist.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lv_address.setSelection(0);
            tv_title.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /*
     查询全市所有（地级市）县，优先从数据库查，如果没有就去服务器上查
    */
    private void queryCounties() {
        countylist = tqWeatherDB.loadCounties(selectedCity.getId());
        if (0 < countylist.size()) {
            datalist.clear();
            for (County county : countylist) {
                datalist.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            lv_address.setSelection(0);
            tv_title.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    /*
    查询选中市内所有的县，优先从数据库查询，如果没有就去服务器查询
     */
    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvincesResponse(tqWeatherDB, response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCitiesResponse(tqWeatherDB, response, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountiesResponse(tqWeatherDB, response, selectedCity.getId());
                }
                /*
                通过runOnUiThread（）方法回到主线程处理逻辑
                 */
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog() {
        if (null == progressDialog) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
            Log.d("dialog", "dialog");
        }
    }

    private void closeProgressDialog() {
        if (null != progressDialog) {
            progressDialog.dismiss();
            Log.d("dismissDialog", "dismissDialog");
        }
    }

    @Override
    public void onBackPressed() {
        if (LEVEL_COUNTY == currentLevel) {
            queryCities();
        } else if (LEVEL_CITY == currentLevel) {
            queryProvinces();
        } else {
            if (isFromWeatherActivity) {
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
            finish();
        }
    }
}
