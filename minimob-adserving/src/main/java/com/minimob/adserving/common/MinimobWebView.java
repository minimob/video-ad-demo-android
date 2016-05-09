package com.minimob.adserving.common;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.minimob.adserving.adzones.AdStatus;
import com.minimob.adserving.helpers.MinimobHelper;
import com.minimob.adserving.helpers.MinimobJSInterface;

/**
 * Created by v.prantzos on 21/1/2016.
 */
public class MinimobWebView extends WebView
{
    private String webViewId = "";
    private WebViewClient webViewClient;
    private WebChromeClient webChromeClient;
    private Activity activity;
    private AdStatus adStatus;

    public MinimobWebView(WebViewClient webViewClient, WebChromeClient webChromeClient, Activity activity)
    {
        this(activity);
        init(webViewClient, webChromeClient, activity, false, false, false);
    }

    public MinimobWebView(Context context)
    {
        this(context, null);
    }

    public MinimobWebView(Context context, AttributeSet st)
    {
        this(context, st, 0);
    }

    public MinimobWebView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(null, null, null, false, false, false);
    }

    public void setWebViewId(String webViewId)
    {
        this.webViewId = webViewId;
    }

    public String getWebViewId()
    {
        if (this.webViewId == null || this.webViewId.isEmpty())
        {
            this.webViewId = MinimobHelper.getInstance().getUniqueId();
        }

        return this.webViewId;
    }

    public void setAdStatus(AdStatus adStatus)
    {
        this.adStatus = adStatus;
    }

    public AdStatus getAdStatus()
    {
        return this.adStatus;
    }

    public void init(Activity activity, boolean isInterstitial, boolean isFullScreen, boolean isVideo)
    {
        init(this.webViewClient, this.webChromeClient, activity, isInterstitial, isFullScreen, isVideo);
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void init(WebViewClient webViewClient, WebChromeClient webChromeClient, final Activity activity, boolean isInterstitial, boolean isFullScreen, boolean isVideo)
    {
        try
        {
            this.adStatus = AdStatus.AD_STATUS_UNKNOWN;

            this.webViewClient = webViewClient;
            this.webChromeClient = webChromeClient;
            this.activity = activity;

            if (this.webViewId.isEmpty())
            {
                this.webViewId = MinimobHelper.getInstance().getUniqueId();
            }

            if (!isFullScreen && !isInterstitial)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                {
                    // interface for communication of the webview with the system
                    MinimobJSInterface jsInterface = new MinimobJSInterface(activity, this);
                    this.addJavascriptInterface(jsInterface, "minimobjsInterface");
                }
            }

            // we DON'T clear the cache so that the Save/Restore of the WebView's state will work!
            // Only clear the cache when it is a video ad
            if (isVideo)
            {
                this.clearCache(true);
            }

            // enable remote debugging
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                //if application is not hidden and is debuggable
                if (0 != (getContext().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE))
                {
                    WebView.setWebContentsDebuggingEnabled(true);
                }
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            {
                this.setBackgroundColor(Color.BLACK);
            }
            else
            {
                if (isFullScreen)
                {
                    this.setBackgroundColor(Color.BLACK);

                    if (isVideo)
                    {
                        this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    }
                    else
                    {
                        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    }
                }
                else
                {
                    this.setBackgroundColor(Color.TRANSPARENT);
                }
            }


            // enable javascript and other settings
            WebSettings webSettings = this.getSettings();
            // NO caching
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            webSettings.setUseWideViewPort(true);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setAllowFileAccess(true);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
            {
                webSettings.setAllowContentAccess(true);
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
            {
                webSettings.setAllowFileAccessFromFileURLs(true);
                webSettings.setAllowUniversalAccessFromFileURLs(true);
            }
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webSettings.setAppCacheEnabled(true);
            webSettings.setDomStorageEnabled(true);

            // If you cannot set the width of the viewport in the HTML, then you should call setUseWideViewPort() to ensure the page is given a larger viewport.
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);

            // for auto playing videos
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
            {
                webSettings.setMediaPlaybackRequiresUserGesture(false);
            }

            // faster webview
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

            // WebClients
            if (webViewClient == null)
            {
                webViewClient = new MinimobWebViewClient(activity);
            }
            this.setWebViewClient(webViewClient);

            if (webChromeClient == null)
            {
                webChromeClient = new MinimobWebChromeClient();
            }
            this.setWebChromeClient(webChromeClient);


            // enable cookies application-wide (for all WebViews)
            CookieManager.getInstance().setAcceptCookie(true);

            // cookies for this webview
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
            {
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.setAcceptThirdPartyCookies(this, true);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
