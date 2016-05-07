package com.minimob.adserving.common;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.minimob.adserving.adzones.AdZone;
import com.minimob.adserving.helpers.MinimobHelper;
import com.minimob.adserving.views.MinimobBaseActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by v.prantzos on 22/4/2016.
 */
public class MinimobClickWebViewClient extends WebViewClient
{
    private static final String TAG = MinimobClickWebViewClient.class.getSimpleName();

    private Activity _activity;
    private String _originalUrl;
    private String _currentUrl;
    private String _upgradedUrl;
    private AdZone _adZone;

    private Timer _timer = new Timer();
    private TimerTask _redirectTimerTask;
    private TimerTask _timeoutTimerTask;

    private final long _timeoutDelay = 15*1000;
    private final long _redirectDelay = 3*1000;

    private String marketUrl = "market://details?id=";
    private String playStoreUrl = "https://play.google.com/store/apps/details?id=";

    private Runnable runnableTimeout = new Runnable()
    {
        @Override
        public void run() {
            cancelTimeoutTimerTask();
            cancelRedirectTimerTask();

            openUrl(getMarketUrl());
        }
    };

    private Runnable runnableRedirectError = new Runnable()
    {
        @Override
        public void run() {
            cancelTimeoutTimerTask();
            cancelRedirectTimerTask();

            openUrl(getMarketUrl());
        }
    };

    public MinimobClickWebViewClient(Activity activity, String originalUrl, AdZone adZone)
    {
        this._activity = activity;
        this._adZone = adZone;
        this._originalUrl = originalUrl;
        this._currentUrl = originalUrl;

        _timeoutTimerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                runnableTimeout.run();
            }
        };
        _timer.schedule(_timeoutTimerTask, _timeoutDelay);

        MinimobHelper.getInstance().logMessage(TAG, "Original URL: " + _originalUrl);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        MinimobHelper.getInstance().logMessage(TAG, "shouldOverrideUrlLoading for " + url);

        cancelRedirectTimerTask();

        _currentUrl = url;
        if (url.startsWith(playStoreUrl))
        {
            cancelTimeoutTimerTask();

            _upgradedUrl = url;
            if (MinimobHelper.getInstance().isGooglePlayStoreInstalled(_activity))
            {
                _upgradedUrl = marketUrl + url.substring(playStoreUrl.length());
            }

            openUrl(_upgradedUrl);

            view.destroy();

            _activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    MinimobBaseActivity minimobBaseActivity = (MinimobBaseActivity) _activity;
                    MinimobHelper.getInstance().toggleLoading(minimobBaseActivity, false);
                }
            });

            return true;
        }
        else if (url.startsWith(marketUrl))
        {
            cancelTimeoutTimerTask();

            _upgradedUrl = url;
            if (!MinimobHelper.getInstance().isGooglePlayStoreInstalled(_activity))
            {
                _upgradedUrl = playStoreUrl + url.substring(marketUrl.length());
            }

            openUrl(_upgradedUrl);

            view.destroy();

            _activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    MinimobBaseActivity minimobBaseActivity = (MinimobBaseActivity) _activity;
                    MinimobHelper.getInstance().toggleLoading(minimobBaseActivity, false);
                }
            });

            return true;
        }
        return false;
    }

    @Override
    public void onPageFinished(WebView view, String url)
    {
        MinimobHelper.getInstance().logMessage(TAG, "onPageFinished for " + url);

        cancelRedirectTimerTask();
        super.onPageFinished(view, url);

        if (_upgradedUrl == null)
        {
            cancelRedirectTimerTask();

            _redirectTimerTask = new TimerTask() {
                @Override
                public void run() {
                    runnableRedirectError.run();
                }
            };

            _timer.schedule(_redirectTimerTask, _redirectDelay);
        }
        else
        {
            cancelTimeoutTimerTask();
            cancelRedirectTimerTask();
        }
    }

    public void cancelTimeoutTimerTask()
    {
        if(_timeoutTimerTask != null){
            _timeoutTimerTask.cancel();
            _timeoutTimerTask = null;
        }
    }

    private void cancelRedirectTimerTask()
    {
        if(_redirectTimerTask != null){
            _redirectTimerTask.cancel();
            _redirectTimerTask = null;
        }
    }

    private String getMarketUrl()
    {
        if (_adZone.packageId.isEmpty())
        {
            return _currentUrl;
        }
        else
        {
            if (MinimobHelper.getInstance().isGooglePlayStoreInstalled(_activity))
            {
                return marketUrl + _adZone.packageId;
            }
            else
            {
                return playStoreUrl + _adZone.packageId;
            }
        }
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

                    MinimobHelper.getInstance().logMessage(TAG, "startActivity with intent url: " + url);
                    _activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
                catch (ActivityNotFoundException ex)
                {
                    MinimobHelper.getInstance().handleCrash(TAG, ex);
                }
                finally
                {
                    MinimobBaseActivity minimobBaseActivity = (MinimobBaseActivity) _activity;
                    MinimobHelper.getInstance().toggleLoading(minimobBaseActivity, false);
                }
            }
        });
    }
}
