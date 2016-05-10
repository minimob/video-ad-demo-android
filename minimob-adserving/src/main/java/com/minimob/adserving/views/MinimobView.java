package com.minimob.adserving.views;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.minimob.adserving.common.MinimobBaseView;
import com.minimob.adserving.common.MinimobWebChromeClient;
import com.minimob.adserving.common.MinimobWebView;
import com.minimob.adserving.interfaces.IMinimobViewListener;
import com.minimob.adserving.helpers.MinimobViewLog;
import com.minimob.adserving.helpers.MinimobViewCommandParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by v.prantzos on 19/4/2016.
 */
public class MinimobView extends MinimobBaseView
{
    //region VARIABLES
    private final static String TAG = MinimobView.class.getSimpleName();
    private static String baseUrl = "http://s.rtad.bid/";

    public final static int STATE_LOADING = 0;
    public final static int STATE_DEFAULT = 1;
    public final static int STATE_EXPANDED = 2;
    public final static int STATE_HIDDEN = 4;

    //region UI elements
    private MinimobWebView _webView;
    private MinimobWebView _webViewPart2;
    private MinimobWebView _currentWebView;
    private MinimobWebChromeClient _minimobWebChromeClient;
    private MinimobWebViewClient _minimobWebViewClient;
    private RelativeLayout _expandedView;
    private RelativeLayout _resizedView;
    //endregion UI elements

    private Activity _activity;
    private String _minimobScript;

    // gesture detector for capturing unwanted gestures
    private GestureDetector _gestureDetector;

    private final boolean _isInterstitial;
    private int _state;

    private IMinimobViewListener _minimobViewListener;

    // used for setting positions and sizes (all in pixels, not dpi)
    private DisplayMetrics _displayMetrics;
    private Rect _currentPosition;
    private Rect _defaultPosition;

    // state to help set positions and sizes;
    private boolean _isForcingFullScreen;
    private boolean _isExpandingFromDefault;
    private boolean _isClosing;

    // used to force full-screen mode on expand and to restore original state on close
    private View _titleBar;
    private boolean _isFullScreen;
    private boolean _isVideo;
    private boolean _isForceNotFullScreen;
    private int _origTitleBarVisibility;
    private boolean _isActionBarShowing;

