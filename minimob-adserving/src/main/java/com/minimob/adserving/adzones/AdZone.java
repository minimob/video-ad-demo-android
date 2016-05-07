package com.minimob.adserving.adzones;

import android.app.Activity;

import com.minimob.adserving.helpers.AdZoneType;
import com.minimob.adserving.helpers.MinimobHelper;
import com.minimob.adserving.interfaces.IAdsAvailableListener;
import com.minimob.adserving.interfaces.IAdsNotAvailableListener;
import com.minimob.adserving.views.MinimobView;

import java.util.Date;

/**
 * Created by e.petratos on 11/4/2016.
 */
public class AdZone
{
    protected Activity _activity;
    protected MinimobView _minimobView;
    public String packageId;

    public AdZoneType type;
    public String adTag;
    public int originalOrientation;
    public String timeCreated;

    //region Listeners
    protected IAdsAvailableListener _adsAvailableListener;
    protected IAdsNotAvailableListener _adsNotAvailableListener;
    //endregion Listeners

    //region CONSTRUCTORS
    public AdZone(String adTag){
        this.adTag = adTag;
        this.timeCreated = MinimobHelper.getInstance().getTimeString(new Date(), false);
    }
    //endregion CONSTRUCTORS

    //region METHODS
    public Integer Id()
    {
        return getAdZoneId(adTag);
    }

    public static int getAdZoneId(String tag)
    {
        return tag.hashCode();
    }

    public void updateActivity(Activity activity)
    {
        this._activity = activity;

        // update the activity in MinimobView
        this._minimobView.updateActivity(activity);
    }

    public boolean checkIfActivitiesMatch(Activity activity)
    {
        return this._activity == activity;
    }

    public MinimobView getMinimobView() {
        return this._minimobView;
    }
    //endregion METHODS

    //region Listener setters
    public void setAdsAvailableListener(IAdsAvailableListener listener) {
        this._adsAvailableListener = listener;
    }

    public void setAdsNotAvailableListener(IAdsNotAvailableListener listener) {
        this._adsNotAvailableListener = listener;
    }
    //endregion Listener setters
}
