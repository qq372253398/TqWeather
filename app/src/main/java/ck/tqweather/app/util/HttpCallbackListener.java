package ck.tqweather.app.util;

/**
 * Created by ck on 2016/5/23.
 */
public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);
}

