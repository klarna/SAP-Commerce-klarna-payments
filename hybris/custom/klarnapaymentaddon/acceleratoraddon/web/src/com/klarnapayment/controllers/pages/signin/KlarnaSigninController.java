/**
 *
 */
package com.klarnapayment.controllers.pages.signin;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.klarna.api.signin.model.KlarnaSigninResponse;
import com.klarna.payment.enums.KlarnaSigninProfileStatus;
import com.klarna.payment.facades.KlarnaSignInFacade;
import com.klarnapayment.controllers.KlarnapaymentaddonControllerConstants;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;

/**
 * @author aloshni.kruba
 *
 */
@Controller
@RequestMapping(value = "/klarna/signin")
public class KlarnaSigninController extends AbstractPageController
{

	private static final Logger LOG = Logger.getLogger(KlarnaSigninController.class);

	@Resource(name = "klarnaSignInFacade")
	private KlarnaSignInFacade klarnaSignInFacade;

	@Resource(name = "cmsPageService")
	private CMSPageService cmsPageService;

	@Resource(name = "multiStepCheckoutBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "userService")
	private UserService userService;

	private static final String SIGN_IN_ERROR_MSG = "Login failed! Please check the Klarna user id or password is valid.";
	private static final String KLARNA_SIGNIN_CONSENT_PAGE = "KlarnaSigninRegisterPage";
	private static final String KLARNA_SIGNIN_REGISTER = "/klarna/signin/register";

	@RequestMapping(value = "/initiateSignIn", method = RequestMethod.POST)
	@ResponseBody
	public String initiateSignIn(@RequestBody
	final KlarnaSigninResponse klarnaSigninResponse, final HttpSession httpSession, final HttpServletRequest request,
			final HttpServletResponse response, final Model model)
	{
		StringBuffer requestUrl = request.getRequestURL();
		String regHeader = request.getHeader("Referer");
		sessionService.setAttribute("signInRefererPage", regHeader);
		sessionService.setAttribute("klarnaSigninResponse", klarnaSigninResponse);
		KlarnaSigninProfileStatus profileStatus = KlarnaSigninProfileStatus.LOGIN_FAILED;
		if (klarnaSigninResponse != null)
		{
			profileStatus = klarnaSignInFacade.checkAndUpdateProfile(klarnaSigninResponse);
			if (profileStatus.equals(KlarnaSigninProfileStatus.ACCOUNT_UPDATED))
			{
				// login the user
			}
		}
		return profileStatus.getValue();
	}

	@RequestMapping(value = "/userConsent", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showUserConsentPage(@RequestParam(name = "profileStatus")
	final String profileStatus, final Model model, final HttpServletRequest request, final HttpServletResponse response)
	{
		KlarnaSigninResponse klarnaSigninResponse = (KlarnaSigninResponse) sessionService.getCurrentSession()
				.getAttribute("klarnaSigninResponse");
		model.addAttribute("klarnaSigninResponse", klarnaSigninResponse);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				resourceBreadcrumbBuilder.getBreadcrumbs("klarna.signin.register.breadcrumb"));
		model.addAttribute("metaRobots", "noindex,nofollow");
		model.addAttribute("profileStatus", profileStatus);
		try
		{
			final ContentPageModel signinRegisterPage = getContentPageForLabelOrId(KLARNA_SIGNIN_CONSENT_PAGE);
			storeCmsPageInModel(model, signinRegisterPage);
			setUpMetaDataForContentPage(model, signinRegisterPage);
		}
		catch (CMSItemNotFoundException ex)
		{
			LOG.error(ex);
		}
		return new ModelAndView(KlarnapaymentaddonControllerConstants.Views.Pages.Signin.KlarnaSigninConsentPage);
		//return getViewForPage(model);
	}

	@RequestMapping(value = "/createNewCustomer", method = RequestMethod.POST)
	public String createNewCustomer(@ModelAttribute("klarnaSigninResponse")
	KlarnaSigninResponse klarnaSigninResponse, final Model model, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		LOG.info("klarnaSigninResponse " + klarnaSigninResponse);
		if (klarnaSigninResponse == null)
		{
			klarnaSigninResponse = (KlarnaSigninResponse) sessionService.getAttribute("klarnaSigninResponse");
		}
		try
		{
			klarnaSignInFacade.createNewCustomer(klarnaSigninResponse);
		}
		catch (Exception e)
		{
			LOG.error(e);
		}
		String prevPage = sessionService.getCurrentSession().getAttribute("signInRefererPage");
		LOG.info("signInRefererPage " + prevPage);
		return "redirect:" + prevPage;
	}

	@RequestMapping(value = "/updateCustomer", method = RequestMethod.POST)
	public String updateCustomer(@ModelAttribute("klarnaSigninResponse")
	KlarnaSigninResponse klarnaSigninResponse, final Model model, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		String prevPage = "";
		LOG.info("klarnaSigninResponse " + klarnaSigninResponse);
		if (klarnaSigninResponse == null)
		{
			klarnaSigninResponse = (KlarnaSigninResponse) sessionService.getAttribute("klarnaSigninResponse");
		}
		if (klarnaSigninResponse != null && klarnaSigninResponse.getUserAccountProfile() != null
				&& StringUtils.isNotEmpty(klarnaSigninResponse.getUserAccountProfile().getEmail()))
		{
			try
			{
				CustomerModel customer = (CustomerModel) userService
						.getUserForUID(klarnaSigninResponse.getUserAccountProfile().getEmail());
				klarnaSignInFacade.updateCustomer(customer, klarnaSigninResponse.getUserAccountProfile(),
						klarnaSigninResponse.getUserAccountLinking());
				prevPage = sessionService.getCurrentSession().getAttribute("signInRefererPage");
			}
			catch (Exception e)
			{
				LOG.error(e);
			}
		}
		LOG.info("signInRefererPage " + prevPage);
		return "redirect:" + prevPage;
	}

}
