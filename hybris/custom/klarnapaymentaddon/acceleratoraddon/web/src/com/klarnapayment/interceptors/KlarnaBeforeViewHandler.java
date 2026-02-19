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


public class KlarnaBeforeViewHandler implements BeforeViewHandlerAdaptee
{
	private static final Logger LOG = Logger.getLogger(KlarnaBeforeViewHandler.class);

	private static final String KLARNA_CONFIG = "klarnaConfig";

	private static final String KLARNA_WEB_SDK_V1_URL = "klarnaWebSDKv1Url";
	private static final String KLARNA_WEB_SDK_V2_URL = "klarnaWebSDKv2Url";
	private static final String KLARNA_WEB_SDK_V1_URL_CONFIG_KEY = "klarna.web.sdk.v1.url";
	private static final String KLARNA_WEB_SDK_V2_URL_CONFIG_KEY = "klarna.web.sdk.v2.url";


	private static final String IS_KLARNA_EXP_CHECKOUT_ENABLED = "isKlarnaExpCheckoutEnabled";
	private static final String IS_KLARNA_SIGN_IN_ENABLED = "isKlarnaSignInEnabled";
	private static final String KLARNA_CLIENT_ID = "klarnaClientId";
	private static final String KLARNA_ENV = "klarnaEnv";
	private static final String KLARNA_LOCALE = "klarnaLocale";
	private static final String KLARNA_COUNTRY = "klarnaCountry";
	private static final String SCRIPT_URL_KEC = "scriptUrlKEC";
	private static final String SHOW_KEC_IN_PDP = "showKECInPDP";
	private static final String SHOW_KEC_IN_CART_PAGE = "showKECInCartPage";
	private static final String SHOW_KEC_IN_CART_POPUP = "showKECInCartPopup";
	private static final String KEC_BUTTON_THEME = "kecButtonTheme";
	private static final String KEC_BUTTON_SHAPE = "kecButtonShape";
	private static final String SCRIPT_URL_SIWK = "scriptUrlSIWK";
	private static final String SIWK_CONFIG_DATA = "siwkConfigData";
	private static final String SIWK_V1_ENABLED = "siwkV1Enabled";

	@Resource(name = "klarnaConfigFacade")
	private KlarnaConfigFacade klarnaConfigFacade;

	@Resource(name = "i18NService")
	private I18NService i18NService;

	@Resource(name = "commonI18NService")
	CommonI18NService commonI18NService;

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
		if (klarnaConfig != null && Boolean.TRUE.equals(klarnaConfig.getActive()))
		{
			setKlarnaModelAttributes(model, klarnaConfig);
		}

		if (klarnaConfig != null && klarnaConfig.getCredential() != null)
		{
			model.addAttribute(KLARNA_CLIENT_ID, klarnaConfig.getCredential().getClientId());
			model.addAttribute(KLARNA_ENV, klarnaConfig.getEnvironment());
			model.addAttribute(KLARNA_COUNTRY, klarnaConfig.getCredential().getMarketCountry());
			model.addAttribute(KLARNA_LOCALE,
					i18NService.getCurrentLocale() + "-" + klarnaConfig.getCredential().getMarketCountry());

			setKlarnaExpressCheckoutAttributes(model, klarnaConfig);

			setKlarnaSignInAttributes(model, klarnaConfig);
		}

		return viewName;
	}

	private void setKlarnaModelAttributes(final ModelMap model, final KlarnaConfigData klarnaConfig)
	{
		model.addAttribute(KLARNA_CONFIG, klarnaConfig);
		model.addAttribute(KLARNA_WEB_SDK_V1_URL,
				configurationService.getConfiguration().getString(KLARNA_WEB_SDK_V1_URL_CONFIG_KEY));
		model.addAttribute(KLARNA_WEB_SDK_V2_URL,
				configurationService.getConfiguration().getString(KLARNA_WEB_SDK_V2_URL_CONFIG_KEY));
		model.addAttribute(KLARNA_LOCALE, i18NService.getCurrentLocale() + "-" + klarnaConfig.getCredential().getMarketCountry());
	}

	private void setKlarnaExpressCheckoutAttributes(final ModelMap model, final KlarnaConfigData klarnaConfig)
	{
		if (klarnaConfig.getKecConfig() != null)
		{
			model.addAttribute(IS_KLARNA_EXP_CHECKOUT_ENABLED, Boolean.TRUE);
			model.addAttribute(SCRIPT_URL_KEC,
					configurationService.getConfiguration().getString(KlarnapaymentConstants.KLARNA_KEC_SCRIPT_URL));
			model.addAttribute(SHOW_KEC_IN_PDP, klarnaConfig.getKecConfig().getShowInPDPPage());
			model.addAttribute(SHOW_KEC_IN_CART_PAGE, klarnaConfig.getKecConfig().getShowInCartPage());
			model.addAttribute(SHOW_KEC_IN_CART_POPUP, klarnaConfig.getKecConfig().getShowInMiniCartPage());
			model.addAttribute(KEC_BUTTON_THEME, klarnaConfig.getKecConfig().getButtonTheme());
			model.addAttribute(KEC_BUTTON_SHAPE, klarnaConfig.getKecConfig().getButtonShape());
		}
		else
		{
			model.addAttribute(IS_KLARNA_EXP_CHECKOUT_ENABLED, Boolean.FALSE);
		}
	}

	private void setKlarnaSignInAttributes(final ModelMap model, final KlarnaConfigData klarnaConfig)
	{
		KlarnaSIWKConfigData siwkData = klarnaConfig.getSiwkConfig();
		if (siwkData != null)
		{
			model.addAttribute(IS_KLARNA_SIGN_IN_ENABLED, Boolean.TRUE);
			model.addAttribute(SIWK_CONFIG_DATA, siwkData);
			model.addAttribute(SCRIPT_URL_SIWK,
					configurationService.getConfiguration().getString(KlarnapaymentConstants.KLARNA_SIWK_SCRIPT_URL));
			model.addAttribute(SIWK_V1_ENABLED,
					configurationService.getConfiguration().getBoolean(KlarnapaymentConstants.KLARNA_SIWK_V1_ENABLED, Boolean.TRUE));
		}
		else
		{
			model.addAttribute(IS_KLARNA_SIGN_IN_ENABLED, Boolean.FALSE);
		}
	}

}
