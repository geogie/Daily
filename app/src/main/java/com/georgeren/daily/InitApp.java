package com.georgeren.daily;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * Created by georgeRen on 2017/8/28.
 */

public class InitApp extends Application {
    public static Context AppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.addLogAdapter(new AndroidLogAdapter());
        AppContext = getApplicationContext();
        if (BuildConfig.DEBUG) {
            SdkManager.initStetho(AppContext);
        }
    }
}
