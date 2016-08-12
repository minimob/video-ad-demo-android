<div id="D-Entire-document">
<div id="D-adtag-refimp">
<h2>A Reference Implementation of VAST Video Ads using JavaScript Ad Tags</h2>
   <p>This project is an implementation of an Android app that can serve Video Ads from Minimob’s marketplace. This app demonstrates how to request and display VAST Video Ads using JavaScript Ad Tags.</p>
<h3>Before starting the project</h3>
   <p>Before starting the project, the following have been completed:</p>
<ol>
    <li>Created an account at Minimob </li>
    <li>Created an app under <strong>Monetize &gt; Video Ads</strong> </li>
    <li>Created an ad zone under an app</li>
</ol>
   <p>Upon creating the ad zone, the corresponding JavaScript Ad Tag is generated. This is the Ad Tag which, later on, will be copied and pasted at the relevant point in the code.</p>
<h3>Workflow overview</h3>
   <p>The following steps were completed:</p>
<ol>
    <li>Updating the app’s manifest file</li>
    <li>Importing the Minimob ad-serving module to the project</li>
    <li>Using the imported module for requesting, loading and displaying video ads from Minimob </li>
</ol>
<h3>Updating the app’s manifest file</h3>
   <p>At the app’s <code>AndroidManifest.xml</code> file, the following line has been added in the <code>&lt;application&gt;</code> node (it enables hardware-accelerated rendering for all activities and views in the application):</p>
<pre class="prettyprint linenums">
<code>android:hardwareAccelerated="true"
</code>
</pre>
<h3>Importing the Minimob ad-serving module to your project</h3>
   <p>The required Minimob ad-serving module can be imported from the source code of the <a href="https://github.com/minimob/video-ad-serving" target="_blank">Minimob video-ad-serving project</a> or, provided that <strong>Gradle</strong> is used, it can be retrieved from online repositories.</p>
   <p>In this implementation, the choice was to retrieve the module from the Github repository. The following were carried out:</p>
<ol>
    <li>At the <strong>build.gradle</strong> script file of <em>minimob-addemos module</em>, the following line was added in the <strong>dependencies</strong> block:</li>
<pre class="prettyprint linenums">
<code>compile 'com.github.minimob:video-ad-serving:1.0.28'
</code>
</pre>
    <li>At the <strong>build.gradle</strong> script file of the <em>project</em>, the following line was added in the <strong>repositories</strong> block:</li>
<pre class="prettyprint linenums">
<code>maven { url "https://jitpack.io" }
</code>
</pre>
</ol>
<h3>Requesting, loading and displaying video ads from Minimob</h3>
   <p>There is one activity (<strong>MinimobDemoActivity</strong>) that hosts the different fragments we want to show. The menu has two items (video and video preloaded) and each item loads a different fragment in the container <em>Activity</em>.</p>
   <p>Two distinct cases were implemented:</p>
<ul>
    <li><strong>VideoFragment</strong>: where a single call is used for loading and showing a video ad</li>
    <li><strong>VideoPreloadFragment</strong>: where two separate calls are used, one for loading a video ad and another for showing the video ad</li>
</ul>
<h5>VideoFragment</h5>
   <p>In this case, we just inflate a layout with one button that once clicked, the code to load and show the video ad is triggered.</p>
<h5>VideoPreloadFragment</h5>
   <p>In this case, we inflate the same layout, but we first load the <strong>AdZone</strong> in the <strong>onResume</strong> method that we override from the <em>Fragment</em> class. When the click event of the button is triggered, we just show the ad that was loaded in the background.</p>
   <p>Inline comments have been included in the code of the above two fragments in order to provide clarifications on their implementation.</p>
<h3>Further resources</h3>
   <p>You can get this demo app on Google Play at: <a href="https://play.google.com/store/apps/details?id=com.minimob.addemos" target="_blank">Minimob demos</a></p>
   <p>Further integration instructions as well as more detailed information about the parameters used at the JavaScript Ad Tag can be found at the <a href="https://github.com/minimob/video-ad-serving" target="_blank">Minimob video-ad-serving project</a></p>
</div>
<div id="D-adtag-license">
<h2>License</h2>
   <p>This is free and unencumbered software released into the public domain.</p>
   <p>Anyone is free to copy, modify, publish, use, compile, sell, or<br />
distribute this software, either in source code form or as a compiled<br />
binary, for any purpose, commercial or non-commercial, and by any<br />
means.</p>
   <p>In jurisdictions that recognize copyright laws, the author or authors<br />
of this software dedicate any and all copyright interest in the<br />
software to the public domain. We make this dedication for the benefit<br />
of the public at large and to the detriment of our heirs and<br />
successors. We intend this dedication to be an overt act of<br />
relinquishment in perpetuity of all present and future rights to this<br />
software under copyright law.</p>
   <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,<br />
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF<br />
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.<br />
IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR<br />
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,<br />
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR<br />
OTHER DEALINGS IN THE SOFTWARE.</p>
   <p>For more information, please refer to <a href="http://unlicense.org/" target="_blank">http://unlicense.org</a></p>
</div>
</div>
