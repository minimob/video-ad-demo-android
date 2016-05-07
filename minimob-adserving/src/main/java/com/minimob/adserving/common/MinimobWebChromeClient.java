package com.minimob.adserving.common;

import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.minimob.adserving.helpers.MinimobViewLog;
import com.minimob.adserving.interfaces.IMinimobWebViewLoadedListener;

/**
 * Created by v.prantzos on 12/2/2016.
 */
public class MinimobWebChromeClient extends WebChromeClient
{
    private static String TAG = "MINIMOB-" + MinimobWebChromeClient.class.getSimpleName();
    IMinimobWebViewLoadedListener _minimobWebViewLoadedListener;

    public MinimobWebChromeClient()
    {

    }

    public void setMinimobWebViewLoadedListener(IMinimobWebViewLoadedListener listener) {
        this._minimobWebViewLoadedListener = listener;
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage cm)
    {
        if (cm == null || cm.message() == null)
        {
            return false;
        }
        if (!cm.message().contains("Uncaught ReferenceError"))
        {
            MinimobViewLog.i("JS console", cm.message()
                    + (cm.sourceId() == null ? "" : " at " + cm.sourceId())
                    + ":" + cm.lineNumber());
        }
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result)
    {
        MinimobViewLog.d(TAG + "-" + "JS confirm", message);
        return handlePopups(result);
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result)
    {
        MinimobViewLog.d(TAG + "-" + "JS prompt", message);
        return handlePopups(result);
    }

    private boolean handlePopups(JsResult result)
    {
        result.cancel();
        return true;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress)
    {
        if (newProgress == 100)
        {
            MinimobWebView minimobWebView = (MinimobWebView) view;

            if (_minimobWebViewLoadedListener != null)
            {
                _minimobWebViewLoadedListener.onMinimobWebViewLoaded(minimobWebView);
            }
        }
        //super.onProgressChanged(view, newProgress);
    }
}
