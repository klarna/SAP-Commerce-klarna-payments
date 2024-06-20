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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.klarna.data.KlarnaKECConfigData;
import com.klarna.model.KlarnaKECConfigModel;
import com.klarna.payment.constants.GeneratedKlarnapaymentConstants.Enumerations.KlarnaButtonShape;
import com.klarna.payment.constants.GeneratedKlarnapaymentConstants.Enumerations.KlarnaButtonTheme;
import com.klarna.payment.enums.KlarnaKECPlacement;


/**
 *
 */
public class KlarnaKECConfigPopulator implements Populator<KlarnaKECConfigModel, KlarnaKECConfigData>
{
	private static Logger LOG = Logger.getLogger(KlarnaKECConfigPopulator.class);

	@Override
	public void populate(final KlarnaKECConfigModel source, final KlarnaKECConfigData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setActive(source.getActive());
		target.setPlacements(getPlacementCodes(source.getPlacements()));
		target.setButtonShape(source.getButtonShape() != null ? source.getButtonShape().getCode() : KlarnaButtonShape.DEFAULT);
		target.setButtonTheme(source.getButtonTheme() != null ? source.getButtonTheme().getCode() : KlarnaButtonTheme.DEFAULT);
	}

	/**
	 *
	 */
	private List<String> getPlacementCodes(final List<KlarnaKECPlacement> placements)
	{
		final List<String> placementCodes = new ArrayList<String>();
		for (final KlarnaKECPlacement placement : placements)
		{
			placementCodes.add(placement.getCode());
		}
		return placementCodes;
	}

}
