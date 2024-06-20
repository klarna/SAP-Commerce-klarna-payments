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

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.apache.log4j.Logger;

import com.klarna.data.KlarnaConfigData;
import com.klarna.data.KlarnaCredentialData;
import com.klarna.data.KlarnaKECConfigData;
import com.klarna.data.KlarnaKPConfigData;
import com.klarna.data.KlarnaSIWKConfigData;
import com.klarna.model.KlarnaConfigModel;
import com.klarna.model.KlarnaCredentialModel;
import com.klarna.model.KlarnaKECConfigModel;
import com.klarna.model.KlarnaKPConfigModel;
import com.klarna.model.KlarnaSIWKConfigModel;
import com.klarna.payment.facades.KPConfigFacade;


/**
 *
 */
public class KlarnaConfigPopulator implements Populator<KlarnaConfigModel, KlarnaConfigData>
{
	private static Logger LOG = Logger.getLogger(KlarnaConfigPopulator.class);
	private Converter klarnaCredentialConverter;
	private Converter klarnaKPConfigConverter;
	private Converter klarnaKECConfigConverter;
	private Converter klarnaSIWKConfigConverter;
	private KPConfigFacade kpConfigFacade;

	@Override
	public void populate(final KlarnaConfigModel source, final KlarnaConfigData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setEnvironment(source.getEnvironment() != null ? source.getEnvironment().getCode() : null);

		final KlarnaCredentialModel credentialModel = getKpConfigFacade().getKlarnaCredentialForSite(source);
		if (credentialModel != null)
		{
			final KlarnaCredentialData credential = new KlarnaCredentialData();
			klarnaCredentialConverter.convert(credentialModel, credential);
			target.setCredential(credential);
		}

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
	 * @return the kpConfigFacade
	 */
	public KPConfigFacade getKpConfigFacade()
	{
		return kpConfigFacade;
	}




	/**
	 * @param kpConfigFacade
	 *           the kpConfigFacade to set
	 */
	public void setKpConfigFacade(final KPConfigFacade kpConfigFacade)
	{
		this.kpConfigFacade = kpConfigFacade;
	}

}
