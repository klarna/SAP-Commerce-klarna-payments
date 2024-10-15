/**
 *
 */
package com.klarnapayment.controllers.pages.signin;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.acceleratorstorefrontcommons.strategy.CartRestorationStrategy;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.klarna.api.signin.model.KlarnaSigninResponse;
import com.klarna.data.KlarnaConfigData;
import com.klarna.payment.enums.KlarnaSigninProfileStatus;
import com.klarna.payment.facades.KlarnaConfigFacade;
import com.klarna.payment.facades.KlarnaSignInFacade;
import com.klarnapayment.controllers.KlarnapaymentaddonControllerConstants;
import com.klarnapayment.strategy.impl.DefaultKlarnaSignInLoginStrategy;


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

	@Resource(name = "guidCookieStrategy")
	private GUIDCookieStrategy guidCookieStrategy;

	@Resource(name = "cartRestorationStrategy")
	private CartRestorationStrategy cartRestorationStrategy;

	@Resource(name = "klarnaAutoLoginStrategy")
	private DefaultKlarnaSignInLoginStrategy klarnaAutoLoginStrategy;

	@Resource(name = "klarnaConfigFacade")
	private KlarnaConfigFacade klarnaConfigFacade;

	private static final String SIGN_IN_ERROR_MSG = "Login failed! Please check the Klarna user id or password is valid.";
	private static final String KLARNA_SIGNIN_CONSENT_PAGE = "KlarnaSigninRegisterPage";
	private static final String KLARNA_SIGNIN_CONSENT_URL = "/klarna/signin/consent";
	private static final String REQ_PARAM_PROFILE_STATUS = "?profileStatus=";
	private static final String LOGIN_URL = "/login";
	private static final String CHECKOUT_URL = "/checkout/multi/delivery-address/add";
	private static final String CHECKOUT_LOGIN_URL = "/checkout/login";
	private static final String FORWARD_SLASH = "/";

	@RequestMapping(value = "/initiate", method = RequestMethod.POST)
	@ResponseBody
	public String initiateSignIn(@RequestBody
	final KlarnaSigninResponse klarnaSigninResponse, final HttpSession httpSession, final HttpServletRequest request,
			final HttpServletResponse response, final Model model, final RedirectAttributes redirectAttr)
	{
		StringBuffer requestUrl = request.getRequestURL();
		String prevPage = request.getHeader("Referer");
		sessionService.getCurrentSession().setAttribute("signInRefererPage", prevPage);
		sessionService.getCurrentSession().setAttribute("klarnaSigninResponse", klarnaSigninResponse);
		model.addAttribute("klarnaSigninResponse", klarnaSigninResponse);

		final KlarnaConfigData klarnaConfig = klarnaConfigFacade.getKlarnaConfig();
		if(klarnaConfig != null && StringUtils.isNotBlank(klarnaConfig.getEnvironment()))
		{
			KlarnaSigninProfileStatus profileStatus = KlarnaSigninProfileStatus.LOGIN_FAILED;
			if (klarnaSigninResponse != null)
			{
				if(klarnaSignInFacade.validateSigninToken(klarnaSigninResponse, klarnaConfig.getEnvironment())) {
   				String redirectURL = "";
   				profileStatus = klarnaSignInFacade.checkAndUpdateProfile(klarnaSigninResponse);
   				if (profileStatus.equals(KlarnaSigninProfileStatus.ACCOUNT_UPDATED)
   						&& klarnaSigninResponse.getUserAccountProfile() != null)
   				{
   					redirectURL = authenticateAndLogin(klarnaSigninResponse.getUserAccountProfile().getEmail(), request, response,
   							redirectAttr, prevPage);
   					return redirectURL;
   				}
   				else if (profileStatus.equals(KlarnaSigninProfileStatus.LOGIN_FAILED))
   				{
   					redirectURL = getRedirectURlOnError(prevPage);
   					GlobalMessages.addFlashMessage(redirectAttr, GlobalMessages.ERROR_MESSAGES_HOLDER, "klarna.signin.error");
   					return redirectURL;
   				}
   				else
   				{
   					return KLARNA_SIGNIN_CONSENT_URL + REQ_PARAM_PROFILE_STATUS + profileStatus;
   				}
				}
			}
		}

		return LOGIN_URL;
	}

	@RequestMapping(value = "/consent", method = RequestMethod.GET)
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
		return new ModelAndView(KlarnapaymentaddonControllerConstants.Views.Pages.Signin.KlarnaSigninConsentPage,
				"klarnaSigninResponse", klarnaSigninResponse);
	}

	@RequestMapping(value = "/create-customer", method = RequestMethod.POST)
	public String createCustomer(@ModelAttribute("klarnaSigninResponse")
	KlarnaSigninResponse klarnaSigninResponse, final Model model, final HttpServletRequest request,
			final HttpServletResponse response, final RedirectAttributes redirectAttr)
	{
		String prevPage = sessionService.getAttribute("signInRefererPage");
		String reqMapping = LOGIN_URL;
		LOG.info(" createCustomer - klarnaSigninResponse " + klarnaSigninResponse);
		LOG.info(" createCustomer - signInRefererPage " + prevPage);

		klarnaSigninResponse = (KlarnaSigninResponse) sessionService.getAttribute("klarnaSigninResponse");
		try
		{
			klarnaSignInFacade.createNewCustomer(klarnaSigninResponse);
			GlobalMessages.addFlashMessage(redirectAttr, GlobalMessages.INFO_MESSAGES_HOLDER,
					"klarna.signin.create.customer.success");
		}
		catch (Exception e)
		{
			LOG.error(e);
			reqMapping = getRedirectURlOnError(prevPage);
			GlobalMessages.addFlashMessage(redirectAttr, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"klarna.signin.create.customer.failure");
		}
		return REDIRECT_PREFIX + reqMapping;
	}

	@RequestMapping(value = "/merge-account", method = RequestMethod.POST)
	public String mergeAccount(@ModelAttribute("klarnaSigninResponse")
	KlarnaSigninResponse klarnaSigninResponse, final Model model, final HttpServletRequest request,
			final HttpServletResponse response, final RedirectAttributes redirectAttr)
	{
		String prevPage = sessionService.getAttribute("signInRefererPage");
		String reqMapping = LOGIN_URL;
		LOG.info(" mergeAccount - klarnaSigninResponse " + klarnaSigninResponse);
		LOG.info(" mergeAccount - signInRefererPage " + prevPage);

		klarnaSigninResponse = (KlarnaSigninResponse) sessionService.getAttribute("klarnaSigninResponse");
		if (klarnaSigninResponse != null && klarnaSigninResponse.getUserAccountProfile() != null
				&& StringUtils.isNotEmpty(klarnaSigninResponse.getUserAccountProfile().getEmail()))
		{
			try
			{
				CustomerModel customer = (CustomerModel) userService
						.getUserForUID(klarnaSigninResponse.getUserAccountProfile().getEmail());
				klarnaSignInFacade.updateCustomer(customer, klarnaSigninResponse.getUserAccountProfile(),
						klarnaSigninResponse.getUserAccountLinking());
				GlobalMessages.addFlashMessage(redirectAttr, GlobalMessages.INFO_MESSAGES_HOLDER,
						"klarna.signin.merge.account.success");
			}
			catch (Exception e)
			{
				LOG.error(e);
				reqMapping = getRedirectURlOnError(prevPage);
				GlobalMessages.addFlashMessage(redirectAttr, GlobalMessages.ERROR_MESSAGES_HOLDER,
						"klarna.signin.merge.account.failure");
			}
		}
		return REDIRECT_PREFIX + reqMapping;
	}

	String authenticateAndLogin(final String emailId, final HttpServletRequest request, final HttpServletResponse response,
			final RedirectAttributes redirectAttr, final String prevPage)
	{
		String redirectURL = "";
		boolean loginSuccessful = true;
		// login the user
		try
		{
			klarnaAutoLoginStrategy.login(emailId, null, request, response);
		}
		catch (Exception e)
		{
			LOG.error("Error while loggin in the USER " + emailId + e.getMessage(), e);
			loginSuccessful = false;
		}
		if (loginSuccessful)
		{
			if (StringUtils.isNotEmpty(prevPage) && prevPage.endsWith(CHECKOUT_LOGIN_URL))
			{
				redirectURL = CHECKOUT_URL;
			}
			else if (StringUtils.isNotEmpty(prevPage) && prevPage.endsWith(LOGIN_URL))
			{
				redirectURL = FORWARD_SLASH;
			}
			GlobalMessages.addFlashMessage(redirectAttr, GlobalMessages.INFO_MESSAGES_HOLDER, "klarna.signin.success");
		}
		else
		{
			redirectURL = getRedirectURlOnError(prevPage);
			GlobalMessages.addFlashMessage(redirectAttr, GlobalMessages.ERROR_MESSAGES_HOLDER, "klarna.signin.failure");
		}
		return redirectURL;
	}

	String getRedirectURlOnError(final String prevPage)
	{
		String redirectURL = "";

		if (StringUtils.isNotEmpty(prevPage) && (prevPage.contains(LOGIN_URL + FORWARD_SLASH) || prevPage.endsWith(LOGIN_URL)))
		{
			redirectURL = LOGIN_URL;
		}
		else if (StringUtils.isNotEmpty(prevPage)
				&& (prevPage.contains(CHECKOUT_URL + FORWARD_SLASH) || prevPage.endsWith(CHECKOUT_LOGIN_URL)))
		{
			redirectURL = CHECKOUT_LOGIN_URL;
		}
		return redirectURL;
	}
}
