package com.minimob.adserving.helpers;

import java.util.EnumSet;

/**
 * Created by e.petratos on 11/4/2016.
 */
public enum AdZoneType
{
    Video(0),
    VideoPreloaded(1);

    private int value;

    AdZoneType(int value)
    {
        this.value = value;
    }

    public static AdZoneType getEnumValue(int ordinal)
    {
        for (AdZoneType enumVal : EnumSet.allOf(AdZoneType.class)) {
            if (enumVal.value == ordinal)
                return enumVal;
        }
        return null;
    }

    public int getValue()
    {
        return this.value;
    }
}
