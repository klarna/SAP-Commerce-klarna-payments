ACC.klarnaexpcheckout = {
	keepPolling: false,
	pollStartTime: null,
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
				    //var payload = payLoadResult;
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
	
	initKlarnaPaymentButton(klarnaSDK) {
		// Express Checkout button is displayed in PDP and Cart
	    const currentPageUrl = window.location.pathname;
	    if(currentPageUrl.includes('/p/') || currentPageUrl.includes('/cart')) {
			const integratedViaPSP = $("#integratedViaPSP").val();
			ACC.klarnaexpcheckout.loadKlarnaPaymentButton(klarnaSDK, "#klarna_exp_checkout_container_default", integratedViaPSP);
			// Button will be displayed in two places in Cart page
		    if((window.location.pathname).includes('/cart')) {
				ACC.klarnaexpcheckout.loadKlarnaPaymentButton(klarnaSDK, "#klarna_exp_checkout_container_checkout_display", integratedViaPSP);
			}
			//if(integratedViaPSP === false) {
		    klarnaSDK.Payment.on('shippingaddresschange', async (paymentRequest, shippingAddress) => {
	            try {
	                const shippingAddressResponse = await ACC.klarnaexpcheckout.updateShippingAddress(paymentRequest, shippingAddress);
	                if (shippingAddressResponse.status === "success") {
						console.log('Shipping change response: '+JSON.stringify(shippingAddressResponse.successResponse));
						return JSON.stringify(shippingAddressResponse.successResponse);
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
	
	        klarnaSDK.Payment.on('shippingoptionselect', async (paymentRequest, shippingOption) => {
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
	        //}
	        
			klarnaSDK.Payment.on('complete', async (paymentRequest) => {										
				const key = paymentRequest?.paymentRequestId 
				             || paymentRequest?.paymentRequestReference
				             || JSON.stringify(paymentRequest);				
			    if (window.KlarnaV2._completedRequests.has(key)) {
			        console.debug("Duplicate complete suppressed:", key);
			        return false;
			    }			
			    window.KlarnaV2._completedRequests.add(key);					    
		        if (paymentRequest) {
					console.log("Payment Request: ", JSON.stringify(paymentRequest)); 
		            const paymentCompleteResponse = await ACC.klarnaexpcheckout.onPaymentComplete(paymentRequest);
		            console.log("Payment Response: ", JSON.stringify(paymentCompleteResponse)); 
		            if(integratedViaPSP === true) {
						// Redirect to PSP specific checkout
						// Returning false to prevent redirection temporarily
						return false;
					}
					else {
						if(paymentCompleteResponse.status === "SUCCESS") {
							// Redirect to placeOrder
							window.location = paymentCompleteResponse.redirectUrl;
						}
						else {
							ACC.klarnaexpcheckout.showMessage('Klarna Express Checkout Failed. Please try again later.');
						}
					}
					return false;
		        }
		        
		    });
		
		    klarnaSDK.Payment.on('error', (error, paymentRequest) => { 
		        return false;
		    });
		
		    klarnaSDK.Payment.on('abort', (paymentRequest) => { 
		        return false;
		    });
		}
	},
	
	loadKlarnaPaymentButton : function(klarnaSDK, containerId, integratedViaPSP) {
		const $kecDiv = $("#kecDiv");
    	const buttonshape = $kecDiv.data("buttonshape");
	    const buttontheme = $kecDiv.data("buttontheme");
		klarnaSDK.Payment.button({ 
	        shape: buttonshape,
	        theme: buttontheme,
	        //locale: klarnaLocale,
        	intents: ['PAY'],
        	initiationMode: 'DEVICE_BEST',
	        initiate: async () => {
				const paymentResponse = await ACC.klarnaexpcheckout.createPaymentRequest();
				if (!paymentResponse || !paymentResponse.payment_request_id) {
	                //console.log("Error in creating payment request");
			    	ACC.klarnaexpcheckout.showMessage($("#klarnaExpCheckoutErrorMessage").val());
	                return;
	            }
	            // console.log("paymentResponse.paymentRequestId:: "+paymentResponse.payment_request_id);
				// Start Polling
				if(integratedViaPSP == "false") {
					keepPolling = true;
					pollStartTime = Date.now();
					//ACC.klarnaexpcheckout.pollPaymentStatus(paymentResponse.payment_request_id);
				}	
				var paymentRequestId = { paymentRequestId: paymentResponse.payment_request_id };
	            return paymentRequestId;
	        }
    	}).mount(containerId);	        
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
	
	pollPaymentStatus: function(paymentRequestId) {
		if (!keepPolling) return;
	    // Check if polling has exceeded maximum duration
	    var elapsedTime = Date.now() - pollStartTime;
	    // Continue polling for 5 mins
	    if (elapsedTime > 300000) {
	        keepPolling = false;
	        pollStartTime = null;
	        ACC.klarnaexpcheckout.showMessage('Klarna Express Checkout Failed. Please try again later.');
	        return;
	    }	
		ACC.klarnaexpcheckout.checkPaymentStatus(paymentRequestId).done(function(response) {
		    console.log("Payment status:", response); // "SUCCESS"
		    if(response.status === 'SUCCESS') {
				window.location = response.redirectUrl;
			}
			else if(response.status === 'NOT_READY') {
				if (keepPolling) {
		            setTimeout(function () { ACC.klarnaexpcheckout.pollPaymentStatus(paymentRequestId); }, 2000);
		        }
			} else {
	            keepPolling = false;
	            pollStartTime = null;
	            ACC.klarnaexpcheckout.showMessage('Klarna Express Checkout Failed. Please try again later.');
	            return;
	        }
		});    
	},
	
	checkPaymentStatus: function(paymentRequestId) {
		var kecCheckPaymentStatusUrl = $("#kecCheckPaymentStatusUrl").val();
		return $.ajax({
			url: kecCheckPaymentStatusUrl,
			data: JSON.stringify({
                paymentRequestId: paymentRequestId
            }),
			method: 'POST',
			dataType: 'json',
			contentType: 'application/json'
		});
	},
	
	
};	

window.klarnaAsyncCallback = function () {
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
};

