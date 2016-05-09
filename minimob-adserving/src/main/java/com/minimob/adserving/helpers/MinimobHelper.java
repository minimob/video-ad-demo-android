package com.minimob.adserving.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.minimob.adserving.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by v.prantzos on 15/1/2016.
 */
public class MinimobHelper
{
    //region VARIABLES
    private String TAG = "MINIMOB-" + MinimobHelper.class.getSimpleName();

    private static final MinimobHelper _instance = new MinimobHelper();

    FrameLayout _loadingView;
    private Toast _lastToast;

    public long videoCachingTimeInSeconds = 300;
    public final String ADCLICK_INTENT_ACTION = "com.minimob.adserving.adclick";
    public final String ADCLICK_INTENT_EXTRA_CLICKURL = "clickUrl";
    //endregion VARIABLES

    //region CONSTRUCTORS
    private MinimobHelper() {
    }

    public static MinimobHelper getInstance()
    {
        return _instance;
    }
    //endregion CONSTRUCTORS

    //region Misc
    public String getUniqueId()
    {
        GregorianCalendar cal = new GregorianCalendar();

        String utcNowString = String.format(Locale.US, "%02d%02d%02d%02d%02d",
                //cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND)
        );

        Integer id = Integer.valueOf(utcNowString);
        return String.valueOf(id);
    }

