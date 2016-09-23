package iitkgp.btp.activitytracker;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.splunk.mint.Mint;

/**
 * Created by sayan on 23/9/16.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate()
    {
        super.onCreate();
        Mint.enableDebugLog();
        Mint.initAndStartSession(this, "4d58ba58");
        Mint.enableLogging(true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
