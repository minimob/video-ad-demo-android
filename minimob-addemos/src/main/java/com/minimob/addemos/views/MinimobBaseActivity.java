package com.minimob.addemos.views;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.minimob.addemos.R;

/**
 * Created by v.prantzos on 15/1/2016.
 */
public class MinimobBaseActivity extends AppCompatActivity
{
    public static String TAG = "MINIMOB";

    public void HandleCrash(String tag, Throwable ex)
    {
        ex.printStackTrace();
        Log.d(TAG + "-" + tag, ex.getMessage(), ex);
        Crashlytics.logException(ex);
    }

    protected void ToggleDrawer()
    {
        try
        {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer != null)
            {
                if (drawer.isDrawerOpen(GravityCompat.START))
                {
                    drawer.closeDrawer(GravityCompat.START);
                }
                else
                {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        }
        catch (Exception ex)
        {
            HandleCrash(TAG, ex);
        }
    }
}
