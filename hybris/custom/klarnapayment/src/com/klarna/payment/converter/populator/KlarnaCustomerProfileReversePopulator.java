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
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import com.klarna.api.signin.model.KlarnaSigninUserAccountProfile;
import com.klarna.payment.model.KlarnaCustomerProfileModel;


/**
 *
 */
public class KlarnaCustomerProfileReversePopulator
		implements Populator<KlarnaSigninUserAccountProfile, KlarnaCustomerProfileModel>
{
	//@Resource(name = "klarnaPaymentsAddressReverseConverter")
	//private Converter klarnaPaymentsAddressReverseConverter;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Override
	public void populate(final KlarnaSigninUserAccountProfile source, final KlarnaCustomerProfileModel target)
			throws ConversionException
	{
		target.setEmail(source.getEmail());
		target.setFamilyName(source.getFamilyName());
		target.setGivenName(source.getGivenName());
		target.setPhone(source.getPhone());
		//		target.setDateOfBirth(
		//				KlarnaDateFormatterUtil.getFormattedDate(source.getDateOfBirth(), KlarnaDateFormatterUtil.DATE_FORMAT_YEAR_PATTERN));
		//		target.setNationalIdentificationNumber(source.getNationalIdentificationNumber());
		//		target.setNationalIdentificationNumberCountry(source.getNationalIdentificationNumberCountry());
		//		target.setLocale(source.getLocale());
		//		target.setEmailVerified(source.getEmailVerified());
		//		target.setPhoneVerified(source.getPhoneVerified());
		//		if (source.getBillingAddress() != null)
		//		{
		//			if (target.getBillingAddress() == null)
		//			{
		//				target.setBillingAddress(modelService.create(KlarnaCustomerProfileModel.class));
		//			}
		//			klarnaPaymentsAddressReverseConverter.convert(source.getBillingAddress(), target.getBillingAddress());
		//		}
		//		else
		//		{
		//			target.setBillingAddress(null);
		//		}
	}

}
