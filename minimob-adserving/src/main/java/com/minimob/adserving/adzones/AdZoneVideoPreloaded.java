package com.minimob.adserving.adzones;

import android.app.Activity;
import android.content.Intent;

import com.minimob.adserving.common.MinimobBaseView;
import com.minimob.adserving.controllers.MinimobAdController;
import com.minimob.adserving.helpers.AdZoneType;
import com.minimob.adserving.helpers.MinimobHelper;
import com.minimob.adserving.interfaces.IAdZoneCompleted;
import com.minimob.adserving.interfaces.IMinimobViewListener;
import com.minimob.adserving.interfaces.IVideoClosedListener;
import com.minimob.adserving.interfaces.IVideoFinishedListener;
import com.minimob.adserving.interfaces.IVideoPlayingListener;
import com.minimob.adserving.interfaces.IVideoLoadedListener;
import com.minimob.adserving.interfaces.IVideoLoadingListener;
import com.minimob.adserving.views.MinimobBaseActivity;
import com.minimob.adserving.views.MinimobView;
import com.minimob.adserving.views.VideoActivity;

import java.util.Date;

public class AdZoneVideoPreloaded extends AdZone implements IMinimobViewListener
{
    //region VARIABLES
    private static final String TAG = AdZoneVideoPreloaded.class.getSimpleName();

    private final Object _criticalSection = new Object();
    private State _state;
    private Date _dateLoaded;

    //region Listeners
    private IVideoLoadingListener _videoLoadingListener;
    private IVideoLoadedListener _videoLoadedListener;
    private IVideoPlayingListener _videoPlayingListener;
    private IVideoFinishedListener _videoFinishedListener;
    private IVideoClosedListener _videoClosedListener;
    private IAdZoneCompleted _adZoneCompletedListener;
    //endregion Listeners

    //region State Machine
    public enum Event
    {
        LOAD,
        SHOW,
        ADS_AVAILABLE,
        ADS_NOT_AVAILABLE,
        CLOSE
    }

