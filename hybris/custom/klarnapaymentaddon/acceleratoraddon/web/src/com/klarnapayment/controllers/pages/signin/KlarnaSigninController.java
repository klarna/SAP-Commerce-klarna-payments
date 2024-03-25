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

	@RequestMapping(value = "/process", method = RequestMethod.POST)
	public String processAuthorizeResponse(@RequestBody
	final KlarnaSigninResponse klarnaSigninResponse, final HttpSession httpSession, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		KlarnaSignInConfigData signinConfig = klarnaSignInFacade.getKlarnaSignInConfigData();
		klarnaSignInFacade.processCustomer(klarnaSigninResponse);
		return StringUtils.isNotEmpty(signinConfig.getRedirectUri()) ? signinConfig.getRedirectUri() : "";
	}

}
