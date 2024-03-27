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
	
	const siwkButton = klarna.Identity.button({
		scope:				"openid offline_access",
		redirectUri:		redirectUri,
		interactionMode:	"DEVICE_BEST",
		hideOverlay:		hideOverlay,
		shape:				buttonShape,
		theme:				buttonTheme,
		logoAlignment:		buttonLogoAlignment
	})
	siwkButton.mount("#klarna-signin-container");

	//klarna.Identity.handleRedirect();
	var signInSuccess = false;
	klarna.Identity.on("signin", 
		(data) => {
		// implement logic
		console.log("signin " + JSON.stringify(data));
		},
		(error) =>{
		console.log("signin " + JSON.stringify(error));
		});
	if(!signInSuccess){
		klarna.Identity.on("error", (data) => {
		// implement logic
		console.log("error " + JSON.stringify(data));
		});
	}
}

