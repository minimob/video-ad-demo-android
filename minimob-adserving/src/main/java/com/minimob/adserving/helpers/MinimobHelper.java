package com.minimob.adserving.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
import com.minimob.adserving.R;
import com.minimob.adserving.adzones.AdZone;
import com.minimob.adserving.common.MinimobWebView;
import com.minimob.adserving.views.LoadingFragment;
import com.minimob.adserving.views.MinimobBaseActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by v.prantzos on 15/1/2016.
 */
public class MinimobHelper
{
    //region VARIABLES
    private String TAG = "MINIMOB-" + MinimobHelper.class.getSimpleName();

    private static final MinimobHelper _instance = new MinimobHelper();

    private LoadingFragment _loadingFragment;
    FrameLayout _loadingView;
    private Toast _lastToast;

    public long videoCachingTimeInSeconds = 300;
    public String gaid;
    public final String ADCLICK_INTENT_ACTION = "com.minimob.adserving.adclick";
    public final String ADCLICK_INTENT_EXTRA_CLICKURL = "clickUrl";


    public enum AdStatus
    {
        AD_STATUS_UNKNOWN(0),
        ADS_AVAILABLE(1),
        ADS_NOT_AVAILABLE(2);

        private int value;

        AdStatus(int value)
        {
            this.value = value;
        }

        public AdStatus getEnumValue(int ordinal)
        {
            for (AdStatus enumVal : EnumSet.allOf(AdStatus.class)) {
                if (enumVal.value == ordinal)
                    return enumVal;
            }
            return null;
        }

        public String asString()
        {
            switch (this)
            {
                case ADS_NOT_AVAILABLE: return "ads NOT available";
                case AD_STATUS_UNKNOWN: return "ads status unknown";
                case ADS_AVAILABLE: return "ads available";
            }
            return this.toString();
        }

        public int getValue()
        {
            return this.value;
        }
    }
    //endregion VARIABLES

    //region CONSTRUCTORS
    private MinimobHelper(){
        _loadingFragment = new LoadingFragment();
    }

    public static MinimobHelper getInstance()
    {
        return _instance;
    }
    //endregion CONSTRUCTORS

    //region AdTag Settings
    public boolean checkWifiConnectivity(Context ctx)
    {
        try
        {
            ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connManager.getActiveNetworkInfo();
            return netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI &&  netInfo.isConnected();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e(TAG + "-" + "checkWifiConnectivity", ex.getMessage());
        }
        return false;
    }

