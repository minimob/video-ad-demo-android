package com.minimob.addemos.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.minimob.addemos.R;

import com.minimob.adserving.adzones.AdTag;
import com.minimob.adserving.adzones.AdZone;
import com.minimob.adserving.adzones.AdZoneVideo;
import com.minimob.adserving.controllers.MinimobAdController;
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
        //set the title to the ActionBar
        _activity.setTitle(this.titleRes);
        //a progress bar to show on loading times
        abProgress = (ProgressBar)_activity.findViewById(R.id.actionbar_progress);
        //hide the progress bar
        this.showProgress(false);

        //button to load and show the video ad
        video_btnFullscreen_play = (Button) _activity.findViewById(R.id.video_btnFullscreen_play);
        //when the user clicks on the button
        video_btnFullscreen_play.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    //show the progress bar
                    showProgress(true);
                    //setup an AdZone and show it
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
            //set the listener that gets called when the AdZone is created by MinimobAdController
            MinimobAdController.getInstance().setAdZoneCreatedListener(new IAdZoneCreatedListener()
            {
                @Override
                public void onAdZoneCreated(AdZone adZone)
                {
                    //cast the returned AdZone to the AdZoneVideo type
                    adZoneVideo = (AdZoneVideo) adZone;
                    if (adZoneVideo != null)
                    {
                        //set a listener for when the ad server returns the event that there are ads available
                        adZoneVideo.setAdsAvailableListener(new IAdsAvailableListener() {
                            @Override
                            public void onAdsAvailable(AdZone adZone) {
                                //hide the progress bar
                                showProgress(false);
                                //MinimobHelper.getInstance().showToast(_activity, "ads available", Toast.LENGTH_SHORT);
                            }
                        });
                        //set a listener for when the ad server returns the event that there are NO ads available
                        adZoneVideo.setAdsNotAvailableListener(new IAdsNotAvailableListener() {
                            @Override
                            public void onAdsNotAvailable(AdZone adZone) {
                                //hide the progress bar
                                showProgress(false);
                                //MinimobHelper.getInstance().showToast(_activity, "ads NOT available", Toast.LENGTH_SHORT);
                            }
                        });
                        //set a listener for when the video started playing
                        adZoneVideo.setVideoPlayingListener(new IVideoPlayingListener() {
                            @Override
                            public void onVideoPlaying(AdZone adZone) {
                                //hide the progress bar
                                showProgress(false);
                                //MinimobHelper.getInstance().showToast(_activity, "video playing", Toast.LENGTH_SHORT);
                            }
                        });
                        //set a listener for when the video finished playing
                        adZoneVideo.setVideoFinishedListener(new IVideoFinishedListener() {
                            @Override
                            public void onVideoFinished(AdZone adZone) {
                                //MinimobHelper.getInstance().showToast(_activity, "video finished", Toast.LENGTH_SHORT);
                            }
                        });
                        //set a listener for when the video was closed by the user
                        adZoneVideo.setVideoClosedListener(new IVideoClosedListener() {
                            @Override
                            public void onVideoClosed(AdZone adZone) {
                                //MinimobHelper.getInstance().showToast(_activity, "video closed", Toast.LENGTH_SHORT);
                            }
                        });

                        //show the video
                        adZoneVideo.show();
                    }
                }
            });

            // TEST ADTAG
            String adTagString = "<script> \n" +
                    " var mmAdTagSettings = { \n" +
                    " imei: \"[imei]\", \n" +
                    " android_id: \"[android_id]\", \n" +
                    " gaid: \"[gaid]\", \n" +
                    " idfa: \"[idfa]\", \n" +
                    " idfv: \"[idfv]\", \n" +
                    " category: \"[category]\", \n" +
                    " age: \"[age]\", \n" +
                    " gender: \"[gender]\", \n" +
                    " keywords: \"[keywords]\", \n" +
                    " lat: \"[lat]\", \n" +
                    " lon: \"[lon]\", \n" +
                    " device_width: \"[device_width]\", \n" +
                    " device_height: \"[device_height]\", \n" +
                    " mnc: \"[mnc]\", \n" +
                    " mcc: \"[mcc]\", \n" +
                    " wifi: \"[wifi]\", \n" +
                    " ios_version: \"[ios_version]\", \n" +
                    " android_version: \"[android_version]\", \n" +
                    " placement_width: \"[placement_width]\", \n" +
                    " placement_height: \"[placement_height]\", \n" +
                    " preload: \"[preload]\", \n" +
                    " custom_tracking_data: \"[custom_tracking_data]\"}; \n" +
                    " \n" +
                    " var mmAdTagSettings_auto = { \n" +
                    " adzoneId:\"571793a200000a\", \n" +
                    " templateId: \"video-fullscreen2.html\", \n" +
                    " mobile_web: false, \n" +
                    " video_supported: true, \n" +
                    " appId: \"57174ada000002\", \n" +
                    " bundleId: \"com.minimob.addemos\", \n" +
                    " placement: \"video fullscreen interstitial\"}; \n" +
                    " </script> \n" +
                    " <script id=\"sdk-loader\" onerror=\"if(typeof(mmji)!='undefined'){mmji.noAds()}\" type=\"text/javascript\" src=\"http://s.rtad.bid/assets/video-fullscreen-mmji.js\"></script>";

            //create the AdTag object
            AdTag adTag = new AdTag(getContext(), adTagString);
            //set the custom tracking data (optional)
            adTag.setCustomTrackingData("some tracking data");
            //request the AdZone
            MinimobAdController.getInstance().getVideo(_activity, adTag);
        }
        catch (Exception ex)
        {
            _activity.HandleCrash(TAG, ex);
        }
    }
    //endregion HELPERS
}
