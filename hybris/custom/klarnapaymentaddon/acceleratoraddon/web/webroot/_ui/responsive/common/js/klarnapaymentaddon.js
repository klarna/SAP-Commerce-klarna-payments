
document.addEventListener("DOMContentLoaded", async () => {
    
    const $loadWebSDKv1Div = $('#loadWebSDKv1Div');
	const isSDKv1Enabled = $loadWebSDKv1Div.length > 0 && $loadWebSDKv1Div.data('enabled') === true;
	const $kecDiv = $("#kecDiv");
	if(isSDKv1Enabled && $kecDiv.length > 0) {	
        console.debug("KEC v1 is active");
		if(!isSDKv1Enabled) {
			ACC.klarnaexpcheckout.initKECButtonV1();
		}
	}	
    
    const $loadWebSDKv2Div = $('#loadWebSDKv2Div');
	const isSDKv2Enabled = $loadWebSDKv2Div.length > 0 && $loadWebSDKv2Div.data('enabled') === true;
    if(isSDKv2Enabled) {
		try {
	        const { KlarnaSDK } = await import(window.klarnaWebSDKv2Url);   
	        
	        const $klarnaDiv = $("#klarnaDiv");
	        const clientid = $klarnaDiv.data("clientid");
	        const locale = $klarnaDiv.data("locale");
		    const productsJson = $klarnaDiv.data("products");
		    //const integratorJson = $klarnaDiv.data("integrator");   
		    //const originatorsJson = $klarnaDiv.data("originators"); 
	        
	        const klarnaSDKConfig = {
				clientId: clientid,
		      	locale: locale,
		      	products: productsJson
		      	//integrator: JSON.parse(integratorJson),
		      	//originators: JSON.parse(originatorsJson),
	        };	        
	        window.initializedKlarnaSDK = await KlarnaSDK(klarnaSDKConfig);
	        console.debug("Klarna SDK initialized");
	        
	        const $kecDiv = $("#kecDiv");
	        if ($kecDiv.length > 0) {
				console.debug("KEC v2 is active");
				if(!isSDKv1Enabled) {
					ACC.klarnaexpcheckout.initKECButtonV2('#klarna_exp_checkout_container_default');
				}			
			}
	                
	    } catch (error) {
	        console.error("Failed to load Klarna SDK", error);
	    }
	}
    
});