    private int getMNC(Context ctx)
    {
        int mnc = 0;
        try
        {
            TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

            if(telephonyManager != null)
            {
                String networkOperator = telephonyManager.getNetworkOperator();

                if (networkOperator != null && !networkOperator.isEmpty())
                {
                    mnc = Integer.parseInt(networkOperator.substring(3));
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e(TAG + "-" + "getMCC", ex.getMessage());
        }
        return mnc;
    }

    private int getMCC(Context ctx)
    {
        int mcc = 0;
        try
        {
            TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

            if (telephonyManager != null)
            {
                String networkOperator = telephonyManager.getNetworkOperator();

                if (networkOperator != null && !networkOperator.isEmpty())
                {
                    mcc = Integer.parseInt(networkOperator.substring(0, 3));
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e(TAG + "-" + "getMCC", ex.getMessage());
        }

        return mcc;
    }

    private String getIMEI(Context ctx)
    {
        String imei = "";
        try
        {
            TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

            if (telephonyManager != null)
            {
                imei = telephonyManager.getDeviceId();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e(TAG + "-" + "getIMEI", ex.getMessage());
        }
        return imei;
    }

    // NOTE: This method CANNOT be run from the UI thread.
//    public void getGAID(final Context ctx)
//    {
//        new Thread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                // Moves the current Thread into the background
//                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
//
//                AdvertisingIdClient.AdInfo adInfo = null;
//                try
//                {
//                    adInfo = AdvertisingIdClient.getAdvertisingIdInfo(ctx);
//                    final String id = adInfo.getId();
//                    //final boolean isLAT = adInfo.isLimitAdTrackingEnabled();
//                    gaid = id;
//                }
//                catch (Exception ex)
//                {
//                    // Unrecoverable error connecting to Google Play services (e.g.,
//                    // the old version of the service doesn't support getting AdvertisingId).
//                    ex.printStackTrace();
//                    Log.e(TAG + "-" + "getGAID", ex.getMessage());
//                }
//            }
//        }).start();
//    }

    private float getDeviceDensity(Context ctx)
    {
        return ctx.getResources().getDisplayMetrics().density;
    }

    private int[] getWebViewDimensions(WebView webView)
    {
        int width = webView.getWidth();
        int height = webView.getHeight();

        int[] dims = new int[2];
        dims[0] = width;
        dims[1] = height;

        return dims;
    }

    private int[] getScreenDimensions(Context ctx)
    {
        WindowManager windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        int[] dims = new int[2];
        dims[0] = width;
        dims[1] = height;

        return dims;
    }

    private String getAndroidId(Context ctx)
    {
        return Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private double[] getLatLon(Context ctx)
    {
        double[] latlon = new double[2];
        LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager == null)
        {
            return latlon;
        }

        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            latlon[0] = 0;
            latlon[1] = 0;
        }
        else
        {
            Location location = null;
            if (isNetworkEnabled)
            {
                // Assume thisActivity is the current activity
                if (checkPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION))
                {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }

            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled)
            {
                // Assume thisActivity is the current activity
                if (checkPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }

            if (location != null)
            {
                latlon[0] = location.getLatitude();
                latlon[1] = location.getLongitude();
            }
        }

        return latlon;
    }
    //endregion AdTag Settings

    //region WebViews
    public void toggleWebViewVisibility(final MinimobWebView minimobWebView, boolean visible)
    {
        int visibility = visible ? View.VISIBLE : View.GONE;
        // make webview visible
        minimobWebView.setVisibility(visibility);

        // make the container of the webview visible
        ((View) minimobWebView.getParent()).setVisibility(visibility);

        Log.i(TAG, "MinimobWebView with id:" + minimobWebView.getWebViewId() + " is " + (visible ? "visible" : "gone"));
    }

    public MinimobWebView loadMinimobWebView(MinimobWebView minimobWebView, Activity activity, String minimobScript, boolean isInterstitial, boolean isFullScreen, boolean isVideo)
    {
        MinimobWebView result = minimobWebView;

        // initialize webview in case it came from an inflated webview
        result.init(activity, isInterstitial, isFullScreen, isVideo);

//        // check for cached webview first
//        if (adListener.isCachingEnabled() && !isVideo)
//        {
//            // check if we have it cached
//            MinimobWebView cachedWebView = adListener.getWebView(minimobWebView.getWebViewId());
//
//            // we check for the ids to be the same due to a weird bug.
//            if (cachedWebView != null && cachedWebView.getWebViewId().equals(minimobWebView.getWebViewId()))
//            {
//                result = cachedWebView;
//                Log.d(TAG + "-" + "loadMinimobWebView", "Restored webView with webViewId " + minimobWebView.getWebViewId() + " from the cached webview with id " + cachedWebView.getWebViewId());
//                return result;
//            }
//        }

        if (!minimobScript.isEmpty())
        {
            String html = generateHtml(activity.getApplicationContext(), minimobScript, isVideo, result);

            // load url
            result.loadDataWithBaseURL(AdTagHelper.getInstance().baseUrl, html, "text/html", "utf-8", null);
            Log.d(TAG + "-" + "loadMinimobWebView", "Loaded the url to webView with webViewId " + result.getWebViewId());
        }

        return result;
    }

    private String generateHtml(Context ctx, String minimobScript, boolean isVideo, MinimobWebView minimobWebView)
    {
        StringBuffer processedHtml = new StringBuffer();

        try
        {
            // first fill in the adTag settings
            String processedMinimobScript = fillInAdTagSettings(ctx, minimobScript, minimobWebView);

            // begin building the rest of the Html
            processedHtml = new StringBuffer(processedMinimobScript);
            String ls = System.getProperty("line.separator");

            String regex;
            Pattern pattern;
            Matcher matcher;

            // Add html, head, and body tags.
            String body = "<body>";
            if (isVideo)
            {
                body = "<body style=\"background-color:#000000\">";
            }
            //<script src="http://172.30.8.123:8080/target/target-script-min.js#anonymous"></script>
            processedHtml.insert(0, "<html>" + ls + "<head>" + ls +/*"<script src=\"http://172.30.6.171:8085/target/target-script-min.js#anonymous\"></script>"+*/ "</head>" + ls + body + /*"<div align='center'>" +*/ ls);
            processedHtml.append(/*"</div></body>"*/"</body>" + ls + "</html>");

            // Add meta tag to head tag.
            String metaTag = "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1\">";

            regex = "<head[^>]*>";
            pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(processedHtml);
            int idx = 0;
            while (matcher.find(idx)) {
                processedHtml.insert(matcher.end(), ls + metaTag);
                idx = matcher.end();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e(TAG + "-" + "generateHtml", ex.getMessage());
        }

        return processedHtml.toString();
    }

    private String fillInAdTagSettings(Context ctx, String minimobScript, MinimobWebView minimobWebView)
    {
        try
        {
            minimobScript = setAdTagSetting("[imei]", getIMEI(ctx), minimobScript);
            minimobScript = setAdTagSetting("[android_id]", getAndroidId(ctx), minimobScript);
            minimobScript = setAdTagSetting("[gaid]", gaid, minimobScript);
            minimobScript = setAdTagSetting("[idfa]", "", minimobScript);
            minimobScript = setAdTagSetting("[idfv]", "", minimobScript);
            minimobScript = setAdTagSetting("[category]", "", minimobScript);
            minimobScript = setAdTagSetting("[age]", "", minimobScript);
            minimobScript = setAdTagSetting("[gender]", "", minimobScript);
            minimobScript = setAdTagSetting("[keywords]", "", minimobScript);
            minimobScript = setAdTagSetting("[lat]", String.valueOf(getLatLon(ctx)[0]), minimobScript);
            minimobScript = setAdTagSetting("[lon]", String.valueOf(getLatLon(ctx)[1]), minimobScript);
            minimobScript = setAdTagSetting("[device_width]", String.valueOf(getScreenDimensions(ctx)[0]), minimobScript);
            minimobScript = setAdTagSetting("[device_height]", String.valueOf(getScreenDimensions(ctx)[1]), minimobScript);
            minimobScript = setAdTagSetting("[mnc]", String.valueOf(getMNC(ctx)), minimobScript);
            minimobScript = setAdTagSetting("[mcc]", String.valueOf(getMCC(ctx)), minimobScript);
            minimobScript = setAdTagSetting("[wifi]", String.valueOf(checkWifiConnectivity(ctx)), minimobScript);
            minimobScript = setAdTagSetting("[placement_width]", String.valueOf(getWebViewDimensions(minimobWebView)[0]), minimobScript);
            minimobScript = setAdTagSetting("[placement_height]", String.valueOf(getWebViewDimensions(minimobWebView)[0]), minimobScript);
            minimobScript = setAdTagSetting("[android_version]", String.valueOf(Build.VERSION.SDK_INT), minimobScript);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e(TAG + "-" + "fillInAdTagSettings", ex.getMessage());
        }

        return minimobScript;
    }

    private String setAdTagSetting(String container, String value, String text)
    {
        try
        {
            // if there is no value, then don't replace.
            if (value==null || value.isEmpty())
            {
                return text;
            }

            text = text.replace(container, value);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e(TAG + "-" + "setAdTagSetting", ex.getMessage());
        }
        return text;
    }
    //endregion WebViews

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

    public String getUniqueCustomDataId()
    {
        try
        {
            String deviceId;
            String serial;
            try
            {
                deviceId = "36" + (Build.BRAND.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10);
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
            Log.e(TAG + "-" + "getUniqueCustomDataId", ex.getMessage());
        }
        return "";
    }

    public boolean checkConnectivity(Context ctx) {
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
            ex.printStackTrace();
            Log.e(TAG + "-" + "checkConnectivity", ex.getMessage());
        }
        return false;
    }

    private void setWifiTethering(Context ctx, boolean enable) {
        try
        {
            WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);

            Method[] methods = wifiManager.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals("setWifiApEnabled")) {
                    try
                    {
                        method.invoke(wifiManager, null, enable);
                    }
                    catch (Exception ex)
                    {
                        Log.e(TAG + "-" + "setWifiTethering", ex.getMessage());
                    }
                    break;
                }
            }
        }
        catch (Exception ex)
        {
            Log.e("setWifiTetheringEnabled", ex.getMessage());
        }
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

    public String getSQLFullDateTime(Date date, boolean isLocal)
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
            } catch (IOException e)
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

    public void showActionBarProgress(ProgressBar abProgress, Context ctx) {
        if (abProgress != null && ctx != null) {
            animFadeInView(ctx, abProgress);
        }
    }

    public void hideActionBarProgress(ProgressBar abProgress, Context ctx) {
        if (abProgress != null && ctx != null) {
            animFadeOutView(ctx, abProgress);
        }
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

    private boolean checkPermission(Context ctx, String permission)
    {
        //int res = ctx.checkSelfPermission(permission);
        int res = ctx.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
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
        Log.d(TAG + "-" + tag, ex.getMessage(), ex);
    }

    public void handleCrash(Throwable ex)
    {
        ex.printStackTrace();
        Log.d(TAG, ex.getMessage(), ex);
    }

    public void logMessage(String suffix, String message)
    {
        Log.d(TAG + "-" + suffix, message);
    }
    public void logError(String suffix, String message)
    {
        Log.d(TAG + "-" + suffix, message);
    }

    public String getAppVersionName(Context context)
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

    public void showProgress(ProgressBar abProgress, Activity activity, final boolean show)
    {
        try
        {
            if (show)
            {
                this.showActionBarProgress(abProgress, activity.getApplicationContext());
            }
            else
            {
                this.hideActionBarProgress(abProgress, activity.getApplicationContext());
            }
        }
        catch (Exception ex)
        {
            handleCrash(ex);
        }
    }

    public DisplayMetrics getDisplayMetrics(Activity activity)
    {
        return activity.getResources().getDisplayMetrics();
    }

    public void clickAdUrl(Activity activity, String url, AdZone adZone)
    {
        // set up the BroadcastReceiver
        AdClickReceiver adClickReceiver = new AdClickReceiver();
        adClickReceiver.setActivityListener(activity);
        adClickReceiver.setAdZoneListener(adZone);
        IntentFilter adClickReceiverIntentFilter = new IntentFilter(MinimobHelper.getInstance().ADCLICK_INTENT_ACTION);
        activity.registerReceiver(adClickReceiver, adClickReceiverIntentFilter);

        // send broadcast for the click action intent so as AdClickReceiver catches that
        Intent adClickIntent = new Intent();
        adClickIntent.setAction(MinimobHelper.getInstance().ADCLICK_INTENT_ACTION);
        adClickIntent.putExtra(MinimobHelper.getInstance().ADCLICK_INTENT_EXTRA_CLICKURL, url);
        activity.sendBroadcast(adClickIntent);
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

    private void attachFragment(MinimobBaseActivity activity, Fragment fragment)
    {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.attach(fragment).commit();
    }

    private void detachFragment(MinimobBaseActivity activity, Fragment fragment)
    {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.detach(fragment).commit();
    }

    public void showFragment(AppCompatActivity activity, int container, Fragment fragment)
    {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

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

    private void hideFragment(MinimobBaseActivity activity, Fragment fragment)
    {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        if (fragment != null)
        {
            String tag = ((Object) fragment).getClass().toString();
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .remove(fragment)
                    .commit();

            fragmentManager.executePendingTransactions();
        }
    }

    public void toggleLoading(Activity activity, boolean show)
    {
        toggleLoadingView(activity, show);
    }

    private void toggleLoadingView(Activity activity, boolean show)
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

    private void toggleLoadingFragment(MinimobBaseActivity activity, boolean show)
    {
        if (show)
        {
            //attachFragment(activity, _loadingFragment);
            showFragment(activity, R.id.loading_container, _loadingFragment);
        }
        else
        {
            //detachFragment(activity, _loadingFragment);
            hideFragment(activity, _loadingFragment);
        }
    }

    public void toggleTouches(View view, final boolean toggle)
    {
        view.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return toggle;
            }
        });

        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                toggleTouches(child, toggle);
            }
        }
    }

//    public <Type> Type GetObjectFromJSONString(String sObj, java.lang.Class<Type> classOfT)
//    {
//        try
//        {
//            GsonBuilder gsonBuilder = new GsonBuilder();
//            gsonBuilder
//                    //.setPrettyPrinting()
//                    .serializeNulls()
//            ;
//
//            Gson gson = gsonBuilder.create();
//
//            return gson.fromJson(sObj, classOfT);
//        }
//        catch (Exception ex)
//        {
//            Log.e(TAG, "GetObjectFromJSONString" + " (" + classOfT.getSimpleName() + ")", ex);
//            return null;
//        }
//    }
//
//    public String GetJSONStringFromObject(Object o)
//    {
//        try
//        {
//            Gson gson = new Gson();
//            return gson.toJson(o);
//        }
//        catch (Exception ex)
//        {
//            Log.e(TAG, "GetJSONStringFromObject", ex);
//            return "";
//        }
//    }

    //endregion Misc
}
