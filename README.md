
<div id="D-adtag-integration">
<h2>Integrating VAST Video Ads using JavaScript Ad Tags</h2>
<p>The JavaScript Ad Tag is a static script for requesting and displaying an ad within an HTML-based page. For implementing this, it is required to insert the JavaScript Ad Tag into the html code of a mobile web application or mobile in-app web controller. </p>
Note that this method is intended for real time ad serving and presentation, between the Minimob ad servers and mobile devices.  It cannot be used for server-to-server batch retrieval of ads. For each ad impression, a separate request must be made to the ad servers, supplying all the required information plus any additional data, if available. By specifying values at the parameters included at the ad tag, an application can forward extra information towards the ad servers, which can be useful for delivering better targeted ads.
In case of preloading ads to an app, the lifetime of ads delivered should not exceed 5 minutes. If an application is open for periods longer than this duration, the app should refresh the presented ads at regular intervals well under that limit.

<h3>Prerequisites</h3>
Before you proceed, make sure that you already have:
<ol>
<li>Registered to Minimob </li>
<li>Created an app under <strong>Monetize > Video Ads</strong> </li>
<li>Created an ad zone under an app</li>
</ol>

Then, follow the instructions given in this guide for enabling your app to request and display video ads from Minimob.

<h3>Workflow overview</h3>
When developing your app, you need to carry out the following tasks:
<ol>
<li>Import the Minimob ad-serving module to your project</li>
<li>Use the imported module for requesting, loading and displaying video ads from Minimob </li>
</ol>

<h4>Importing the Minimob ad-serving module to your project</h4>
You can import the required Minimob ad-serving module either manually or automatically.
<h5>Manually</h5>
First, download the <a href="https://github.com/minimob/video-ad-serving#">Minimob ad-serving project</a> from Github and import the <strong>minimob-adserving</strong> module to your project.
Then, assuming you are using <strong>Gradle</strong>, go to the <strong>build.gradle</strong> script file of your app module and add the following line in the <strong>dependencies</strong> block:
<pre class="prettyprint linenums=5"><code>
compile project(':minimob-adserving')
 
</code></pre>

<h5>Automatically</h5>
If you are using <strong>Gradle</strong>, you can automatically import the module by 
<ul>
<li>At the <strong>build.gradle</strong> script file of your <em>app module</em>, add the following line in the <strong>dependencies</strong> block:</li>
</ul>
<pre class="prettyprint linenums=5"><code>
compile 'com.github.minimob:video-ad-serving:1.0.24'

</code></pre>

<ul>
<li>At the <strong>build.gradle</strong> script file of your <em>project</em>, add the following line in the <strong>repositories</strong> block:</li>
</ul>
<pre class="prettyprint linenums=5"><code>
 maven { url "https://jitpack.io" }

</code></pre>
For example:
<pre class="prettyprint linenums=5"><code>
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}

</code></pre>

<h4>Requesting, loading and displaying video ads from Minimob</h4>
Two distinct cases are distinguished:
<ul>
<li><strong>Video ad</strong>: a single call is used for loading and showing a video ad </li>
<li><strong>Preloaded video ad</strong>: two separate calls are used, one for loading a video ad, and another for showing the video ad</li>
</ul>

