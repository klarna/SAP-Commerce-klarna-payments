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
		window.location = ACC.config.encodedContextPath + data;
		},
		(error) =>{
		console.log("signin " + JSON.stringify(error));
		});
	if(!signInSuccess){
		klarna.Identity.on("error", (data) => {
		// implement logic
		console.log("error " + JSON.stringify(data));
		ACC.signin.processSignInResponse();
		});
	}
};
ACC.signin = {
	processSignInResponse: function (){
		console.log("processSignInResponse");
		var processSigninURL = $("#processSigninResponseUrl").val();
		var authResponse = {
			"user_account_profile": 
			{	"user_id": "aloshni-rrrrrrr@tryzens.com"	,
				"given_name": "Aloshni",
				"family_name": "Kruba",
				"email": "aloshni-rrrrrrr@tryzens.com",
				"phone": "9646962364"
			},
			"user_account_linking":
			{	"user_account_linking_refresh_token": "2222222223333333",
				"user_account_linking_id_token": "sssss33333333333333"
			}
		};
		$.ajax(processSigninURL, {
		        data: JSON.stringify(authResponse),
		        dataType: "json",
				contentType: 'application/json',
		        type: "post"
		}).done(function(data, textStatus, jqXHR) {
		if(data != null){
		//window.location = ACC.config.encodedContextPath + data;
		console.log("Profile status "+data);
			if(data=='CREATE_AFTER_CONSENT' || data=='MERGE_AFTER_CONSENT' ){
				document.getElementById("profileStatus").value = data;
				showRegisterPage(data);
			}
		}
		else{
			console.log(" no data ",data);
		} 
		}).fail(function (error) {
		console.log("Authorize reponse error:", JSON.stringify(error));
		});
	},
	showRegisterPage:function(){
		var signinRegisterPageURL	=	$("#signinRegisterPageURL").val();
		$.ajax({
		  url: signinRegisterPageURL,
		  type: "get",
		  data: { 
			profileStatus: profileStatus, 
		  },
		success: function(response) {
		    console.log("response "+response);
		},
		error: function(xhr) {
		}
		});
	},
};
