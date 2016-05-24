package ck.tqweather.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ck.tqweather.app.service.AutoUpdateService;

/**
 * Created by ck on 2016/5/24.
 */
public class AutoUpdateRecevier extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
