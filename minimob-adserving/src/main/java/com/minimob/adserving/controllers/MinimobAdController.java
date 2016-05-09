package com.minimob.adserving.controllers;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.minimob.adserving.adzones.AdTag;
import com.minimob.adserving.adzones.AdZone;
import com.minimob.adserving.adzones.AdZoneVideo;
import com.minimob.adserving.adzones.AdZoneVideoPreloaded;
import com.minimob.adserving.helpers.AdZoneType;
import com.minimob.adserving.helpers.AdvertisingIdClient;
import com.minimob.adserving.interfaces.IAdZoneCompleted;
import com.minimob.adserving.interfaces.IAdZoneCreatedListener;
import com.minimob.adserving.views.MinimobBaseActivity;

import java.util.HashMap;
import java.util.Map;

public class MinimobAdController {
    private String TAG = "MINIMOB-" + MinimobAdController.class.getSimpleName();
    private static final MinimobAdController _instance = new MinimobAdController();
    private final Map<Integer, AdZone> _dictionary;
    private Integer _originalOrientation;
    private boolean _initialized = false;
    private GaidAsyncTask _gaidAsyncTask = null;
    public String gaid;
    private AdZone _adZone;

    //region accessors
    public void setAdZone(AdZone adZone) {
        this._adZone = adZone;
    }

    public AdZone getAdZone() {
        return this._adZone;
    }
    //endregion accessors

    private IAdZoneCreatedListener _adZoneCreatedListener;
    public void setAdZoneCreatedListener(IAdZoneCreatedListener adZoneCreatedListener) {
        this._adZoneCreatedListener = adZoneCreatedListener;
    }

    public void updateActivity(MinimobBaseActivity activity)
    {
        _adZone.updateActivity(activity);
    }

    private MinimobAdController() {
        this._dictionary = new HashMap<>();
    }

    public static MinimobAdController getInstance() {
        return _instance;
    }

    public void getVideo(final Activity activity, final AdTag adTag)
    {
        adTag.setPreload(false);
        if (!_initialized)
        {
            new AsyncTask<Activity, Void, String>()
            {
                @Override
                protected String doInBackground(Activity... params)
                {
                    String gaid = "";
                    // Moves the current Thread into the background
                    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                    AdvertisingIdClient.AdInfo adInfo = null;
                    try
                    {
                        adInfo = AdvertisingIdClient.getAdvertisingIdInfo(activity);
                        final String id = adInfo.getId();
                        //final boolean isLAT = adInfo.isLimitAdTrackingEnabled();
                        gaid = id;

                    }
                    catch (Exception ex)
                    {
                        // Unrecoverable error connecting to Google Play services (e.g.,
                        // the old version of the service doesn't support getting AdvertisingId).
                        gaid = "";
                        ex.printStackTrace();
                        Log.e(TAG + "-" + "getGAID", ex.getMessage());
                    }
                    finally
                    {
                        _initialized = true;
                    }

                    return gaid;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    gaid = s;
                    adTag.setGaid(gaid);
                    AdZoneVideo az = _getVideo(activity, adTag.getAdTag());
                    //call listener
                    if (_adZoneCreatedListener != null){
                        _adZoneCreatedListener.onAdZoneCreated(az);
                    }
                }
            }.execute(activity);
        }
        else
        {
            AdZoneVideo az = _getVideo(activity, adTag.getAdTag());
            if (_adZoneCreatedListener != null) {
                _adZoneCreatedListener.onAdZoneCreated(az);
            }
        }
    }

    private AdZoneVideo _getVideo(Activity activity, String adTag)
    {
        if (_originalOrientation == null) {
            _originalOrientation = activity.getRequestedOrientation();
        }

        int key = AdZone.getAdZoneId(adTag);
        AdZone adZone = null;

        synchronized (_dictionary)
        {
            adZone = this._dictionary.get(key);

            if (adZone == null)
            {
                adZone = new AdZoneVideo(activity, adTag, _originalOrientation);
                _dictionary.put(adZone.Id(), adZone);
            }
            else
            {
                if (!adZone.checkIfActivitiesMatch(activity)) {
                    adZone.updateActivity(activity);
                }
            }
        }

        if (adZone.type != AdZoneType.Video) {
            return null;
        }

        ((AdZoneVideo) adZone).setAdZoneCompletedListener(new IAdZoneCompleted() {
            @Override
            public void onAdZoneCompleted(int adZoneId) {
                MinimobAdController.getInstance().RemoveFromCache(adZoneId);
            }
        });

        return (AdZoneVideo) adZone;
    }

