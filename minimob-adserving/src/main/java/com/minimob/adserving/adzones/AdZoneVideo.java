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
import com.minimob.adserving.views.MinimobBaseActivity;
import com.minimob.adserving.views.MinimobView;
import com.minimob.adserving.views.VideoActivity;

public class AdZoneVideo extends AdZone implements IMinimobViewListener
{
    //region VARIABLES
    private static final String TAG = AdZoneVideo.class.getSimpleName();

    private final Object _criticalSection = new Object();
    private State _state;

    //region Listeners
    private IVideoPlayingListener _videoPlayingListener;
    private IVideoFinishedListener _videoFinishedListener;
    private IVideoClosedListener _videoClosedListener;
    private IAdZoneCompleted _adZoneCompletedListener;
    //endregion Listeners

    //region State Machine
    public enum Event
    {
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
            State doAction(Event event, AdZoneVideo adZone)
            {
                synchronized (adZone._criticalSection)
                {
                    if (event == Event.SHOW)
                    {
                        adZone._loadMinimobView();
                        return LOADING;
                    }
                    else if (event == Event.ADS_AVAILABLE)
                    {
                        return INITIAL;
                    }
                    else if (event == Event.ADS_NOT_AVAILABLE)
                    {
                        return INITIAL;
                    }
                    else if (event == Event.CLOSE)
                    {
                        return INITIAL;
                    }
                    else
                    {
                        return INITIAL;
                    }
                }
            }
        },
        LOADING
        {
            @Override
            State doAction(Event event, AdZoneVideo adZone)
            {
                synchronized (adZone._criticalSection)
                {
                    if (event == Event.SHOW)
                    {
                        return LOADING;
                    }
                    else if (event == Event.ADS_AVAILABLE)
                    {
                        adZone._showInterstitial();
                        return PLAYING;
                    }
                    else if (event == Event.ADS_NOT_AVAILABLE)
                    {
                        adZone._destroy();
                        return INITIAL;
                    }
                    else if (event == Event.CLOSE)
                    {
                        adZone._destroy();
                        return INITIAL;
                    }

                    return LOADING;
                }
            }
        },
        PLAYING
        {
            @Override
            State doAction(Event event, AdZoneVideo adZone)
            {
                synchronized (adZone._criticalSection)
                {
                    if (event == Event.SHOW)
                    {
                        return PLAYING;
                    }
                    else if (event == Event.ADS_AVAILABLE)
                    {
                        return PLAYING;
                    }
                    else if (event == Event.ADS_NOT_AVAILABLE)
                    {
                        return PLAYING;
                    }
                    else if (event == Event.CLOSE)
                    {
                        adZone._destroy();
                        return INITIAL;
                    }

                    return PLAYING;
                }
            }
        },
        FINISHED
        {
        @Override
        State doAction(Event event, AdZoneVideo adZone)
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
                case PLAYING: return "video playing";
                case FINISHED: return "video finished";
            }
            return this.toString();
        }

        abstract State doAction(Event event, AdZoneVideo adZone);
    }
    //endregion State Machine

    //endregion VARIABLES

    //region CONSTRUCTORS
    public AdZoneVideo(Activity activity, String adTag, int originalOrientation)
    {
        super(adTag);
        this.type = AdZoneType.Video;
        this._state = State.INITIAL;
        this._activity = activity;
        this.originalOrientation = originalOrientation;
    }
    //endregion CONSTRUCTORS

    //region METHODS

    //region Public Methods

    public void show()
    {
        this._state = _state.doAction(Event.SHOW, this);
    }

    public State getState()
    {
        synchronized (this._criticalSection)
        {
            return this._state;
        }
    }

    //region Listener setters
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
                _adZoneCompletedListener.onAdZoneCompleted(AdZoneVideo.this.Id());
            }

            (_activity).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (_videoPlayingListener != null)
                    {
                        _videoPlayingListener.onVideoPlaying(AdZoneVideo.this);
                    }
                }
            });
        }
        catch (Exception ex)
        {
            MinimobHelper.getInstance().handleCrash(TAG, ex);
        }
    }

    private void _destroy()
    {
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

        if (this._adsAvailableListener != null)
        {
            (_activity).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    _adsAvailableListener.onAdsAvailable(AdZoneVideo.this);
                }
            });
        }

        if (!packageId.isEmpty())
        {
            this.packageId = packageId;
        }

        this._state = this._state.doAction(Event.ADS_AVAILABLE, this);
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
                    _adsNotAvailableListener.onAdsNotAvailable(AdZoneVideo.this);
                }
            });
        }

        this._state = this._state.doAction(Event.ADS_NOT_AVAILABLE, this);
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
                    _videoFinishedListener.onVideoFinished(AdZoneVideo.this);
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
                    _videoClosedListener.onVideoClosed(AdZoneVideo.this);
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
