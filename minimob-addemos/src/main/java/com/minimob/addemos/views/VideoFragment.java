package com.minimob.addemos.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.minimob.addemos.R;

import com.minimob.adserving.adzones.AdTag;
import com.minimob.adserving.adzones.AdZone;
import com.minimob.adserving.adzones.AdZoneVideo;
import com.minimob.adserving.controllers.MinimobAdController;
import com.minimob.adserving.helpers.AdTagHelper;
import com.minimob.adserving.helpers.MinimobHelper;
import com.minimob.adserving.interfaces.IAdZoneCreatedListener;
import com.minimob.adserving.interfaces.IAdsAvailableListener;
import com.minimob.adserving.interfaces.IAdsNotAvailableListener;
import com.minimob.adserving.interfaces.IVideoClosedListener;
import com.minimob.adserving.interfaces.IVideoFinishedListener;
import com.minimob.adserving.interfaces.IVideoPlayingListener;

public class VideoFragment extends MinimobBaseFragment
{
    private static final String TAG = VideoFragment.class.getSimpleName();

    Button video_btnFullscreen_play;
    AdZoneVideo adZoneVideo;

    public VideoFragment()
    {

    }

    //region OVERRIDES
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        this.titleRes = R.string.videoads_fullscreen;
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
        this.showProgress(false);

        video_btnFullscreen_play = (Button) _activity.findViewById(R.id.video_btnFullscreen_play);
        video_btnFullscreen_play.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    showProgress(true);
                    _setupAdZone();
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
                    adZoneVideo = (AdZoneVideo) adZone;
                    if (adZoneVideo != null)
                    {
                        adZoneVideo.setAdsAvailableListener(new IAdsAvailableListener() {
                            @Override
                            public void onAdsAvailable(AdZone adZone) {
                                showProgress(false);
                                //MinimobHelper.getInstance().showToast(_activity, "ads available", Toast.LENGTH_SHORT);
                            }
                        });
                        adZoneVideo.setAdsNotAvailableListener(new IAdsNotAvailableListener() {
                            @Override
                            public void onAdsNotAvailable(AdZone adZone) {
                                showProgress(false);
                                //MinimobHelper.getInstance().showToast(_activity, "ads NOT available", Toast.LENGTH_SHORT);
                            }
                        });
                        adZoneVideo.setVideoPlayingListener(new IVideoPlayingListener() {
                            @Override
                            public void onVideoPlaying(AdZone adZone) {
                                showProgress(false);
                                //MinimobHelper.getInstance().showToast(_activity, "video playing", Toast.LENGTH_SHORT);
                            }
                        });
                        adZoneVideo.setVideoFinishedListener(new IVideoFinishedListener() {
                            @Override
                            public void onVideoFinished(AdZone adZone) {
                                //MinimobHelper.getInstance().showToast(_activity, "video finished", Toast.LENGTH_SHORT);
                            }
                        });
                        adZoneVideo.setVideoClosedListener(new IVideoClosedListener() {
                            @Override
                            public void onVideoClosed(AdZone adZone) {
                                //MinimobHelper.getInstance().showToast(_activity, "video closed", Toast.LENGTH_SHORT);
                            }
                        });

                        adZoneVideo.show();
                    }
                }
            });

            // TEST ADTAG
            String adTagString = AdTagHelper.getInstance().getMinimobScript(true);
            //create the AdTag object
            AdTag adTag = new AdTag(getContext(), adTagString);
            //set the custom tracking data (optional)
            adTag.setCustomTrackingData("some tracking data");
            //create the AdZone
            MinimobAdController.getInstance().getVideo(_activity, adTag);
        }
        catch (Exception ex)
        {
            _activity.HandleCrash(TAG, ex);
        }
    }

    //endregion HELPERS
}
