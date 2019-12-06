package com.beyondxia.host;

import android.app.Application;

import com.beyondxia.modules.BuildConfig;
import com.beyondxia.modules.ServiceHelper;

/**
 * Created by xiaojunxia on 2018/9/26.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ServiceHelper.init(this, BuildConfig.DEBUG,true);
    }
}
