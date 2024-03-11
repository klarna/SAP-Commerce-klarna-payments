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

package com.klarna.api.merchant_card_service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.klarna.api.BaseApi;
import com.klarna.api.HttpTransport;
import com.klarna.api.merchant_card_service.model.CardServiceSettlementRequest;
import com.klarna.api.merchant_card_service.model.CardServiceSettlementResponse;
import com.klarna.api.model.ApiException;
import com.klarna.api.model.ApiResponse;


/**
 * Merchant Card Service API: Virtual credit card Settlements resource.
 *
 * The Merchant Card Service (MCS) API is used to settle orders with virtual credit cards.
 */
public class VirtualCreditCardSettlementsApi extends BaseApi
{
	protected String PATH = "/merchantcard/v3/settlements";

	public VirtualCreditCardSettlementsApi(final HttpTransport transport)
	{
		super(transport);
	}

	/**
	 * Creates a new settlement. To create a settlement resource provide a completed order identifier and (optionally) a
	 * promise identifier.
	 *
	 * @see examples.MerchantCardServiceExample.CreateSettlementExample
	 *
	 * @param settlement
	 *           Settlement data
	 * @return server response
	 * @throws ApiException
	 *            if API server returned non-20x HTTP CODE and response contains a
	 *            <a href="https://developers.klarna.com/api/#errors">Error</a>
	 * @throws IOException
	 *            if an error occurred when connecting to the server or when parsing a response.
	 */
	public CardServiceSettlementResponse createSettlement(final CardServiceSettlementRequest settlement)
			throws ApiException, IOException
	{
		final byte[] data = objectMapper.writeValueAsBytes(settlement);
		final ApiResponse response = this.post(PATH, data);

		response.expectSuccessful().expectStatusCode(Response.Status.CREATED).expectContentType(MediaType.APPLICATION_JSON);

		return fromJson(response.getBody(), CardServiceSettlementResponse.class);
	}

	/**
	 * Retrieves an existing settlement.
	 *
	 * @see examples.MerchantCardServiceExample.RetrieveExistingSettlementExample
	 *
	 * @param settlementId
	 *           Unique settlement identifier
	 * @param keyId
	 *           Unique identifier for the public key used for encryption of the card data
	 * @return server response
	 * @throws ApiException
	 *            if API server returned non-20x HTTP CODE and response contains a
	 *            <a href="https://developers.klarna.com/api/#errors">Error</a>
	 * @throws IOException
	 *            if an error occurred when connecting to the server or when parsing a response.
	 */
	public CardServiceSettlementResponse retrieveExistingSettlement(final String settlementId, final String keyId)
			throws ApiException, IOException
	{
		final Map<String, String> headers = new HashMap<>();
		headers.put("KeyId", keyId);
		final ApiResponse response = this.get(PATH + "/" + settlementId, headers);

		response.expectSuccessful().expectStatusCode(Response.Status.OK).expectContentType(MediaType.APPLICATION_JSON);

		return fromJson(response.getBody(), CardServiceSettlementResponse.class);
	}

	/**
	 * Retrieves a settled order's settlement.
	 *
	 * @see examples.MerchantCardServiceExample.RetriveOrdersSettlementsExample
	 *
	 * @param orderId
	 *           Unique identifier for the order associated to the settlement
	 * @param keyId
	 *           Unique identifier for the public key used for encryption of the card data
	 * @return server response
	 * @throws ApiException
	 *            if API server returned non-20x HTTP CODE and response contains a
	 *            <a href="https://developers.klarna.com/api/#errors">Error</a>
	 * @throws IOException
	 *            if an error occurred when connecting to the server or when parsing a response.
	 */
	public CardServiceSettlementResponse retrieveSettledOrderSettlement(final String orderId, final String keyId)
			throws ApiException, IOException
	{
		final Map<String, String> headers = new HashMap<>();
		headers.put("KeyId", keyId);
		final ApiResponse response = this.get(PATH + "/order/" + orderId, headers);

		response.expectSuccessful().expectStatusCode(Response.Status.OK).expectContentType(MediaType.APPLICATION_JSON);

		return fromJson(response.getBody(), CardServiceSettlementResponse.class);
	}
}
