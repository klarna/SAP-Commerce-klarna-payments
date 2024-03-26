package com.klarnapayment.interceptors;

import de.hybris.platform.addonsupport.interceptors.BeforeViewHandlerAdaptee;
import de.hybris.platform.servicelayer.i18n.I18NService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;

import com.klarna.payment.data.KlarnaConfigData;
import com.klarna.payment.data.KlarnaExpCheckoutConfigData;
import com.klarna.payment.data.KlarnaSignInConfigData;
import com.klarna.payment.facades.KPConfigFacade;
import com.klarna.payment.facades.KlarnaExpCheckoutFacade;
import com.klarna.payment.facades.KlarnaSignInFacade;


public class KlarnaBeforeViewHandler implements BeforeViewHandlerAdaptee
{
	private static final Logger LOG = Logger.getLogger(KlarnaBeforeViewHandler.class);

	private static final String IS_KLARNA_EXP_CHECKOUT_ENABLED = "isKlarnaExpCheckoutEnabled";
	private static final String KLARNA_EXP_CHECKOUT_CONFIG_DATA = "klarnaExpCheckoutConfigData";
	private static final String CURRENT_LOCALE = "currentLocale";

	@Resource(name = "klarnaExpCheckoutFacade")
	private KlarnaExpCheckoutFacade klarnaExpCheckoutFacade;

	@Resource(name = "kpConfigFacade")
	private KPConfigFacade kpConfigFacade;

	@Resource(name = "i18NService")
	private I18NService i18NService;

	private static final String IS_KLARNA_SIGN_IN_ENABLED = "isKlarnaSignInEnabled";
	private static final String KLARNA_SIGN_IN_CONFIG_DATA = "klarnaSignInConfigData";

	@Resource(name = "klarnaSignInFacade")
	private KlarnaSignInFacade klarnaSignInFacade;

	@Override
	public String beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model,
			final String viewName)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Inside KlarnaPaymentBeforeViewHandler");
		}

		setKlarnaExpressCheckoutAttributes(model);

		setKlarnaSignInAttributes(model);

		return viewName;
	}

	private void setKlarnaExpressCheckoutAttributes(final ModelMap model)
	{
		if (kpConfigFacade != null && klarnaExpCheckoutFacade != null)
		{
			// Enabling Klarna Payment is a prerequisite  for enabling Klarna Express Checkou
			final KlarnaConfigData klarnaConfig = kpConfigFacade.getKlarnaConfig();
			if (klarnaConfig != null && BooleanUtils.isTrue(klarnaConfig.getActive()))
			{
				KlarnaExpCheckoutConfigData klarnaExpCheckoutConfigData = klarnaExpCheckoutFacade.getKlarnaExpCheckoutConfigData();
				if (klarnaExpCheckoutConfigData != null && BooleanUtils.isTrue(klarnaExpCheckoutConfigData.getActive()))
				{
					model.addAttribute(IS_KLARNA_EXP_CHECKOUT_ENABLED, Boolean.TRUE);
					model.addAttribute(KLARNA_EXP_CHECKOUT_CONFIG_DATA, klarnaExpCheckoutConfigData);
					model.addAttribute(CURRENT_LOCALE,
							i18NService.getCurrentLocale() + "-" + klarnaExpCheckoutConfigData.getCountry());
				}
				else
				{
					model.addAttribute(IS_KLARNA_EXP_CHECKOUT_ENABLED, Boolean.FALSE);
				}
			}
			else
			{
				model.addAttribute(IS_KLARNA_EXP_CHECKOUT_ENABLED, Boolean.FALSE);
			}
		}
	}

	private void setKlarnaSignInAttributes(final ModelMap model)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Inside setKlarnaSignInAttributes");
		}
		if (klarnaSignInFacade != null)
		{
			KlarnaSignInConfigData klarnaSignInConfigData = klarnaSignInFacade.getKlarnaSignInConfigData();
			if (klarnaSignInConfigData != null && klarnaSignInConfigData.getActive().booleanValue())
			{
				System.out.println("klarnaSignInConfigData " + klarnaSignInConfigData.getActive() + "script url "
						+ klarnaSignInConfigData.getScriptUrl() + " Scope Data  " + klarnaSignInConfigData.getScopeData());
				model.addAttribute(IS_KLARNA_SIGN_IN_ENABLED, Boolean.TRUE);
				model.addAttribute(KLARNA_SIGN_IN_CONFIG_DATA, klarnaSignInConfigData);
				model.addAttribute(CURRENT_LOCALE, i18NService.getCurrentLocale() + "-" + klarnaSignInConfigData.getCountry());
			}
			else
			{
				model.addAttribute(IS_KLARNA_SIGN_IN_ENABLED, Boolean.FALSE);
			}
		}
	}

}
