package com.minimob.addemos.views;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.minimob.addemos.R;
import com.minimob.adserving.helpers.MinimobHelper;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity
{
	//region VARIABLES
    private boolean timerActive;
    private Timer longTimeout;

	private TextView splash_tvAppName;
	//endregion

	//region OVERRIDES
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try
		{
			super.onCreate(savedInstanceState);

            if (!isTaskRoot()) {
                final Intent intent = getIntent();
                final String intentAction = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                    finish();
                    return;
                }
            }

            setContentView(R.layout.activity_splash);
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            ShowMinimobActivityWithDelay(2000);

			// gets the gaid from google play services and keeps it in MinimobHelper.gaid
			//MinimobHelper.getInstance().getGAID(this);

			longTimeout = new Timer();
			longTimeout.schedule(new TimerTask() {
				@Override
				public void run() {
					ShowMinimobActivity(getIntent().getExtras());
				}
			}, 6000);
		}
		catch (Exception ex){
			HandleCrash(ex);
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		HandleNewIntent(getIntent());
		SetupViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
        timerActive = false;
		setIntent(intent);
	}
	//endregion

	//region HELPERS
	private void HandleNewIntent(Intent intent)
	{
		try
		{
			if (!timerActive)
			{
				ShowMinimobActivity(intent.getExtras());
                CancelLongTimer();
			}
		}
		catch (Exception ex)
		{
			HandleCrash(ex);
		}
	}

	private void SetupViews()
	{
		try
		{
            splash_tvAppName = (TextView) findViewById(R.id.splash_tvAppName);
			Typeface tf = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
			splash_tvAppName.setTypeface(tf);
            splash_tvAppName.setText(R.string.app_name);
		}
		catch (Exception ex)
		{
            HandleCrash(ex);
		}
	}

	public void ShowMinimobActivity(Bundle extras)
	{
		try
		{
			Intent startIntent = new Intent(this, MinimobDemoActivity.class);
			if (extras != null)
				startIntent.putExtras(extras);

			startIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(startIntent);
		}
		catch (Exception ex)
		{
            HandleCrash(ex);
		}
	}

    public void ShowMinimobActivityWithDelay(long delayInMs) {
        timerActive = true;
        new Timer().schedule(new TimerTask() {
            public void run() {
                timerActive = false;
				ShowMinimobActivity(getIntent().getExtras());
				CancelLongTimer();
            }
        }, delayInMs);
    }

	public void HandleCrash(Throwable ex)
	{
		ex.printStackTrace();
		Log.d("Splash: ", ex.getMessage(), ex);
		Crashlytics.logException(ex);
	}

    private void CancelLongTimer() {
        if (longTimeout != null) {
            longTimeout.cancel();
        }
    }
	//endregion
}