    public enum State
    {
        INITIAL
        {
            @Override
            State doAction(Event event, AdZoneVideoPreloaded adZone)
            {
                synchronized (adZone._criticalSection)
                {
                    if (event == Event.LOAD) {
                        adZone._load();
                        return LOADING;
                    } else if (event == Event.SHOW) {
                        adZone._load();
                        return LOADING;
                        //return INITIAL;
                    } else if (event == Event.ADS_AVAILABLE) {
                        return INITIAL;
                    } else if (event == Event.ADS_NOT_AVAILABLE) {
                        return INITIAL;
                    } else if (event == Event.CLOSE) {
                        return INITIAL;
                    } else {
                        return INITIAL;
                    }
                }
            }
        },
        LOADING
        {
            @Override
            State doAction(Event event, final AdZoneVideoPreloaded adZone)
            {
                synchronized (adZone._criticalSection)
                {
                    if (event == Event.LOAD) {
                        if (adZone._videoLoadingListener != null) {
                            (adZone._activity).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adZone._videoLoadingListener.onVideoLoading(adZone);
                                }
                            });
                        }

                        return LOADING;
                    } else if (event == Event.SHOW) {
                        if (adZone._videoLoadingListener != null) {
                            (adZone._activity).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adZone._videoLoadingListener.onVideoLoading(adZone);
                                }
                            });
                        }
                        return LOADING;
                    } else if (event == Event.ADS_AVAILABLE) {
                        return LOADED;
                    } else if (event == Event.ADS_NOT_AVAILABLE) {
                        //adZone._destroy();
                        return INITIAL;
                    } else if (event == Event.CLOSE) {
                        adZone._destroy();
                        return FINISHED;
                    }

                    return LOADING;
                }
            }
        },
        LOADED
        {
            @Override
            State doAction(Event event, AdZoneVideoPreloaded adZone)
            {
                synchronized (adZone._criticalSection)
                {
                    if (event == Event.LOAD) {
                        boolean isVideoOutdated = adZone._isVideoOutdated();
                        if (isVideoOutdated) {
                            adZone._load();
                            return LOADING;
                        } else {
                            adZone._loaded();
                            return LOADED;
                        }
                    } else if (event == Event.SHOW) {
                        boolean isVideoOutdated = adZone._isVideoOutdated();
                        if (isVideoOutdated) {
                            adZone._load();
                            return LOADING;
                        } else {
                            adZone._showInterstitial();
                            return PLAYING;
                        }
                    } else if (event == Event.ADS_AVAILABLE) {
                        return LOADED;
                    } else if (event == Event.ADS_NOT_AVAILABLE) {
                        return LOADED;
                    } else if (event == Event.CLOSE) {
                        adZone._destroy();
                        return FINISHED;
                    }

                    return LOADED;
                }
            }
        },
        PLAYING
        {
            @Override
            State doAction(Event event, AdZoneVideoPreloaded adZone)
            {
                synchronized (adZone._criticalSection)
                {
                    if (event == Event.LOAD) {
                        return PLAYING;
                    } else if (event == Event.SHOW) {
                        return PLAYING;
                    } else if (event == Event.ADS_AVAILABLE) {
                        return PLAYING;
                    } else if (event == Event.ADS_NOT_AVAILABLE) {
                        return PLAYING;
                    } else if (event == Event.CLOSE) {
                        adZone._destroy();
                        return FINISHED;
                    }

                    return PLAYING;
                }
            }
        },
        FINISHED
        {
            @Override
            State doAction(Event event, AdZoneVideoPreloaded adZone)
            {
                synchronized (adZone._criticalSection)
                {
                    if (adZone._adZoneCompletedListener != null)
                    {
                        adZone._adZoneCompletedListener.onAdZoneCompleted(adZone.Id());
                    }

                    return FINISHED;
                }
            }
        };

        public String asString()
        {
            switch (this)
            {
                case INITIAL: return "ad zone initialized";
                case LOADING: return "video loading";
                case LOADED: return "video loaded";
                case PLAYING: return "video playing";
                case FINISHED: return "video finished";
            }
            return this.toString();
        }

        abstract State doAction(Event event, AdZoneVideoPreloaded adZone);
    }
    //endregion State Machine

    //endregion VARIABLES

    //region CONSTRUCTORS
    public AdZoneVideoPreloaded(Activity activity, String adTag, int originalOrientation)
    {
        super(adTag);
        this.type = AdZoneType.VideoPreloaded;
        this._state = State.INITIAL;
        this._activity = activity;
        this.originalOrientation = originalOrientation;
    }
    //endregion CONSTRUCTORS

    //region METHODS

    //region Public Methods

    public void load()
    {
        this._state = _state.doAction(Event.LOAD, this);
    }

    public void show()
    {
        this._state = _state.doAction(Event.SHOW, this);
    }

    public State getState()
    {
        State state;
        synchronized (this._criticalSection)
        {
            state = this._state;
        }
        return state;
    }

    //region Listener setters
    public void setVideoLoadingListener(IVideoLoadingListener listener) {
        this._videoLoadingListener = listener;
    }

    public void setVideoLoadedListener(IVideoLoadedListener listener) {
        this._videoLoadedListener = listener;
    }

    public void setVideoPlayingListener(IVideoPlayingListener listener) {
        this._videoPlayingListener = listener;
    }

    public void setVideoFinishedListener(IVideoFinishedListener listener) {
        this._videoFinishedListener = listener;
    }

    public void setVideoClosedListener(IVideoClosedListener listener) {
        this._videoClosedListener = listener;
    }

    public void setAdZoneCompletedListener(IAdZoneCompleted listener) {
        this._adZoneCompletedListener = listener;
    }
    //endregion Listener setters

    //endregion Public Methods

    //region Private Methods
    private void _load()
    {
        this._loadMinimobView();

        if (this._videoLoadingListener != null)
        {
            (_activity).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    _videoLoadingListener.onVideoLoading(AdZoneVideoPreloaded.this);
                }
            });
        }
    }

    private void _loaded()
    {
        if (this._videoLoadedListener != null)
        {
            (_activity).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    _videoLoadedListener.onVideoLoaded(AdZoneVideoPreloaded.this);
                }
            });
        }
    }

    private void _loadMinimobView()
    {
        try
        {
            _minimobView = new MinimobView(_activity, this.adTag, this, true, true, true, originalOrientation);
        }
        catch (Exception ex)
        {
            MinimobHelper.getInstance().handleCrash(TAG, ex);
        }
    }

    private void _showInterstitial()
    {
        try
        {
            MinimobAdController.getInstance().setAdZone(this);

            // we call this to avoid a bug in StrictMode that reports a android.os.StrictMode$InstanceCountViolation
            // because the garbage collector did not collect the instance of the activity before creating a new one
            System.gc();

            Intent playVideoIntent = new Intent(_activity, VideoActivity.class);
            _activity.startActivity(playVideoIntent);

            // FIRST REMOVE THE ADZONE FROM THE CACHE AND THEN RETURN THE PLAYING EVENT (IN CASE THE DEVELOPER PRELOADS AGAIN)
            if (_adZoneCompletedListener != null)
            {
                _adZoneCompletedListener.onAdZoneCompleted(AdZoneVideoPreloaded.this.Id());
            }

            if (_videoPlayingListener != null)
            {
                (_activity).runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        _videoPlayingListener.onVideoPlaying(AdZoneVideoPreloaded.this);
                    }
                });
            }
        }
        catch (Exception ex)
        {
            MinimobHelper.getInstance().handleCrash(TAG, ex);
        }
    }

    private boolean _isVideoOutdated()
    {
        try
        {
            if (this._dateLoaded == null)
            {
                return true;
            }

            Date dtStart = this._dateLoaded;
            Date dtEnd = new Date();
            float secondsPassed = (dtEnd.getTime() - dtStart.getTime()) / 1000;

            return secondsPassed > MinimobHelper.getInstance().videoCachingTimeInSeconds;
        }
        catch (Exception ex)
        {
            MinimobHelper.getInstance().handleCrash(TAG, ex);
            return true;
        }
    }

    private void _destroy()
    {
        _dateLoaded = null;

        if (_minimobView != null)
        {
            _minimobView.destroy();
        }
    }

    //endregion Private Methods

    //endregion METHODS

    //region IMinimobViewListener implementation
    @Override
    public void onAdsAvailable(MinimobBaseView minimobBaseView, String packageId)
    {
        MinimobHelper.getInstance().logMessage(TAG + "-" + IMinimobViewListener.class.getSimpleName(), "onAdsAvailable");

        this._dateLoaded = new Date();

        if (this._adsAvailableListener != null)
        {
            (_activity).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    _adsAvailableListener.onAdsAvailable(AdZoneVideoPreloaded.this);
                }
            });
        }

        if (_videoLoadedListener != null)
        {
            (_activity).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    _videoLoadedListener.onVideoLoaded(AdZoneVideoPreloaded.this);
                }
            });
        }

        if (!packageId.isEmpty())
        {
            this.packageId = packageId;
        }

        this._state = _state.doAction(Event.ADS_AVAILABLE, this);

    }

    @Override
    public void onAdsNotAvailable(MinimobBaseView minimobBaseView)
    {
        MinimobHelper.getInstance().logMessage(TAG + "-" + IMinimobViewListener.class.getSimpleName(), "onAdsNotAvailable");

        if (this._adsNotAvailableListener != null)
        {
            (_activity).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    _adsNotAvailableListener.onAdsNotAvailable(AdZoneVideoPreloaded.this);
                }
            });
        }

        this._state = _state.doAction(Event.ADS_NOT_AVAILABLE, this);
    }

    @Override
    public void onVideoFinished(MinimobBaseView minimobBaseView)
    {
        MinimobHelper.getInstance().logMessage(TAG + "-" + IMinimobViewListener.class.getSimpleName(), "onVideoFinished");
        this._state = this._state.doAction(Event.CLOSE, this);

        if (this._videoFinishedListener != null)
        {
            (_activity).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    _videoFinishedListener.onVideoFinished(AdZoneVideoPreloaded.this);
                }
            });
        }
    }

    @Override
    public void onMinimobViewClosed(MinimobBaseView minimobBaseView)
    {
        MinimobHelper.getInstance().logMessage(TAG + "-" + IMinimobViewListener.class.getSimpleName(), "onMinimobViewClosed");
        this._state = this._state.doAction(Event.CLOSE, this);

        if (_videoClosedListener != null)
        {
            (_activity).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    _videoClosedListener.onVideoClosed(AdZoneVideoPreloaded.this);
                }
            });
        }
    }

    @Override
    public void onAdClicked(String url)
    {
        MinimobHelper.getInstance().logMessage(TAG + "-" + IMinimobViewListener.class.getSimpleName(), "onAdClicked " + url);

        _activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                MinimobBaseActivity minimobBaseActivity = (MinimobBaseActivity) _activity;
                MinimobHelper.getInstance().toggleLoading(minimobBaseActivity, true);
            }
        });

        this.clickAdUrl(_activity, url, this);
    }
    //endregion IMinimobViewListener implementation
}