package com.minimob.adserving.views;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.minimob.adserving.R;
import com.minimob.adserving.controllers.MinimobAdController;
import com.minimob.adserving.helpers.MinimobHelper;

public class VideoActivity extends MinimobBaseActivity
{
    // region VARIABLES
    MinimobView _minimobView;
    //endregion VARIABLES

    //region METHODS

    //region OVERRIDES
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        ShowInterstitial();
    }

    @Override
    public void onBackPressed()
    {
        // do nothing
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        _minimobView.destroy();
        _minimobView = null;
    }

    //endregion OVERRIDES

    //region Public Methods
    public void ShowInterstitial()
    {
        try
        {
            _minimobView = MinimobAdController.getInstance().getAdZone().getMinimobView();

            if (_minimobView != null)
            {
                MinimobAdController.getInstance().updateActivity(this);
                _minimobView.setVisibility(View.VISIBLE);
                _minimobView.showAsInterstitial();
            }
        }
        catch (Exception ex)
        {
            MinimobHelper.getInstance().handleCrash(TAG, ex);
        }
    }
    //endregion Public Methods

    //endregion METHODS
}