    // This is the contents of minimobjsinterface.js. We keep it around in case we need to inject it
    // into webViewPart2 (2nd part of 2-part expanded ad).
    public static final String _minimobjsinterfaceBase64 = "Ly8NCi8vICBtbWppLmpzDQovLw0KDQooZnVuY3Rpb24oKSB7DQoJDQoJY29uc29sZS5sb2coIm1tamkgb2JqZWN0IGxvYWRpbmcuLi4iKTsNCg0KCXZhciBtbWppID0gd2luZG93Lm1tamkgPSB7fTsNCg0KCS8qKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioNCgkgKiBjb25zb2xlIGxvZ2dpbmcgaGVscGVyDQoJICoqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqLw0KDQoJbW1qaS5MT0dfTEVWRUwgPSB7DQoJCSJERUJVRyIgICA6IDAsDQoJCSJJTkZPIiAgICA6IDEsDQoJCSJXQVJOSU5HIiA6IDIsDQoJCSJFUlJPUiIgICA6IDMsDQoJCSJOT05FIiAgICA6IDQNCgl9Ow0KDQoJbW1qaS5sb2dMZXZlbCA9IG1tamkuTE9HX0xFVkVMLk5PTkU7DQoJdmFyIGxvZyA9IHt9Ow0KDQoJbG9nLmQgPSBmdW5jdGlvbihtc2cpIHsNCgkJaWYgKG1tamkubG9nTGV2ZWwgPD0gbW1qaS5MT0dfTEVWRUwuREVCVUcpIHsNCgkJCWNvbnNvbGUubG9nKCJtbWppIChEKSAiICsgbXNnKTsNCgkJfQ0KCX07DQoNCglsb2cuaSA9IGZ1bmN0aW9uKG1zZykgew0KCQlpZiAobW1qaS5sb2dMZXZlbCA8PSBtbWppLkxPR19MRVZFTC5JTkZPKSB7DQoJCQljb25zb2xlLmxvZygibW1qaSAoSSkgIiArIG1zZyk7DQoJCX0NCgl9Ow0KDQoJbG9nLncgPSBmdW5jdGlvbihtc2cpIHsNCgkJaWYgKG1tamkubG9nTGV2ZWwgPD0gbW1qaS5MT0dfTEVWRUwuV0FSTklORykgew0KCQkJY29uc29sZS5sb2coIm1tamkgKFcpICIgKyBtc2cpOw0KCQl9DQoJfTsNCg0KCWxvZy5lID0gZnVuY3Rpb24obXNnKSB7DQoJCWlmIChtbWppLmxvZ0xldmVsIDw9IG1tamkuTE9HX0xFVkVMLkVSUk9SKSB7DQoJCQljb25zb2xlLmxvZygibW1qaSAoRSkgIiArIG1zZyk7DQoJCX0NCgl9Ow0KDQoJLyoqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKg0KCSAqIGNhbGxiYWNrcyB0byBOYXRpdmUgY29kZQ0KCSAqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKi8NCg0KCW1tamkuY2xvc2UgPSBmdW5jdGlvbigpIHsNCgkJbG9nLmkoIm1tamkuY2xvc2UiKTsNCgkJY2FsbE5hdGl2ZSgiY2xvc2UiKTsNCgl9Ow0KDQoJbW1qaS5leHBhbmQgPSBmdW5jdGlvbih1cmwpDQoJew0KCQlpZiAodXJsID09PSB1bmRlZmluZWQpIHsNCgkJCWxvZy5pKCJtbWppLmV4cGFuZCAoMS1wYXJ0KSIpOw0KCQkJY2FsbE5hdGl2ZSgiZXhwYW5kIik7DQoJCX0gZWxzZSB7DQoJCQlsb2cuaSgibW1qaS5leHBhbmQgIiArIHVybCk7DQoJCQljYWxsTmF0aXZlKCJleHBhbmQ/dXJsPSIgKyBlbmNvZGVVUklDb21wb25lbnQodXJsKSk7DQoJCX0NCgl9Ow0KDQoJbW1qaS5hZHNSZWFkeSA9IGZ1bmN0aW9uKHBhY2thZ2VJZCkgew0KICAgICAgICBjYWxsTmF0aXZlKCJhZHNSZWFkeT9wYWNrYWdlSWQ9IiArIHBhY2thZ2VJZCk7DQogICAgfTsNCg0KCW1tamkubm9BZHMgPSBmdW5jdGlvbigpIHsNCiAgICAgICAgbG9nLmkoIm1tamkubm9BZHMiKTsNCiAgICAgICAgY2FsbE5hdGl2ZSgibm9BZHMiKTsNCiAgICB9Ow0KDQoJLyoqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKg0KCSAqIG1ldGhvZHMgY2FsbGVkIGJ5IE5hdGl2ZSBjb2RlDQoJICoqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqLw0KDQoJbW1qaS5maXJlUmVzdW1lVmlkZW9FdmVudCA9IGZ1bmN0aW9uKCkgew0KICAgICAgICBsb2cuaSgibW1qaS5maXJlUmVzdW1lVmlkZW9FdmVudCIpOw0KICAgICAgICB2aWRlb0hhbmRsZXIucGxheSgpOw0KICAgIH07DQoNCgkvKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqDQoJICogaW50ZXJuYWwgaGVscGVyIG1ldGhvZHMNCgkgKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKiovDQoNCglmdW5jdGlvbiBjYWxsTmF0aXZlKGNvbW1hbmQpIHsNCgkJdmFyIGlmcmFtZSA9IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoIklGUkFNRSIpOw0KCQlpZnJhbWUuc2V0QXR0cmlidXRlKCJzcmMiLCAibW1qaTovLyIgKyBjb21tYW5kKTsNCgkJZG9jdW1lbnQuZG9jdW1lbnRFbGVtZW50LmFwcGVuZENoaWxkKGlmcmFtZSk7DQoJCWlmcmFtZS5wYXJlbnROb2RlLnJlbW92ZUNoaWxkKGlmcmFtZSk7DQoJCWlmcmFtZSA9IG51bGw7DQoJfQ0KDQoJY29uc29sZS5sb2coIm1tamkgb2JqZWN0IGxvYWRlZCIpOw0KDQp9KSgpOw==";
    private String _minimobJSInterface;

