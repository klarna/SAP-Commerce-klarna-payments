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
package com.klarna.payment.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.klarna.payment.data.KlarnaSignInConfigData;
import com.klarna.payment.enums.KlarnaSigninDataScope;
import com.klarna.payment.model.KlarnaSignInConfigModel;


/**
 *
 */
public class KlarnaSignInConfigPopulator implements Populator<KlarnaSignInConfigModel, KlarnaSignInConfigData>
{
	private static final String DELIMITER = "__";
	private static final String DELIMITER_COLON = ":";

	@Override
	public void populate(final KlarnaSignInConfigModel source, final KlarnaSignInConfigData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setActive(source.getActive());
		target.setAutoMergeAccounts(source.getAutoMergeAccounts());

		target.setClientId(source.getClientId());
		target.setEnvironment(source.getEnvironment().getCode());

		target.setScriptUrl(source.getScriptUrl());
		target.setCountry((source.getCountry() != null) ? source.getCountry().getIsocode() : StringUtils.EMPTY);

		setScopeParameters(source, target);

		target.setRedirectUri(source.getRedirectUri());
		target.setHideOverlay(source.getHideOverlay());
		target.setButtonShape(source.getButtonShape().getCode());
		target.setButtonTheme(source.getButtonTheme().getCode());
		target.setButtonLogoAlignment(source.getButtonLogoAlignment().getCode());
	}

	/**
	 *
	 */
	private void setScopeParameters(final KlarnaSignInConfigModel source, final KlarnaSignInConfigData target)
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
}
