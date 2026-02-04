
document.addEventListener("DOMContentLoaded", async () => {
    var $loadWebSDKv2Div = $('#loadWebSDKv2Div');
	var isSDKv2Enabled = $loadWebSDKv2Div.length > 0 && $loadWebSDKv2Div.data('enabled') === true;
    if(isSDKv2Enabled) {
		try {
	        const { KlarnaSDK } = await import(window.klarnaWebSDKv2Url);   
	        
	        const $klarnaDiv = $("#klarnaDiv");
	        const clientId = $klarnaDiv.data("clientId");
	        const locale = $klarnaDiv.data("locale");
		    const productsJson = $klarnaDiv.data("products");
		    //const integratorJson = $klarnaDiv.data("integrator");   
		    //const originatorsJson = $klarnaDiv.data("originators"); 
	        
	        const klarnaSDKConfig = {
				clientId: clientId,
		      	locale: locale,
		      	products: productsJson
		      	//integrator: JSON.parse(integratorJson),
		      	//originators: JSON.parse(originatorsJson),
	        };	        
	        window.initializedKlarnaSDK = await KlarnaSDK(klarnaSDKConfig);
	        console.debug("Klarna SDK initialized");
	        
	        const $kecDiv = $("#kecDiv");
	        if ($kecDiv.length > 0) {
				console.debug("KEC is active");
				const $loadWebSDKv1Div = $('#loadWebSDKv1Div');
				const isSDKv1Enabled = $loadWebSDKv2Div.length > 0 && $loadWebSDKv1Div.data('enabled') === true;
				if(!isSDKv1Enabled) {
					ACC.klarnaexpcheckout.initKECButton('#klarna_exp_checkout_container_default', KlarnaSDK);
				}			
			}
	                
	    } catch (error) {
	        console.error("Failed to load Klarna SDK", error);
	    }
	}
    
});
