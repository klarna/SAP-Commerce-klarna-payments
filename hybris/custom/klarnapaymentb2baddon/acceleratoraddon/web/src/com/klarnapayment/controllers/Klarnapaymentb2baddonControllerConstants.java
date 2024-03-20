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
package com.klarnapayment.controllers;

/**
 */
public interface Klarnapaymentb2baddonControllerConstants
{


	static String ADDON_PREFIX = "addon:/klarnapaymentb2baddon/";

	/**
	 * Class with view name constants
	 */
	interface Views
	{

		interface Pages
		{

			interface MultiStepCheckout
			{
				String AddPaymentMethod = "pages/checkout/multi/addPaymentMethodPage";
				String CheckoutSummaryPage = "pages/checkout/multi/checkoutSummaryPage";
				String KlarnaConfirmation = ADDON_PREFIX + "pages/checkout/multi/klarnaConfirmation";
			}

			interface Checkout
			{
				String KP_REDIRECT_CART = "/cart";
			}
		}

		interface Fragments
		{
			interface Checkout
			{
				String KPBillingAddressForm = ADDON_PREFIX + "fragments/checkout/kpBillingAddressForm";
				String KPPayment = ADDON_PREFIX + "fragments/checkout/kpPayment";
			}
		}
	}
}
