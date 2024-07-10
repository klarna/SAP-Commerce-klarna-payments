window.onload = async function() {
	
	// placement Flags
	var currentURL = window.location.href;
	var showInLoginPage = $("#showSIWKInLoginPage").val();
	var showInRegisterPage = $("#showSIWKInRegisterPage").val();
	var showInCheckoutLoginPage = $("#showSIWKInCheckoutLoginPage").val();
	var showSignInButton = false;
	if(currentURL.endsWith("/login/checkout") && (showInCheckoutLoginPage == "true") )
	{
		showSignInButton = true;
	}
	else if(currentURL.endsWith("/login") && (showInLoginPage == "true" || showInRegisterPage == "true") )
	{
		showSignInButton = true;
	}
	
	if(showSignInButton){
	var clientId			= $("#klarnaClientId").val();
	var environment			= $("#klarnaEnv").val();
	var klarnaLocale		= $("#klarnaLocale").val();
	
	const klarna = await Klarna.init({
		clientId:		clientId,
		environment:	environment,
		locale:			klarnaLocale
	});
	
	var scopeData			= $("#siwkScopeData").val();
	var redirectUri			= $("#siwkRedirectUri").val();
	var buttonTheme			= $("#siwkButtonTheme").val();
	var buttonShape			= $("#siwkButtonShape").val();
	var buttonLogoAlignment	= $("#siwkButtonLogoAlignment").val();
	var klarnaCountry 			= $("#klarnaCountry").val();
	
	const siwkButton = klarna.Identity.button({
		scope:				scopeData,
		redirectUri:		redirectUri,
		interactionMode:	"DEVICE_BEST",
		hideOverlay:		false,
		shape:				buttonShape,
		theme:				buttonTheme,
		logoAlignment:		buttonLogoAlignment,
		market:				country
	})
	siwkButton.mount("#klarna-signin-container");

	//klarna.Identity.handleRedirect();
	
	klarna.Identity.on("signin", 
		(data) => {
		ACC.signin.initiateSignInResponse(data);
		},
		(error) =>{
		var message = $('#signinErrHidden').val();
		ACC.signin.showErrorMessage(message);
		console.log("signin error" + JSON.stringify(error));
		});
		
	klarna.Identity.on("error", (data) => {
		var message = $('#signinErrHidden').val();
		ACC.signin.showErrorMessage(message);
		console.log("error " + JSON.stringify(data));
		});
	}
};
ACC.signin = {
	initiateSignInResponse: function (authResponse){
	console.log("initiateSignInResponse"+authResponse);
	var initiateSignInResponseUrl = $("#initiateSignInResponseUrl").val();
		$.ajax(initiateSignInResponseUrl, {
		        data: JSON.stringify(authResponse),
		        dataType: "json",
				contentType: 'application/json',
		        type: "post"
		}).done(function(url) {
		if(url != null){
		//alert("redirecting now  "+url);
		ACC.signinInitiate.loginRedirect(url);
		}
		else{
			console.log(" no data ",url);
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
};
ACC.signinInitiate = {
	loginRedirect:function(data){
			window.location = ACC.config.encodedContextPath + data;
	},
};