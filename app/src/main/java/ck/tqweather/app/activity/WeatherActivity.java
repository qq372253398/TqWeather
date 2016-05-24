package ck.tqweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ck.tqweather.app.R;
import ck.tqweather.app.util.HttpCallbackListener;
import ck.tqweather.app.util.HttpUtil;
import ck.tqweather.app.util.Utility;

/**
 * Created by ck on 2016/5/23.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {

    private LinearLayout weather_info_layout;
    private TextView tv_cityname;
    private TextView tv_publish;
    private TextView tv_weather_desp;
    private TextView tv_temp1;
    private TextView tv_temp2;
    private TextView tv_current_date;
    private Button btn_home;
    private Button btn_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_info_activity);
        weather_info_layout = (LinearLayout) findViewById(R.id.weather_info_layout);
        tv_cityname = (TextView) findViewById(R.id.tv_cityname);
        tv_publish = (TextView) findViewById(R.id.tv_publish);
        tv_weather_desp = (TextView) findViewById(R.id.tv_weather_desp);
        tv_temp1 = (TextView) findViewById(R.id.tv_temp1);
        tv_temp2 = (TextView) findViewById(R.id.tv_temp2);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        tv_current_date = (TextView) findViewById(R.id.tv_current_date);
        btn_home.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            //当他有县级代号时就去查天气
            tv_publish.setText("同步中...");
            weather_info_layout.setVisibility(View.INVISIBLE);
            tv_cityname.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            /*
            没有县级代号时显示本地天气
             */
            showWeather();
        }
    }

    /*
    查询县级代号对应的天气代号
     */
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }

    /*
   查询天气代号对应的天气
    */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }

    /*
    根据传入的地址和类型去向服务器查询天气代号或者是天气信息
     */
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        //从服务器返回数据中解析天气代号
                        String[] array = response.split("\\|");
                        if (null != array && 2 == array.length) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_publish.setText("同步失败");
                    }
                });
            }
        });
    }

    /*
    从sharepreferences文件中读取存储的天气信息，并显示到UI。
     */
    private void showWeather() {
        SharedPreferences sp = getSharedPreferences("weatherInfo", MODE_PRIVATE);
        tv_cityname.setText(sp.getString("city_name", ""));
        tv_temp1.setText(sp.getString("temp1", ""));
        tv_temp2.setText(sp.getString("temp2", ""));
        tv_weather_desp.setText(sp.getString("weather_desp", ""));
        tv_publish.setText(sp.getString("publish_time", "") + "发布");
        tv_current_date.setText(sp.getString("current_date", ""));
        weather_info_layout.setVisibility(View.VISIBLE);
        tv_cityname.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_refresh:
                tv_publish.setText("同步中...");
                SharedPreferences sp = getSharedPreferences("weatherInfo", MODE_PRIVATE);
                String weatherCode = sp.getString("weather_code", "");
                if (!TextUtils.isEmpty(weatherCode)) {
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }
}
