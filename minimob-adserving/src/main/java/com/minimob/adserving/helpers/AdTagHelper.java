package com.minimob.adserving.helpers;

import java.util.Date;

/**
 * Created by v.prantzos on 21/4/2016.
 */
public class AdTagHelper
{
    //region VARIABLES
    private String TAG = "MINIMOB-" + AdTagHelper.class.getSimpleName();

    private static final AdTagHelper _instance = new AdTagHelper();

    private String baseUrl_Local = "http://172.30.6.171:3000";
    private String baseUrl_Cloud = "http://s.rtad.bid/";
    public String baseUrl = baseUrl_Cloud;
    private String assetsUrl = baseUrl + "assets";

    private String adZoneId = "571793a200000a"; //5714eb100000ed,571793a200000a
    private String appId = "57174ada000002"; //56e047b7000050,57174ada000002
    private String bundleId = "com.minimob.addemos"; //com.bestcoolfungames.swingjetpack,com.minimob.addemos
    private String templateId_Cloud = "video_interstitial_" + adZoneId;
    private String templateId_Local = "video-fullscreen2.html";
    private String templateId = templateId_Local;
    private boolean dataLocal = false;

    private String _cacheBustingString;
    //endregion VARIABLES

    //region CONSTRUCTORS
    private AdTagHelper(){
        this._cacheBustingString = MinimobHelper.getInstance().getDateTimeString(new Date(), true);
    }

    public static AdTagHelper getInstance()
    {
        return _instance;
    }
    //endregion CONSTRUCTORS

    //region METHODS
    public String getMinimobScript(boolean isVideo)
    {
        String baseJS = isVideo ? "video-fullscreen-mmji.js" : "minimob.js";
        // cache busting
        baseJS += "?" + this._cacheBustingString;
        String placement = "video fullscreen interstitial";

        String dev_settings =
                dataLocal ?
                "\t\tvar dev_settings = {\n" +
                "\t\t\tdataUrl:\"http://172.30.6.171:3000/adserver/servep/\",\n" +
                "\t\t\ttemplateUrl:\"http://172.30.6.171:3000/assets/\",\n" +
                "\t\t\tx_debug_ip:\"193.41.229.7\"\n" +
                "\t\t};\n"
                :
                ""
                ;

        String minimobScript = "<script>\n" +
                "\t\tvar mmAdTagSettings = {\n" +
                "\t\t\timei: \"[imei]\",\n" +
                "\t\t\tandroid_id: \"[android_id]\",\n" +
                "\t\t\tgaid: \"[gaid]\",\n" +
                "\t\t\tidfa: \"[idfa]\",\n" +
                "\t\t\tidfv: \"[idfv]\",\n" +
                "\t\t\tcategory: \"[category]\",\n" +
                "\t\t\tage: \"[age]\",\n" +
                "\t\t\tgender: \"[gender]\",\n" +
                "\t\t\tkeywords: \"[keywords]\",\n" +
                "\t\t\tlat: \"[lat]\",\n" +
                "\t\t\tlon: \"[lon]\",\n" +
                "\t\t\tdevice_width: \"[device_width]\",\n" +
                "\t\t\tdevice_height: \"[device_height]\",\n" +
                "\t\t\tmnc: \"[mnc]\",\n" +
                "\t\t\tmcc: \"[mcc]\",\n" +
                "\t\t\twifi: \"[wifi]\",\n" +
                "\t\t\tandroid_version: \"[android_version]\",\n" +
                "\t\t\tplacement_width: \"[placement_width]\",\n" +
                "\t\t\tplacement_height: \"[placement_height]\",\n" +
                "\t\t\tpreload: \"[preload]\",\n" +
                "\t\t\tcustom_tracking_data: \"[customTrackingData]\"\n" +
                "\t\t};\n" +
                "\n" +
                "\t\tvar mmAdTagSettings_auto = {\n" +
                "\t\t\tadzoneId: \"" + adZoneId + "\",\n" +
                "\t\t\ttemplateId: \"" + templateId + "\",\n" +
                "\t\t\tmobile_web: false,\n" +
                "\t\t\tmraid_supported: false,\n" +
                "\t\t\tvideo_supported: true,\n" +
                "\t\t\tappId: \"" + appId + "\",\n" +
                "\t\t\tbundleId: \"" + bundleId + "\",\n" +
                "\t\t\tplacement: \"" + placement + "\"\n" +
                "\t\t};\n" +
                "\n" +
                dev_settings +
                "\t</script>\n" +
                "\t<script id=\"sdk-loader\" onerror=\"mmji.noAds()\" type=\"text/javascript\" src=\"" + assetsUrl + "/"+ baseJS + "\"></script>\n";

        return minimobScript;
    }
    //endregion METHODS
}
