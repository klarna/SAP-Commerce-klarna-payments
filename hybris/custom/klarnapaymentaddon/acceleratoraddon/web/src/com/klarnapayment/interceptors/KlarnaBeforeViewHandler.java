package com.klarnapayment.interceptors;

import de.hybris.platform.addonsupport.interceptors.BeforeViewHandlerAdaptee;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;

import com.klarna.data.KlarnaConfigData;
import com.klarna.data.KlarnaKECConfigData;
import com.klarna.data.KlarnaSIWKConfigData;
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

	@Resource(name = "commonI18NService")
	CommonI18NService commonI18NService;

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
				KlarnaKECConfigData klarnaKECConfigData = klarnaConfig.getKecConfig();
				if (klarnaKECConfigData != null && BooleanUtils.isTrue(klarnaKECConfigData.getActive()))
				{
					model.addAttribute(IS_KLARNA_EXP_CHECKOUT_ENABLED, Boolean.TRUE);
					model.addAttribute(KLARNA_EXP_CHECKOUT_CONFIG_DATA, klarnaKECConfigData);
					model.addAttribute(CURRENT_LOCALE,
							i18NService.getCurrentLocale() + "-" + commonI18NService.getCurrentCurrency().getIsocode());
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
		final KlarnaConfigData klarnaConfig = kpConfigFacade.getKlarnaConfig();
		if (klarnaConfig != null)
		{
			KlarnaSIWKConfigData siwkData = klarnaConfig.getSiwkConfig();
			if (siwkData != null && Boolean.TRUE.equals(siwkData.getActive()))
			{
				System.out.println("KlarnaSIWKConfigData " + siwkData.getActive() + " Scope Data  " + siwkData.getScopeData());
				model.addAttribute(IS_KLARNA_SIGN_IN_ENABLED, Boolean.TRUE);
				model.addAttribute(KLARNA_SIGN_IN_CONFIG_DATA, siwkData);
				model.addAttribute(CURRENT_LOCALE,
						i18NService.getCurrentLocale() + "-" + commonI18NService.getCurrentCurrency().getIsocode());
			}
			else
			{
				model.addAttribute(IS_KLARNA_SIGN_IN_ENABLED, Boolean.FALSE);
			}
		}
	}

}
