/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.klarna.payment.facades.impl;

import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.user.UserService;

import com.klarna.payment.facades.KPCustomerFacade;


public class DefaultKPCustomerFacade implements KPCustomerFacade
{

	CustomerEmailResolutionService customerEmailResolutionService;
	CartService cartService;
	UserService userService;

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @param customerEmailResolutionService
	 *           the customerEmailResolutionService to set
	 */
	public void setCustomerEmailResolutionService(final CustomerEmailResolutionService customerEmailResolutionService)
	{
		this.customerEmailResolutionService = customerEmailResolutionService;
	}

	@Override
	public String getCurrentCustomerEmail()
	{
		final CustomerModel currentCustomer = (CustomerModel) cartService.getSessionCart().getUser();
		final String email = customerEmailResolutionService.getEmailForCustomer(currentCustomer);
		return email;
	}

	@Override
	public boolean isAnonymousCheckout()
	{
		return userService.isAnonymousUser(userService.getCurrentUser());
	}
}
