package com.minimob.addemos.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

/**
 * Created by v.prantzos on 3/2/2016.
 */
public class MinimobBaseFragment extends Fragment
{
    //region VARIABLES
    public MinimobBaseActivity _activity;
    int titleRes;
    ProgressBar abProgress;
    FrameLayout _loadingView;
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
}
