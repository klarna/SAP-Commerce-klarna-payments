/*
 * Copyright 2018 Klarna AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.klarna.api.payments;

import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.klarna.api.BaseApi;
import com.klarna.api.HttpTransport;
import com.klarna.api.model.ApiException;
import com.klarna.api.model.ApiResponse;
import com.klarna.api.order_management.OrderManagementOrdersApi;
import com.klarna.api.payments.model.PaymentsMerchantSession;
import com.klarna.api.payments.model.PaymentsSession;


/**
 * Payments API: Sessions resource.
 *
 * The payments API is used to create a session to offer Klarna's payment methods as part of your checkout.
 *
 * As soon as the purchase is completed the order should be read and handled using the {@link OrderManagementOrdersApi
 * Order Management API}.
 */
public class PaymentsSessionsApi extends BaseApi
{
	protected String PATH = "/payments/v1/sessions";

	public PaymentsSessionsApi(final HttpTransport transport)
	{
		super(transport);
	}

	/**
	 * Creates a new credit session.
	 *
	 * @see examples.PaymentsExample.CreateNewCreditSessionExample
	 *
	 * @param session
	 *           Session information
	 * @return server response
	 * @throws ApiException
	 *            if API server returned non-20x HTTP CODE and response contains a
	 *            <a href="https://developers.klarna.com/api/#errors">Error</a>
	 * @throws IOException
	 *            if an error occurred when connecting to the server or when parsing a response.
	 */
	public PaymentsMerchantSession create(final PaymentsSession session) throws ApiException, IOException
	{
		final byte[] data = objectMapper.writeValueAsBytes(session);
		final ApiResponse response = this.post(PATH, data);

		response.expectSuccessful().expectStatusCode(Response.Status.OK).expectContentType(MediaType.APPLICATION_JSON);

		return fromJson(response.getBody(), PaymentsMerchantSession.class);
	}

	/**
	 * Reads an existing credit session.
	 *
	 * @see examples.PaymentsExample.ReadCreditSessionExample
	 *
	 * @param sessionId
	 *           Session ID
	 * @return server response
	 * @throws ApiException
	 *            if API server returned non-20x HTTP CODE and response contains a
	 *            <a href="https://developers.klarna.com/api/#errors">Error</a>
	 * @throws IOException
	 *            if an error occurred when connecting to the server or when parsing a response.
	 */
	public PaymentsSession fetch(final String sessionId) throws ApiException, IOException
	{
		final ApiResponse response = this.get(PATH + '/' + sessionId);

		response.expectSuccessful().expectStatusCode(Response.Status.OK).expectContentType(MediaType.APPLICATION_JSON);

		return fromJson(response.getBody(), PaymentsSession.class);
	}

	/**
	 * Updates an existing credit session.
	 *
	 * @see examples.PaymentsExample.UpdateExistingCreditSessionExample
	 *
	 * @param sessionId
	 *           Session ID
	 * @param session
	 *           Session information
	 * @throws ApiException
	 *            if API server returned non-20x HTTP CODE and response contains a
	 *            <a href="https://developers.klarna.com/api/#errors">Error</a>
	 * @throws IOException
	 *            if an error occurred when connecting to the server or when parsing a response.
	 */
	public void update(final String sessionId, final PaymentsSession session) throws ApiException, IOException
	{
		final byte[] data = objectMapper.writeValueAsBytes(session);
		final ApiResponse response = this.post(PATH + '/' + sessionId, data);

		response.expectSuccessful().expectStatusCode(Response.Status.NO_CONTENT);
	}
}
