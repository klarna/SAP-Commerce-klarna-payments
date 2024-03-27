/**
 *
 */
package com.klarnapayment.controllers.pages.signin;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.klarna.api.signin.model.KlarnaSigninResponse;
import com.klarna.payment.data.KlarnaSignInConfigData;
import com.klarna.payment.enums.KlarnaSigninProfileStatus;
import com.klarna.payment.facades.KlarnaSignInFacade;
import com.klarnapayment.controllers.KlarnapaymentaddonControllerConstants;


/**
 * @author aloshni.kruba
 *
 */
@Controller
@RequestMapping(value = "/klarna/signin")
public class KlarnaSigninController extends AbstractPageController
{
	@Resource(name = "klarnaSignInFacade")
	private KlarnaSignInFacade klarnaSignInFacade;
	private static final String KLARNA_SIGNIN_ERROR = "KLARNA_SIGNIN_ERROR";
	private static final String SIGN_IN_ERROR_MSG = "Login failed! Please check the Klarna user id or password is valid.";

	@RequestMapping(value = "/checkprofile", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView processAuthorizeResponse(@RequestBody
	final KlarnaSigninResponse klarnaSigninResponse, final HttpSession httpSession, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		KlarnaSignInConfigData signinConfig = klarnaSignInFacade.getKlarnaSignInConfigData();
		Boolean mergeEnabled = Boolean.FALSE;
		if (signinConfig != null && signinConfig.getAutoMergeAccounts() != null)
		{
			mergeEnabled = signinConfig.getAutoMergeAccounts();
		}

		if (klarnaSigninResponse == null)
		{
			showError(SIGN_IN_ERROR_MSG, httpSession, request, response);
		}
		KlarnaSigninProfileStatus profileStatus = klarnaSignInFacade.checkUserProfileStatus(klarnaSigninResponse);
		request.setAttribute("KlarnaSigninProfileStatus", profileStatus.getValue());
		request.setAttribute("klarnaSigninResponse", klarnaSigninResponse);
		request.setAttribute("mergeEnabled", mergeEnabled);
		return new ModelAndView(KlarnapaymentaddonControllerConstants.Views.Pages.Signin.KlarnaSigninRegisterPage);
	}

	@RequestMapping(value = "/error", method = RequestMethod.POST)
	public String showError(@RequestParam
	final String errorResponse, final HttpSession httpSession, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		KlarnaSignInConfigData signinConfig = klarnaSignInFacade.getKlarnaSignInConfigData();
		request.setAttribute(KLARNA_SIGNIN_ERROR, StringUtils.isNotBlank(errorResponse) ? errorResponse : SIGN_IN_ERROR_MSG);
		return KlarnapaymentaddonControllerConstants.Views.Pages.Signin.KlarnaSigninRegisterPage;
	}

	@RequestMapping(value = "/process-signin", method = RequestMethod.POST)
	public String loginUser(@RequestBody
	final KlarnaSigninResponse klarnaSigninResponse, @RequestParam(name = "profileStatus")
	final String profileStatus, final HttpSession httpSession, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		klarnaSignInFacade.processCustomer(profileStatus, klarnaSigninResponse);
		//request.setAttribute(KLARNA_SIGNIN_ERROR, errorResponse);
		return StringUtils.isNotEmpty(klarnaSignInFacade.getRedirectURI()) ? klarnaSignInFacade.getRedirectURI() : "/login";
	}

}
