/**
 *
 */
package com.klarnapayment.controllers.pages.signin;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

	private static final String SIGN_IN_ERROR_MSG = "Login failed! Please check the Klarna user id or password is valid.";
	private static final String KLARNA_SIGNIN_PAGE = "KlarnaSigninRegisterPage";
	private static final String KLARNA_SIGNIN_REGISTER = "/klarna/signin/register";

	@RequestMapping(value = "/checkprofile", method = RequestMethod.POST)
	@ResponseBody
	public String processAuthorizeResponse(@RequestBody
	final KlarnaSigninResponse klarnaSigninResponse, final HttpSession httpSession, final HttpServletRequest request,
			final HttpServletResponse response, final Model model)
	{
		Session session = sessionService.getCurrentSession();

		StringBuffer requestUrl = request.getRequestURL();
		if (requestUrl != null && StringUtils.isNotEmpty(requestUrl.toString()))
		{
			session.setAttribute("pervious_page", requestUrl.toString());
		}
		else
		{
			String regHeader = request.getHeader("Referer");
			session.setAttribute("pervious_page", regHeader);
		}
		session.setAttribute("klarnaSigninResponse", klarnaSigninResponse);
		KlarnaSigninProfileStatus profileStatus = KlarnaSigninProfileStatus.LOGIN_FAILED;
		if (klarnaSigninResponse != null)
		{
			profileStatus = klarnaSignInFacade.checkUserProfileStatus(klarnaSigninResponse);
			if (profileStatus.equals(KlarnaSigninProfileStatus.MERGE_AUTO))
			{
				klarnaSignInFacade.processCustomer(profileStatus.getValue(), klarnaSigninResponse);
			}
		}
		return profileStatus.getValue();
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView showSigninPage(@RequestParam(name = "profileStatus")
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
			final ContentPageModel signinRegisterPage = getContentPageForLabelOrId(KLARNA_SIGNIN_PAGE);
			storeCmsPageInModel(model, signinRegisterPage);
			setUpMetaDataForContentPage(model, signinRegisterPage);
		}
		catch (CMSItemNotFoundException ex)
		{
			LOG.error(ex);
		}
		return new ModelAndView(KlarnapaymentaddonControllerConstants.Views.Pages.Signin.KlarnaSigninRegisterPage,
				"klarnaSigninResponse", klarnaSigninResponse);
		//return getViewForPage(model);
	}

	@RequestMapping(value = "/process-signin", method = RequestMethod.GET)
	public String processSignin(@RequestParam(name = "profileStatus")
	final String profileStatus, final Model model, final HttpServletRequest request, final HttpServletResponse response)
	{
		KlarnaSigninResponse klarnaSigninResponse = (KlarnaSigninResponse) sessionService.getCurrentSession()
				.getAttribute("klarnaSigninResponse");
		try
		{
			klarnaSignInFacade.processCustomer(profileStatus, klarnaSigninResponse);
		}
		catch (Exception e)
		{
			LOG.error(e);
		}
		String prevPage = sessionService.getCurrentSession().getAttribute("pervious_page");
		LOG.info("prevPage " + prevPage);
		return prevPage;
	}

}