<h5>Video ad </h5>
At the point in your code where you want to show the video ad, you need to include the necessary lines. The following example will be used for providing instructions and clarifications on how to perform this task.
<pre class="prettyprint linenums=5"><code>
private void _setupAdZone()
{
    MinimobAdController.getInstance().setAdZoneCreatedListener(new IAdZoneCreatedListener()
    {
        @Override
        public void onAdZoneCreated(AdZone adZone)
        {
            adZoneVideo = (AdZoneVideo) adZone;
            if (adZoneVideo != null)
            {
                adZoneVideo.setAdsAvailableListener(new IAdsAvailableListener() {
                    @Override
                    public void onAdsAvailable(AdZone adZone) {
                        showProgress(false);
                    }
                });
                adZoneVideo.setAdsNotAvailableListener(new IAdsNotAvailableListener() {
                    @Override
                    public void onAdsNotAvailable(AdZone adZone) {
                        showProgress(false);
                    }
                });
                adZoneVideo.setVideoPlayingListener(new IVideoPlayingListener() {
                    @Override
                    public void onVideoPlaying(AdZone adZone) {
                        showProgress(false);
                    }
                });
                adZoneVideo.setVideoFinishedListener(new IVideoFinishedListener() {
                    @Override
                    public void onVideoFinished(AdZone adZone) {
                    }
                });
                adZoneVideo.setVideoClosedListener(new IVideoClosedListener() {
                    @Override
                    public void onVideoClosed(AdZone adZone) {
                    }
                });

                adZoneVideo.show();
            }
        }
    });

    // ADTAG
    String adTagString = "<script>…</script>";
    //create the AdTag object
    AdTag adTag = new AdTag(getContext(), adTagString);
    //set the custom tracking data (optional)
    adTag.setCustomTrackingData("some tracking data");
    //create the AdZone
    MinimobAdController.getInstance().getVideo(_activity, adTag);
}

</code></pre>

<ul>
<li>Use <strong>setAdZoneCreatedListener</strong> of <strong>MinimobAdController</strong> and override the <strong>onAdZoneCreated</strong> method. In this method the <strong>adZone</strong> is returned.
In the <strong>onAdZoneCreated</strong> method, you can optionally set listeners for events such as: <strong>ads available</strong>, <strong>ads NOT available, video playing</strong>, <strong>video finished</strong>, <strong>video closed</strong>. This enables you to customize the user experience according to the needs of your app. 
The <strong>adZoneVideo.show</strong> call loads and shows the video.</li>
</ul>
<pre class="prettyprint linenums=5"><code>
    MinimobAdController.getInstance().setAdZoneCreatedListener(new IAdZoneCreatedListener()
    {
        @Override
        public void onAdZoneCreated(AdZone adZone)
        {
            adZoneVideo = (AdZoneVideo) adZone;
            if (adZoneVideo != null)
            {
                adZoneVideo.setAdsAvailableListener(new IAdsAvailableListener() {
                    @Override
                    public void onAdsAvailable(AdZone adZone) {
                        showProgress(false);
                    }
                });
                adZoneVideo.setAdsNotAvailableListener(new IAdsNotAvailableListener() {
                    @Override
                    public void onAdsNotAvailable(AdZone adZone) {
                        showProgress(false);
                    }
                });
                adZoneVideo.setVideoPlayingListener(new IVideoPlayingListener() {
                    @Override
                    public void onVideoPlaying(AdZone adZone) {
                        showProgress(false);
                    }
                });
                adZoneVideo.setVideoFinishedListener(new IVideoFinishedListener() {
                    @Override
                    public void onVideoFinished(AdZone adZone) {
                    }
                });
                adZoneVideo.setVideoClosedListener(new IVideoClosedListener() {
                    @Override
                    public void onVideoClosed(AdZone adZone) {
                    }
                });

                adZoneVideo.show();
            }
        }
    });


</code></pre>

<ul>
<li>The following lines specify and perform the ad request. 
You need to copy the JavaScript Ad Tag that is given at the corresponding Ad Zone under <strong>Monetize > Video Ads</strong> of the Minimob dashboard, and paste it at the <strong>adTagString</strong>.
Besides specifying custom tracking data using the <strong>adTag.setCustomTrackingData</strong> method, you can specify <em>Age</em>, <em>Category</em> and <em>Gender</em> using the <strong>adTag.setAge</strong>, <strong>adTag.setCategory</strong> and <strong>adTag.setGender</strong> methods respectively.
</li>
</ul>
<pre class="prettyprint linenums=5"><code>
    // ADTAG
    String adTagString = "<script>…</script>";
    //create the AdTag object
    AdTag adTag = new AdTag(getContext(), adTagString);
    //set the custom tracking data (optional)
    adTag.setCustomTrackingData("some tracking data");
    //create the AdZone
    MinimobAdController.getInstance().getVideo(_activity, adTag);

