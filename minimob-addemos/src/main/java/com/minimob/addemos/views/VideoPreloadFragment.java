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
import com.minimob.adserving.adzones.AdZoneVideoPreloaded;
import com.minimob.adserving.controllers.MinimobAdController;
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
        //set the title to the ActionBar
        _activity.setTitle(this.titleRes);
        //a progress bar to show on loading times
        abProgress = (ProgressBar)_activity.findViewById(R.id.actionbar_progress);
        //hide the progress bar
       this.showProgress(false);

        try
        {
            //setup an AdZone and load it
            this._setupAdZone();
        }
        catch (Exception ex)
        {
            _activity.HandleCrash(TAG, ex);
        }

        //button to load and show the video ad
        video_btnFullscreen_play = (Button) _activity.findViewById(R.id.video_btnFullscreen_play_preloaded);
        //when the user clicks on the button
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
            //set the listener that gets called when the AdZone is created by MinimobAdController
            MinimobAdController.getInstance().setAdZoneCreatedListener(new IAdZoneCreatedListener()
            {
                @Override
                public void onAdZoneCreated(AdZone adZone)
                {
                    //cast the returned AdZone to the AdZoneVideoPreloaded type
                    adZoneVideoPreloaded = (AdZoneVideoPreloaded) adZone;
                    if (adZoneVideoPreloaded != null)
                    {
                        //set a listener for when the ad server returns the event that there are ads available
                        adZoneVideoPreloaded.setAdsAvailableListener(new IAdsAvailableListener()
                        {
                            @Override
                            public void onAdsAvailable(AdZone adZone) {
                                //hide the progress bar
                                showProgress(false);
//                                MinimobHelper.getInstance().showToast(_activity, "ads available " + adZone.timeCreated, Toast.LENGTH_SHORT);
                            }
                        });
                        //set a listener for when the ad server returns the event that there are NO ads available
                        adZoneVideoPreloaded.setAdsNotAvailableListener(new IAdsNotAvailableListener() {
                            @Override
                            public void onAdsNotAvailable(AdZone adZone) {
                                //hide the progress bar
                                showProgress(false);
//                                MinimobHelper.getInstance().showToast(_activity, "ads NOT available " + adZone.timeCreated, Toast.LENGTH_SHORT);
                            }
                        });
                        //set a listener for when the video started loading
                        adZoneVideoPreloaded.setVideoLoadingListener(new IVideoLoadingListener() {
                            @Override
                            public void onVideoLoading(AdZone adZone) {
                                //show the progress bar
                                showProgress(true);
                                MinimobHelper.getInstance().showToast(_activity, "video loading " + adZone.timeCreated, Toast.LENGTH_SHORT);
                            }
                        });
                        //set a listener for when the video finished loading
                        adZoneVideoPreloaded.setVideoLoadedListener(new IVideoLoadedListener() {
                            @Override
                            public void onVideoLoaded(AdZone adZone) {
                                //hide the progress bar
                                showProgress(false);
                                MinimobHelper.getInstance().showToast(_activity, "video loaded " + adZone.timeCreated, Toast.LENGTH_SHORT);
                            }
                        });
                        //set a listener for when the video started playing
                        adZoneVideoPreloaded.setVideoPlayingListener(new IVideoPlayingListener() {
                            @Override
                            public void onVideoPlaying(AdZone adZone) {
                                //hide the progress bar
                                showProgress(false);
                                //MinimobHelper.getInstance().showToast(_activity, "video playing " + adZone.timeCreated, Toast.LENGTH_SHORT);

                                //setup a new AdZone (to be ready and loading while the video is playing)
                                _setupAdZone();
                            }
                        });
                        //set a listener for when the video finished playing
                        adZoneVideoPreloaded.setVideoFinishedListener(new IVideoFinishedListener() {
                            @Override
                            public void onVideoFinished(AdZone adZone) {
                                //MinimobHelper.getInstance().showToast(_activity, "video finished " + adZone.timeCreated, Toast.LENGTH_SHORT);
                            }
                        });
                        //set a listener for when the video was closed by the user
                        adZoneVideoPreloaded.setVideoClosedListener(new IVideoClosedListener() {
                            @Override
                            public void onVideoClosed(AdZone adZone) {
                                //MinimobHelper.getInstance().showToast(_activity, "video closed " + adZone.timeCreated, Toast.LENGTH_SHORT);

                                _setupAdZone();
                            }
                        });

                        //load the video
                        adZoneVideoPreloaded.load();
                    }
                }
            });

            // The ad tag (copied from the ad zone in minimob dashboard) is pasted as a string here
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
            MinimobAdController.getInstance().getVideoPreloaded(_activity, adTag);
        }
        catch (Exception ex)
        {
            _activity.HandleCrash(TAG, ex);
        }
    }
    //endregion HELPERS
}
