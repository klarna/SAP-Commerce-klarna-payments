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
	}
	
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

