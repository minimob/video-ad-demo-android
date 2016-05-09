package com.minimob.adserving.helpers;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.minimob.adserving.adzones.AdStatus;
import com.minimob.adserving.common.MinimobWebView;
import com.minimob.adserving.interfaces.IMinimobWebViewLoadedListener;

/**
 * Created by v.prantzos on 20/1/2016.
 */
public class MinimobJSInterface extends AppCompatActivity
{
    private String TAG = "MINIMOB-" + MinimobJSInterface.class.getSimpleName();
    MinimobWebView minimobWebView;
    Activity _activity;
    IMinimobWebViewLoadedListener _minimobWebViewLoadedListener;

    public MinimobJSInterface(Activity activity, MinimobWebView minimobWebView)
    {
        this._activity = activity;
        this.minimobWebView = minimobWebView;
    }

    @JavascriptInterface
    public void onAdsReady()
    {
        Log.d(TAG, "onAdsReady");

        if (this._minimobWebViewLoadedListener != null)
        {
            this._activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    minimobWebView.setAdStatus(AdStatus.ADS_AVAILABLE);
                    _minimobWebViewLoadedListener.onMinimobWebViewLoaded(minimobWebView);
                }
            });
        }
    }

    @JavascriptInterface
    public void onNoAds()
    {
        Log.d(TAG, "onNoAds");

        if (this._activity != null)
        {
            this._activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    minimobWebView.setAdStatus(AdStatus.ADS_NOT_AVAILABLE);
                    _minimobWebViewLoadedListener.onMinimobWebViewLoaded(minimobWebView);
                }
            });
        }
    }

    public void setMinimobWebViewLoadedListener(IMinimobWebViewLoadedListener listener) {
        this._minimobWebViewLoadedListener = listener;
    }
}
