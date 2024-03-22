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
package com.klarna.payment.enums;

/**
 *
 */
public enum KlarnaSigninProfileStatus
{

	ACCOUNTLINKED("ACCOUNTLINKED"), ACCOUNTNOTLINKED("ACCOUNTNOTLINKED"), LOGINFAILED("LOGINFAILED"), USERNOTFOUND("USERNOTFOUND");


	private String value;

	private KlarnaSigninProfileStatus(final String value)
	{
		this.value = value.intern();
	}

	public String getValue()
	{
		return value;
	}
}
