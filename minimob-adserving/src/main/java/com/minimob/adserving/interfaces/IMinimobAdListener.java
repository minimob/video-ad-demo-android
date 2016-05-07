package com.minimob.adserving.interfaces;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;

/**
 * Created by v.prantzos on 15/1/2016.
 */
public interface IMinimobAdListener
{
    void HandleCrash(String tag, Throwable ex);
    void LogMessage(String suffix, String message);
    void LogError(String suffix, String message);

    void ShowFragment(int container, Fragment fragment);
    Context GetAppContext();
    Context GetActivityContext();
    DisplayMetrics GetDisplayMetrics();
    void RunOnUIThread(Runnable runnable);
}
