//
//  mmji.js
//

(function() {
	
	console.log("mmji object loading...");

	var mmji = window.mmji = {};

	/***************************************************************************
	 * console logging helper
	 **************************************************************************/

	mmji.LOG_LEVEL = {
		"DEBUG"   : 0,
		"INFO"    : 1,
		"WARNING" : 2,
		"ERROR"   : 3,
		"NONE"    : 4
	};

	mmji.logLevel = mmji.LOG_LEVEL.NONE;
	var log = {};

	log.d = function(msg) {
		if (mmji.logLevel <= mmji.LOG_LEVEL.DEBUG) {
			console.log("mmji (D) " + msg);
		}
	};

	log.i = function(msg) {
		if (mmji.logLevel <= mmji.LOG_LEVEL.INFO) {
			console.log("mmji (I) " + msg);
		}
	};

	log.w = function(msg) {
		if (mmji.logLevel <= mmji.LOG_LEVEL.WARNING) {
			console.log("mmji (W) " + msg);
		}
	};

	log.e = function(msg) {
		if (mmji.logLevel <= mmji.LOG_LEVEL.ERROR) {
			console.log("mmji (E) " + msg);
		}
	};

	/***************************************************************************
	 * callbacks to Native code
	 **************************************************************************/

	mmji.close = function() {
		log.i("mmji.close");
		callNative("close");
	};

	mmji.expand = function(url)
	{
		if (url === undefined) {
			log.i("mmji.expand (1-part)");
			callNative("expand");
		} else {
			log.i("mmji.expand " + url);
			callNative("expand?url=" + encodeURIComponent(url));
		}
	};

	mmji.adsReady = function(packageId) {
        callNative("adsReady?packageId=" + packageId);
    };

	mmji.noAds = function() {
        log.i("mmji.noAds");
        callNative("noAds");
    };

	/***************************************************************************
	 * methods called by Native code
	 **************************************************************************/

	mmji.fireResumeVideoEvent = function() {
        log.i("mmji.fireResumeVideoEvent");
        videoHandler.play();
    };

	/***************************************************************************
	 * internal helper methods
	 **************************************************************************/

	function callNative(command) {
		var iframe = document.createElement("IFRAME");
		iframe.setAttribute("src", "mmji://" + command);
		document.documentElement.appendChild(iframe);
		iframe.parentNode.removeChild(iframe);
		iframe = null;
	}

	console.log("mmji object loaded");

})();