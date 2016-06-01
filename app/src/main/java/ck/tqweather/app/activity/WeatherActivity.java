package ck.tqweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;

import ck.tqweather.app.R;
import ck.tqweather.app.adapter.galleryAdapter;
import ck.tqweather.app.service.AutoUpdateService;
import ck.tqweather.app.util.HttpCallbackListener;
import ck.tqweather.app.util.HttpUtil;
import ck.tqweather.app.util.Utility;

/**
 * Created by ck on 2016/5/23.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {

    private LinearLayout weather_info_layout;
    private TextView tv_cityname;
    private TextView tv_wendu;
    private TextView tv_state;
    private Gallery mygallery;
    private Button btn_home;
    private Button btn_refresh;
    private SharedPreferences sp;
    private SharedPreferences spf;
    private String[] mdate;
    private String[] mloworhigh;
    private String[] mtype;
    private String[] mfeng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_info_activity);
        sp = getSharedPreferences("weatherInfo", MODE_PRIVATE);
        spf = getSharedPreferences("weatherCode", MODE_PRIVATE);
        weather_info_layout = (LinearLayout) findViewById(R.id.weather_info_layout);
        tv_cityname = (TextView) findViewById(R.id.tv_cityname);
        tv_wendu = (TextView) findViewById(R.id.tv_wendu);
        tv_state = (TextView) findViewById(R.id.tv_state);
        mygallery = (Gallery) findViewById(R.id.myGallery);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        btn_home.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            //当他有县级代号时就去查天气
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
        //String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        String address = "http://wthrcdn.etouch.cn/weather_mini?citykey=" + weatherCode;
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
                            SharedPreferences.Editor editor = spf.edit();
                            editor.putString("weatherCode", weatherCode);
                            editor.commit();
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
                    }
                });
            }
        });
    }

    /*
    从sharepreferences文件中读取存储的天气信息，并显示到UI。
     */
    private void showWeather() {
        tv_cityname.setText(sp.getString("cityName", ""));
        tv_wendu.setText("当前温度：" + sp.getString("wendu", "") + "℃");
        tv_state.setText("         " + sp.getString("weatherState", ""));
        mdate = new String[]{sp.getString("yesterdaydate", "")
                , sp.getString("date", "")
                , sp.getString("tomorrowdate", "")
                , sp.getString("thirddaydate", "")};
        mloworhigh = new String[]{sp.getString("yesterdaylow", "") + "~" + sp.getString("yesterdayhigh", "")
                , sp.getString("low", "") + "~" + sp.getString("high", "")
                , sp.getString("tomorrowlow", "") + "~" + sp.getString("tomorrowhigh", "")
                , sp.getString("thirddaylow", "") + "~" + sp.getString("thirddayhigh", "")};
        mtype = new String[]{sp.getString("yesterdaytype", "")
                , sp.getString("type", "")
                , sp.getString("tomorrowtype", "")
                , sp.getString("thirddaytype", "")};
        mfeng = new String[]{sp.getString("yesterdayfengxiang", "") + ":" + sp.getString("yesterdayfengli", "")
                , sp.getString("fengxiang", "") + ":" + sp.getString("fengli", "")
                , sp.getString("tomorrowfengxiang", "") + ":" + sp.getString("tomorrowfengli", "")
                , sp.getString("thirddayfengxiang", "") + ":" + sp.getString("thirddayfengli", "")};
        mygallery.setAdapter(new galleryAdapter(WeatherActivity.this
                , mdate
                , mloworhigh
                , mtype
                , mfeng));
        mygallery.setSelection(1);
        mygallery.setSpacing(120);
        mygallery.setUnselectedAlpha(150.0f);

        weather_info_layout.setVisibility(View.VISIBLE);
        tv_cityname.setVisibility(View.VISIBLE);

        Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                finish();
                break;
            case R.id.btn_refresh:
                String weatherCode = spf.getString("weatherCode", "");
                if (!TextUtils.isEmpty(weatherCode)) {
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }
}
