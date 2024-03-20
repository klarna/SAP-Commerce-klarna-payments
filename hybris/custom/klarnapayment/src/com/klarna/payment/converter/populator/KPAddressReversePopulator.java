/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.klarna.payment.converter.populator;

import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 */
public class KPAddressReversePopulator implements Populator<AddressData, AddressModel>
{

	/**
	 * @return the addressReversePopulator
	 */
	public AddressReversePopulator getAddressReversePopulator()
	{
		return addressReversePopulator;
	}


	/**
	 * @param addressReversePopulator
	 *           the addressReversePopulator to set
	 */
	public void setAddressReversePopulator(final AddressReversePopulator addressReversePopulator)
	{
		this.addressReversePopulator = addressReversePopulator;
	}


	AddressReversePopulator addressReversePopulator;


	@Override
	public void populate(final AddressData addressData, final AddressModel addressModel) throws ConversionException
	{
		addressReversePopulator.populate(addressData, addressModel);
		addressModel.setStreetname(addressData.getLine1());
		addressModel.setStreetnumber(addressData.getLine2());
		addressModel.setBuilding(addressData.getBuilding());
		addressModel.setEmail(addressData.getEmail());
		addressModel.setDateOfBirth(addressData.getDateOfBirth());

	}
}
