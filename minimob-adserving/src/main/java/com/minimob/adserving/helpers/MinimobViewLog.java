package com.minimob.adserving.helpers;

import android.util.Log;

public class MinimobViewLog
{
    private static final String TAG = "MINIMOB" + "-" + MinimobViewLog.class.getSimpleName();

    public enum LOG_LEVEL
    {
        debug(0),
        info(1),
        warning(2),
        error(3),
        none(4);

        private int value;

        private LOG_LEVEL(int value)
        {
            this.value = value;

        }

        public int getValue()
        {
            return value;
        }

    }

    private static LOG_LEVEL LEVEL = LOG_LEVEL.debug;

    public static void d(String msg)
    {
        if (LEVEL.getValue() <= LOG_LEVEL.debug.getValue())
        {
            Log.d(TAG, msg);
        }
    }

    public static void i(String msg)
    {
        if (LEVEL.getValue() <= LOG_LEVEL.info.getValue())
        {
            Log.i(TAG, msg);
        }
    }

    public static void w(String msg)
    {
        if (LEVEL.getValue() <= LOG_LEVEL.warning.getValue())
        {
            Log.w(TAG, msg);
        }
    }

    public static void e(String msg)
    {
        if (LEVEL.getValue() <= LOG_LEVEL.error.getValue())
        {
            Log.e(TAG, msg);
        }
    }

    public static void d(String subTag, String msg)
    {
        if (LEVEL.getValue() <= LOG_LEVEL.debug.getValue())
        {
            msg = "[" + subTag + "] " + msg;
            Log.d(TAG, msg);
        }
    }

    public static void i(String subTag, String msg)
    {
        if (LEVEL.getValue() <= LOG_LEVEL.info.getValue())
        {
            msg = "[" + subTag + "] " + msg;
            Log.i(TAG, msg);
        }
    }

    public static void w(String subTag, String msg)
    {
        if (LEVEL.getValue() <= LOG_LEVEL.warning.getValue())
        {
            msg = "[" + subTag + "] " + msg;
            Log.w(TAG, msg);
        }
    }

    public static void e(String subTag, String msg)
    {
        if (LEVEL.getValue() <= LOG_LEVEL.error.getValue())
        {
            msg = "[" + subTag + "] " + msg;
            Log.e(TAG, msg);
        }
    }

    public static void setLoggingLevel(LOG_LEVEL logLevel)
    {
        Log.i(TAG, "Changing logging level from :" + LEVEL + ". To:" + logLevel);
        LEVEL = logLevel;
    }

    public static LOG_LEVEL getLoggingLevel()
    {
        return LEVEL;
    }
}
