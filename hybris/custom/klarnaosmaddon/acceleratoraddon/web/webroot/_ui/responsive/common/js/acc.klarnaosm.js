ACC.klarnaosm = {
		initKOSM : function(klarnaSDK) {
			var osmPayload = {
	        		key: $("#datakey").val(),
	        		locale: $("#klarnaLocale").val(),
	        		theme: $("#theme").val(),
	        		amount:  $("#purchaseAmount").val()
	    		};
	
			klarnaSDK.Messaging.placement(osmPayload).mount("#kosm_prod");	
		}
}