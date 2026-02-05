ACC.klarnaexpcheckout = {
	klarnaButtonLoad : function(containerId) {
		var buttonTheme = $("#kecButtonTheme").val();
		var buttonShape = $("#kecButtonShape").val();
		var klarnaLocale = $("#klarnaLocale").val();
		window.Klarna.Payments.Buttons.load(
	   	{
	        container: containerId,
	        theme: buttonTheme,
	        shape: buttonShape,
	        locale: klarnaLocale,
	        on_click: (authorize) => {
			  	ACC.klarnaexpcheckout.getOrderPayload().done(function(payLoadResult) {
				    var payload = payLoadResult;
				    //console.log("Payload: ", JSON.stringify(payLoadResult));
				    var collectShippingAddress = true;
					var autoFinalize = false; // autoFinalize should always be false as order review step is mandatory			
					
			        authorize(					
						{ collect_shipping_address: collectShippingAddress, auto_finalize: autoFinalize },  
						//{ auto_finalize: autoFinalize },
						payLoadResult,  	           
			            (authResponse) => {
							//console.log("auth result", authResponse);
			              	// The result, if successful contains the client token
			            	if(authResponse && authResponse.approved) { // && authResponse.approved
								ACC.klarnaexpcheckout.processAuthorizeResponse(authResponse);
							}
							else {
								if(authResponse.error) {
									//console.log("Authorize response error -> ", authResponse.error);
								}
								ACC.klarnaexpcheckout.showMessage($("#klarnaExpCheckoutErrorMessage").val());
							}
			            },
			     	);
				}).fail(function() {
			    	//console.log("Error in getting order payload");
			    	ACC.klarnaexpcheckout.showMessage($("#klarnaExpCheckoutErrorMessage").val());
				});
	        },
      	},
      	function load_callback(loadResult) {
        	// Here you can handle the result of loading the button
        	//console.log("Klarna button load result: ", loadResult);
      	},
	    );
	},
 
	getOrderPayload: function() {
		// If express checkout is triggered from Cart page, product code and quantity not required 
		var productCode;
		var productQty;
		if($("#klarnaExpCheckoutProductCode").val() == undefined) {
			productCode = null;
			productQty = null;
		}
		else {
			// If express checkout is triggered from PDP page, pass product code and quantity to add to new cart	
			productCode = $("#klarnaExpCheckoutProductCode").val();
			productQty =  $("#pdpAddtoCartInput").val();
			if (productQty == undefined) {
				productQty = "1";
			}
		}
		// Get order payload for authorize call
		var getOrderPayloadUrl = $("#expCheckoutAuthorizePayloadUrl").val();
		return $.ajax({
			url: getOrderPayloadUrl,
			data: {"productCode":productCode, "productQty":productQty},
			method: 'GET',
			dataType: 'json',
			contentType: 'application/json'
		});
	},

	processAuthorizeResponse: function(authResponse) {
	    //Show loading Indicator
	    //showLoadingIndicator();
	    var authorizeResponseUrl = $("#expCheckoutProcessAuthorizeResponseUrl").val();
	    $.ajax(authorizeResponseUrl, {
	        data: JSON.stringify(authResponse),
	        dataType: "json",
			contentType: 'application/json',
	        type: "post"
	    }).done(function(data, textStatus, jqXHR) {
			if(data != null) {
				window.location = ACC.config.encodedContextPath + data;
			}
			else {
				ACC.klarnaexpcheckout.showMessage(expressCheckoutErrorMessage);
			} 
	    }).fail(function (error) {
			//console.log("Authorize reponse error:", JSON.stringify(error));
			ACC.klarnaexpcheckout.showMessage(expressCheckoutErrorMessage);
		});
	},
	
	showMessage(message) {
		if(message == undefined) {
			message = "Klarna Express Checkout Failed. Please try again later.";
		}
		ACC.colorbox.open(message,{
	        html : $(document).find("#klarnaErrorMessage").html(),
	        maxWidth:"100%",
	        width:"420px",
	        initialWidth :"420px",
	        height:"300px"
	  	});
	},
	
	initKECButtonV1 : function() {
		const clientKey = $("#klarnaClientId").val();
		//console.log("clientKey", clientKey);
		window.Klarna.Payments.Buttons.init({
	      client_key: clientKey,
	    });
	    // Express Checkout button is displayed in PDP and Cart
	    const currentPageUrl = window.location.pathname;
	    if(currentPageUrl.includes('/p/') || currentPageUrl.includes('/cart')) {
			ACC.klarnaexpcheckout.klarnaButtonLoad("#klarna_exp_checkout_container_default");
			// Button will be displayed in two places in Cart page
		    if((window.location.pathname).includes('/cart')) {
				ACC.klarnaexpcheckout.klarnaButtonLoad("#klarna_exp_checkout_container_checkout_display");
			}
		}
	},	
	
	initKECButtonV2 : function(containerId) {
		const $kecDiv = $("#kecDiv");
    	const buttonshape = $kecDiv.data("buttonshape");
	    const buttontheme = $kecDiv.data("buttontheme");
		window.initializedKlarnaSDK.Payment.button({ 
	        shape: buttonshape,
	        theme: buttontheme,
	        //locale: klarnaLocale,
        	intents: ['PAY'],
        	initiationMode: 'DEVICE_BEST',
	        initiate: async () => {
				const paymentResponse = await ACC.klarnaexpcheckout.createPaymentRequest();
				if (!responseJson || !responseJson.paymentRequestId) {
	                //console.log("Error in creating payment request");
			    	ACC.klarnaexpcheckout.showMessage($("#klarnaExpCheckoutErrorMessage").val());
	                return;
	            }
				// TODO Polling
				var paymentRequestId = { paymentRequestId: paymentResponse.paymentRequestId };
	            return paymentRequestId;
	        }
    	}).mount(containerId);
    	
    	// Express Checkout button is displayed in PDP and Cart
	    const currentPageUrl = window.location.pathname;
	    if(currentPageUrl.includes('/p/') || currentPageUrl.includes('/cart')) {
			window.initializedKlarnaSDK.Payment.button.mount("#klarna_exp_checkout_container_default");
			// Button will be displayed in two places in Cart page
		    if((window.location.pathname).includes('/cart')) {
				window.initializedKlarnaSDK.Payment.button.mount("#klarna_exp_checkout_container_checkout_display");
			}
		}
    	
    	// Only register shipping address change handler if not PSP integrated and single step mode is enabled
    	var integratedViaPSP = $("#integratedViaPSP").val();
	    if (!integratedViaPSP) {
	        klarna.Payment.on('shippingaddresschange', async (paymentRequest, shippingAddress) => {
	            try {
	                const shippingAddressResponse = await ACC.klarnaexpcheckout.updateShippingAddress(paymentRequest, shippingAddress);
	                if (shippingAddressResponse.status === "success") {
						return shippingAddressResponse.successResponse;
					}
					if(shippingAddressResponse.rejectionResponse && shippingAddressResponse.rejectionResponse.rejectionReason) {
						 var rejectionMap = {
	                        COUNTRY_NOT_SUPPORTED: klarna.Payment.ShippingRejectionReason.COUNTRY_NOT_SUPPORTED,
	                        POSTAL_CODE_NOT_SUPPORTED: klarna.Payment.ShippingRejectionReason.POSTAL_CODE_NOT_SUPPORTED,
	                        CITY_NOT_SUPPORTED: klarna.Payment.ShippingRejectionReason.CITY_NOT_SUPPORTED,
	                        REGION_NOT_SUPPORTED: klarna.Payment.ShippingRejectionReason.REGION_NOT_SUPPORTED,
	                        ADDRESS_NOT_SUPPORTED: klarna.Payment.ShippingRejectionReason.ADDRESS_NOT_SUPPORTED
	                    };
	                    var mappedRejection = rejectionMap[responseJson.rejectionReason];
	                    return { rejection_reason: mappedRejection || klarna.Payment.ShippingRejectionReason.ADDRESS_NOT_SUPPORTED };
					}
					else {
						return { rejection_reason: klarna.Payment.ShippingRejectionReason.ADDRESS_NOT_SUPPORTED };
					}
	            } catch (error) {
	                return { rejection_reason: klarna.Payment.ShippingRejectionReason.ADDRESS_NOT_SUPPORTED };
	            }
	        });
	
	        klarna.Payment.on('shippingoptionselect', async (paymentRequest, shippingOption) => {
	            try {
	                const shippingOptionResponse = await ACC.klarnaexpcheckout.updateShippingOption(paymentRequest, shippingOption);
	                if (shippingOptionResponse.status === "success") {
						return shippingOptionResponse.successResponse;
					}
					else {
						return { rejection_reason: 'INVALID_OPTION' };
					}
	            } catch (error) {
	                return { rejection_reason: 'INVALID_OPTION' };
	            }
	        });
		}
		klarna.Payment.on('complete', async (paymentRequest) => {
	        if (paymentRequest && paymentRequest.stateContext && paymentRequest.stateContext.interoperabilityToken) {
	            // Save the interoperability token and notify PSPs so they can use the token
	            await ACC.klarnaexpcheckout.onPaymentComplete(paymentRequest);
	        }
	        // Return a boolean to skip redirection.
	        return false;
	    });
	
	    klarna.Payment.on('error', (error, paymentRequest) => { 
	        return false;
	    });
	
	    klarna.Payment.on('abort', (paymentRequest) => { 
	        return false;
	    });	        
	},	
	
	createPaymentRequest: function() {
		// If express checkout is triggered from Cart page, product code and quantity not required 
		var productCode;
		var productQty;
		if($("#klarnaExpCheckoutProductCode").val() == undefined) {
			productCode = null;
			productQty = null;
		}
		else {
			// If express checkout is triggered from PDP page, pass product code and quantity to add to new cart	
			productCode = $("#klarnaExpCheckoutProductCode").val();
			productQty =  $("#pdpAddtoCartInput").val();
			if (productQty == undefined) {
				productQty = "1";
			}
		}
		var createPaymentRequestUrl = $("#expCheckoutPaymentRequestUrl").val();
		return $.ajax({
			url: createPaymentRequestUrl,
			data: {"productCode":productCode, "productQty":productQty},
			method: 'GET',
			dataType: 'json',
			contentType: 'application/json'
		});
	},
	
	updateShippingAddress: function(paymentRequest, shippingAddress) {
		var kecUpdateShippingAddressUrl = $("#kecUpdateShippingAddressUrl").val();
		return $.ajax({
			url: kecUpdateShippingAddressUrl,
			data: JSON.stringify({
                shippingAddress: shippingAddress,
                paymentRequestId: paymentRequest
            }),
			method: 'POST',
			dataType: 'json',
			contentType: 'application/json'
		});
	},
	
	updateShippingOption: function(paymentRequest, shippingOption) {
		var kecUpdateShippingMethodUrl = $("#kecUpdateShippingMethodUrl").val();
		return $.ajax({
			url: kecUpdateShippingMethodUrl,
			data: JSON.stringify({
                shippingOption: shippingOption,
                paymentRequestId: paymentRequest
            }),
			method: 'POST',
			dataType: 'json',
			contentType: 'application/json'
		});
	},
	
	onPaymentComplete: function(paymentRequest) {
		var kecOnPaymentCompleteUrl = $("#kecOnPaymentCompleteUrl").val();
		return $.ajax({
			url: kecOnPaymentCompleteUrl,
			data: JSON.stringify({
                paymentRequest: paymentRequest
            }),
			method: 'POST',
			dataType: 'json',
			contentType: 'application/json'
		});
	},
	
};	