</code></pre>

<h5>Preloaded video ad</h5>
At the point in your code where you want to preload the video ad, you need to include the necessary lines. The following example will be used for providing instructions and clarifications on how to perform this task.
<pre class="prettyprint linenums=5"><code>
private void _setupAdZone()
{
    MinimobAdController.getInstance().setAdZoneCreatedListener(new IAdZoneCreatedListener()
    {
        @Override
        public void onAdZoneCreated(AdZone adZone)
        {
            adZoneVideoPreloaded = (AdZoneVideoPreloaded) adZone;
            if (adZoneVideoPreloaded != null)
            {
                adZoneVideoPreloaded.setAdsAvailableListener(new IAdsAvailableListener()
                {
                    @Override
                    public void onAdsAvailable(AdZone adZone) {
                        showProgress(false);
                    }
                });
                adZoneVideoPreloaded.setAdsNotAvailableListener(new IAdsNotAvailableListener() {
                    @Override
                    public void onAdsNotAvailable(AdZone adZone) {
                        showProgress(false);
                    }
                });
                adZoneVideoPreloaded.setVideoLoadingListener(new IVideoLoadingListener() {
                    @Override
                    public void onVideoLoading(AdZone adZone) {
                        showProgress(true);
                    }
                });
                adZoneVideoPreloaded.setVideoLoadedListener(new IVideoLoadedListener() {
                    @Override
                    public void onVideoLoaded(AdZone adZone) {
                        showProgress(false);
                    }
                });
                adZoneVideoPreloaded.setVideoPlayingListener(new IVideoPlayingListener() {
                    @Override
                    public void onVideoPlaying(AdZone adZone) {
                        showProgress(false);
                        _setupAdZone();
                    }
                });
                adZoneVideoPreloaded.setVideoFinishedListener(new IVideoFinishedListener() {
                    @Override
                    public void onVideoFinished(AdZone adZone) {
                    }
                });
                adZoneVideoPreloaded.setVideoClosedListener(new IVideoClosedListener() {
                    @Override
                    public void onVideoClosed(AdZone adZone) {
                        _setupAdZone();
                    }
                });
                adZoneVideoPreloaded.load();
            }
        }
    });

    // ADTAG
    String adTagString = "<script>…</script>";
    //create the AdTag object
    AdTag adTag = new AdTag(getContext(), adTagString);
    //set the custom tracking data (optional)
    adTag.setCustomTrackingData("some tracking data");
    //create the AdZone
    MinimobAdController.getInstance().getVideoPreloaded(_activity, adTag);
}

</code></pre>