    private Handler _handler;

    // Stores the requested orientation for the Activity to which this MinimobView belongs.
    // This is needed to restore the Activity's requested orientation in the event that
    // the view itself requires an orientation lock.
    private int _originalOrientation;

    //endregion VARIABLES

    //region CONSTRUCTORS
    public MinimobView(Activity activity, String minimobScript, IMinimobViewListener minimobViewListener, boolean isVideo, int originalOrientation)
    {
        this(activity, minimobScript, minimobViewListener, false, false, isVideo, originalOrientation);
    }

    public MinimobView(Activity activity, String minimobScript, IMinimobViewListener minimobViewListener,
                     boolean isInterstitial, boolean isFullScreen, boolean isVideo, int originalOrientation)
    {
        super(activity);

        this._activity = activity;
        this._minimobScript = minimobScript;
        this._isInterstitial = isInterstitial;
        this._isFullScreen = isFullScreen;
        this._isVideo = isVideo;

        //listener for MinimobView javascript events
        this._minimobViewListener = minimobViewListener;

        _displayMetrics = new DisplayMetrics();
        (_activity).getWindowManager().getDefaultDisplay().getMetrics(_displayMetrics);

        _currentPosition = new Rect();
        _defaultPosition = new Rect();

        this._originalOrientation = originalOrientation;
        MinimobViewLog.d(TAG, "originalRequestedOrientation " + getOrientationString(_originalOrientation));

        // ignore scroll gestures
        _gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
            {
                return true;
            }
        });

        _handler = new Handler(Looper.getMainLooper());

        //web clients
        _minimobWebChromeClient = new MinimobWebChromeClient();
        _minimobWebViewClient = new MinimobWebViewClient();

        //create the container (webview) and set the state to default
        _webView = createWebView();
        _currentWebView = _webView;
        addView(_webView);

        //load the minimobViewInterface library into the container (webview)
        injectMinimobJSInterface(_webView);

        //load the ad to the container (
        _webView = loadMinimobWebView(_webView, _activity, this._minimobScript, isInterstitial, isFullScreen, isVideo);

        MinimobViewLog.d("log level = " + MinimobViewLog.getLoggingLevel());
        if (MinimobViewLog.getLoggingLevel() == MinimobViewLog.LOG_LEVEL.debug)
        {
            injectJavaScript(_webView, "mmji.logLevel = mmji.LOG_LEVEL.DEBUG;");
        }
        else if (MinimobViewLog.getLoggingLevel() == MinimobViewLog.LOG_LEVEL.info)
        {
            injectJavaScript(_webView, "mmji.logLevel = mmji.LOG_LEVEL.INFO;");
        }
        else if (MinimobViewLog.getLoggingLevel() == MinimobViewLog.LOG_LEVEL.warning)
        {
            injectJavaScript(_webView, "mmji.logLevel = mmji.LOG_LEVEL.WARNING;");
        }
        else if (MinimobViewLog.getLoggingLevel() == MinimobViewLog.LOG_LEVEL.error)
        {
            injectJavaScript(_webView, "mmji.logLevel = mmji.LOG_LEVEL.ERROR;");
        }
        else if (MinimobViewLog.getLoggingLevel() == MinimobViewLog.LOG_LEVEL.none)
        {
            injectJavaScript(_webView, "mmji.logLevel = mmji.LOG_LEVEL.NONE;");
        }
    }
    //endregion CONSTRUCTORS

    //region METHODS

    //region View Methods
    @SuppressLint("SetJavaScriptEnabled")
    private MinimobWebView createWebView()
    {
        MinimobWebView wv = new MinimobWebView(_minimobWebViewClient, _minimobWebChromeClient, _activity)
        {
            private final String TAG = MinimobWebView.class.getSimpleName();

            @SuppressWarnings("deprecation")
            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom)
            {
                super.onLayout(changed, left, top, right, bottom);
                onLayoutWebView(this, changed, left, top, right, bottom);
            }

            @Override
            public void onConfigurationChanged(Configuration newConfig)
            {
                super.onConfigurationChanged(newConfig);
                MinimobViewLog.d(TAG, "onConfigurationChanged " + (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT ? "portrait" : "landscape"));

                if (_isInterstitial)
                {
                    (_activity).getWindowManager().getDefaultDisplay().getMetrics(_displayMetrics);
                }
            }

            @Override
            protected void onWindowVisibilityChanged(int visibility)
            {
                super.onWindowVisibilityChanged(visibility);
                int actualVisibility = getVisibility();
                MinimobViewLog.d(TAG, "onWindowVisibilityChanged " + getVisibilityString(visibility) + " (actual " + getVisibilityString(actualVisibility) + ")");

                if (visibility != View.VISIBLE)
                {
                    pauseWebView(this);
                }
                else
                {
                    resumeWebView(this);
                }
            }
        };

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        wv.setLayoutParams(params);

        wv.setScrollContainer(false);
        wv.setVerticalScrollBarEnabled(false);
        wv.setHorizontalScrollBarEnabled(false);
        wv.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        wv.setFocusableInTouchMode(false);
        wv.setOnTouchListener(new OnTouchListener()
        {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        // isTouched = true;
                        if (!v.hasFocus())
                        {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });

//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//		    if (0 != (context.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
//		        WebView.setWebContentsDebuggingEnabled(true);
//		    }
//		}

        return wv;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (_gestureDetector.onTouchEvent(event))
        {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.onTouchEvent(event);
    }

    public void clearView()
    {
        if (_webView != null)
        {
            _webView.setWebChromeClient(null);
            _webView.setWebViewClient(null);
            _webView.loadUrl("about:blank");
        }
    }

    public void destroy()
    {
        if (_webView != null)
        {
            _webView.setWebChromeClient(null);
            _webView.setWebViewClient(null);
            _webView.destroy();
            _webView = null;
        }

        if (_webViewPart2 != null)
        {
            _webViewPart2.setWebChromeClient(null);
            _webViewPart2.setWebViewClient(null);
            _webViewPart2.destroy();
            _webViewPart2 = null;
        }

        if (_currentWebView != null)
        {
            _currentWebView.setWebChromeClient(null);
            _currentWebView.setWebViewClient(null);
            _currentWebView.destroy();
            _currentWebView = null;
        }

        if (_expandedView != null)
        {
            _expandedView = null;
        }

        if (_resizedView != null)
        {
            _resizedView = null;
        }

        this._minimobViewListener = null;

        if (_activity instanceof VideoActivity) {
            this._activity.finish();
        }

        this._activity = null;
    }
    //endregion View Methods

    //region JS callback Methods
    private void parseCommandUrl(String commandUrl)
    {
        MinimobViewLog.d(TAG, "parseCommandUrl " + commandUrl);

        MinimobViewCommandParser parser = new MinimobViewCommandParser();
        Map<String, String> commandMap = parser.parseCommandUrl(commandUrl);

        if (commandMap == null)
        {
            return;
        }

        String command = commandMap.get("command");

        final String[] commandsWithNoParam = {
                "close",
                "noAds",
        };

        final String[] commandsWithString = {
                "expand",
                "adsReady",
        };

        try
        {
            if (Arrays.asList(commandsWithNoParam).contains(command))
            {
                Method method = getClass().getDeclaredMethod(command);
                method.invoke(this);
            }
            else if (Arrays.asList(commandsWithString).contains(command))
            {
                Method method = getClass().getDeclaredMethod(command, String.class);
                String key = "id";

                if (command.equals("expand")) {
                    key = "url";
                }
                else if (command.equals("adsReady")) {
                    key = "packageId";
                }

                String val = commandMap.get(key);
                method.invoke(this, val);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void adsReady()
    {
        MinimobViewLog.d(TAG + "-" + "JS callback", "adsReady");
        _minimobViewListener.onAdsAvailable(MinimobView.this, "");
    }

    private void adsReady(String packageId)
    {
        MinimobViewLog.d(TAG + "-" + "JS callback", "adsReady");
        _minimobViewListener.onAdsAvailable(MinimobView.this, packageId);
    }

    private void noAds()
    {
        MinimobViewLog.d(TAG + "-" + "JS callback", "noAds");
        _minimobViewListener.onAdsNotAvailable(MinimobView.this);
    }

    private void close()
    {
        MinimobViewLog.d(TAG + "-" + "JS callback", "close");
        _handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (_state == STATE_LOADING || (_state == STATE_DEFAULT && !_isInterstitial) || _state == STATE_HIDDEN)
                {
                    // do nothing
                }
                else if (_state == STATE_DEFAULT || _state == STATE_EXPANDED)
                {
                    closeFromExpanded();
                }
            }
        });
    }

    private void videoFinished()
    {
        MinimobViewLog.d(TAG + "-" + "JS callback", "videoFinished");
        _minimobViewListener.onVideoFinished(MinimobView.this);
    }

    // Note: This method is also used to present an interstitial ad.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void expand(String url)
    {
        MinimobViewLog.d(TAG + "-" + "JS callback", "expand " + (url != null ? url : "(1-part)"));

        // The only time it is valid to call expand on a banner ad is
        // when the ad is currently in either default or resized state.
        // The only time it is valid to (internally) call expand on an interstitial ad is
        // when the ad is currently in loading state.
        if ((_isInterstitial && _state != STATE_LOADING) || (!_isInterstitial && _state != STATE_DEFAULT))
        {
            // do nothing
            return;
        }

        // 1-part expansion
        if (TextUtils.isEmpty(url))
        {
            if (_isInterstitial || _state == STATE_DEFAULT)
            {
                if (_webView.getParent() != null)
                {
                    ((ViewGroup) _webView.getParent()).removeView(_webView);
                }
                else
                {
                    removeView(_webView);
                }
            }
            expandHelper(_webView);
            return;
        }

        // 2-part expansion

        // First, try to get the content of the second (expanded) part of the creative.

        try
        {
            url = URLDecoder.decode(url, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return;
        }

        final String finalUrl = url;

        // Go onto a background thread to read the content from the URL.
        (new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final String content = getStringFromUrl(finalUrl);
                if (!TextUtils.isEmpty(content))
                {
                    // Get back onto the main thread to create and load a new WebView.
                    _activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            _webView.setWebChromeClient(null);
                            _webView.setWebViewClient(null);
                            _webViewPart2 = createWebView();
                            injectMinimobJSInterface(_webViewPart2);

                            _webViewPart2 = loadMinimobWebView(_webViewPart2, _activity, MinimobView.this._minimobScript, _isInterstitial, _isFullScreen, _isVideo);

                            _currentWebView = _webViewPart2;
                            expandHelper(_currentWebView);
                        }
                    });
                }
                else
                {
                    MinimobViewLog.e("Could not load part 2 expanded content for URL: " + finalUrl);
                }
            }
        }, "2-part-content")).start();
    }
    private void open(String url)
    {
        try
        {
            url = URLDecoder.decode(url, "UTF-8");
            final String urlStr = url;
            MinimobViewLog.d(TAG + "-JS callback", "open " + url);

            _activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    //_activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlStr)));

                    if (_minimobViewListener != null)
                    {
                        _activity.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                _minimobViewListener.onAdClicked(urlStr);
                            }
                        });
                    }
                }
            });
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    //endregion JS callback Methods

    //region Native to JS Methods
    private void fireResumeVideoEvent()
    {
        MinimobViewLog.d(TAG, "fireResumeVideoEvent");
        injectJavaScript("mmji.fireResumeVideoEvent();");
    }
    //endregion Native to JS Methods

    //region HELPERS

    private String getStringFromUrl(String url)
    {
        // Support second part from file system - mostly not used on real web creatives
        if (url.startsWith("file:///"))
        {
            return getStringFromFileUrl(url);
        }

        String content = null;
        InputStream is = null;
        try
        {
            HttpURLConnection conn = (HttpURLConnection) (new URL(url)).openConnection();
            int responseCode = conn.getResponseCode();
            MinimobViewLog.d(TAG, "response code " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                MinimobViewLog.d(TAG, "getContentLength " + conn.getContentLength());
                is = conn.getInputStream();
                byte[] buf = new byte[1500];
                int count;
                StringBuilder sb = new StringBuilder();
                while ((count = is.read(buf)) != -1)
                {
                    String data = new String(buf, 0, count);
                    sb.append(data);
                }
                content = sb.toString();
                MinimobViewLog.d(TAG, "getStringFromUrl ok, length=" + content.length());
            }
            conn.disconnect();
        } catch (IOException e)
        {
            MinimobViewLog.e(TAG, "getStringFromUrl failed " + e.getLocalizedMessage());
        } finally
        {
            try
            {
                if (is != null)
                {
                    is.close();
                }
            } catch (IOException e)
            {
                // do nothing
            }
        }
        return content;
    }

    private String getStringFromFileUrl(String fileURL)
    {
        StringBuilder mLine = new StringBuilder("");
        String[] urlElements = fileURL.split("/");
        if (urlElements[3].equals("android_asset"))
        {
            try
            {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(_activity.getAssets().open(urlElements[4])));

                // do reading, usually loop until end of file reading
                String line = reader.readLine();
                mLine.append(line);
                while (line != null)
                {
                    line = reader.readLine();
                    mLine.append(line);
                }

                reader.close();
            } catch (IOException e)
            {
                MinimobViewLog.e("Error fetching file: " + e.getMessage());
            }

            return mLine.toString();
        }
        else
        {
            MinimobViewLog.e("Unknown location to fetch file content");
        }

        return "";
    }

    private void expandHelper(MinimobWebView webView)
    {
        if (!_isInterstitial)
        {
            _state = STATE_EXPANDED;
        }
        // If this MinimobView is an interstitial, we'll set the state to default and
        // fire the state change event after the view has been laid out.
        forceFullScreen();
        forceLandscape();
        _expandedView = new RelativeLayout(_activity);
        _expandedView.addView(webView);

        _activity.addContentView(_expandedView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        _isExpandingFromDefault = true;
        if (_isInterstitial)
        {
            _state = STATE_DEFAULT;
        }

        if (_isVideo)
        {
            this.fireResumeVideoEvent();
        }
    }

    public void updateActivity(Activity activity)
    {
        this._activity = activity;
    }

    private void closeFromExpanded()
    {
        _handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                restoreOriginalScreenState();
                restoreOriginalOrientation();
            }
        });

        if (_state == STATE_DEFAULT && _isInterstitial)
        {
            _state = STATE_HIDDEN;
            clearView();

            _handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (_minimobViewListener != null)
                    {
                        _minimobViewListener.onMinimobViewClosed(MinimobView.this);
                    }
                }
            });
        }
        else if (_state == STATE_EXPANDED)
        {
            _state = STATE_DEFAULT;
        }

        _isClosing = true;

        _expandedView.removeAllViews();

        FrameLayout rootView = (FrameLayout) _activity.findViewById(android.R.id.content);
        rootView.removeView(_expandedView);
        _expandedView = null;

        if (_webViewPart2 == null)
        {
            // close from 1-part expansion
            addView(_webView);
        }
        else
        {
            // close from 2-part expansion
            _webViewPart2.setWebChromeClient(null);
            _webViewPart2.setWebViewClient(null);
            _webViewPart2.destroy();
            _webViewPart2 = null;
            _webView.setWebChromeClient(_minimobWebChromeClient);
            _webView.setWebViewClient(_minimobWebViewClient);
            _currentWebView = _webView;
        }
    }
    
    private void forceLandscape()
    {
        MinimobViewLog.d(TAG, "forceLandscape");
        _activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void forceFullScreen()
    {
        MinimobViewLog.d(TAG, "forceFullScreen");

        // store away the original state
        int flags = _activity.getWindow().getAttributes().flags;
        _isFullScreen = ((flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0);
        _isForceNotFullScreen = ((flags & WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN) != 0);
        _origTitleBarVisibility = -9;

        // First, see if the activity has an action bar.
        boolean hasActionBar = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            ActionBar actionBar = _activity.getActionBar();
            if (actionBar != null)
            {
                hasActionBar = true;
                _isActionBarShowing = actionBar.isShowing();
                actionBar.hide();
            }
        }

        // If not, see if the app has a title bar
        if (!hasActionBar)
        {
            // http://stackoverflow.com/questions/6872376/how-to-hide-the-title-bar-through-code-in-android
            _titleBar = null;
            try
            {
                _titleBar = (View) _activity.findViewById(android.R.id.title).getParent();
            }
            catch (NullPointerException npe)
            {
                // do nothing
            }
            if (_titleBar != null)
            {
                _origTitleBarVisibility = _titleBar.getVisibility();
                _titleBar.setVisibility(View.GONE);
            }
        }

        MinimobViewLog.d(TAG, "isFullScreen " + _isFullScreen);
        MinimobViewLog.d(TAG, "isForceNotFullScreen " + _isForceNotFullScreen);
        MinimobViewLog.d(TAG, "isActionBarShowing " + _isActionBarShowing);
        MinimobViewLog.d(TAG, "origTitleBarVisibility " + getVisibilityString(_origTitleBarVisibility));

        // force fullscreen mode
        _activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        _activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        _isForcingFullScreen = !_isFullScreen;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void restoreOriginalScreenState()
    {
        if (_activity == null)
            return;

        if (!_isFullScreen)
        {
            _activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        if (_isForceNotFullScreen)
        {
            _activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && _isActionBarShowing)
        {
            ActionBar actionBar = _activity.getActionBar();

            if (actionBar != null) {
                actionBar.show();
            }
        }
        else if (_titleBar != null)
        {
            _titleBar.setVisibility(_origTitleBarVisibility);
        }
    }

    private void restoreOriginalOrientation()
    {
        MinimobViewLog.d(TAG, "restoreOriginalOrientation");
        _activity.setRequestedOrientation(_originalOrientation);
    }

    private static String getVisibilityString(int visibility)
    {
        switch (visibility)
        {
            case View.GONE:
                return "GONE";
            case View.INVISIBLE:
                return "INVISIBLE";
            case View.VISIBLE:
                return "VISIBLE";
            default:
                return "UNKNOWN";
        }
    }
    private static String getOrientationString(int orientation)
    {
        switch (orientation)
        {
            case ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED:
                return "UNSPECIFIED";
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                return "LANDSCAPE";
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                return "PORTRAIT";
            default:
                return "UNKNOWN";
        }
    }

    private int px2dip(int pixels)
    {
        return pixels * DisplayMetrics.DENSITY_DEFAULT / _displayMetrics.densityDpi;
        // return pixels;
    }

    private void onLayoutWebView(MinimobWebView wv, boolean changed, int left, int top, int right, int bottom)
    {
        boolean isCurrent = (wv == _currentWebView);
        MinimobViewLog.w(TAG, "onLayoutWebView " + (wv == _webView ? "1 " : "2 ") + isCurrent + " (" + _state + ") " +
                changed + " " + left + " " + top + " " + right + " " + bottom);
        if (!isCurrent)
        {
            MinimobViewLog.d(TAG, "onLayoutWebView ignored, not current");
            return;
        }
        if (_isForcingFullScreen)
        {
            MinimobViewLog.d(TAG, "onLayoutWebView ignored, isForcingFullScreen");
            _isForcingFullScreen = false;
            return;
        }

        // If closing from expanded state, just set currentPosition to default position in onLayout above.
        if (!_isClosing)
        {
            if (_isInterstitial)
            {
                // For interstitials, the default position is always the current position
                if (!_defaultPosition.equals(_currentPosition))
                {
                    _defaultPosition = new Rect(_currentPosition);
                }
            }
        }

        if (_isExpandingFromDefault)
        {
            _isExpandingFromDefault = false;
            if (_isInterstitial)
            {
                _state = STATE_DEFAULT;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void pauseWebView(MinimobWebView webView)
    {
        MinimobViewLog.d(TAG, "pauseWebView " + webView.toString());
        // Stop any video/animation that may be running in the WebView.
        // Otherwise, it will keep playing in the background.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            webView.onPause();
        }
        else
        {
            webView.loadUrl("about:blank");
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void resumeWebView(MinimobWebView webView)
    {
        MinimobViewLog.d(TAG, "resumeWebView " + webView.toString());
        // Resume any video/animation that was running in the WebView.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            webView.onResume();
        }
        else
        {
            webView.loadUrl(webView.getUrl());
        }
    }

    private void injectMinimobJSInterface(final MinimobWebView wv)
    {
        if (TextUtils.isEmpty(_minimobJSInterface))
        {
            String str = _minimobjsinterfaceBase64;
            byte[] minimobJSInterfaceBytes = Base64.decode(str, Base64.DEFAULT);
            _minimobJSInterface = new String(minimobJSInterfaceBytes);
        }
        MinimobViewLog.d(TAG, "injectMinimobJSInterface ok " + _minimobJSInterface.length());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            wv.loadData("<html></html>", "text/html", "UTF-8");
            wv.evaluateJavascript(_minimobJSInterface, new ValueCallback<String>()
            {
                @Override
                public void onReceiveValue(String value)
                {

                }
            });
        }
        else
        {
            wv.loadUrl("javascript:" + _minimobJSInterface);
        }
    }
    @SuppressLint("NewApi")
    private void injectJavaScript(String js)
    {
        injectJavaScript(_currentWebView, js);
    }

    @SuppressLint("NewApi")
    private void injectJavaScript(MinimobWebView webView, String js)
    {
        if (!TextUtils.isEmpty(js))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                MinimobViewLog.d(TAG, "evaluating js: " + js);
                webView.evaluateJavascript(js, new ValueCallback<String>()
                {
                    @Override
                    public void onReceiveValue(String value)
                    {
						MinimobViewLog.d(TAG, "evaluate js complete: "+value);
                    }
                });
            }
            else
            {
                MinimobViewLog.d(TAG, "loading url: " + js);
                webView.loadUrl("javascript:" + js);
            }
        }
    }

    public void showAsInterstitial()
    {
        expand(null);
    }

    private void toggleWebViewVisibility(final MinimobWebView minimobWebView, boolean visible)
    {
        int visibility = visible ? View.VISIBLE : View.GONE;
        // make webview visible
        minimobWebView.setVisibility(visibility);

        // make the container of the webview visible
        ((View) minimobWebView.getParent()).setVisibility(visibility);

        Log.i(TAG, "MinimobWebView with id:" + minimobWebView.getWebViewId() + " is " + (visible ? "visible" : "gone"));
    }
    //endregion HELPERS

    //region AdTag Settings
    private static MinimobWebView loadMinimobWebView(MinimobWebView minimobWebView, Activity activity, String minimobScript, boolean isInterstitial, boolean isFullScreen, boolean isVideo)
    {
        // initialize webview in case it came from an inflated webview
        minimobWebView.init(activity, isInterstitial, isFullScreen, isVideo);

        if (!minimobScript.isEmpty())
        {
            String html = generateHtml(minimobScript, isVideo, minimobWebView);

            // load url
            minimobWebView.loadDataWithBaseURL(baseUrl, html, "text/html", "utf-8", null);
            Log.d(TAG + "-" + "loadMinimobWebView", "Loaded the url to webView with webViewId " + minimobWebView.getWebViewId());
        }

        return minimobWebView;
    }

    private static String generateHtml(String minimobScript, boolean isVideo, MinimobWebView minimobWebView)
    {
        StringBuffer processedHtml = new StringBuffer();

        try
        {
            // first replace the adTag settings
            String processedMinimobScript = setAdTagSettings(minimobScript, minimobWebView);

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
            processedHtml.append(/*"</div></body>"*/"</body>");
            processedHtml.append(ls);
            processedHtml.append("</html>");

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

    private static String setAdTagSettings(String minimobScript, MinimobWebView minimobWebView)
    {
        try
        {
            minimobScript = setAdTagSetting("[placement_width]", String.valueOf(getWebViewDimensions(minimobWebView)[0]), minimobScript);
            minimobScript = setAdTagSetting("[placement_height]", String.valueOf(getWebViewDimensions(minimobWebView)[0]), minimobScript);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e(TAG + "-" + "setAdTagSettings", ex.getMessage());
        }

        return minimobScript;
    }

    private static String setAdTagSetting(String container, String value, String text)
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

    private static int[] getWebViewDimensions(WebView webView)
    {
        int width = webView.getWidth();
        int height = webView.getHeight();

        int[] dims = new int[2];
        dims[0] = width;
        dims[1] = height;

        return dims;
    }
    //endregion AdTag

    //endregion METHODS

    //region WebView Clients
    private class MinimobWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            MinimobViewLog.d(TAG, "shouldOverrideUrlLoading: " + url);
            if (url.startsWith("mmji://"))
            {
                parseCommandUrl(url);
                return true;
            }
            else
            {
                open(url);
                return true;
            }
        }
    }
    //endregion WebView Clients
}
