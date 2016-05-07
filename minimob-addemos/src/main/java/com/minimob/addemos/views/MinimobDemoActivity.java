package com.minimob.addemos.views;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.minimob.addemos.R;
import com.minimob.adserving.helpers.MinimobHelper;
import com.minimob.adserving.helpers.MinimobViewLog;


/**
 * Created by v.prantzos on 18/1/2016.
 */
public class MinimobDemoActivity extends MinimobBaseActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private String TAG_TITLE = "TAG_TITLE";
    protected ProgressBar abProgress;

    // region OVERRIDES
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            setContentView(R.layout.activity_ad_demos);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            abProgress = (ProgressBar) findViewById(R.id.actionbar_progress);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            if (drawer != null)
            {
                drawer.setDrawerListener(toggle);
            }
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            if (navigationView != null)
            {
                navigationView.setNavigationItemSelectedListener(this);
            }

            MinimobHelper.getInstance().showFragment(this, R.id.container, new BlankAdFragment());

            // set minimobjsinterface log level
            MinimobViewLog.setLoggingLevel(MinimobViewLog.LOG_LEVEL.info);
        }
        catch (Exception ex)
        {
            HandleCrash(TAG, ex);
        }
    }

    @Override
    public void onBackPressed()
    {
        try
        {
            ToggleDrawer();
        }
        catch (Exception ex)
        {
            HandleCrash(TAG, ex);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.native_ad_demos, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        ImageView minimobLogo = (ImageView) this.findViewById(R.id.minimobLogo);
        if (minimobLogo != null)
        {
            minimobLogo.setVisibility(View.GONE);
        }

        if (id == R.id.videos_fullscreen)
        {
            VideoFragment adFragment = new VideoFragment();
            MinimobHelper.getInstance().showFragment(this, R.id.container, adFragment);
        }
        else if (id == R.id.videos_fullscreen_preloaded)
        {
            VideoPreloadFragment adFragment = new VideoPreloadFragment();
            MinimobHelper.getInstance().showFragment(this, R.id.container, adFragment);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null)
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putString(TAG_TITLE, getTitle().toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        String title = savedInstanceState.getString(TAG_TITLE);
        this.setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }
    // endregion OVERRIDES

//    public void minimobWebViewLoaded(final MinimobWebView minimobWebView)
//    {
//        MinimobHelper.logMessage(TAG + "-" + MinimobDemoActivity.class.getSimpleName(), "minimobWebViewLoaded");
//        MinimobHelper.ShowProgress(abProgress, this, false);
//        MinimobHelper.showToast(this, minimobWebView.getAdStatus().asString(), Toast.LENGTH_SHORT);
//
//        // if ads are ready then make it visible
//        if (minimobWebView.getAdStatus() == MinimobHelper.AdStatus.ADS_AVAILABLE)
//        {
//            MinimobHelper.toggleWebViewVisibility(minimobWebView, true);
//        }
//        else if (minimobWebView.getAdStatus() == MinimobHelper.AdStatus.ADS_NOT_AVAILABLE)
//        {
//            MinimobHelper.toggleWebViewVisibility(minimobWebView, false);
//            minimobWebView.destroy();
//        }
//        else if (minimobWebView.getAdStatus() == MinimobHelper.AdStatus.AD_STATUS_UNKNOWN)
//        {
//            MinimobHelper.toggleWebViewVisibility(minimobWebView, false);
//        }
//    }
}
