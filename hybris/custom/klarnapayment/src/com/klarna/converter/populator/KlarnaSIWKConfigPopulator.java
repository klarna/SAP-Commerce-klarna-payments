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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import com.klarna.data.KlarnaSIWKConfigData;
import com.klarna.model.KlarnaSIWKConfigModel;
import com.klarna.payment.constants.GeneratedKlarnapaymentConstants.Enumerations.KlarnaButtonLogoAlignment;
import com.klarna.payment.constants.GeneratedKlarnapaymentConstants.Enumerations.KlarnaButtonShape;
import com.klarna.payment.constants.GeneratedKlarnapaymentConstants.Enumerations.KlarnaButtonTheme;
import com.klarna.payment.enums.KlarnaSIWKPlacement;
import com.klarna.payment.enums.KlarnaSigninDataScope;


public class KlarnaSIWKConfigPopulator implements Populator<KlarnaSIWKConfigModel, KlarnaSIWKConfigData>
{
	private static Logger LOG = Logger.getLogger(KlarnaSIWKConfigPopulator.class);

	private static final String DELIMITER = "__";
	private static final String DELIMITER_COLON = ":";

	@Override
	public void populate(final KlarnaSIWKConfigModel source, final KlarnaSIWKConfigData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setActive(source.getActive());
		setSIWKPlacements(source.getPlacements(), target);
		target.setRedirectUri(source.getRedirectUri());
		setScopeParameters(source, target);
		target.setButtonShape(source.getButtonShape() != null ? source.getButtonShape().getCode() : KlarnaButtonShape.DEFAULT);
		target.setButtonTheme(source.getButtonTheme() != null ? source.getButtonTheme().getCode() : KlarnaButtonTheme.DEFAULT);
		target.setButtonLogoAlignment(source.getButtonLogoAlignment() != null ? source.getButtonLogoAlignment().getCode()
				: KlarnaButtonLogoAlignment.CENTER);
	}

	private void setScopeParameters(final KlarnaSIWKConfigModel source, final KlarnaSIWKConfigData target)
	{
		final List<KlarnaSigninDataScope> scopeList = source.getScopeData();
		if (CollectionUtils.isNotEmpty(scopeList))
		{
			final StringBuilder scope = new StringBuilder();
			for (final KlarnaSigninDataScope klarnaDataScope : scopeList)
			{
				scope.append(klarnaDataScope.getCode().replace(DELIMITER, DELIMITER_COLON));
				scope.append(" ");
			}
			target.setScopeData(scope.toString().trim());
		}
		else
		{
			target.setScopeData("");
		}
	}

	private void setSIWKPlacements(final List<KlarnaSIWKPlacement> list, final KlarnaSIWKConfigData target)
	{
		for (final KlarnaSIWKPlacement placement : list)
		{
			if (KlarnaSIWKPlacement.LOGIN_PAGE.equals(placement))
			{
				target.setShowInLoginPage(Boolean.TRUE);
			}
			if (KlarnaSIWKPlacement.CHECKOUT_LOGIN_PAGE.equals(placement))
			{
				target.setShowInCheckoutLoginPage(Boolean.TRUE);
			}
			if (KlarnaSIWKPlacement.REGISTER_PAGE.equals(placement))
			{
				target.setShowInRegisterPage(Boolean.TRUE);
			}
		}
	}
}
