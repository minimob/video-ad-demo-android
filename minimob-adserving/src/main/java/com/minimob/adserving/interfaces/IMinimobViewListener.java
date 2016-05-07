package com.minimob.adserving.interfaces;

import com.minimob.adserving.common.MinimobBaseView;

/**
 * Created by v.prantzos on 19/4/2016.
 */
public interface IMinimobViewListener
{
    void onAdClicked(String url);
    void onAdsAvailable(MinimobBaseView minimobBaseView, String packageId);
    void onAdsNotAvailable(MinimobBaseView minimobBaseView);
    void onVideoFinished(MinimobBaseView minimobBaseView);
    void onMinimobViewClosed(MinimobBaseView minimobBaseView);
}
