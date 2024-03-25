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

import com.klarna.api.signin.model.KlarnaSigninResponse;
import com.klarna.payment.data.KlarnaSignInConfigData;
import com.klarna.payment.facades.KlarnaSignInFacade;


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

	@RequestMapping(value = "/process", method = RequestMethod.POST)

	@ResponseBody
	public String processAuthorizeResponse(@RequestBody
	final KlarnaSigninResponse klarnaSigninResponse, final HttpSession httpSession, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		if (klarnaSigninResponse == null)
		{
			showError(SIGN_IN_ERROR_MSG, httpSession, request, response);
		}
		KlarnaSignInConfigData signinConfig = klarnaSignInFacade.getKlarnaSignInConfigData();
		klarnaSignInFacade.processCustomer(klarnaSigninResponse);
		return StringUtils.isNotEmpty(signinConfig.getRedirectUri()) ? signinConfig.getRedirectUri() : "";
	}

	@RequestMapping(value = "/error", method = RequestMethod.POST)
	public String showError(@RequestParam
	final String errorResponse, final HttpSession httpSession, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		KlarnaSignInConfigData signinConfig = klarnaSignInFacade.getKlarnaSignInConfigData();
		request.setAttribute(KLARNA_SIGNIN_ERROR, StringUtils.isNotBlank(errorResponse) ? errorResponse : SIGN_IN_ERROR_MSG);
		return StringUtils.isNotEmpty(signinConfig.getRedirectUri()) ? signinConfig.getRedirectUri() : "";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String loginUser(@RequestParam
	final String errorResponse, final HttpSession httpSession, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		KlarnaSignInConfigData signinConfig = klarnaSignInFacade.getKlarnaSignInConfigData();
		request.setAttribute(KLARNA_SIGNIN_ERROR, errorResponse);
		return StringUtils.isNotEmpty(signinConfig.getRedirectUri()) ? signinConfig.getRedirectUri() : "";
	}

}
