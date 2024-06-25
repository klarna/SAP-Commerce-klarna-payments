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
package com.klarna.payment.constants;

/**
 * Global class for all Klarnapayment constants. You can add global constants for your extension into this class.
 */
public final class KlarnapaymentConstants extends GeneratedKlarnapaymentConstants
{
	public static final String EXTENSIONNAME = "klarnapayment";

	private KlarnapaymentConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension

	public static final String PLATFORM_LOGO_CODE = "klarnapaymentPlatformLogo";

	public static final String KLARNA_MARKET_COUNTRY_FOR_SITE = "klarna.market.country";
	public static final String KLARNA_MARKET_REGION_FOR_SITE = "klarna.market.region";
	public static final String KLARNA_MARKET_COUNTRY_ALL = "ALL";
	public static final String KLARNA_MARKET_REGION_EUROPE = "EU";
	public static final String KLARNA_MARKET_REGION_AMERICAS = "NA";
	public static final String KLARNA_MARKET_REGION_ASIA_AND_OCEANIA = "OC";
	public static final String KLARNA_MARKET_COUNTRY_NETHERLANDS = "NL";

	public static final String MERCHANT_CONFIRM_PAGE_URL_NOT_FIND = "Coulld Not find URL of merchant confirmation page";
	public static final String KP_MERCHANT_URL_CONFIRMATION = "klarnacheckout.merchant.url.confirmation";
	public static final String KP_MERCHANT_URL_NOTIFICATION = "klarnacheckout.merchant.url.notification";
}
