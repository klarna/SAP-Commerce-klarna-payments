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
package com.klarna.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.List;

import com.klarna.data.KlarnaKOSMConfigData;
import com.klarna.osm.model.KlarnaKOSMConfigModel;
import com.klarna.payment.enums.KlarnaOSMPlacement;


public class KlarnaKOSMConfigPopulator implements Populator<KlarnaKOSMConfigModel, KlarnaKOSMConfigData>
{


	/**
	 * Populate data from KlarnaConfigModel to KlarnaConfigData
	 */
	@Override
	public void populate(final KlarnaKOSMConfigModel source, final KlarnaKOSMConfigData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setActive(source.getActive());
		setKOSMPlacements(source.getPlacements(), target);
		if (null != source.getTheme())
		{
			target.setTheme(source.getTheme().getCode());
		}
		else
		{
			target.setTheme(source.getTheme().DEFAULT.getCode());
		}
		target.setCustomStyle(source.getCustomStyle());

	}

	private void setKOSMPlacements(final List<KlarnaOSMPlacement> placements, final KlarnaKOSMConfigData target)
	{
		for (final KlarnaOSMPlacement placement : placements)
		{
			if (KlarnaOSMPlacement.CART_PAGE.equals(placement))
			{
				target.setShowInCartPage(Boolean.TRUE);
			}
			if (KlarnaOSMPlacement.PRODUCT_DETAILS_PAGE.equals(placement))
			{
				target.setShowInPDPPage(Boolean.TRUE);
			}
		}
	}
}
