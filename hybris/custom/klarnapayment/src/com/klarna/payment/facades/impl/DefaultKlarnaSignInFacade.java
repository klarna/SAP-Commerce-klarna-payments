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
package com.klarna.payment.facades.impl;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.klarna.api.signin.model.KlarnaSigninResponse;
import com.klarna.api.signin.model.KlarnaSigninUserAccountProfile;
import com.klarna.payment.data.KlarnaSignInConfigData;
import com.klarna.payment.facades.KlarnaSignInFacade;
import com.klarna.payment.model.KlarnaCustomerProfileModel;
import com.klarna.payment.model.KlarnaSignInConfigModel;


/**
 *
 */
public class DefaultKlarnaSignInFacade implements KlarnaSignInFacade
{
	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	@Resource(name = "klarnaSignInConfigConverter")
	private Converter klarnaSignInConfigConverter;

	@Resource(name = "klarnaCustomerProfileReverseConverter")
	private Converter klarnaCustomerProfileReverseConverter;

	@Resource(name = "addressConverter")
	private Converter<AddressModel, AddressData> addressConverter;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "cartService")
	private CartService cartService;

	@Resource(name = "customerNameStrategy")
	private CustomerNameStrategy customerNameStrategy;

	@Resource(name = "customerAccountService")
	private CustomerAccountService customerAccountService;

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	private static final Logger LOG = Logger.getLogger(DefaultKlarnaSignInFacade.class);

	@Override
	public KlarnaSignInConfigData getKlarnaSignInConfigData()
	{
		final BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
		final KlarnaSignInConfigModel model = baseStore.getKlarnaSignInConfig();
		if (model != null)
		{
			return (KlarnaSignInConfigData) klarnaSignInConfigConverter.convert(model);
		}
		return null;
	}

	@Override
	public boolean processCustomer(final KlarnaSigninResponse klarnaSigninResponse)
	{
		final KlarnaSigninUserAccountProfile klarnaSigninUserAccountProfile = klarnaSigninResponse.getUserAccountProfile();
		if (klarnaSigninUserAccountProfile != null)
		{
			final String useremail = StringUtils.isNotEmpty(klarnaSigninUserAccountProfile.getEmail())
					? klarnaSigninUserAccountProfile.getEmail()
					: null;
			if (useremail != null)
			{
				final UserModel user = userService.getUserForUID(useremail);
				if (user == null)
				{
					return createNewCustomer(klarnaSigninUserAccountProfile);
				}
				else if (user instanceof CustomerModel)
				{
					updateCustomer((CustomerModel) user, klarnaSigninUserAccountProfile);
					return true;
				}
				else
				{
					LOG.error("Cannot create customer account. Another user exists with same UID :: "
							+ klarnaSigninUserAccountProfile.getEmail());
				}
			}
		}
		return false;
	}

	private boolean createNewCustomer(final KlarnaSigninUserAccountProfile klarnaSigninUserAccountProfile)
	{
		try
		{
			final CustomerModel customer = modelService.create(CustomerModel.class);
			customer.setUid(klarnaSigninUserAccountProfile.getEmail());
			customer.setName(customerNameStrategy.getName(klarnaSigninUserAccountProfile.getGivenName(),
					klarnaSigninUserAccountProfile.getFamilyName()));
			customer.setOriginalUid(klarnaSigninUserAccountProfile.getEmail());
			customer.setSessionLanguage(commonI18NService.getCurrentLanguage());
			customer.setSessionCurrency(commonI18NService.getCurrentCurrency());
			customer.setCustomerID(UUID.randomUUID().toString());
			customerAccountService.register(customer, null);
			return true;
		}
		catch (final Exception e)
		{
			LOG.error("Error creating new customer account for email id " + klarnaSigninUserAccountProfile.getEmail() + " :: ", e);
			return false;
		}
	}

	private void updateCustomer(final CustomerModel customer, final KlarnaSigninUserAccountProfile klarnaSigninUserAccountProfile)
	{
		try
		{
			KlarnaCustomerProfileModel klarnaCustomerProfileModel = customer.getKlarnaCustomerProfile();
			if (klarnaCustomerProfileModel == null)
			{
				klarnaCustomerProfileModel = modelService.create(KlarnaCustomerProfileModel.class);
			}
			// Modify account info in customer model only if the current value matches that of Klarna customer profile
			// If it doesn't match, it means customer has created or updated account info within the site
			// In such cases, the account info shouldn't be overridden with the values from Klarna
			if (!StringUtils.equals(klarnaCustomerProfileModel.getFamilyName(), klarnaSigninUserAccountProfile.getFamilyName())
					|| !StringUtils.equals(klarnaCustomerProfileModel.getGivenName(), klarnaSigninUserAccountProfile.getGivenName()))
			{
				final String currentCustomerNameInKlarna = customerNameStrategy.getName(klarnaCustomerProfileModel.getGivenName(),
						klarnaCustomerProfileModel.getFamilyName());
				if (StringUtils.equals(currentCustomerNameInKlarna, customer.getName()))
				{
					// Set new name
					customer.setName(customerNameStrategy.getName(klarnaSigninUserAccountProfile.getGivenName(),
							klarnaSigninUserAccountProfile.getFamilyName()));
				}
			}
			klarnaCustomerProfileReverseConverter.convert(klarnaSigninUserAccountProfile, klarnaCustomerProfileModel);
			customer.setKlarnaCustomerProfile(klarnaCustomerProfileModel);
			modelService.saveAll(klarnaCustomerProfileModel, customer);
		}
		catch (final Exception e)
		{
			LOG.error("Error updating customer account for email id " + klarnaSigninUserAccountProfile.getEmail() + " :: ", e);
		}
	}

}
