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

//import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
//import de.hybris.platform.acceleratorstorefrontcommons.strategy.CartRestorationStrategy;
//import de.hybris.platform.commercefacades.consent.CustomerConsentDataStrategy;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
//import de.hybris.platform.servicelayer.security.spring.HybrisSessionFixationProtectionStrategy;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Sets;
import com.klarna.api.signin.model.KlarnaSigninResponse;
import com.klarna.api.signin.model.KlarnaSigninUserAccountLinking;
import com.klarna.api.signin.model.KlarnaSigninUserAccountProfile;
import com.klarna.payment.enums.KlarnaSigninProfileStatus;
import com.klarna.payment.facades.KlarnaSignInFacade;
import com.klarna.payment.model.KlarnaCustomerProfileModel;



/**
 *
 */
public class DefaultKlarnaSignInFacade implements KlarnaSignInFacade
{
	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

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

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	//	@Resource(name = "guidCookieStrategy")
	//	private GUIDCookieStrategy guidCookieStrategy;

	//	@Resource(name = "cartRestorationStrategy")
	//	private CartRestorationStrategy cartRestorationStrategy;

	//	@Resource(name = "rememberMeServices")
	//	private RememberMeServices rememberMeServices;

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	//@Resource(name = "cookieGenerator")
	//private CookieGenerator cookieGenerator;

	//@Resource(name = "userDetailsService")
	//private UserDetailsService userDetailsService;

	//@Resource(name = "sessionFixationStrategy")
	//private HybrisSessionFixationProtectionStrategy sessionFixationStrategy;

	//@Resource(name = "customerConsentDataStrategy")
	//private CustomerConsentDataStrategy customerConsentDataStrategy;

	private static final Logger LOG = Logger.getLogger(DefaultKlarnaSignInFacade.class);

	private static final String CUSTOMER_GROUP = "customergroup";

	@Override
	public KlarnaSigninProfileStatus checkAndUpdateProfile(final KlarnaSigninResponse klarnaSigninResponse)
	{
		final KlarnaSigninUserAccountProfile klarnaSigninUserAccountProfile = klarnaSigninResponse.getUserAccountProfile();
		if (klarnaSigninUserAccountProfile != null)
		{
			final String useremail = StringUtils.isNotEmpty(klarnaSigninUserAccountProfile.getEmail())
					? klarnaSigninUserAccountProfile.getEmail()
					: null;
			if (useremail != null)
			{
				UserModel user = null;
				try
				{
					user = userService.getUserForUID(useremail);
				}
				catch (final UnknownIdentifierException uie)
				{
					LOG.error(uie.getMessage());
				}

				if (user == null)
				{
					return KlarnaSigninProfileStatus.CREATE_AFTER_CONSENT;
				}
				else if (user instanceof CustomerModel)
				{
					final CustomerModel customer = (CustomerModel) user;
					if (customer.getKlarnaCustomerProfile() == null)
					{
						return KlarnaSigninProfileStatus.MERGE_AFTER_CONSENT;
					}
					else
					{
						updateCustomer(customer, klarnaSigninUserAccountProfile, klarnaSigninResponse.getUserAccountLinking());
						return KlarnaSigninProfileStatus.ACCOUNT_UPDATED;
					}
				}
			}
		}
		return KlarnaSigninProfileStatus.LOGIN_FAILED;
	}

	@Override
	public boolean createNewCustomer(final KlarnaSigninResponse klarnaSigninResponse)
	{
		final KlarnaSigninUserAccountProfile klarnaSigninUserAccountProfile = klarnaSigninResponse.getUserAccountProfile();
		if (klarnaSigninUserAccountProfile != null)
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
				try
				{
					final UserGroupModel group = userService.getUserGroupForUID(CUSTOMER_GROUP);
					customer.setGroups(Sets.newHashSet(group));
				}
				catch (final Exception e)
				{
					LOG.error(e.getMessage(), e);
				}
				customerAccountService.register(customer, null);
				modelService.save(customer);
				updateCustomer(customer, klarnaSigninUserAccountProfile, klarnaSigninResponse.getUserAccountLinking());
				return true;
			}
			catch (final Exception e)
			{
				LOG.error("Error creating new customer account for email id " + klarnaSigninUserAccountProfile.getEmail() + " :: ",
						e);
				return false;
			}
		}
		return false;
	}

	@Override
	public void updateCustomer(final CustomerModel customer, final KlarnaSigninUserAccountProfile klarnaSigninUserAccountProfile,
			final KlarnaSigninUserAccountLinking klarnaSigninUserAccountLinking)
	{
		try
		{
			if (customer != null)
			{
				KlarnaCustomerProfileModel klarnaCustomerProfileModel = customer.getKlarnaCustomerProfile();
				if (klarnaCustomerProfileModel == null)
				{
					klarnaCustomerProfileModel = modelService.create(KlarnaCustomerProfileModel.class);
				}
				updateCustomerName(klarnaCustomerProfileModel, klarnaSigninUserAccountProfile, customer);

				klarnaCustomerProfileReverseConverter.convert(klarnaSigninUserAccountProfile, klarnaCustomerProfileModel);
				if (klarnaSigninUserAccountLinking != null)
				{
					klarnaCustomerProfileModel.setRefreshToken(klarnaSigninUserAccountLinking.getUserAccountLinkingRefreshToken());
				}
				modelService.save(klarnaCustomerProfileModel);
				customer.setKlarnaCustomerProfile(klarnaCustomerProfileModel);
				customer.setDefaultPaymentAddress(klarnaCustomerProfileModel.getBillingAddress());
				modelService.save(customer);
			}
		}
		catch (final Exception e)
		{
			LOG.error("Error updating customer account for email id " + klarnaSigninUserAccountProfile.getEmail() + " :: ", e);
		}
	}

	void updateCustomerName(final KlarnaCustomerProfileModel klarnaCustomerProfileModel,
			final KlarnaSigninUserAccountProfile klarnaSigninUserAccountProfile, final CustomerModel customer)
	{
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
	}

}
