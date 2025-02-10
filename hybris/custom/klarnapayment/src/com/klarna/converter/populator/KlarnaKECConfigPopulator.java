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

import java.util.List;

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
	@Override
	public void populate(final KlarnaKECConfigModel source, final KlarnaKECConfigData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setActive(source.getActive());
		setKECPlacements(source.getPlacements(), target);
		target.setButtonShape(source.getButtonShape() != null ? source.getButtonShape().getCode() : KlarnaButtonShape.DEFAULT);
		target.setButtonTheme(source.getButtonTheme() != null ? source.getButtonTheme().getCode() : KlarnaButtonTheme.DEFAULT);
	}

	private void setKECPlacements(final List<KlarnaKECPlacement> list, final KlarnaKECConfigData target)
	{
		for (final KlarnaKECPlacement placement : list)
		{
			if (KlarnaKECPlacement.MINI_CART_POPUP.equals(placement))
			{
				target.setShowInMiniCartPage(Boolean.TRUE);
			}
			if (KlarnaKECPlacement.CART_PAGE.equals(placement))
			{
				target.setShowInCartPage(Boolean.TRUE);
			}
			if (KlarnaKECPlacement.PRODUCT_DETAILS_PAGE.equals(placement))
			{
				target.setShowInPDPPage(Boolean.TRUE);
			}
		}
	}

}
