package com.klarna.payment.facades.impl;

import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import com.klarna.payment.data.KlarnaConfigData;
import com.klarna.payment.enums.KPEndpointType;
import com.klarna.payment.facades.KPConfigFacade;
import com.klarna.payment.model.KlarnaPayConfigModel;


public class DefaultKPConfigFacade implements KPConfigFacade
{
	private BaseStoreService baseStoreService;

	private Converter klarnaPaymentConfigConverter;

	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @return the klarnaPaymentConfigConverter
	 */
	public Converter getKlarnaPaymentConfigConverter()
	{
		return klarnaPaymentConfigConverter;
	}

	/**
	 * @param klarnaPaymentConfigConverter
	 *           the klarnaPaymentConfigConverter to set
	 */
	public void setKlarnaPaymentConfigConverter(final Converter klarnaPaymentConfigConverter)
	{
		this.klarnaPaymentConfigConverter = klarnaPaymentConfigConverter;
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
		final KlarnaPayConfigModel model = baseStore.getKlarnaPayConfig();

		if (model == null)
		{
			return null;
		}
		return (KlarnaConfigData) getKlarnaPaymentConfigConverter().convert(model);
	}

	@Override
	public boolean isNorthAmerianKlarnaPayment()
	{
		final KlarnaConfigData klarnaConfig = getKlarnaConfig();
		return klarnaConfig != null && klarnaConfig.getEndpointType().equals(KPEndpointType.NORTH_AMERICA.getCode()) ? true : false;
	}


	@Override
	public String getPaymentOption()
	{
		final KlarnaConfigData klarnaConfig = getKlarnaConfig();
		return klarnaConfig.getPaymentOption();
	}

	@Override
	public String getLogo()
	{
		final KlarnaConfigData klarnaConfig = getKlarnaConfig();
		return klarnaConfig.getKlarnaPayLogo();
	}


	@Override
	public String getDisplayName()
	{
		final KlarnaConfigData klarnaConfig = getKlarnaConfig();
		return klarnaConfig.getKlarnaPayDisplayName();
	}

	@Override
	public boolean isNLKlarnaPayment()
	{
		final KlarnaConfigData klarnaConfig = getKlarnaConfig();
		return klarnaConfig != null && klarnaConfig.getEndpointType().equals(KPEndpointType.EUROPE.getCode())
				&& klarnaConfig.getPurchaseCountry().equals("NL") ? true : false;

	}


	@Override
	public KlarnaPayConfigModel getKlarnaConfigForStore(final BaseStoreModel store)
	{
		final KlarnaPayConfigModel model = store.getKlarnaPayConfig();

		if (model == null)
		{
			return null;
		}
		return model;
	}
}
