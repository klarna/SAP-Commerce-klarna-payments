
document.addEventListener("DOMContentLoaded", async () => {
    
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
	        
	        const $klarnaDiv = $("#klarnaDiv");
	        const clientid = $klarnaDiv.data("clientid");
	        const locale = $klarnaDiv.data("locale");
		    const products = ["PAYMENT","MESSAGING", "IDENTITY"];
		    //const productsJson = $klarnaDiv.data("products");
		    //const integratorJson = $klarnaDiv.data("integrator");   
		    //const originatorsJson = $klarnaDiv.data("originators"); 
	        
	        const klarnaSDKConfig = {
				clientId: clientid,
		      	locale: locale,
		      	products: products
		      	//integrator: JSON.parse(integratorJson),
		      	//originators: JSON.parse(originatorsJson),
	        };	        
	        const initializedKlarnaSDK = await KlarnaSDK(klarnaSDKConfig);
	        console.debug("Klarna SDK initialized");
	        
	        const $kecDiv = $("#kecDiv");
	        if ($kecDiv.length > 0) {
				console.debug("KEC v2 is active");
				if(!isSDKv1Enabled) {
					ACC.klarnaexpcheckout.initKlarnaPaymentButton(initializedKlarnaSDK);
				}			
			}
	        
	        // Loading OSM Component for product
	        const $kosmDiv = $("#kosm_prod");
	        if ($kosmDiv.length > 0) {
	        	var osmPayload = {
		        		key: $("#datakey").val(),
		        		//locale: $("#klarnaLocale").val(),
		        		theme: $("#theme").val(),
		        		amount:  $("#purchaseAmount").val()
		    		};
	        	ACC.klarnaosm.initKOSM(initializedKlarnaSDK);
	        }
	        
	        
	        // Loading Klana sign in Component
	        // placement Flags
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
	    		ACC.signin.initiateSigninButton(initializedKlarnaSDK);
	    	}
	                
	    } catch (error) {
	        console.error("Failed to load Klarna SDK", error);
	    }
	    
	   
	}
    
});
