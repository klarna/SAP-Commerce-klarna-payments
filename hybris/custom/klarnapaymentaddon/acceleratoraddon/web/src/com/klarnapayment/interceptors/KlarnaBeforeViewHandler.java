package com.klarnapayment.interceptors;

import de.hybris.platform.addonsupport.interceptors.BeforeViewHandlerAdaptee;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;

import com.klarna.data.KlarnaConfigData;
import com.klarna.data.KlarnaSIWKConfigData;
import com.klarna.payment.constants.KlarnapaymentConstants;
import com.klarna.payment.facades.KlarnaConfigFacade;
import com.klarna.payment.facades.KlarnaExpCheckoutFacade;
import com.klarna.payment.facades.KlarnaSignInFacade;


public class KlarnaBeforeViewHandler implements BeforeViewHandlerAdaptee
{
	private static final Logger LOG = Logger.getLogger(KlarnaBeforeViewHandler.class);

	private static final String IS_KLARNA_EXP_CHECKOUT_ENABLED = "isKlarnaExpCheckoutEnabled";
	private static final String KLARNA_CONFIG_DATA = "klarnaConfigData";
	private static final String CURRENT_LOCALE = "currentLocale";
	private static final String SCRIPT_URL_KEC = "scriptUrlKEC";
	private static final String SCRIPT_URL_SIWK = "scriptUrlSIWK";

	@Resource(name = "klarnaExpCheckoutFacade")
	private KlarnaExpCheckoutFacade klarnaExpCheckoutFacade;

	@Resource(name = "klarnaConfigFacade")
	private KlarnaConfigFacade klarnaConfigFacade;

	@Resource(name = "i18NService")
	private I18NService i18NService;

	@Resource(name = "commonI18NService")
	CommonI18NService commonI18NService;

	private static final String IS_KLARNA_SIGN_IN_ENABLED = "isKlarnaSignInEnabled";

	@Resource(name = "klarnaSignInFacade")
	private KlarnaSignInFacade klarnaSignInFacade;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Override
	public String beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model,
			final String viewName)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Inside KlarnaPaymentBeforeViewHandler");
		}
		final KlarnaConfigData klarnaConfig = klarnaConfigFacade.getKlarnaConfig();
		if (klarnaConfig != null)
		{
			setKlarnaCommonAttributes(model, klarnaConfig);

			setKlarnaExpressCheckoutAttributes(model, klarnaConfig);

			setKlarnaSignInAttributes(model, klarnaConfig);
		}

		return viewName;
	}

	/**
	 * @param model
	 * @param klarnaConfig
	 */
	private void setKlarnaCommonAttributes(final ModelMap model, final KlarnaConfigData klarnaConfig)
	{
		model.addAttribute(KLARNA_CONFIG_DATA, klarnaConfig);
		model.addAttribute(CURRENT_LOCALE, i18NService.getCurrentLocale() + "-" + klarnaConfig.getCredential().getMarketCountry());
	}

	private void setKlarnaExpressCheckoutAttributes(final ModelMap model, final KlarnaConfigData klarnaConfig)
	{
		// KecConfig and Credential will not be null if klarnaConfig is not null
		if (klarnaConfig != null && klarnaConfig.getKecConfig() != null)
		{
			model.addAttribute(IS_KLARNA_EXP_CHECKOUT_ENABLED, Boolean.TRUE);
			model.addAttribute(SCRIPT_URL_KEC,
					configurationService.getConfiguration().getString(KlarnapaymentConstants.KLARNA_KEC_SCRIPT_URL));
		}
		else
		{
			model.addAttribute(IS_KLARNA_EXP_CHECKOUT_ENABLED, Boolean.FALSE);
		}
	}

	private void setKlarnaSignInAttributes(final ModelMap model, final KlarnaConfigData klarnaConfig)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Inside setKlarnaSignInAttributes");
		}
		KlarnaSIWKConfigData siwkData = klarnaConfig.getSiwkConfig();
		// siwkData will not be null if it's active
		if (siwkData != null)
		{
			System.out.println("KlarnaSIWKConfigData " + siwkData.getActive() + " Scope Data  " + siwkData.getScopeData());
			model.addAttribute(IS_KLARNA_SIGN_IN_ENABLED, Boolean.TRUE);
			model.addAttribute(SCRIPT_URL_SIWK,
					configurationService.getConfiguration().getString(KlarnapaymentConstants.KLARNA_SIWK_SCRIPT_URL));
		}
		else
		{
			model.addAttribute(IS_KLARNA_SIGN_IN_ENABLED, Boolean.FALSE);
		}
	}

}