<ul>
<li>Use <strong>setAdZoneCreatedListener</strong> of <strong>MinimobAdController</strong> and override the <strong>onAdZoneCreated</strong> method. In this method the <strong>adZone</strong> is returned.
In the <strong>onAdZoneCreated</strong> method, you can optionally set listeners for events such as: <strong>ads available</strong>, <strong>ads NOT available, video loading</strong>, <strong>video loaded</strong>, <strong>video playing</strong>, <strong>video finished</strong>, <strong>video closed</strong>. This enables you to customize the user experience according to the needs of your app. In the example above, the <strong>_setupAdZone</strong> method calls itself within the overridden methods of the <strong>IVideoPlayingListener</strong> and <strong>IVideoClosedListener</strong> listeners, to achieve preloading of the next video ad in advance. 
The <strong>adZoneVideoPreloaded.load</strong> call only loads the video. A separate call must be used for showing the video, as described further below.</li>
</ul>
<pre class="prettyprint linenums=5"><code>
    MinimobAdController.getInstance().setAdZoneCreatedListener(new IAdZoneCreatedListener()
    {
        @Override
        public void onAdZoneCreated(AdZone adZone)
        {
            adZoneVideoPreloaded = (AdZoneVideoPreloaded) adZone;
            if (adZoneVideoPreloaded != null)
            {
                adZoneVideoPreloaded.setAdsAvailableListener(new IAdsAvailableListener()
                {
                    @Override
                    public void onAdsAvailable(AdZone adZone) {
                        showProgress(false);
                    }
                });
                adZoneVideoPreloaded.setAdsNotAvailableListener(new IAdsNotAvailableListener() {
                    @Override
                    public void onAdsNotAvailable(AdZone adZone) {
                        showProgress(false);
                    }
                });
                adZoneVideoPreloaded.setVideoLoadingListener(new IVideoLoadingListener() {
                    @Override
                    public void onVideoLoading(AdZone adZone) {
                        showProgress(true);
                    }
                });
                adZoneVideoPreloaded.setVideoLoadedListener(new IVideoLoadedListener() {
                    @Override
                    public void onVideoLoaded(AdZone adZone) {
                        showProgress(false);
                    }
                });
                adZoneVideoPreloaded.setVideoPlayingListener(new IVideoPlayingListener() {
                    @Override
                    public void onVideoPlaying(AdZone adZone) {
                        showProgress(false);
                        _setupAdZone();
                    }
                });
                adZoneVideoPreloaded.setVideoFinishedListener(new IVideoFinishedListener() {
                    @Override
                    public void onVideoFinished(AdZone adZone) {
                    }
                });
                adZoneVideoPreloaded.setVideoClosedListener(new IVideoClosedListener() {
                    @Override
                    public void onVideoClosed(AdZone adZone) {
                        _setupAdZone();
                    }
                });
                adZoneVideoPreloaded.load();
            }
        }
    });

</code></pre>

<ul>
<li>The following lines specify and perform the ad request. 
You need to copy the JavaScript Ad Tag that is given at the corresponding Ad Zone under <strong>Monetize > Video Ads</strong> of the Minimob dashboard, and paste it at the <strong>adTagString</strong>. 
Besides specifying custom tracking data using the <strong>adTag.setCustomTrackingData</strong> method, you can specify <em>Age</em>, <em>Category</em> and <em>Gender</em> using the <strong>adTag.setAge</strong>, <strong>adTag.setCategory</strong> and <strong>adTag.setGender</strong> methods respectively.</li>
</ul>
<pre class="prettyprint linenums=5"><code>
    // ADTAG
    String adTagString = "<script>…</script>";
    //create the AdTag object
    AdTag adTag = new AdTag(getContext(), adTagString);
    //set the custom tracking data (optional)
    adTag.setCustomTrackingData("some tracking data");
    //create the AdZone
    MinimobAdController.getInstance().getVideo(_activity, adTag);

</code></pre>

<ul>
<li>At the point in your code where you want to show the video ad, you need to call the <strong>adZoneVideoPreloaded.show</strong> method. 
The <strong>adZoneVideoPreloaded.show</strong> method shows the video. For example, assuming that you want to show the preloaded video when the user clicks the <strong>video_btnFullscreen_play</strong> button:</li>
</ul>
<pre class="prettyprint linenums=5"><code>
video_btnFullscreen_play = (Button) _activity.findViewById(R.id.video_btnFullscreen_play_preloaded);
video_btnFullscreen_play.setOnClickListener(new View.OnClickListener()
{
    @Override
    public void onClick(View view)
    {
        if (adZoneVideoPreloaded != null) {
            adZoneVideoPreloaded.show();
        }
    }
});

</code></pre>

<h3>Ad Tag parameters</h3>
The JavaScript Ad Tag of an ad zone holds the following variables:
<ul>
<li><strong>mmAdTagSettings</strong>: this variable contains parameters that are optional. They are used to pass extra information, such as device or user information, which can then be utilized by Minimob in order to return more relevant ads.</li>
<li><strong>mmAdTagSettings_auto</strong>: this variable contains parameters that are mandatory. These parameters are automatically generated by Minimob and <strong>you should NOT modify them</strong>.</li>
</ul>

