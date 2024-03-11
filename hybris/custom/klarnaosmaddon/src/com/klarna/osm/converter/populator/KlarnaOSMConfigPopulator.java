/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.klarna.osm.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.klarna.data.KlarnaOSMConfigData;
import com.klarna.osm.model.KlarnaOSMConfigModel;


public class KlarnaOSMConfigPopulator implements Populator<KlarnaOSMConfigModel, KlarnaOSMConfigData>
{


	/**
	 * Populate data from KlarnaConfigModel to KlarnaConfigData
	 */
	@Override
	public void populate(final KlarnaOSMConfigModel source, final KlarnaOSMConfigData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setPdpEnabled(source.getPdpEnabled());
		target.setCartEnabled(source.getCartEnabled());
		if (source.getDataInlineEnabled() == null)
		{
			target.setDataInlineEnabled(Boolean.FALSE);
		}
		else
		{
			target.setDataInlineEnabled(source.getDataInlineEnabled());
		}
		target.setCountry(source.getCountry().getIsocode());
		target.setCartPlacementTagID(source.getCartPlacementTagID());
		target.setScriptUrl(source.getScriptUrl());
		target.setProductPlacementTagID(source.getProductPlacementTagID());
		target.setUci(source.getUci());
		if (null != source.getCartTheme())
		{
			target.setCartTheme(source.getCartTheme().getCode());
		}
		else
		{
			target.setCartTheme(source.getCartTheme().DEFAULT.getCode());
		}
		if (null != source.getPdpTheme())
		{
			target.setPdpTheme(source.getPdpTheme().getCode());
		}
		else
		{
			target.setPdpTheme(source.getPdpTheme().DEFAULT.getCode());
		}
		target.setCustomStyle(source.getCustomStyle());

	}

}
