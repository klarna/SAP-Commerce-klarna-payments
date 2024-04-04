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
import com.klarna.api.signin.model.KlarnaSigninBillingAddress;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.core.model.user.AddressModel;

import org.apache.commons.lang3.StringUtils;

import com.klarna.api.payments.model.PaymentsAddress;

/**
 *
 */
public class KlarnaSignInAddressPopulator  implements Populator<KlarnaSigninBillingAddress, AddressModel>
{

			@Override
			public void populate(final KlarnaSigninBillingAddress source, final AddressModel target)
					throws ConversionException
			{
//				target.setTitle(source.getTitle() != null ? source.getTitle().replace(".", "") : "");
//				target.setGivenName(source.getFirstName());
//				target.setFamilyName(source.getLastName());
//				target.setEmail(source.getEmail());
//				target.setPhone(source.getPhone());
//
//				target.setStreetAddress((source.getLine1() != null) ? source.getLine1() : source.getStreetname());
//				target.setStreetAddress2((source.getLine2() != null) ? source.getLine2() : source.getStreetnumber());
//
//				target.setPostalCode(source.getPostalCode());
//				target.setCity(source.getTown());
//				target.setOrganizationName(source.getCompanyName());
//
//				if (source.getCountry() != null)
//				{
//					target.setCountry(source.getCountry().getIsocode());
//
//				}
//				if (source.getRegion() != null)
//				{
//					target.setRegion(source.getRegion().getIsocodeShort());
//				}
		//	}
		}
}
