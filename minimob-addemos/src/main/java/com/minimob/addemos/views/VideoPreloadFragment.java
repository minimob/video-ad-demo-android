package com.minimob.addemos.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.minimob.addemos.R;

import com.minimob.adserving.adzones.AdZone;
import com.minimob.adserving.adzones.AdZoneVideoPreloaded;
import com.minimob.adserving.controllers.MinimobAdController;
import com.minimob.adserving.helpers.AdTagHelper;
import com.minimob.adserving.helpers.MinimobHelper;
import com.minimob.adserving.interfaces.IAdZoneCreatedListener;
import com.minimob.adserving.interfaces.IAdsAvailableListener;
import com.minimob.adserving.interfaces.IAdsNotAvailableListener;
import com.minimob.adserving.interfaces.IVideoClosedListener;
import com.minimob.adserving.interfaces.IVideoFinishedListener;
import com.minimob.adserving.interfaces.IVideoPlayingListener;
import com.minimob.adserving.interfaces.IVideoLoadedListener;
import com.minimob.adserving.interfaces.IVideoLoadingListener;

public class VideoPreloadFragment extends MinimobBaseFragment
{
    private static final String TAG = VideoPreloadFragment.class.getSimpleName();

    Button video_btnFullscreen_play;
    String adTag = AdTagHelper.getInstance().getMinimobScript(true, true);
    AdZoneVideoPreloaded adZoneVideoPreloaded;

    public VideoPreloadFragment()
    {

    }

    //region OVERRIDES
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_video_preloaded, container, false);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        this.titleRes = R.string.videoads_fullscreen_preloaded;
        super.onResume();
        this._setupUI();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }
    //endregion OVERRIDES

    //region HELPERS
    private void _setupUI()
    {
        _activity.setTitle(this.titleRes);
        abProgress = (ProgressBar)_activity.findViewById(R.id.actionbar_progress);
        MinimobHelper.getInstance().showProgress(abProgress, _activity, false);

        try
        {
            this._setupAdZone();
        }
        catch (Exception ex)
        {
            _activity.HandleCrash(TAG, ex);
        }

        video_btnFullscreen_play = (Button) _activity.findViewById(R.id.video_btnFullscreen_play_preloaded);
        video_btnFullscreen_play.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    if (adZoneVideoPreloaded != null) {
                        adZoneVideoPreloaded.show();
                    }
                }
                catch (Exception ex)
                {
                    _activity.HandleCrash(TAG, ex);
                }
            }
        });
    }

    private void _setupAdZone()
    {
        try
        {
            MinimobAdController.getInstance().setAdZoneCreatedListener(new IAdZoneCreatedListener()
            {
                @Override
                public void onAdZoneCreated(AdZone adZone)
                {
                    adZoneVideoPreloaded = (AdZoneVideoPreloaded) adZone;
                    if (adZoneVideoPreloaded != null)
                    {
//                        adZoneVideoPreloaded.setAdsAvailableListener(new IAdsAvailableListener()
//                        {
//                            @Override
//                            public void onAdsAvailable(AdZone adZone)
//                            {
//                                MinimobHelper.getInstance().ShowProgress(abProgress, _activity, false);
//                                MinimobHelper.getInstance().showToast(_activity, "ads available " + adZone.timeCreated, Toast.LENGTH_SHORT);
//                            }
//                        });
                        adZoneVideoPreloaded.setAdsNotAvailableListener(new IAdsNotAvailableListener() {
                            @Override
                            public void onAdsNotAvailable(AdZone adZone) {
                                MinimobHelper.getInstance().showProgress(abProgress, _activity, false);
                                //MinimobHelper.getInstance().showToast(_activity, "ads NOT available " + adZone.timeCreated, Toast.LENGTH_SHORT);
                            }
                        });
                        adZoneVideoPreloaded.setVideoLoadingListener(new IVideoLoadingListener() {
                            @Override
                            public void onVideoLoading(AdZone adZone) {
                                MinimobHelper.getInstance().showProgress(abProgress, _activity, true);
                                MinimobHelper.getInstance().showToast(_activity, "video loading " + adZone.timeCreated, Toast.LENGTH_SHORT);
                            }
                        });
                        adZoneVideoPreloaded.setVideoLoadedListener(new IVideoLoadedListener() {
                            @Override
                            public void onVideoLoaded(AdZone adZone) {
                                MinimobHelper.getInstance().showProgress(abProgress, _activity, false);
                                MinimobHelper.getInstance().showToast(_activity, "video loaded " + adZone.timeCreated, Toast.LENGTH_SHORT);
                            }
                        });
                        adZoneVideoPreloaded.setVideoPlayingListener(new IVideoPlayingListener() {
                            @Override
                            public void onVideoPlaying(AdZone adZone) {
                                MinimobHelper.getInstance().showProgress(abProgress, _activity, false);
                                //MinimobHelper.getInstance().showToast(_activity, "video playing " + adZone.timeCreated, Toast.LENGTH_SHORT);

                                _setupAdZone();
                            }
                        });
                        adZoneVideoPreloaded.setVideoFinishedListener(new IVideoFinishedListener() {
                            @Override
                            public void onVideoFinished(AdZone adZone) {
                                //MinimobHelper.getInstance().showToast(_activity, "video finished " + adZone.timeCreated, Toast.LENGTH_SHORT);
                            }
                        });
                        adZoneVideoPreloaded.setVideoClosedListener(new IVideoClosedListener() {
                            @Override
                            public void onVideoClosed(AdZone adZone) {
                                //MinimobHelper.getInstance().showToast(_activity, "video closed " + adZone.timeCreated, Toast.LENGTH_SHORT);

                                _setupAdZone();
                            }
                        });
                        adZoneVideoPreloaded.load();
                    }
                }
            });

            MinimobAdController.getInstance().getVideoPreloaded(_activity, adTag);

        }
        catch (Exception ex)
        {
            _activity.HandleCrash(TAG, ex);
        }
    }
    //endregion HELPERS
}