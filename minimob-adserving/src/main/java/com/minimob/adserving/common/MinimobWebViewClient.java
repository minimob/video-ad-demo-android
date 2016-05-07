package com.minimob.adserving.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.minimob.adserving.interfaces.IMinimobWebViewLoadedListener;

/**
 * Created by v.prantzos on 26/1/2016.
 */
public class MinimobWebViewClient extends WebViewClient
{
    private static String TAG = "MINIMOB-" + MinimobWebViewClient.class.getSimpleName();
    IMinimobWebViewLoadedListener _minimobWebViewLoadedListener;
    Activity activity;

    public MinimobWebViewClient(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        // we call this to avoid a bug in StrictMode that reports a android.os.StrictMode$InstanceCountViolation
        // because the garbage collector did not collect the instance of the activity before creating a new one
        System.gc();

        //load the url in the external browser
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        this.activity.getApplicationContext().startActivity(i);

        // return true since the url loading was handled
        return true;
    }

//    // NOTE: Google Play security alerted that it is unsafe
//    @Override
//    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
//    {
//        handler.proceed();
//    }

    @Override
    public void onPageFinished(WebView view, String url)
    {
        super.onPageFinished(view, url);

        // visibility is managed from MinimobJSInterface
        // make it again GONE since by itself the webview changes it to VISIBLE
        view.setVisibility(View.GONE);

        // notify that webview has loaded
        MinimobWebView minimobWebView = (MinimobWebView) view;
        if (_minimobWebViewLoadedListener != null)
        {
            _minimobWebViewLoadedListener.onMinimobWebViewLoaded(minimobWebView);
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon)
    {
        // hides the webview until the page has finished loading. Prevents flickering.
        view.setVisibility(View.GONE);
        super.onPageStarted(view, url, favicon);
    }
}
