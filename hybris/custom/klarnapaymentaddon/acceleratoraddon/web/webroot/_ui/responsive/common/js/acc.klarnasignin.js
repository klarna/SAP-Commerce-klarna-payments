window.onload = async function() {
	
	var clientId			= $("#clientId").val();
	var environment			= $("#environment").val();
	var currentLocale		= $("#currentLocale").val();
	
	const klarna = await Klarna.init({
		clientId:		clientId,
		environment:	environment,
		locale:			currentLocale
	});
	
	var scopeData			= $("#scopeData").val();
	var redirectUri			= $("#redirectUri").val();
	var hideOverlay			= $("#hideOverlay").val();
	var buttonTheme			= $("#buttonTheme").val();
	var buttonShape			= $("#buttonShape").val();
	var buttonLogoAlignment	= $("#buttonLogoAlignment").val();
	var country 			= $("#country").val();
	
	//scopeData = "offline_access openid profile:name profile:email profile:phone profile:billing_address";
	console.log("clientId "+clientId);
	console.log("environment "+environment);
	console.log("currentLocale "+currentLocale);
	console.log("scopeData "+scopeData);
	console.log("redirectUri "+redirectUri);
	console.log("hideOverlay "+hideOverlay);
	console.log("buttonTheme "+buttonTheme);
	console.log("buttonShape "+buttonShape);
	console.log("buttonLogoAlignment "+buttonLogoAlignment);
	console.log("market "+country);
	
	const siwkButton = klarna.Identity.button({
		scope:				scopeData,
		redirectUri:		redirectUri,
		interactionMode:	"DEVICE_BEST",
		hideOverlay:		hideOverlay,
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
		}).done(function(data, textStatus, jqXHR) {
		if(data != null){
		console.log("Profile status "+data);
			if(data=='CREATE_AFTER_CONSENT' || data=='MERGE_AFTER_CONSENT' ){
				ACC.signinConsent.showUserConsentPage(data);
			}
			else{
				window.location = ACC.config.encodedContextPath + data;
			}
		}
		else{
			console.log(" no data ",data);
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
ACC.signinConsent = {
	showUserConsentPage:function(data){
		var userConsentPageURL	=	$("#userConsentPageURL").val();
		window.location = userConsentPageURL+"?profileStatus="+data;
	},
};


