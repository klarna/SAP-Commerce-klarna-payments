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
package com.klarna.payment.facades;

import de.hybris.platform.core.model.user.CustomerModel;

import com.klarna.api.signin.model.KlarnaSigninResponse;
import com.klarna.api.signin.model.KlarnaSigninUserAccountLinking;
import com.klarna.api.signin.model.KlarnaSigninUserAccountProfile;
import com.klarna.payment.enums.KlarnaSigninProfileStatus;


/**
 *
 */
public interface KlarnaSignInFacade
{
	KlarnaSigninProfileStatus checkAndUpdateProfile(final KlarnaSigninResponse klarnaSigninResponse);

	boolean createNewCustomer(final KlarnaSigninResponse klarnaSigninResponse);

	void updateCustomer(CustomerModel customer, final KlarnaSigninUserAccountProfile klarnaSigninUserAccountProfile,
			final KlarnaSigninUserAccountLinking klarnaSigninUserAccountLinking);
}