<h5>mmAdTagSettings Parameters</h5>
 
<table>
  <tr>
    <th>Parameter</th>
    <th>Type</th>
    <th>Description</th>
    <th>Value placeholder</th>
    <th>Example of actual value</th>
  </tr>
  <tr>
    <td>imei</td>
    <td>String</td>
    <td>The International Mobile Station Equipment Identity number that uniquely identifies mobile phones
Hashed MD5 or SHA1</td>
    <td>"[imei]"</td>
    <td>imei: " d41d8cd98f00b204e9800998ecf8427e"</td>
  </tr>
  <tr>
    <td>android_id</td>
    <td>String</td>
    <td>[android only]
The Android ID of the device
Hashed MD5 or SHA1</td>
    <td>"[android_id]"</td>
    <td>android_id: " d3b4f06fc2bd14b417f39f7d7e72f47f"</td>
  </tr>
  <tr>
    <td>gaid</td>
    <td>String </td>
    <td>[android only]
Raw (clear text) Google Advertising ID</td>
    <td>"[gaid]"</td>
    <td>gaid: "3D016490-C470-4B04-99AD-B4FFF3330D46"</td>
  </tr>
  <tr>
    <td>idfa</td>
    <td>String </td>
    <td>[iOS only]
Raw (clear text) Apple’s Identifier For Advertisers (IFA/IDFA)</td>
    <td>"[idfa]"</td>
    <td>idfa: "236A005B-700F-4889-B9CE-999EAB2B605D"</td>
  </tr>
  <tr>
    <td>idfv</td>
    <td>String </td>
    <td>[iOS only]
Raw (clear text) Apple’s Identifier For Vendor (IFA/IDFA)</td>
    <td>"[idfv]"</td>
    <td>idfv: "599F9C00-92DC-4B5C-9464-7971F01F8370"</td>
  </tr>
  <tr>
    <td>category</td>
    <td>Integer
comma separated multiple values are allowed</td>
    <td>The category ID of advertised apps, as defined by <a href="http://www.iab.com/wp-content/uploads/2016/03/OpenRTB-API-Specification-Version-2-4-FINAL.pdf#">IAB</a>.
Category is used for filtering the ads. Several comma separated values can be specified to filter for multiple categories. Only ads from apps that belong to the specified categories will be served. </td>
    <td>"[category]"</td>
    <td>category: "IAB1-1,IAB2"</td>
  </tr>
  <tr>
    <td>age</td>
    <td>Integer</td>
    <td>The age of the device user
If omitted (i.e. null), it implies "unknown".</td>
    <td>"[age]"</td>
    <td>age: "37"</td>
  </tr>
  <tr>
    <td>gender</td>
    <td>String
M = male, f = female, o = known to be other</td>
    <td>The gender of the device user 
If omitted (i.e. null), it implies "unknown". </td>
    <td>"[gender]"</td>
    <td>gender: "F"</td>
  </tr>
  <tr>
    <td>keywords</td>
    <td>String
comma separated multiple values are allowed</td>
    <td>List of keywords to filter ads by (e.g. list of interests of the device user)</td>
    <td>"[keywords]"</td>
    <td>keywords: "fashion,sailing"</td>
  </tr>
  <tr>
    <td>lat</td>
    <td>Float 
from -90.0 to +90.0, where negative is south</td>
    <td>Latitude of the mobile device
Should be used together with the <strong>lon</strong> parameter.</td>
    <td>"[lat]"</td>
    <td>lat: "+10.2"</td>
  </tr>
  <tr>
    <td>lon</td>
    <td>Float 
from -180.0 to +180.0, where negative is west</td>
    <td>Longitude of the mobile device 
Should be used together with the <strong>lat</strong> parameter.</td>
    <td>"[lon]"</td>
    <td>lon: "-102.8"</td>
  </tr>
  <tr>
    <td>device_width</td>
    <td>Integer</td>
    <td>The width of the mobile device, in physical pixels</td>
    <td>"[device_width]"</td>
    <td>device_width: "1440"
 
