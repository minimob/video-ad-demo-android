package com.minimob.addemos.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ProgressBar;

import com.minimob.adserving.helpers.MinimobHelper;

/**
 * Created by v.prantzos on 3/2/2016.
 */
public class MinimobBaseFragment extends Fragment
{
    //region VARIABLES
    private String TAG = MinimobBaseFragment.class.getSimpleName();
    public MinimobBaseActivity _activity;
    int titleRes;
    ProgressBar abProgress;
    //endregion VARIABLES

    //region CONSTRUCTORS
    public MinimobBaseFragment()
    {

    }
    //endregion CONSTRUCTORS

    //region OVERRIDES
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        _activity = (MinimobBaseActivity) getActivity();
    }
    //endregion OVERRIDES

    //region Public Methods
    protected void showProgress(final boolean show)
    {
        try
        {
            if (show)
            {
                this.showActionBarProgress(abProgress, _activity.getApplicationContext());
            }
            else
            {
                this.hideActionBarProgress(abProgress, _activity.getApplicationContext());
            }
        }
        catch (Exception ex)
        {
            _activity.HandleCrash(TAG, ex);
        }
    }

    private void showActionBarProgress(ProgressBar abProgress, Context ctx) {
        if (abProgress != null && ctx != null) {
            MinimobHelper.getInstance().animFadeInView(ctx, abProgress);
        }
    }

    private void hideActionBarProgress(ProgressBar abProgress, Context ctx) {
        if (abProgress != null && ctx != null) {
            MinimobHelper.getInstance().animFadeOutView(ctx, abProgress);
        }
    }
    //endregion Public Methods
}
