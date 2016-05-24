package ck.tqweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ck.tqweather.app.R;

/**
 * Created by ck on 2016/5/24.
 */
public class SplanshActivity extends Activity {
    LinearLayout ll_splash;

    /*
     * 闪屏页面
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);
        // 设置渐变动画效果
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1);
        ll_splash = (LinearLayout) findViewById(R.id.ll_splash);
        ll_splash.startAnimation(animation);
        // 定义线程
        Thread thrend = new Thread() {
            public void run() {
                // 等待三秒
                try {
                    // sleep线程等待函数
                    sleep(3000);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                } finally {
                    // 运行mainactivity
                    // Intent()传入AndroidManifest.xml里的对应的action：name
                    Intent openMainActivity = new Intent(
                            "android.intent.action.ChooseAreaActivity");
                    startActivity(openMainActivity);
                }
            }
        };
        // 启动线程
        thrend.start();

    }

    // 结束线程

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}


