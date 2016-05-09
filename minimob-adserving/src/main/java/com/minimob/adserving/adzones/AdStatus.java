package com.minimob.adserving.adzones;

import java.util.EnumSet;

/**
 * Created by v.prantzos on 9/5/2016.
 */
public enum AdStatus
{
    AD_STATUS_UNKNOWN(0),
    ADS_AVAILABLE(1),
    ADS_NOT_AVAILABLE(2);

    private int value;

    AdStatus(int value)
    {
        this.value = value;
    }

    public AdStatus getEnumValue(int ordinal)
    {
        for (AdStatus enumVal : EnumSet.allOf(AdStatus.class)) {
            if (enumVal.value == ordinal)
                return enumVal;
        }
        return null;
    }

    public String asString()
    {
        switch (this)
        {
            case ADS_NOT_AVAILABLE: return "ads NOT available";
            case AD_STATUS_UNKNOWN: return "ads status unknown";
            case ADS_AVAILABLE: return "ads available";
        }
        return this.toString();
    }

    public int getValue()
    {
        return this.value;
    }
}
