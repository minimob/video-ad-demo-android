package com.minimob.adserving.adzones;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebView;

import com.minimob.adserving.helpers.MinimobHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by v.prantzos on 9/5/2016.
 */
public class AdTag
{
    //region VARIABLES
    private String TAG = "MINIMOB-" + AdTag.class.getSimpleName();
    
    private String _adTag;
    private Context _context;
    private boolean _preload = false;
    //endregion VARIABLES

    //region CONSTRUCTORS
    public AdTag(Context context, String adTag)
    {
        this._adTag = adTag;
        this._context = context;

        setAdTagSettings();
    }
    //endregion CONSTRUCTORS

    //region METHODS

    //region Public Methods
    public void setPreload(boolean preload)
    {
        this._adTag = setAdTagSetting("[preload]", String.valueOf(preload), this._adTag);
    }

    public void setCustomTrackingData(String customTrackingData)
    {
        this._adTag = setAdTagSetting("[customTrackingData]", customTrackingData, this._adTag);
    }

    public void setGaid(String gaid)
    {
        this._adTag = setAdTagSetting("[gaid]", gaid, this._adTag);
    }

    public void setAge(String age)
    {
        this._adTag = setAdTagSetting("[age]", age, this._adTag);
    }

    public void setCategory(String category)
    {
        this._adTag = setAdTagSetting("[category]", category, this._adTag);
    }

    public void setGender(String gender)
    {
        this._adTag = setAdTagSetting("[gender]", gender, this._adTag);
    }

    public String getAdTag()
    {
        return this._adTag;
    }
    //endregion Public Methods

    //region Private Methods
    private void setAdTagSettings()
    {
        try
        {
            _adTag = setAdTagSetting("[imei]", getIMEI(), _adTag);
            _adTag = setAdTagSetting("[android_id]", getAndroidId(), _adTag);
            _adTag = setAdTagSetting("[idfa]", "", _adTag);
            _adTag = setAdTagSetting("[idfv]", "", _adTag);
            _adTag = setAdTagSetting("[category]", "", _adTag);
            _adTag = setAdTagSetting("[age]", "", _adTag);
            _adTag = setAdTagSetting("[gender]", "", _adTag);
            _adTag = setAdTagSetting("[keywords]", "", _adTag);
            _adTag = setAdTagSetting("[lat]", String.valueOf(getLatLon()[0]), _adTag);
            _adTag = setAdTagSetting("[lon]", String.valueOf(getLatLon()[1]), _adTag);
            _adTag = setAdTagSetting("[device_width]", String.valueOf(getScreenDimensions()[0]), _adTag);
            _adTag = setAdTagSetting("[device_height]", String.valueOf(getScreenDimensions()[1]), _adTag);
            _adTag = setAdTagSetting("[mnc]", String.valueOf(getMNC()), _adTag);
            _adTag = setAdTagSetting("[mcc]", String.valueOf(getMCC()), _adTag);
            _adTag = setAdTagSetting("[wifi]", String.valueOf(checkWifiConnectivity()), _adTag);
            _adTag = setAdTagSetting("[android_version]", String.valueOf(Build.VERSION.SDK_INT), _adTag);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e(TAG + "-" + "setAdTagSettings", ex.getMessage());
        }
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

    private boolean checkWifiConnectivity()
    {
        try
        {
            ConnectivityManager connManager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    private int getMNC()
    {
        int mnc = 0;
        try
        {
            TelephonyManager telephonyManager = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);

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

    private int getMCC()
    {
        int mcc = 0;
        try
        {
            TelephonyManager telephonyManager = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);

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

    private String getIMEI()
    {
        String imei = "";
        try
        {
            TelephonyManager telephonyManager = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);

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
        return this.getMD5(imei);
    }

    private int[] getScreenDimensions()
    {
        WindowManager windowManager = (WindowManager) _context.getSystemService(Context.WINDOW_SERVICE);
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

    private String getAndroidId()
    {
        String androidId = Settings.Secure.getString(_context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return this.getMD5(androidId);
    }

    private double[] getLatLon()
    {
        double[] latlon = new double[2];
        LocationManager locationManager = (LocationManager) _context.getSystemService(Context.LOCATION_SERVICE);

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
                if (this.checkPermission(_context, Manifest.permission.ACCESS_COARSE_LOCATION))
                {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }

            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled)
            {
                // Assume thisActivity is the current activity
                if (this.checkPermission(_context, Manifest.permission.ACCESS_FINE_LOCATION))
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

    private boolean checkPermission(Context ctx, String permission)
    {
        //int res = ctx.checkSelfPermission(permission);
        int res = ctx.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private final String getMD5(final String s)
    {
        final String MD5 = "MD5";
        try
        {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest)
            {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException ex)
        {
            ex.printStackTrace();
            Log.e(TAG + "-" + "getMD5", ex.getMessage());
        }
        return "";
    }
    //endregion Private Methods

    //endregion METHODS
}
