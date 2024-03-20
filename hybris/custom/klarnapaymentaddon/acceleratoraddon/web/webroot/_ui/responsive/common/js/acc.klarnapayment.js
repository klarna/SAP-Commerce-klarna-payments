ACC.klarnaPayment = {
		klarnaData : '',
		paymentOption : '',
		kpUpdateSessionUrl : '', 
		getKlarnaFormUrl :'',
		checkPaymentSelected: function () {
			klarnaData={};
			$("form input:radio[name='paymentMethod']").click(function () {
			   var paymentMethod = $('input[type=radio][name=paymentMethod]:checked').val();
			   if (paymentMethod == 'card') {
				   $('.submit_silentOrderPostForm').bind('click');
				   $('#klarna_container').empty();
				   $('#klarna_billing').hide();
				   $('#silentOrderPostForm').show();
				   ACC.silentorderpost.bindSubmitSilentOrderPostForm();
			   } else if (paymentMethod == 'klarna_exp_checkout') {
				   $('.submit_silentOrderPostForm').unbind('click');
				   $('#silentOrderPostForm').hide();
				   $('#klarna_container').empty();
				   $('#klarna_billing').hide();
				   ACC.klarnaPayment.bindPaymentsubmit();
			   } else {
				   $('#klarna_container').empty();
				   $('.submit_silentOrderPostForm').unbind('click');
				   ACC.klarnaPayment.bindPaymentsubmit();
			       $('#silentOrderPostForm').hide();
			       $('#klarna_billing').show();
			       kpUpdateSessionUrl = $("#updateSessionUrl").val();
			       getKlarnaFormUrl = $("#getKlarnaFormUrl").val();
			       paymentOption = paymentMethod;
			       var clientToken = $("#clientToken").val();
			       ACC.klarnaPayment.getKlarnaPaymentForm(clientToken);
			   }
			});
			
			
		},
		createSession: function(kpCreateSessionUrl) {
			$.ajax({
				url: kpCreateSessionUrl,
				method: 'GET',
				dataType: 'json',
				contentType: 'application/json',
				success: function (data) {
					$("#clientToken").val(data);
					ACC.klarnaPayment.LoadForm(data);
					},
				complete: function() {
				},
				error: function (x,e) {
					console.debug(x.status);
					console.debug(e);
				}
			});
		},
		getKlarnaPaymentForm: function(clientToken) {
			$.ajax({
				url:$("#getKlarnaFormUrl").val(),
				method: 'GET',
				dataType: 'html',
				
				beforeSend: function () {
				},
				success: function (data) {
					$("#klarna_billing").html(data);
					ACC.klarnaPayment.LoadForm(clientToken);
				},
				complete: function() {
				},
				error: function (x,e) {
					console.debug(x.status);
					console.debug(e);
				}
			});
		},
		LoadForm: function(clientToken) {
			Klarna.Payments.init({
				  client_token: clientToken
				});
			Klarna.Payments.load({
				container: '#klarna_container',
			    payment_method_category: paymentOption
			  }, function (res) {
			    //console.debug(res);
			    $("#klarna_billing").show();
			})
					
		},
		updateSession: function() {
			$.ajax({
				url: kpUpdateSessionUrl,
				method: 'POST',
				dataType: 'json',
				contentType: 'application/json',
				data:JSON.stringify(klarnaData.billingAddress),
				beforeSend: function () {
					//ACC.klarnaCheckout.suspendKlarlaCheckout();
					//ACC.klarnaCheckout.showCartLoader();
				},
				success: function (data) {
					Klarna.Payments.authorize({
						  payment_method_category: paymentOption, auto_finalize: false
						}, klarnaData, function(res) {
							//console.debug("response debug");
							//console.debug(res);
						  if (res.approved) {
							  if (res.authorization_token != null || res.finalize_required) {
								  ACC.klarnaPayment.saveAuth(res.authorization_token, paymentOption,res.finalize_required, $('#klarnaPaymentDetailsForm'));
							  }
							  
						  }  else {
							  $("#klaranAuthError").val("error");
						  }
						})
				},
				complete: function() {
				},
				error: function (x,e) {
				}
			});
		},
		saveAuth: function(authorizationToken, paymentOption, finalizeRequired, form) {
			if (authorizationToken == undefined ) {
				authorizationToken = 0;
			}
			var saveAuthorizeUrl = $("#saveAuthUrl").val();
			$.ajax({
				url: saveAuthorizeUrl,
				data: {authorizationToken: authorizationToken, paymentOption : paymentOption, finalizeRequired:finalizeRequired},
				method: 'POST',
				dataType: 'json',
				beforeSend: function () {
					//ACC.klarnaCheckout.suspendKlarlaCheckout();
					//ACC.klarnaCheckout.showCartLoader();
				},
				success: function () {
					form.submit();
				},
				complete: function() {
					
				},
				error: function (x,e) {
					
				}
			});
		},
		bindPaymenttActions: function () {
			var isKarnaFormError = $('input[type=hidden][id=isklarnaFormError]').val();
			
			if (isKarnaFormError !=null && isKarnaFormError!=undefined) {
				paymentOption=$('input[type=radio][name=paymentMethod]:checked').val();
				ACC.klarnaPayment.getKlarnaPaymentForm($("#clientToken").val());
				
			} else {
				var isExpCheckout =  $("#isKlarnaExpCheckout").val();
				if(isExpCheckout) {
					$('#silentOrderPostForm').hide();
					$('#klarna_billing').hide();
					$('input[type=radio][id=paymentMethod_klarna_exp_checkout]').prop( "checked", true );
				}
				else {
					$('input[type=radio][id=paymentMethod_card]').prop( "checked", true );
					$('#klarna_billing').hide();	
				}
			}
		},
		bindPaymentsubmit: function () {
			$('.submit_silentOrderPostForm').click(function ()
			{

				var selectedpayMethod = $('input[type=radio][name=paymentMethod]:checked').val();
				
				if(selectedpayMethod == 'klarna_exp_checkout'){
					$('#klarnaExpCheckoutPaymentDetailsForm').submit();
				}
				else {
					if(selectedpayMethod == 'card'){
					//Cancel Authorization	
						ACC.klarnaPayment.cancelKlarnaAuthorization();
						// $('#klarnaPaymentDetailsForm')
					}
							
					$("#klarnaPaymentDetailsForm input[id=paymentId]").val(paymentOption);
					klarnaData={};
					var billingAddress={};
					billingAddress['email']=$("#klarnaPaymentDetailsForm input[name=billTo_email]").val();
					billingAddress['givenName']=$("#klarnaPaymentDetailsForm input[name=billTo_firstName]").val();
					billingAddress['familyName']=$("#klarnaPaymentDetailsForm input[name=billTo_lastName]").val();
					billingAddress['title']=$("#klarnaPaymentDetailsForm select[name=billTo_titleCode] option:selected").val();
					billingAddress['streetAddress']=$("#klarnaPaymentDetailsForm input[name=billTo_street1]").val();
					billingAddress['streetAddress2']=$("#klarnaPaymentDetailsForm input[name=billTo_street2]").val();
					billingAddress['postalCode']=$("#klarnaPaymentDetailsForm input[name=billTo_postalCode]").val();
					billingAddress['city']=$("#klarnaPaymentDetailsForm input[name=billTo_city]").val();
					billingAddress['country']=$("#klarnaPaymentDetailsForm select[name=billTo_country] option:selected").val();
					if ($("#klarnaPaymentDetailsForm select[name=billTo_state]").val() != undefined) {
						billingAddress['region']=$("#klarnaPaymentDetailsForm select[name=billTo_state]").val();
					}
					billingAddress['phone']=$("#klarnaPaymentDetailsForm input[name=billTo_phone]").val();
					if ($("#klarnaPaymentDetailsForm input[name=billTo_houseExtension]").val()!= undefined ) {
						billingAddress['houseExtension']=$("#klarnaPaymentDetailsForm input[name=billTo_houseExtension]").val();
					}
					
					if ($("#klarnaPaymentDetailsForm input[name=dateOfBirth]").val()!= undefined ) {
						billingAddress['dateOfBirth']=$("#klarnaPaymentDetailsForm input[name=dateOfBirth]").val();
					}
					klarnaData['billingAddress']=billingAddress;
					
					ACC.klarnaPayment.updateSession();
				}
								
			});
		},
		
		cancelKlarnaAuthorization:function(){
			
			var cancelAuthorizeUrl = $("#cancelAuthUrl").val();
			$.ajax({
				url: cancelAuthorizeUrl,
				method: 'POST',
				dataType: 'json',
				success: function (data) {
					console.log(data);
				},
				complete: function() {
					console.log(data);	
				},
				error: function (x,e) {
					console.log(data);	
				}
			});
			
		}
}
$(document).ready(function(){
	ACC.klarnaPayment.checkPaymentSelected();
	ACC.klarnaPayment.bindPaymenttActions();
	ACC.klarnaPayment.bindPaymentsubmit();
});