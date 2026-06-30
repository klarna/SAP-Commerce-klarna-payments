
ACC.klarnasignin = {
	initiateSigninButton: function (klarna) {
		console.debug("Entering initiateSigninButton")
		ACC.klarnasignin.initiateSigninData(klarna);
	},
	initiateSigninData: function (klarna) {
		var scopeData			= $("#siwkScopeData").val();
		var redirectUri			= $("#siwkRedirectUri").val();
		var buttonTheme			= $("#siwkButtonTheme").val();
		var buttonShape			= $("#siwkButtonShape").val();
		var buttonLogoAlignment	= $("#siwkButtonLogoAlignment").val();
		var klarnaCountry 			= $("#klarnaCountry").val();

		const siwkButton = klarna.Identity.button({
			scope:				scopeData+" customer:login",
			redirectUri:		redirectUri,
			interactionMode:	"DEVICE_BEST",
			shape:				buttonShape,
			theme:				buttonTheme,
			logoAlignment:		buttonLogoAlignment,
			market:				klarnaCountry,
		})
		siwkButton.mount("#klarna-signin-container");

		//klarna.Identity.handleRedirect();

		klarna.Identity.on("signin", (data) => {
			console.log("Signin success response:", JSON.stringify(data));
			ACC.klarnasignin.initiateSignInResponse(data);
			},
			(error) =>{
			var message = $('#signinErrHidden').val();
			ACC.klarnasignin.showErrorMessage(message);
			console.log("signin error" + JSON.stringify(error));
		});

		klarna.Identity.on("error", (data) => {
			console.debug("Signin error response:", JSON.stringify(data));
			var message = $('#signinErrHidden').val();
			ACC.klarnasignin.showErrorMessage(message);
			console.log("error " + JSON.stringify(data));
		});
		
	
	},
	initiateSignInResponse: function (authResponse){
	console.debug("initiateSignInResponse"+authResponse);
	var initiateSignInResponseUrl = $("#initiateSignInResponseUrl").val();
		$.ajax(initiateSignInResponseUrl, {
		        data: JSON.stringify(authResponse),
		        dataType: "json",
				contentType: 'application/json',
		        type: "post"
		}).done(function(url) {
		if(url != null){
			ACC.klarnasignin.loginRedirect(url);
		}
		else{
			var message = $('#signinErrHidden').val();
			ACC.klarnasignin.showErrorMessage(message);
		}
		}).fail(function (error) {
		console.log("Authorize reponse error:", JSON.stringify(error));
		});
	},
	showErrorMessage: function(message) {
		console.log(message);
		ACC.colorbox.open(message,{
	        html : $(document).find("#klarna-signin-err").html(),
	        maxWidth:"100%",
	        width:"420px",
	        initialWidth :"420px",
	        height:"300px"
	  	});
	},
	loginRedirect:function(redirectURL){
		let currentURL = window.location.href ;
		if(currentURL && currentURL.endsWith("/login/checkout"))
		{
			redirectURL = "/checkout";
		}
		window.location = ACC.config.encodedContextPath + redirectURL;
	}
};