    public String getUniqueDeviceId()
    {
        try
        {
            String deviceId;
            String serial;
            try
            {
                deviceId = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);
                serial = Build.class.getField("SERIAL").get(null).toString();
                return new UUID(deviceId.hashCode(), serial.hashCode()).toString();
            }
            catch (Exception ex)
            {
                Log.e(TAG, ex.getMessage());
                serial = "serial";
                deviceId = "deviceid";
            }
            return new UUID(deviceId.hashCode(), serial.hashCode()).toString();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e(TAG + "-" + "getUniqueDeviceId", ex.getMessage());
        }
        return "";
    }

    public boolean checkConnectivity(Context ctx)
    {
        try
        {
            ConnectivityManager conMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

            return conMgr.getActiveNetworkInfo() != null  &&
                    (conMgr.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED
                            || conMgr.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTING)
                    &&
                    (conMgr.getActiveNetworkInfo().getState() != NetworkInfo.State.DISCONNECTED
                            && conMgr.getActiveNetworkInfo().getState() != NetworkInfo.State.DISCONNECTED
                            && conMgr.getActiveNetworkInfo().getState() != NetworkInfo.State.UNKNOWN);
        }
        catch (Exception ex)
        {
            handleCrash(TAG + "-" + "checkConnectivity", ex);
        }
        return false;
    }

    public String getDateTimeString(Date date, boolean isLocal)
    {
        try
        {
            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);

            // if the parameter is null, we use the current Date
            Date dt = new Date();
            if (date != null)
            {
                dt = date;
            }

            if (!isLocal)
            {
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
            }

            return df.format(dt);
        }
        catch (Exception ex)
        {
            Log.e(TAG + "-" + "getSQLFullDateTime", ex.getMessage());
            return "";
        }
    }

    public String getTimeString(Date date, boolean isLocal)
    {
        try
        {
            DateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.US);

            // if the parameter is null, we use the current Date
            Date dt = new Date();
            if (date != null)
            {
                dt = date;
            }

            if (!isLocal)
            {
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
            }

            return df.format(dt);
        }
        catch (Exception ex)
        {
            Log.e(TAG + "-" + "getSQLFullDateTime", ex.getMessage());
            return "";
        }
    }

    public String getFullDateTime(Date date, boolean isLocal)
    {
        try
        {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

            // if the parameter is null, we use the current Date
            Date dt = new Date();
            if (date != null)
            {
                dt = date;
            }

            if (!isLocal)
            {
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
            }

            return df.format(dt);
        }
        catch (Exception ex)
        {
            Log.e(TAG + "-" + "getSQLFullDateTime", ex.getMessage());
            return "";
        }
    }

    public Date convertDate(Date date, String format)
    {
        try
        {
            if (format.equals(""))
            {
                format = "yyyy-MM-dd";
            }

            DateFormat df = new SimpleDateFormat(format, Locale.US);

            // if the parameter is null, we use the current Date
            Date dt = new Date();
            if (date != null)
            {
                dt = date;
            }

            String sDate = df.format(dt);

            date = df.parse(sDate);
        } catch (Exception ex)
        {
            Log.e(TAG + "-" + "convertDate", ex.getMessage());
        }
        return date;
    }

    public String getStringFromFileUrl(Context context, String fileURL)
    {
        StringBuilder sb = new StringBuilder();
        String[] urlElements = fileURL.split("/");
        if (urlElements[3].equals("android_asset"))
        {
            try
            {
                InputStream is = context.getResources().getAssets().open(urlElements[4]);
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(is));

                // do reading, usually loop until end of file reading
                String line = bufferReader.readLine();
                sb.append(line);
                while (line != null)
                {
                    line = bufferReader.readLine();
                    sb.append(line);
                }

                bufferReader.close();
            }
            catch (IOException e)
            {
                handleCrash("Error fetching file: " + e.getMessage(), e);
            }

            logMessage("MinimobHelper", sb.toString());
            return sb.toString();
        }
        else
        {
            logMessage("MinimobHelper", "Unknown location to fetch file content");
        }
        return "";
    }


    public void animFadeInView(Context c, View v)
    {
        if (v != null)
        {
            animFadeInView(c, v, 0, 0, null);
        }
    }

    private void animFadeInView(Context c, View v, int offset, int duration, Animation.AnimationListener listener)
    {
        Animation animFadeIn = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);
        if (duration > 0) {
            animFadeIn.setDuration(duration);
        }
        if (offset > 0) {
            animFadeIn.setStartOffset(offset);
        }
        if (listener != null) {
            animFadeIn.setAnimationListener(listener);
        }
        v.startAnimation(animFadeIn);
        v.setVisibility(View.VISIBLE);
    }

    public void animFadeOutView(Context c, View v)
    {
        if (v != null)
        {
            animFadeOutView(c, v, 0, 0, null);
        }
    }

    private void animFadeOutView(Context c, View v, int offset, int duration, Animation.AnimationListener listener)
    {
        Animation animFadeOut = AnimationUtils.loadAnimation(c, android.R.anim.fade_out);
        if (duration > 0) {
            animFadeOut.setDuration(duration);
        }
        if (offset > 0) {
            animFadeOut.setStartOffset(offset);
        }
        if (listener != null) {
            animFadeOut.setAnimationListener(listener);
        }
        v.startAnimation(animFadeOut);
        v.setVisibility(View.GONE);
    }

    public void showToast(final Activity activity, final String message, final int duration) {
        showToast(activity, message, duration, false);
    }

    private void showToast(final Activity activity, final String message, final int duration, final boolean cancelPrevious)
    {
        if (activity == null)
        {
            return;
        }

        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if (_lastToast != null && cancelPrevious)
                    {
                        _lastToast.cancel();
                    }
                    _lastToast = Toast.makeText(activity.getApplicationContext(), message, duration);
                    _lastToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, (int) (100f * getDisplayMetrics(activity).density));
                    _lastToast.show();
                }
                catch(Exception ex)
                {
                    handleCrash(ex);
                }
            }
        });
    }

    public void handleCrash(String tag, Throwable ex)
    {
        ex.printStackTrace();
        Log.e(TAG + "-" + tag, ex.getMessage(), ex);
    }

    public void handleCrash(Throwable ex)
    {
        ex.printStackTrace();
        Log.e(TAG, ex.getMessage(), ex);
    }

    public void logMessage(String suffix, String message)
    {
        Log.d(TAG + "-" + suffix, message);
    }
    public void logError(String suffix, String message)
    {
        Log.e(TAG + "-" + suffix, message);
    }

    public DisplayMetrics getDisplayMetrics(Activity activity)
    {
        return activity.getResources().getDisplayMetrics();
    }

    public boolean isGooglePlayStoreInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try
        {
            PackageInfo info = pm.getPackageInfo("com.android.vending", PackageManager.GET_ACTIVITIES);
            String label = (String) info.applicationInfo.loadLabel(pm);
            app_installed = (label != null && !label.equals("Market"));
        }
        catch (PackageManager.NameNotFoundException e)
        {
            app_installed = false;
        }
        return app_installed;
    }

    public void toggleLoading(Activity activity, boolean show)
    {
        if (show)
        {
            _loadingView = (FrameLayout) activity.getLayoutInflater().inflate(R.layout.loading, null);

            // disable touches
            _loadingView.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    return true;
                }
            });

            activity.addContentView(_loadingView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            MinimobHelper.getInstance().animFadeInView(activity, _loadingView);
        }
        else
        {
            if (_loadingView != null)
            {
                MinimobHelper.getInstance().animFadeOutView(activity, _loadingView);

                FrameLayout rootView = (FrameLayout) activity.findViewById(R.id.container);
                if (rootView != null)
                {
                    rootView.removeView(_loadingView);
                }

                _loadingView = null;
            }
        }
    }
    //endregion Misc
}
