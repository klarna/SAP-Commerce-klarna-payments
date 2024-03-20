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
package com.klarna.payment.facades;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;

import java.io.IOException;

import com.klarna.api.model.ApiException;
import com.klarna.api.order_management.model.OrderManagementOrder;
import com.klarna.api.payments.model.PaymentsOrder;
import com.klarna.payment.data.KPPaymentInfoData;


/**
 *
 */
public interface KPPaymentCheckoutFacade
{
	boolean isKlarnaPayment();

	void saveKlarnaOrderId(PaymentsOrder authorizationResponse) throws ApiException, IOException;

	boolean isCartSynchronization(final CartData cartData, final OrderManagementOrder orderData);

	void saveAuthorization(final String authorizationToken, final String paymentOption, final Boolean finalizeRequired);

	void processPayment(AddressData addressData, final String sessionId);

	boolean hasNoPaymentInfo();

	public void updateCart(final OrderManagementOrder klarnaOrderData);

	String getUserGUID();

	public void removePaymentInfo();

	KPPaymentInfoData getKPPaymentInfo();

	public void updatePaymentInfo(final OrderManagementOrder klarnaOrderData);

	public void sendFailedNotification(String kpErrorMessage);

	void doAutoCapture(final String kpOrderId) throws ApiException, IOException;

}
