package com.klarna.osm.facade.impl;

import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import com.klarna.data.KlarnaOSMConfigData;
import com.klarna.osm.facade.KlarnaOSMConfigFacade;
import com.klarna.osm.model.KlarnaOSMConfigModel;


public class DefaultKlarnaOSMConfigFacade implements KlarnaOSMConfigFacade
{
	private BaseStoreService baseStoreService;

	private Converter klarnaOsmConfigConverter;

	/**
	 * @return the klarnaOsmConfigConverter
	 */
	public Converter getKlarnaOsmConfigConverter()
	{
		return klarnaOsmConfigConverter;
	}


	/**
	 * @param klarnaOsmConfigConverter
	 *           the klarnaOsmConfigConverter to set
	 */
	public void setKlarnaOsmConfigConverter(final Converter klarnaOsmConfigConverter)
	{
		this.klarnaOsmConfigConverter = klarnaOsmConfigConverter;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public KlarnaOSMConfigData getKlarnaConfig()
	{
		final BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
		final KlarnaOSMConfigModel model = baseStore.getKlarnaOsmConfig();

		if (model == null)
		{
			return null;
		}
		return (KlarnaOSMConfigData) klarnaOsmConfigConverter.convert(model);
	}


	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

}
