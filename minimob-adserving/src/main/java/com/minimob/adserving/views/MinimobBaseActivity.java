package com.minimob.adserving.views;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;

import com.minimob.adserving.interfaces.IMinimobAdListener;

/**
 * Created by v.prantzos on 15/1/2016.
 */
public class MinimobBaseActivity extends AppCompatActivity implements IMinimobAdListener
{
    public static String TAG = "MINIMOB";

    public void HandleCrash(String tag, Throwable ex)
    {
        ex.printStackTrace();
        Log.d(TAG + "-" + tag, ex.getMessage(), ex);
    }

    public void LogMessage(String suffix, String message)
    {
        Log.d(TAG + "-" + suffix, message);
    }

    public void LogError(String suffix, String message)
    {
        Log.e(TAG + "-" + suffix, message);
}

    public void ShowFragment(int container, Fragment fragment)
    {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragment != null)
        {
            String tag = ((Object) fragment).getClass().toString();
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(container, fragment, tag)
                    // DON'T add to the stack!
                    //.addToBackStack(null)
                    .commit();

            fragmentManager.executePendingTransactions();
        }
    }

    public static String getAppVersionName(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public Context GetAppContext()
    {
        return this.getApplicationContext();
    }

    public Context GetActivityContext()
    {
        return this.getBaseContext();
    }

    public DisplayMetrics GetDisplayMetrics()
    {
        return this.getResources().getDisplayMetrics();
    }

    public void RunOnUIThread(Runnable runnable)
    {
        this.runOnUiThread(runnable);
    }
}
