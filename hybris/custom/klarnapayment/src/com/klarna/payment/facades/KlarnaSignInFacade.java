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

import com.klarna.api.signin.model.KlarnaSigninResponse;
import com.klarna.payment.data.KlarnaSignInConfigData;
import com.klarna.payment.enums.KlarnaSigninProfileStatus;


/**
 *
 */
public interface KlarnaSignInFacade
{
	KlarnaSignInConfigData getKlarnaSignInConfigData();

	boolean processCustomer(final String profileStatus, final KlarnaSigninResponse klarnaSigninResponse);

	KlarnaSigninProfileStatus checkUserProfileStatus(final KlarnaSigninResponse klarnaSigninResponse);

	boolean isMergeEnabled();

	String getRedirectURI();
}