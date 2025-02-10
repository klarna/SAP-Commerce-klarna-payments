/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2024 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.klarna.converter.populator;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.klarna.data.KlarnaConfigData;
import com.klarna.data.KlarnaCredentialData;
import com.klarna.data.KlarnaKECConfigData;
import com.klarna.data.KlarnaKOSMConfigData;
import com.klarna.data.KlarnaKPConfigData;
import com.klarna.data.KlarnaSIWKConfigData;
import com.klarna.model.KlarnaConfigModel;
import com.klarna.model.KlarnaCredentialModel;
import com.klarna.model.KlarnaKECConfigModel;
import com.klarna.model.KlarnaKPConfigModel;
import com.klarna.model.KlarnaMarketCountryModel;
import com.klarna.model.KlarnaMarketRegionModel;
import com.klarna.model.KlarnaSIWKConfigModel;
import com.klarna.osm.model.KlarnaKOSMConfigModel;
import com.klarna.payment.constants.KlarnapaymentConstants;


/**
 *
 */
public class KlarnaConfigPopulator implements Populator<KlarnaConfigModel, KlarnaConfigData>
{
	private Converter klarnaCredentialConverter;
	private Converter klarnaKPConfigConverter;
	private Converter klarnaKECConfigConverter;
	private Converter klarnaSIWKConfigConverter;
	private Converter klarnaKOSMConfigConverter;

	@Resource(name = "siteConfigService")
	private SiteConfigService siteConfigService;

	@Override
	public void populate(final KlarnaConfigModel source, KlarnaConfigData target) throws ConversionException
	{
		final KlarnaCredentialModel credentialModel = getKlarnaCredentialForSite(source);
		if (credentialModel != null)
		{
			target.setCode(source.getCode());
			target.setActive(source.getActive());
			target.setEnvironment(source.getEnvironment() != null ? source.getEnvironment().getCode() : null);
			final KlarnaCredentialData credential = new KlarnaCredentialData();
			klarnaCredentialConverter.convert(credentialModel, credential);
			target.setCredential(credential);
			final KlarnaKPConfigModel klarnaKPConfigModel = source.getKpConfig();
			if (klarnaKPConfigModel != null && Boolean.TRUE == klarnaKPConfigModel.getActive())
			{
				final KlarnaKPConfigData klarnaKPConfigData = new KlarnaKPConfigData();
				klarnaKPConfigConverter.convert(klarnaKPConfigModel, klarnaKPConfigData);
				target.setKpConfig(klarnaKPConfigData);
			}

			final KlarnaKECConfigModel klarnaKECConfigModel = source.getKecConfig();
			if (klarnaKECConfigModel != null && Boolean.TRUE == klarnaKECConfigModel.getActive())
			{
				final KlarnaKECConfigData kecConfigData = new KlarnaKECConfigData();
				klarnaKECConfigConverter.convert(klarnaKECConfigModel, kecConfigData);
				target.setKecConfig(kecConfigData);
			}

			final KlarnaSIWKConfigModel klarnaSIWKConfigModel = source.getSiwkConfig();
			if (klarnaSIWKConfigModel != null && Boolean.TRUE == klarnaSIWKConfigModel.getActive())
			{
			final KlarnaSIWKConfigData siwkConfigData = new KlarnaSIWKConfigData();
			klarnaSIWKConfigConverter.convert(klarnaSIWKConfigModel, siwkConfigData);
			target.setSiwkConfig(siwkConfigData);
			}

			final KlarnaKOSMConfigModel klarnaKOSMConfigModel = source.getOsmConfig();
			if (klarnaKOSMConfigModel != null && Boolean.TRUE == klarnaKOSMConfigModel.getActive())
			{
				final KlarnaKOSMConfigData osmConfigData = new KlarnaKOSMConfigData();
				klarnaKOSMConfigConverter.convert(klarnaKOSMConfigModel, osmConfigData);
				target.setOsmConfig(osmConfigData);
			}
		}
		else
		{
			target = null;
		}
	}

	public KlarnaCredentialModel getKlarnaCredentialForSite(final KlarnaConfigModel klarnaConfig)
	{
		final String marketCountry = siteConfigService.getString(KlarnapaymentConstants.KLARNA_MARKET_COUNTRY_FOR_SITE,
				StringUtils.EMPTY);
		if (StringUtils.isNotBlank(marketCountry))
		{
			final String marketRegion = siteConfigService.getString(KlarnapaymentConstants.KLARNA_MARKET_REGION_FOR_SITE,
					StringUtils.EMPTY);
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

	/**
	 * @return the klarnaCredentialConverter
	 */
	public Converter getKlarnaCredentialConverter()
	{
		return klarnaCredentialConverter;
	}

	/**
	 * @param klarnaCredentialConverter
	 *           the klarnaCredentialConverter to set
	 */
	public void setKlarnaCredentialConverter(final Converter klarnaCredentialConverter)
	{
		this.klarnaCredentialConverter = klarnaCredentialConverter;
	}

	/**
	 * @return the klarnaKPConfigConverter
	 */
	public Converter getKlarnaKPConfigConverter()
	{
		return klarnaKPConfigConverter;
	}

	/**
	 * @param klarnaKPConfigConverter
	 *           the klarnaKPConfigConverter to set
	 */
	public void setKlarnaKPConfigConverter(final Converter klarnaKPConfigConverter)
	{
		this.klarnaKPConfigConverter = klarnaKPConfigConverter;
	}

	/**
	 * @return the klarnaKECConfigConverter
	 */
	public Converter getKlarnaKECConfigConverter()
	{
		return klarnaKECConfigConverter;
	}

	/**
	 * @param klarnaKECConfigConverter
	 *           the klarnaKECConfigConverter to set
	 */
	public void setKlarnaKECConfigConverter(final Converter klarnaKECConfigConverter)
	{
		this.klarnaKECConfigConverter = klarnaKECConfigConverter;
	}

	/**
	 * @return the klarnaSIWKConfigConverter
	 */
	public Converter getKlarnaSIWKConfigConverter()
	{
		return klarnaSIWKConfigConverter;
	}

	/**
	 * @param klarnaSIWKConfigConverter
	 *           the klarnaSIWKConfigConverter to set
	 */
	public void setKlarnaSIWKConfigConverter(final Converter klarnaSIWKConfigConverter)
	{
		this.klarnaSIWKConfigConverter = klarnaSIWKConfigConverter;
	}

	/**
	 * @return the klarnaKOSMConfigConverter
	 */
	public Converter getKlarnaKOSMConfigConverter()
	{
		return klarnaKOSMConfigConverter;
	}

	/**
	 * @param klarnaKOSMConfigConverter
	 *           the klarnaKOSMConfigConverter to set
	 */
	public void setKlarnaKOSMConfigConverter(final Converter klarnaKOSMConfigConverter)
	{
		this.klarnaKOSMConfigConverter = klarnaKOSMConfigConverter;
	}

}