</td>
  </tr>
  <tr>
    <td>device_height</td>
    <td>Integer</td>
    <td>The height of the mobile device, in physical pixels</td>
    <td>"[device_height]"</td>
    <td>device_height: "2560"
 
</td>
  </tr>
  <tr>
    <td>mnc</td>
    <td>Integer</td>
    <td>The mobile network code of the mobile network operator of the mobile device
It is used in combination with the mobile country code (MCC) to uniquely identify the mobile phone operator/carrier.</td>
    <td>"[mnc]"</td>
    <td>mnc: "260"
 
</td>
  </tr>
  <tr>
    <td>mcc</td>
    <td>Integer</td>
    <td>The mobile country code of the mobile network operator of the mobile device
It is used in combination with the mobile network code (MNC) to uniquely identify the mobile phone operator/carrier.</td>
    <td>"[mcc]"</td>
    <td>mcc: " 310"
 
</td>
  </tr>
  <tr>
    <td>wifi</td>
    <td>Boolean</td>
    <td>The network connection type of the mobile device 
true: indicates that the device is currently connected to the internet via WiFi
false: indicates that the current network connection of the device is NOT via WiFi </td>
    <td>"[wifi]", </td>
    <td>wifi: "true"
 
</td>
  </tr>
  <tr>
    <td>ios_version</td>
    <td>String</td>
    <td>[iOS only]
The iOS version of the operating system of the device </td>
    <td>"[ios_version]"</td>
    <td>ios_version: "7.1.2"
 
</td>
  </tr>
  <tr>
    <td>android_version</td>
    <td>String</td>
    <td>[android only]
The android version of the operating system of the device</td>
    <td>"[android_version]"</td>
    <td>android_version: "4.4.2"
 
</td>
  </tr>
  <tr>
    <td>placement_width</td>
    <td>Integer</td>
    <td>The width of the advertising area within the app, where the ads are placed, in pixels</td>
    <td>"[placement_width]"</td>
    <td>placement_width: "720"
 
</td>
  </tr>
  <tr>
    <td>placement_height</td>
    <td>Integer</td>
    <td>The height of the advertising area within the app, where the ads are placed, in pixels</td>
    <td>"[placement_height]"</td>
    <td>placement_height: "1280"
 </td>
  </tr>
  <tr>
    <td>preload</td>
    <td>Boolean</td>
    <td>true: indicates that the requested video ad will be preloaded
false: indicates that the requested video ad will NOT be preloaded</td>
    <td>preload: "[preload]"</td>
    <td>preload: "true"</td>
  </tr>
  <tr>
    <td>custom_tracking_data</td>
    <td>String</td>
    <td>Custom information, defined by the app developer </td>
    <td>"[custom_tracking_data]" </td>
    <td>custom_tracking_data: "00000000-5a19-dc1b-ffff-ffffef05ac4a"</td>
  </tr>
</table>
 

<h5>mmAdTagSettings_auto Parameters</h5>
 
<table>
  <tr>
    <th>Parameter</th>
    <th>Type</th>
    <th>Description</th>
    <th>Example</th>
  </tr>
  <tr>
    <td> adzoneId</td>
    <td>string</td>
    <td>The unique id of the particular ad zone</td>
    <td>"56f1727aFAKEc3"</td>
  </tr>
  <tr>
    <td> templateId</td>
    <td>string</td>
    <td>The id of the template of the ad zone, i.e. the html code that will be used for rendering the ads.</td>
    <td> "video-fullscreen2.html"</td>
  </tr>
  <tr>
    <td> mobile_web</td>
    <td>string</td>
    <td>true: for web app supported ads 
false: [default] for in-app supported ads </td>
    <td> false</td>
  </tr>
  <tr>
    <td> video_supported</td>
    <td>string</td>
    <td>true: [default] for allowing video media supported ads 