    public void getVideoPreloaded(final Activity activity, final AdTag adTag)
    {
        adTag.setPreload(true);
        if (!_initialized)
        {
            new AsyncTask<Activity, Void, String>()
            {
                @Override
                protected String doInBackground(Activity... params)
                {
                    String gaid = "";
                    // Moves the current Thread into the background
                    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                    AdvertisingIdClient.AdInfo adInfo = null;
                    try
                    {
                        adInfo = AdvertisingIdClient.getAdvertisingIdInfo(activity);
                        final String id = adInfo.getId();
                        //final boolean isLAT = adInfo.isLimitAdTrackingEnabled();
                        gaid = id;

                    }
                    catch (Exception ex)
                    {
                        // Unrecoverable error connecting to Google Play services (e.g.,
                        // the old version of the service doesn't support getting AdvertisingId).
                        gaid = "";
                        ex.printStackTrace();
                        Log.e(TAG + "-" + "getGAID", ex.getMessage());
                    }
                    finally
                    {
                        _initialized = true;
                    }

                    return gaid;
                }

                @Override
                protected void onPostExecute(String s)
                {
                    super.onPostExecute(s);
                    gaid = s;
                    adTag.setGaid(gaid);
                    AdZoneVideoPreloaded az = _getVideoPreloaded(activity, adTag.getAdTag());
                    //call listener
                    if (_adZoneCreatedListener != null){
                        _adZoneCreatedListener.onAdZoneCreated(az);
                    }
                }
            }.execute(activity);
        }
        else
        {
            AdZoneVideoPreloaded az = _getVideoPreloaded(activity, adTag.getAdTag());
            if (_adZoneCreatedListener != null) {
                _adZoneCreatedListener.onAdZoneCreated(az);
            }
        }
    }

    private AdZoneVideoPreloaded _getVideoPreloaded(Activity activity, String adTag)
    {
        if (_originalOrientation == null) {
            _originalOrientation = activity.getRequestedOrientation();
        }

        int key = AdZone.getAdZoneId(adTag);
        AdZone adZone = null;

        synchronized (_dictionary)
        {
            adZone = this._dictionary.get(key);

            if (adZone == null) {
                adZone = new AdZoneVideoPreloaded(activity, adTag, _originalOrientation);
                _dictionary.put(adZone.Id(), adZone);
            } else {
                if (!adZone.checkIfActivitiesMatch(activity)) {
                    adZone.updateActivity(activity);
                }
            }
        }

        if (adZone.type != AdZoneType.VideoPreloaded) {
            return null;
        }

        ((AdZoneVideoPreloaded) adZone).setAdZoneCompletedListener(new IAdZoneCompleted() {
            @Override
            public void onAdZoneCompleted(int adZoneId) {
                MinimobAdController.getInstance().RemoveFromCache(adZoneId);
            }
        });

        return (AdZoneVideoPreloaded) adZone;
    }

    private void RemoveFromCache(Integer id) {
        synchronized (_dictionary) {
            if (_dictionary.containsKey(id)) {
                _dictionary.remove(id);
            }
        }
    }

    public void StartGaidAsyncTask(final Activity activity, final String adTag)
    {
        try
        {
            // start the task
            if (_gaidAsyncTask == null || _gaidAsyncTask.getStatus().equals(AsyncTask.Status.FINISHED))
            {
                _gaidAsyncTask = new GaidAsyncTask(activity, adTag);
                _gaidAsyncTask.execute();
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage());
        }
    }

    private class GaidAsyncTask extends AsyncTask<Activity, Void, String>
    {
        private final Activity _activity;
        private final String _adTag;

        GaidAsyncTask(Activity activity, String adTag)
        {
            _activity = activity;
            _adTag = adTag;
        }

        @Override
        protected String doInBackground(Activity... params)
        {
            String gaid = "";
            // Moves the current Thread into the background
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            AdvertisingIdClient.AdInfo adInfo = null;
            try
            {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(_activity);
                final String id = adInfo.getId();
                //final boolean isLAT = adInfo.isLimitAdTrackingEnabled();
                gaid = id;

            }
            catch (Exception ex)
            {
                // Unrecoverable error connecting to Google Play services (e.g.,
                // the old version of the service doesn't support getting AdvertisingId).
                gaid = "";
                ex.printStackTrace();
                Log.e(TAG + "-" + "getGAID", ex.getMessage());
            }
            finally
            {
                _initialized = true;
            }

            return gaid;
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            gaid = s;
        }
    }
}
