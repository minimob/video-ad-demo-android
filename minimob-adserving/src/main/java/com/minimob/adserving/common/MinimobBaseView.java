package com.minimob.adserving.common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by v.prantzos on 15/3/2016.
 */
public class MinimobBaseView extends RelativeLayout
{
    public MinimobBaseView(Context context)
    {
        this(context, null);
    }

    public MinimobBaseView(Context context, AttributeSet st)
    {
        this(context, st, 0);
    }

    public MinimobBaseView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
}
