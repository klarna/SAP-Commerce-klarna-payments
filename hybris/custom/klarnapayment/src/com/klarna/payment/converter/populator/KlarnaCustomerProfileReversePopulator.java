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

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.AddressService;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.klarna.api.payments.model.PaymentsAddress;
import com.klarna.api.signin.model.KlarnaSigninUserAccountProfile;
import com.klarna.payment.model.KlarnaCustomerProfileModel;


/**
 *
 */
public class KlarnaCustomerProfileReversePopulator
		implements Populator<KlarnaSigninUserAccountProfile, KlarnaCustomerProfileModel>
{
	public static Logger LOG = Logger.getLogger(KlarnaCustomerProfileReversePopulator.class);

	@Resource(name = "klarnaPaymentsAddressReverseConverter")
	private Converter<PaymentsAddress, AddressData> klarnaPaymentsAddressReverseConverter;

	@Resource(name = "addressReverseConverter")
	private Converter<AddressData, AddressModel> addressReverseConverter;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "addressService")
	private AddressService addressService;

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	@Override
	public void populate(final KlarnaSigninUserAccountProfile source, final KlarnaCustomerProfileModel target)
			throws ConversionException
	{
		target.setEmail(source.getEmail());
		target.setEmailVerified(source.getEmailVerified());
		target.setFamilyName(source.getFamilyName());
		target.setGivenName(source.getGivenName());
		target.setPhone(source.getPhone());
		target.setPhoneVerified(source.getPhoneVerified());
		target.setNationalId(source.getNationalId());
		if (source.getBillingAddress() != null)
		{
			try
			{
				final PaymentsAddress billingAddress = source.getBillingAddress();
				final AddressData addressData = new AddressData();
				// Converting the JSON response to addressData
				klarnaPaymentsAddressReverseConverter.convert(billingAddress, addressData);
				final CustomerModel customer = (CustomerModel) userService.getUserForUID(source.getEmail());
				AddressModel addressModel = null;
				// updating address for the first time
				if (target.getBillingAddress() == null)
				{
					addressModel = addressService.createAddressForOwner(customer);
					addressModel.setBillingAddress(Boolean.TRUE);
				}
				// address already linked
				else
				{
					addressModel = target.getBillingAddress();
				}
				// Reverse converting Address Data to Address Model
				addressReverseConverter.convert(addressData, addressModel);
				modelService.save(addressModel);
				// Setting the Address to Klarna Customer Profile
				target.setBillingAddress(addressModel);
				modelService.save(target);
			}
			catch (final ModelSavingException mse)
			{
				LOG.error("Model saving Exception while creating billing adrress for " + target.getEmail() + " Error "
						+ mse.getMessage());
			}

		}

	}

}
