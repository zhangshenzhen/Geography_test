package com.lbs;

import android.app.Application;
import android.content.Context;

/**
 * Created by lx on 2018-03-30.
 */

public class LbsApplication extends Application {

    //获取上下文
    private static Context context;
    public static Context getAppContext() {
        return LbsApplication.context;
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
