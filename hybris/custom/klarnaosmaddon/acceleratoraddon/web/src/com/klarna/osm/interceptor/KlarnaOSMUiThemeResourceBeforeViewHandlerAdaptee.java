
package com.klarna.osm.interceptor;

import de.hybris.platform.addonsupport.interceptors.BeforeViewHandlerAdaptee;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.I18NService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;

import com.klarna.data.KlarnaConfigData;
import com.klarna.data.KlarnaKOSMConfigData;
import com.klarna.payment.facades.KlarnaConfigFacade;

import com.klarna.osm.constants.KlarnaosmaddonWebConstants;


public class KlarnaOSMUiThemeResourceBeforeViewHandlerAdaptee implements BeforeViewHandlerAdaptee
{
	private static final Logger LOG = Logger.getLogger(KlarnaOSMUiThemeResourceBeforeViewHandlerAdaptee.class);

	@Resource(name = "klarnaConfigFacade")
	KlarnaConfigFacade klarnaConfigFacade;

	@Resource(name = "i18NService")
	private I18NService i18NService;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Override
	public String beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model,
			final String viewName)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("intercepting KlarnaUiThemeResourceBeforeViewHandlerAdaptee  ...");
		}
		final KlarnaConfigData configData = klarnaConfigFacade.getKlarnaConfig();

		if (configData != null)
		{
			final KlarnaKOSMConfigData osmConfigData = configData.getOsmConfig();
			if (osmConfigData != null)
			{
				model.addAttribute("scriptUrlKOSM",
						configurationService.getConfiguration().getString(KlarnaosmaddonWebConstants.KLARNA_OSM_SCRIPTURL));
				//model.addAttribute("cartPlacementTagId", configData.getCartPlacementTagID());
				//model.addAttribute("productPlacementTagId", configData.getProductPlacementTagID());
				model.addAttribute("osmPlacements", osmConfigData.getPlacements());
				model.addAttribute("osmCountry", configData.getCredential().getMarketCountry()); //klarnaConfig.getCredential().getMarketCountry()
				// when KlarnaConfigData is not null credential will also not be null
				model.addAttribute("uci", configData.getCredential().getClientId());
				model.addAttribute("locale", i18NService.getCurrentLocale());
				model.addAttribute("osmTheme", osmConfigData.getTheme());
				model.addAttribute("customStyle", osmConfigData.getCustomStyle());
			}
		}
		return viewName;
	}

}
