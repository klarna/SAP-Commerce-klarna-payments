
package com.klarna.osm.interceptor;

import de.hybris.platform.addonsupport.interceptors.BeforeViewHandlerAdaptee;
import de.hybris.platform.servicelayer.i18n.I18NService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;

import com.klarna.data.KlarnaOSMConfigData;
import com.klarna.osm.facade.KlarnaOSMConfigFacade;


public class KlarnaOSMUiThemeResourceBeforeViewHandlerAdaptee implements BeforeViewHandlerAdaptee
{
	private static final Logger LOG = Logger.getLogger(KlarnaOSMUiThemeResourceBeforeViewHandlerAdaptee.class);

	KlarnaOSMConfigFacade klarnaOSMConfigFacade;
	@Resource(name = "i18NService")
	private I18NService i18NService;

	/**
	 * @return the klarnaOSMConfigFacade
	 */
	public KlarnaOSMConfigFacade getKlarnaOSMConfigFacade()
	{
		return klarnaOSMConfigFacade;
	}

	/**
	 * @param klarnaOSMConfigFacade
	 *           the klarnaOSMConfigFacade to set
	 */
	public void setKlarnaOSMConfigFacade(final KlarnaOSMConfigFacade klarnaOSMConfigFacade)
	{
		this.klarnaOSMConfigFacade = klarnaOSMConfigFacade;
	}

	@Override
	public String beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model,
			final String viewName)
	{

		if (LOG.isDebugEnabled())
		{
			LOG.debug("intercepting KlarnaUiThemeResourceBeforeViewHandlerAdaptee  ...");
		}
		final KlarnaOSMConfigData configData = klarnaOSMConfigFacade.getKlarnaConfig();
		if (configData != null)
		{
			model.addAttribute("isPdpEnabled", configData.getPdpEnabled());
			model.addAttribute("isCartEnabled", configData.getCartEnabled());
			model.addAttribute("isDataInlineEnabled", configData.getDataInlineEnabled());
			model.addAttribute("scriptUrl", configData.getScriptUrl());
			model.addAttribute("cartPlacementTagId", configData.getCartPlacementTagID());
			model.addAttribute("productPlacementTagId", configData.getProductPlacementTagID());
			model.addAttribute("osmCountry", configData.getCountry());
			model.addAttribute("uci", configData.getUci());
			model.addAttribute("locale", i18NService.getCurrentLocale());
			model.addAttribute("cartTheme", configData.getCartTheme());
			model.addAttribute("pdpTheme", configData.getPdpTheme());
			model.addAttribute("customStyle", configData.getCustomStyle());

		}

		return viewName;
	}

}
