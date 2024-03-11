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

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import com.klarna.api.Client;
import com.klarna.api.merchant_card_service.model.CardServiceSettlementRequest;
import com.klarna.api.merchant_card_service.model.CardServiceSettlementResponse;
import com.klarna.api.model.ApiException;
import com.klarna.api.order_management.OrderManagementOrdersApi;
import com.klarna.api.payments.PaymentsOrdersApi;
import com.klarna.api.payments.PaymentsSessionsApi;
import com.klarna.api.payments.model.PaymentsOrder;
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.payment.model.KPPaymentInfoModel;


/**
 *
 */
public interface KPPaymentFacade
{
	public PaymentsSession getORcreateORUpdateSession(final HttpSession httpSession, final AddressData addressData,
			final boolean isPaymentSelected, final boolean isFinal) throws ApiException, IOException;

	PaymentsOrdersApi getKlarnaAuthorization(final String authorizationToken);

	PaymentsOrdersApi getKlarnaDeleteAuthorization(final String authorizationToken) throws ApiException, IOException;

	PaymentsOrder getPaymentAuthorization(String sessionId) throws ApiException, IOException;

	public CardServiceSettlementResponse createSettlement(final CardServiceSettlementRequest settlementData)
			throws ApiException, IOException;

	OrderManagementOrdersApi getKlarnaOrderById();

	Client getKlarnaClient();

	PaymentsSessionsApi getKlarnaCreditSession(final String sessionId);

	public void acknowledgeOrderNotify(String kpOrderId, final OrderManagementOrdersApi klarnaOrder,
			final de.hybris.platform.commercefacades.order.data.OrderData hybrisOrder) throws ApiException, IOException;

	/**
	 *
	 */
	String getAuthToken();

	public void createPaymentTransaction();

	public void createTransactionEntry(String token, KPPaymentInfoModel kpPaymentInfo, final PaymentTransactionModel transaction,
			final PaymentTransactionType paymentTransactionType, final String transactionStatus,
			final String transactionStatusDetail);

	public void deletePaymentTransaction();

}
