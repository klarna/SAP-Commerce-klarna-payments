
document.addEventListener("DOMContentLoaded", async () => {
    const $klarnaDiv = $("#klarnaDiv");
    if($klarnaDiv.length > 0) {	
		const $loadWebSDKv1Div = $('#loadWebSDKv1Div');
		const isSDKv1Enabled = $loadWebSDKv1Div.length > 0 && $loadWebSDKv1Div.data('enabled') === true;
		const $kecDiv = $("#kecDiv");
		if(isSDKv1Enabled && $kecDiv.length > 0) {	
	        console.debug("KEC v1 is active");
	        window.klarnaAsyncCallback();
		}	    
	    const $loadWebSDKv2Div = $('#loadWebSDKv2Div');
		const isSDKv2Enabled = $loadWebSDKv2Div.length > 0 && $loadWebSDKv2Div.data('enabled') === true;
	    if(isSDKv2Enabled) {
			try {
		        const { KlarnaSDK } = await import(window.klarnaWebSDKv2Url);  
		        
		        const clientid = $klarnaDiv.data("clientid"); 
		        const locale = $klarnaDiv.data("locale");
			    const productsJson = $klarnaDiv.data("products");
			    const integratorJson = $klarnaDiv.data("integrator");   
			    const originatorsJson = $klarnaDiv.data("originators"); 
		        
		        const klarnaSDKConfig = {
					clientId: clientid,
			      	locale: locale,
			      	integrator: integratorJson,
			      	originators: originatorsJson,
			      	products: productsJson
			      	
		        };	    
		        	        
		        window.KlarnaV2 = window.KlarnaV2 || {};
		        window.KlarnaV2._completedRequests = new Set();
		        if(!window.KlarnaV2.initializedKlarnaSDK) {
					window.KlarnaV2.initializedKlarnaSDK = await KlarnaSDK(klarnaSDKConfig);
				}    
		        //const initializedKlarnaSDK = await KlarnaSDK(klarnaSDKConfig);
		        console.debug("Klarna SDK initialized");
	        
		        const $kecDiv = $("#kecDiv");
		        if ($kecDiv.length > 0) {
					console.debug("KEC v2 is active");
					if(!isSDKv1Enabled) {
						ACC.klarnaexpcheckout.initKlarnaPaymentButton(window.KlarnaV2.initializedKlarnaSDK);
					}			
				}
		        
		        // Loading OSM Component for product
		        const $kosmDiv = $("#kosm_div");
		        if ($kosmDiv.length > 0) {
		        	ACC.klarnaosm.initKOSM(window.KlarnaV2.initializedKlarnaSDK);
		        }
		        	        
		        // Loading Klana sign in Component
		        const $siwkDiv = $("#siwkDiv");
				if($siwkDiv.length > 0) {
			    	var currentURL = window.location.href;
			    	var showInLoginPage = $("#showSIWKInLoginPage").val();
			    	var showInRegisterPage = $("#showSIWKInRegisterPage").val();
			    	var showInCheckoutLoginPage = $("#showSIWKInCheckoutLoginPage").val();
			    	var showSignInButton = false;
			    	if(currentURL.endsWith("/login/checkout") && (showInCheckoutLoginPage == "true") )
			    	{
			    		showSignInButton = true;
			    	}
			    	else if(currentURL.endsWith("/login") && (showInLoginPage == "true" || showInRegisterPage == "true") )
			    	{
			    		showSignInButton = true;
			    	}
			    	if(showSignInButton){
			    		ACC.klarnasignin.initiateSigninButton(window.KlarnaV2.initializedKlarnaSDK);
			    	}
		        }       
		    } catch (error) {
		        console.error("Failed to load Klarna SDK", error);
		    }	    	   
		}
		const pspIntegration = $klarnaDiv.data("psp-integration");
		if(pspIntegration === true) {
			ACC.klarnapaymentaddon.initNetworkSessionCallback(window.KlarnaV2.initializedKlarnaSDK);
		}
	}	    
    
});

ACC.klarnapaymentaddon = {
	initNetworkSessionCallback: function(Klarna) {
	    if (typeof Klarna !== 'undefined' && Klarna.Network.Session) {
	        // If Klarna.Network.Session is available, listen for the 'tokenupdate' event
	        Klarna.Network.Session.on('tokenupdate', (klarnaNetworkSessionToken) => {
	            // When the token update event is fired, send the updated token to the server via an AJAX POST request
	            ACC.klarnapaymentaddon.saveKlarnaNetworkSessionToken(klarnaNetworkSessionToken);
	        });
	        // Reset the retry count after successful initialization
	        retryCount = 0;
	    } else {
	        // Retry initializing if Klarna.Network.Session event is not yet available and if the retry count is less than or equal to 10
	        retryCount++; 
	        if (retryCount <= 10) {
	            setTimeout(initKlarnaNetworkSessionCallback(Klarna), 500);
	        }
	    }
	},
	
	saveKlarnaNetworkSessionToken: function(klarnaNetworkSessionToken) {
		var saveTokenUrl = $("#saveKlarnaNetworkSessionTokenUrl").val();
		return $.ajax({
			url: saveTokenUrl,
			data: JSON.stringify({
                klarnaNetworkSessionToken: klarnaNetworkSessionToken
            }),
			method: 'POST',
			dataType: 'json',
			contentType: 'application/json'
		});
	}
};	
	


	
