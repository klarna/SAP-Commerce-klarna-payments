	<script>
/* 	  document.addEventListener("DOMContentLoaded", async () => {
		try {
			const { KlarnaSDK } = await import("https://js.klarna.com/web-sdk/v2/klarna.mjs");
	
	  		const Klarna = await KlarnaSDK({
	    		clientId: $("#klarnaClientId").val(),
	    		products: ["IDENTITY", "MESSAGING", "CUSTOMER", "PAYMENT"]
	  		});

		  	var osmPayload = {
			        		key: $("#datakey").val(),
			        		locale: $("#klarnaLocale").val(),
			        		theme: $("#theme").val(),
			        		amount:  $("#purchaseAmount").val()
			    		};
			
			Klarna.Messaging.placement(osmPayload).mount(".#kosm_prod");
		}catch (e) {
	        console.error(e);
	    }
	  }); */
	</script>