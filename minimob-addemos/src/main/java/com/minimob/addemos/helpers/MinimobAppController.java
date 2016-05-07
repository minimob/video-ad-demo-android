package com.minimob.addemos.helpers;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import com.crashlytics.android.Crashlytics;
import com.minimob.adserving.helpers.MinimobHelper;

import io.fabric.sdk.android.Fabric;

/**
 * Created by v.prantzos on 21/1/2016.
 */
public class MinimobAppController extends MultiDexApplication
{
    public static final String TAG = MinimobAppController.class.getSimpleName();

    private static MinimobAppController mInstance;
    private static Context mContext;
    private static boolean DEVELOPER_MODE = false;

    private String _uniqueUserId = MinimobHelper.getInstance().getUniqueDeviceId();

    public static MinimobAppController getInstance()
    {
        return mInstance;
    }

    public static Context getAppContext()
    {
        return mContext;
    }

    @Override
    public void onCreate()
    {
        // STRICT MODE
        if (DEVELOPER_MODE)
        {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
            {
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .penaltyDeath()
                        .build());
            }
            else
            {
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .penaltyDeath()
                        .build());
            }
        }
        super.onCreate();

        mInstance = this;
        mContext = getApplicationContext();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Fabric.with(mContext, new Crashlytics());
                // Use the current user's information
                // You can call any combination of these three methods
                Crashlytics.setUserIdentifier(_uniqueUserId);
                //Crashlytics.setUserEmail("minimob.developer@fabric.io");
                //Crashlytics.setUserName("minimob.developer@gmail.com");
            }
        }).start();
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
