ACC.klarnaplaceorder = {
	paymentOption : '',

	bindAll: function ()
	{
		this.bindPlaceOrder();
		this.updatePlaceOrderButton();
	},

	bindPlaceOrder: function ()
	{
		$("#placeOrder").on("click", function ()
		{
			if ($("#Terms1").prop("checked") == false ) {
				if (!$('#termserror').length){
					var errorMessage =  $("#errorMessage").val();
					var msg = "<spring:message code='checkout.error.terms.not.accepted' />";
					var div = $("<div>").attr("id", "termserror").attr("class", "alert alert-danger alert-dismissable getAccAlert").text(errorMessage);
			    	$('#placeOrderForm1').prepend($(div));
				}
				return false;
		    	
			} else {
				var payOption = $("#paymentOption").val();
				var finalizeRequired = $("#finalizeRequired").val();
				var clientToken = $("#clientToken").val();
				var previousStepUrl = $("#previousStep").val();
				$("#placeOrder").attr('disabled','disabled');
				if (finalizeRequired == "true") {
					Klarna.Payments.init({
						  client_token: clientToken
						});
					Klarna.Payments.finalize(
						{payment_method_category: payOption},
						{}, function (res) {
					    console.debug(res);
					    if (res.approved == true && res.authorization_token != null) {
					    	ACC.klarnaPayment.saveAuth(res.authorization_token, payOption, finalizeRequired, $("#placeOrderForm1"));
					    } else if (res.approved == false && res.show_form == true){
					    	$("#placeOrderForm1").submit();
					    } else if (res.show_form == false){
					    	ACC.klarnaPayment.saveAuth("1", payOption, finalizeRequired, $("#placeOrderForm1"));
					    	$("#placeOrderForm1").submit();
					    }
					})
				} else {
					$("#placeOrderForm1").submit();
				}
				
			}
		});
		
	},
	

	updatePlaceOrderButton: function ()
	{
		$(".place-order").removeAttr("disabled");
		// need rewrite /  class changes
	}
};

$(document).ready(function ()
{
	ACC.klarnaplaceorder.bindAll();
});


