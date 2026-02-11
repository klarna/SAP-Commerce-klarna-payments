ACC.klarnaosm = {
	initKOSM : function(klarnaSDK) {
		const currentPageUrl = window.location.pathname;
		const osmShowInPDP = $("#osmShowInPDP").val();
		const osmShowInCartPage = $("#osmShowInCartPage").val()
    	if((currentPageUrl.includes('/p/') && osmShowInPDP == "true") 
    			|| (currentPageUrl.includes('/cart') && osmShowInCartPage == "true")) {
			var osmPayload = {
        		key: $("#datakey").val(),
        		locale: $("#klarnaLocale").val(),
        		theme: $("#osmTheme").val(),
        		amount:  $("#purchaseAmount").val()
    		};
			klarnaSDK.Messaging.placement(osmPayload).mount("#kosm_div");					
		}		
	}
}