false: for blocking video media supported ads</td>
    <td> true</td>
  </tr>
  <tr>
    <td> appId</td>
    <td>string</td>
    <td>The unique id of the app to which the particular ad zone belongs</td>
    <td> "56d57201FAKE2e"</td>
  </tr>
  <tr>
    <td> bundleId</td>
    <td>string</td>
    <td>The bundle id of the app, as it is registered to the app store</td>
    <td> "com.minimob.addemos "</td>
  </tr>
  <tr>
    <td> placement</td>
    <td>string</td>
    <td>The placement type of the ad</td>
    <td> "video fullscreen interstitial" </td>
  </tr>
</table>

<h3>Examples</h3>
Here is an example of an Ad Tag, as displayed at Minimob’s Monetization dashboard for Video Ads when viewing the details of an Ad Zone that has been created.
<pre class="prettyprint linenums=5"><code>
&ltscript>
    var mmAdTagSettings = {
        imei: "[imei]",
        android_id: "[android_id]",
        gaid: "[gaid]",
        idfa: "[idfa]",
        idfv: "[idfv]",
        category: "[category]",
        age: "[age]",
        gender: "[gender]",
        keywords: "[keywords]",
        lat: "[lat]",
        lon: "[lon]",
        device_width: "[device_width]",
        device_height: "[device_height]",
        mnc: "[mnc]",
        mcc: "[mcc]",
        wifi: "[wifi]",
        ios_version: "[ios_version]",
        android_version: "[android_version]",
        placement_width: "[placement_width]",
        placement_height: "[placement_height]",
        custom_tracking_data: "[custom_tracking_data]"
    };

    var mmAdTagSettings_auto = {
        adzoneId: "56f1727aFAKEc3",
        templateId: "video-fullscreen2.html",
        mobile_web: false,
        video_supported: true,
        appId: "56d57201FAKE2e",
        bundleId: "com.minimob.addemos",
        placement: "video fullscreen interstitial"
    };
&lt/script>
&ltscript id="sdk-loader" onerror="if(typeof(mmji)!='undefined'){mmji.noAds()}" type="text/javascript" src="http://s-dev.rtad.bid/assets/video-fullscreen-mmji.js">&lt/script>

</code></pre>

Here is an example of generated html code.
<pre class="prettyprint linenums=5"><code>
&lthtml>
&lthead>
&ltmeta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
&lt/head>
&ltbody style="background-color:#000000">
&ltscript> 
 var mmAdTagSettings = { 
 imei: "d41d8cd98f00b204e9800998ecf8427e", 
 android_id: "d3b4f06fc2bd14b417f39f7d7e72f47f", 
 gaid: "af13cb72-fafa-4328-b86c-d48875a4561a", 
 idfa: "[idfa]", 
 idfv: "[idfv]", 
 category: "[category]", 
 age: "[age]", 
 gender: "[gender]", 
 keywords: "[keywords]", 
 lat: "0.0", 
 lon: "0.0", 
 device_width: "1080", 
 device_height: "1794", 
 mnc: "260", 
 mcc: "310", 
 wifi: "false", 
 ios_version: "[ios_version]", 
 android_version: "23", 
 placement_width: "1080", 
 placement_height: "1794", 
 preload: "false", 
 custom_tracking_data: "[custom_tracking_data]"}; 
 
 var mmAdTagSettings_auto = { 
 adzoneId:"571793a200000a", 
 templateId: "video-fullscreen2.html", 
 mobile_web: false, 
 video_supported: true, 
 appId: "57174ada000002", 
 bundleId: "com.minimob.addemos", 
 placement: "video fullscreen interstitial"}; 
 &lt/script> 
 &ltscript id="sdk-loader" onerror="if(typeof(mmji)!='undefined'){mmji.noAds()}" type="text/javascript" src="http://s.rtad.bid/assets/video-fullscreen-mmji.js">&lt/script>&lt/body>
&lt/html>
</code></pre></div>


<h2>Reference Implementation</h2>
You can find a reference implementation on GitHub:
<a href="https://github.com/minimob/video-ad-demo#">minimob/video-ad-demo</a>

