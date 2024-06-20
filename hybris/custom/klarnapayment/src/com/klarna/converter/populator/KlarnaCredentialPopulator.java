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

import org.apache.log4j.Logger;

import com.klarna.data.KlarnaCredentialData;
import com.klarna.model.KlarnaCredentialModel;
import com.klarna.payment.constants.KlarnapaymentConstants;
import com.klarna.payment.facades.KPConfigFacade;


/**
 *
 */
public class KlarnaCredentialPopulator implements Populator<KlarnaCredentialModel, KlarnaCredentialData>
{

	private static Logger LOG = Logger.getLogger(KlarnaCredentialPopulator.class);

	private KPConfigFacade kpConfigFacade;

	@Override
	public void populate(final KlarnaCredentialModel source, final KlarnaCredentialData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setApiUserName(source.getApiUserName());
		target.setApiPassword(source.getApiPassword());
		target.setClientId(source.getClientId());
		target.setMarketRegion(source.getMarketRegion() != null ? source.getMarketRegion().getCode() : null);
		target.setMarketCountry(getKpConfigFacade().getConfigurationString(KlarnapaymentConstants.KLARNA_MARKET_COUNTRY_FOR_SITE));
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