package com.klarna.payment.facades.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.klarna.data.KlarnaConfigData;
import com.klarna.data.KlarnaCredentialData;
import com.klarna.model.KlarnaConfigModel;
import com.klarna.model.KlarnaCredentialModel;
import com.klarna.model.KlarnaMarketCountryModel;
import com.klarna.model.KlarnaMarketRegionModel;
import com.klarna.payment.constants.KlarnapaymentConstants;
import com.klarna.payment.facades.KPConfigFacade;


public class DefaultKPConfigFacade implements KPConfigFacade
{
	private static Logger LOG = Logger.getLogger(DefaultKPConfigFacade.class);

	private BaseStoreService baseStoreService;

	private Converter klarnaConfigConverter;


	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@Resource(name = "configurationService")
	ConfigurationService configurationService;


	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KlarnaConfigData getKlarnaConfig()
	{
		final BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
		final KlarnaConfigModel model = baseStore.getConfig();

		if (model == null)
		{
			return null;
		}
		return (KlarnaConfigData) getKlarnaConfigConverter().convert(model);
	}

	@Override
	public boolean isNorthAmerianKlarnaPayment()
	{
		final KlarnaConfigData klarnaConfig = getKlarnaConfig();
		final KlarnaCredentialData credential = klarnaConfig.getCredential();

		return klarnaConfig != null && credential.getMarketRegion().equals(KlarnapaymentConstants.KLARNA_MARKET_REGION_AMERICAS)
				? true
				: false;
	}


	//	@Override
	//	public String getPaymentOption()
	//	{
	//		final KlarnaConfigData klarnaConfig = getKlarnaConfig();
	//		return klarnaConfig.getPaymentOption();
	//	}
	//
	//	@Override
	//	public String getLogo()
	//	{
	//		final KlarnaConfigData klarnaConfig = getKlarnaConfig();
	//		return klarnaConfig.getKlarnaPayLogo();
	//	}
	//
	//	@Override
	//	public String getDisplayName()
	//	{
	//		final KlarnaConfigData klarnaConfig = getKlarnaConfig();
	//		return klarnaConfig.getKlarnaPayDisplayName();
	//	}

	@Override
	public boolean isNLKlarnaPayment()
	{
		final KlarnaConfigData klarnaConfig = getKlarnaConfig();
		final KlarnaCredentialData credential = klarnaConfig.getCredential();

		return klarnaConfig != null
				&& credential.getMarketCountry().equals(KlarnapaymentConstants.KLARNA_MARKET_COUNTRY_NETHERLANDS) ? true : false;
	}

	@Override
	public KlarnaConfigModel getKlarnaConfigForStore(final BaseStoreModel store)
	{
		final KlarnaConfigModel model = store.getConfig();

		if (model == null)
		{
			return null;
		}
		return model;
	}

	@Override
	public KlarnaCredentialModel getKlarnaCredentialForSite(final KlarnaConfigModel klarnaConfig)
	{
		final String marketCountry = getConfigurationString(KlarnapaymentConstants.KLARNA_MARKET_COUNTRY_FOR_SITE);
		if (StringUtils.isNotBlank(marketCountry))
		{
			final String marketRegion = getConfigurationString(KlarnapaymentConstants.KLARNA_MARKET_REGION_FOR_SITE);
			if (StringUtils.isNotBlank(marketRegion))
			{
				final List<KlarnaCredentialModel> credentials = klarnaConfig.getCredentials();
				if (CollectionUtils.isNotEmpty(credentials))
				{
					for (final KlarnaCredentialModel cred : credentials)
					{
						final KlarnaMarketRegionModel region = cred.getMarketRegion();

						if (region != null && region.getCode().equalsIgnoreCase(marketRegion))
						{
							final List<KlarnaMarketCountryModel> markets = cred.getMarketCountries();
							if (CollectionUtils.isNotEmpty(markets))
							{
								for (final KlarnaMarketCountryModel market : markets)
								{
									if (StringUtils.isNotEmpty(marketCountry)
											&& (marketCountry.equalsIgnoreCase(KlarnapaymentConstants.KLARNA_MARKET_COUNTRY_ALL)
													|| marketCountry.equalsIgnoreCase(market.getIsoCode())))
									{
										return cred;
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public String getConfigurationString(final String key)
	{
		String value = null;
		final BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();
		final String siteUid = currentBaseSite.getUid();
		value = configurationService.getConfiguration().getString(key + siteUid);
		return value;
	}

	/**
	 * @return the klarnaConfigConverter
	 */
	public Converter getKlarnaConfigConverter()
	{
		return klarnaConfigConverter;
	}

	/**
	 * @param klarnaConfigConverter
	 *           the klarnaConfigConverter to set
	 */
	public void setKlarnaConfigConverter(final Converter klarnaConfigConverter)
	{
		this.klarnaConfigConverter = klarnaConfigConverter;
	}
}
