package com.klarna.payment.facades.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import com.klarna.data.KlarnaConfigData;
import com.klarna.model.KlarnaConfigModel;
import com.klarna.payment.constants.KlarnapaymentConstants;
import com.klarna.payment.facades.KlarnaConfigFacade;


public class DefaultKlarnaConfigFacade implements KlarnaConfigFacade
{
	private static Logger LOG = Logger.getLogger(DefaultKlarnaConfigFacade.class);

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
		final KlarnaConfigModel klarnaConfigModel = baseStore.getKlarnaConfig();

		if (klarnaConfigModel == null || !Boolean.TRUE.equals(klarnaConfigModel.getActive())
				|| CollectionUtils.isEmpty(klarnaConfigModel.getCredentials()))
		{
			return null;
		}
		return (KlarnaConfigData) getKlarnaConfigConverter().convert(klarnaConfigModel);
	}

	@Override
	public boolean isNorthAmerianKlarnaPayment()
	{
		final KlarnaConfigData klarnaConfig = getKlarnaConfig();

		return klarnaConfig != null && klarnaConfig.getCredential() != null
				&& klarnaConfig.getCredential().getMarketRegion().equals(KlarnapaymentConstants.KLARNA_MARKET_REGION_AMERICAS) ? true
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
		return klarnaConfig != null && klarnaConfig.getCredential() != null
				&& klarnaConfig.getCredential().getMarketCountry().equals(KlarnapaymentConstants.KLARNA_MARKET_COUNTRY_NETHERLANDS)
						? true
						: false;
	}

	@Override
	public KlarnaConfigModel getKlarnaConfigForStore(final BaseStoreModel store)
	{
		final KlarnaConfigModel model = store.getKlarnaConfig();

		if (model == null)
		{
			return null;
		}
		return model;
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
