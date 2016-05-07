package com.minimob.addemos.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.minimob.addemos.R;

/**
 * Created by v.prantzos on 15/1/2016.
 */
public class BlankAdFragment extends MinimobBaseFragment
{
    //region VARIABLES
    private static String TAG = "MINIMOB-" + BlankAdFragment.class.getSimpleName();
    //endregion

    //region constructors
    public BlankAdFragment()
    {
    }
    //endregion

    //region OVERRIDES
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume()
    {
        this.titleRes = R.string.nav_header_title;
        super.onResume();
        abProgress = (ProgressBar)getActivity().findViewById(R.id.actionbar_progress);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.content_ad_demos, container, false);
        return rootView;
    }
    //endregion OVERRIDES
}