/*window.klarnaAsyncCallback = function () {
	// Check if SDK v1 is enabled
	var $loadWebSDKv1Div = $('#loadWebSDKv1Div');
	var isSDKv1Enabled = $loadWebSDKv1Div.length > 0 && $loadWebSDKv1Div.data('enabled') === true;
	
	if (isSDKv1Enabled) {
	    var clientKey = $("#klarnaClientId").val();
		//console.log("clientKey", clientKey);
		window.Klarna.Payments.Buttons.init({
	      client_key: clientKey,
	    });
	    // Express Checkout button is displayed in PDP and Cart
	    var currentPageUrl = window.location.pathname;
	    if(currentPageUrl.includes('/p/') || currentPageUrl.includes('/cart')) {
			ACC.klarnaexpcheckout.klarnaButtonLoad("#klarna_exp_checkout_container_default");
			// Button will be displayed in two places in Cart page
		    if((window.location.pathname).includes('/cart')) {
				ACC.klarnaexpcheckout.klarnaButtonLoad("#klarna_exp_checkout_container_checkout_display");
			}
		}
	}
};*/

/*window.KlarnaSDKCallback = async function () {
		
 	const $klarnaDiv = $("#klarnaDiv");

    const productsJson = $klarnaDiv.data("products");
    const integratorJson = $klarnaDiv.data("integrator");   
    const originatorsJson = $klarnaDiv.data("originators");

	// Check if SDK v2 is enabled
	var $loadWebSDKv2Div = $('#loadWebSDKv2Div');
	var isSDKv2Enabled = $loadWebSDKv2Div.length > 0 && $loadWebSDKv2Div.data('enabled') === true;
	// Check if SDK v1 is enabled 
	var $loadWebSDKv1Div = $('#loadWebSDKv1Div');
	var isSDKv1Enabled = $loadWebSDKv1Div.length > 0 && $loadWebSDKv1Div.data('enabled') === true;
	if (isSDKv2Enabled && !isSDKv1Enabled) {
		
		var klarnaClientId = $("#klarnaClientId").val();
		var klarnaLocale = $("#klarnaLocale").val();
		
		const klarna = await Klarna.init({
	      clientId: klarnaClientId,
	      products: JSON.parse(productsJson),
	      locale: klarnaLocale,
	      integrator: JSON.parse(integratorJson),
	      originators: JSON.parse(originatorsJson),
	    });
		
		var buttonTheme = $("#kecButtonTheme").val();
		var buttonShape = $("#kecButtonShape").val();
		
		
	}	
};*/


