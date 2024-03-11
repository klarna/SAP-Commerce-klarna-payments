ACC.klarnaplaceorder = {
	paymentOption : '',

	bindAll: function ()
	{
		this.bindPlaceOrder();
		this.updatePlaceOrderButton();
	},

	bindPlaceOrder: function ()
	{
		var cssClass = $(document).find('.place-order-form');
		var termsCheckBox = cssClass.find('input[name=termsCheck]');
		var placeOrderBtn = cssClass.find('.btn-place-order'); 
		
		placeOrderBtn.on('click', function(){
			placeOrderBtn.prop('disabled', true);
	        if(termsCheckBox.is(':checked')) {
				var payOption = $("#paymentOption").val();
				var finalizeRequired = $("#finalizeRequired").val();
				var clientToken = $("#clientToken").val();
				//var previousStepUrl = $("#previousStep").val();
				var isExpCheckout =  $("#isKlarnaExpCheckout").val();
				
				$("#placeOrder").attr('disabled','disabled');		
				
				if(isExpCheckout) {
					ACC.klarnaexpcheckout.getOrderPayload().done(function(payLoadResult) {
						//console.log("Payload: ", JSON.stringify(payLoadResult));
						ACC.klarnaplaceorder.finalizePayment(clientToken, null, payLoadResult);
					}).fail(function() {
				    	//console.log("Error in getting order payload");
				    	ACC.klarnaexpcheckout.showMessage($("#klarnaExpCheckoutErrorMessage").val());
					});	
				}
				else {
					if (finalizeRequired == "true") {
						ACC.klarnaplaceorder.finalizePayment(clientToken, payOption, null);
					} else {
						$("#placeOrderForm1").submit();
					}
				}	
			} 
			else {				
				if (!$('#termserror').length){
					var errorMessage =  $("#errorMessage").val();
					var msg = "<spring:message code='checkout.error.terms.not.accepted' />";
					var div = $("<div>").attr("id", "termserror").attr("class", "alert alert-danger alert-dismissable getAccAlert").text(errorMessage);
			    	$('#placeOrderForm1').prepend($(div));
				}
				return false;			
			}
		});
		
	},
	

	updatePlaceOrderButton: function ()
	{
		$(".place-order").removeAttr("disabled");
		// need rewrite /  class changes
	},
	
	finalizePayment: function (clientToken, payOption, payload)
	{
		if(!payOption) {
			payOption = 'klarna';
		}
		Klarna.Payments.init({
			  client_token: clientToken
			});
		Klarna.Payments.finalize(
			{payment_method_category: payOption},
			payload, 
			function (res) {
		    //console.debug(JSON.stringify(res));
		    if (res.approved == true && res.authorization_token != null) {
		    	ACC.klarnaPayment.saveAuth(res.authorization_token, payOption, 'false', $("#placeOrderForm1"));
		    } else if (res.approved == false && res.show_form == true){
				//alert('not approved: '+res.show_form);
		    	$("#placeOrderForm1").submit();
		    } else if (res.show_form == false){
				//alert('res.show_form: '+res.show_form);
		    	ACC.klarnaPayment.saveAuth("1", payOption, finalizeRequired, $("#placeOrderForm1"));
		    	$("#placeOrderForm1").submit();
		    }
		})
	}
	
};

$(document).ready(function ()
{
	ACC.klarnaplaceorder.bindAll();
});


