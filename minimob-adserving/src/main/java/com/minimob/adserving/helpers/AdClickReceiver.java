package com.minimob.adserving.helpers;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.minimob.adserving.adzones.AdZone;
import com.minimob.adserving.common.MinimobClickWebViewClient;
import com.minimob.adserving.views.MinimobBaseActivity;

/**
 * Created by v.prantzos on 22/4/2016.
 */
public class AdClickReceiver extends BroadcastReceiver
{
    private static final String TAG = AdClickReceiver.class.getSimpleName();
    private String marketUrl = "market://details?id=";
    private String playStoreUrl = "https://play.google.com/store/apps/details?id=";

    private Activity _activity;
    private AdZone _adZone;
    public void setActivityListener(Activity activity)
    {
        this._activity = activity;
    }

    public void setAdZoneListener(AdZone adZone)
    {
        this._adZone = adZone;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(MinimobHelper.getInstance().ADCLICK_INTENT_ACTION))
        {
            // get initial url
            String url = intent.getStringExtra(MinimobHelper.getInstance().ADCLICK_INTENT_EXTRA_CLICKURL);

            // create the hidden webview
            WebView hiddenWebView = new WebView(context);
            hiddenWebView.setVisibility(View.INVISIBLE);
            hiddenWebView.setBackgroundColor(Color.TRANSPARENT);
            hiddenWebView.setWebChromeClient(new WebChromeClient());
            hiddenWebView.getSettings().setJavaScriptEnabled(true);

            // set the client
            MinimobClickWebViewClient clickWebViewClient = new MinimobClickWebViewClient(_activity, url, _adZone);
            hiddenWebView.setWebViewClient(clickWebViewClient);

            // load url
            if (url.startsWith("https://play.google.com/store/apps/details?id="))
            {
                clickWebViewClient.cancelTimeoutTimerTask();

                String _upgradedUrl = url;
                if (MinimobHelper.getInstance().isGooglePlayStoreInstalled(context))
                {
                    _upgradedUrl = marketUrl + url.substring(playStoreUrl.length());
                }

                this.openUrl(_upgradedUrl);

                _activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        MinimobBaseActivity minimobBaseActivity = (MinimobBaseActivity) _activity;
                        MinimobHelper.getInstance().toggleLoading(minimobBaseActivity, false);
                    }
                });
            }
            else if (url.startsWith(marketUrl))
            {
                clickWebViewClient.cancelTimeoutTimerTask();

                String _upgradedUrl = url;
                if (!MinimobHelper.getInstance().isGooglePlayStoreInstalled(context))
                {
                    _upgradedUrl = playStoreUrl + url.substring(marketUrl.length());
                }

                this.openUrl(_upgradedUrl);

                _activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        MinimobBaseActivity minimobBaseActivity = (MinimobBaseActivity) _activity;
                        MinimobHelper.getInstance().toggleLoading(minimobBaseActivity, false);
                    }
                });
            }
            else
            {
                hiddenWebView.loadUrl(url);
            }
        }

        _activity.unregisterReceiver(this);
    }

    private void openUrl(final String url)
    {
        _activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    // we call this to avoid a bug in StrictMode that reports a android.os.StrictMode$InstanceCountViolation
                    // because the garbage collector did not collect the instance of the activity before creating a new one
                    System.gc();

                    _activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
                catch (ActivityNotFoundException ex)
                {
                    MinimobHelper.getInstance().handleCrash(TAG, ex);
                }
            }
        });
    }
}
