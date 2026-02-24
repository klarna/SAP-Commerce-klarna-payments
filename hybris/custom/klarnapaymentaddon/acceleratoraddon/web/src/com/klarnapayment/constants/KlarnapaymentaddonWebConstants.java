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
package com.klarnapayment.constants;

/**
 * Global class for all Klarnapaymentaddon web constants. You can add global constants for your extension into this class.
 */
public final class KlarnapaymentaddonWebConstants // NOSONAR
{
	private KlarnapaymentaddonWebConstants()
	{
		//empty to avoid instantiating this constant class
	}

	public static final String KLARNA_EXP_CHECKOUT_CART_ID = "klarnaExpCheckoutCartId";
	public static final String KLARNA_SELECTED_PAYMENT_METHOD = "klarnaSelectedPaymentMethod";

	public static final String KLARNA_GUEST_TEMP_EMAIL_ID = "tempuser@sap.kec";
	public static final String KLARNA_ADDRESS_NOT_SUPPORTED_ERROR = "ADDRESS_NOT_SUPPORTED";
	public static final String KLARNA_INVALID_OPTION_ERROR = "INVALID_OPTION";

	public static final String KLARNA_INTEROPERABILITY_TOKEN = "klarnaInteroperabilityToken";
	public static final String KLARNA_NETWORK_SESSION_TOKEN = "klarnaNetworkSessionToken";

	public static final String KLARNA_RESPONSE_STATUS = "status";
	public static final String KLARNA_RESPONSE_REDIRECT_URL = "redirectUrl";
	public static final String KLARNA_RESPONSE_STATUS_SUCCESS = "SUCCESS";
	public static final String KLARNA_RESPONSE_STATUS_ERROR = "ERROR";
	public static final String KLARNA_RESPONSE_STATUS_NOT_READY = "NOT_READY";

	public static final String KLARNA_PLACE_ORDER_PATH = "/checkout/multi/summary/klarna/placeOrder";
	public static final String IS_PAYMENT_BEING_PROCESSED = "isPaymentBeingProcessed";

}
