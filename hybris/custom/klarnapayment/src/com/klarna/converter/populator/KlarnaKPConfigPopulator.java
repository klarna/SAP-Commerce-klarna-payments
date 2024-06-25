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

import com.klarna.data.KlarnaKPConfigData;
import com.klarna.model.KlarnaKPConfigModel;


/**
 *
 */
public class KlarnaKPConfigPopulator implements Populator<KlarnaKPConfigModel, KlarnaKPConfigData>
{
	private static Logger LOG = Logger.getLogger(KlarnaKPConfigPopulator.class);

	@Override
	public void populate(final KlarnaKPConfigModel source, final KlarnaKPConfigData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setActive(source.getActive());
		target.setAutoCapture(source.getAutoCapture());
		target.setSendEMD(source.getSendEMD());
		target.setProductUrlsRequired(source.getProductUrlsRequired());
		target.setMerchantEmail(source.getMerchantEmail());
		target.setMerchantReference2(source.getMerchantReference2());
		target.setCustomStyle(source.getCustomStyle());
	}
}
