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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klarna.api.Client;
import com.klarna.api.DefaultMapper;
import com.klarna.api.FakeHttpUrlConnectionTransport;
import com.klarna.api.TestCase;
import com.klarna.api.payments.model.PaymentsCreateOrderRequest;
import com.klarna.api.payments.model.PaymentsCustomerTokenCreationRequest;
import com.klarna.api.payments.model.PaymentsCustomerTokenCreationResponse;
import com.klarna.api.payments.model.PaymentsMerchantUrls;
import com.klarna.api.payments.model.PaymentsOrder;


@RunWith(MockitoJUnitRunner.class)
public class OrdersApiTest extends TestCase
{
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	private FakeHttpUrlConnectionTransport transport;

	@Override
	@Before
	public void setUp()
	{
		transport = new FakeHttpUrlConnectionTransport();
	}

	@Test
	public void testCreate() throws IOException
	{
		when(transport.conn.getResponseCode()).thenReturn(200);
		when(transport.conn.getHeaderFields()).thenReturn(new HashMap<String, List<String>>()
		{
			{
				put("Content-Type", Arrays.asList(MediaType.APPLICATION_JSON));
			}
		});
		final String payload = "{ \"order_id\": \"0b1d9815\", \"redirect_url\": \"https://credit.klarna.com/v1/sessions/0b1d9815-165e-42e2-8867-35bc03789e00/redirect\", \"fraud_status\": \"ACCEPTED\" }";
		when(transport.conn.getInputStream()).thenReturn(this.makeInputStream(payload));

		final PaymentsCreateOrderRequest data = new PaymentsCreateOrderRequest();
		data.setOrderAmount(100L);
		final PaymentsMerchantUrls paymentMerchantUrl = new PaymentsMerchantUrls();
		paymentMerchantUrl.setConfirmation("https://example.com/confirm");
		data.setMerchantUrls(paymentMerchantUrl);
		final Client client = new Client(transport);
		final PaymentsOrdersApi api = client.newPaymentsOrdersApi();
		final PaymentsOrder order = api.create("auth-token", data);

		verify(transport.conn, times(1)).setRequestMethod("POST");
		assertEquals("/payments/v1/authorizations/auth-token/order", transport.requestPath);
		assertEquals("0b1d9815", order.getOrderId());
		assertEquals("ACCEPTED", order.getFraudStatus());

		final String requestPayout = transport.requestPayout.toString();
		assertTrue(requestPayout.contains("\"order_amount\":100"));
		assertTrue(requestPayout.contains("\"confirmation\":\"https://example.com/confirm\""));
	}

	@Test
	public void testCreateOrderRequestModel() throws IOException
	{
		final ObjectMapper mapper = new DefaultMapper();
		final String data = "{\"acquiring_channel\": \"test\", \"auto_capture\": true, \"expires_at\": \"2019-11-07T10:10:54.991Z\"}";
		final PaymentsCreateOrderRequest request = mapper.readValue(data, PaymentsCreateOrderRequest.class);
		assertEquals("test", request.getAcquiringChannel());
		assertEquals("2019-11-07T10:10:54.991Z", request.getExpiresAt().toString());
	}

	@Test
	public void testGenerateToken() throws IOException
	{
		when(transport.conn.getResponseCode()).thenReturn(200);
		when(transport.conn.getHeaderFields()).thenReturn(new HashMap<String, List<String>>()
		{
			{
				put("Content-Type", Arrays.asList(MediaType.APPLICATION_JSON));
			}
		});
		final String payload = "{ \"token_id\": \"0b1d9815\", \"redirect_url\": \"https://credit.klarna.com/v1/sessions/0b1d9815-165e-42e2-8867-35bc03789e00/redirect\" }";
		when(transport.conn.getInputStream()).thenReturn(this.makeInputStream(payload));

		final PaymentsCustomerTokenCreationRequest data = new PaymentsCustomerTokenCreationRequest();
		data.setIntendedUse(IntendedUseEnum.SUBSCRIPTION);
		data.setPurchaseCountry("US");

		final Client client = new Client(transport);
		final PaymentsOrdersApi api = client.newPaymentsOrdersApi();
		final PaymentsCustomerTokenCreationResponse token = api.generateToken("auth-token", data);

		verify(transport.conn, times(1)).setRequestMethod("POST");
		assertEquals("/payments/v1/authorizations/auth-token/customer-token", transport.requestPath);
		assertEquals("0b1d9815", token.getTokenId());
		assertEquals("https://credit.klarna.com/v1/sessions/0b1d9815-165e-42e2-8867-35bc03789e00/redirect", token.getRedirectUrl());

		final String requestPayout = transport.requestPayout.toString();
		assertTrue(requestPayout.contains("\"intended_use\":\"SUBSCRIPTION\""));
		assertTrue(requestPayout.contains("\"purchase_country\":\"US\""));
		assertFalse(requestPayout.contains("billing_address"));
	}

	@Test
	public void testAcknowledgeOrder() throws IOException
	{
		when(transport.conn.getResponseCode()).thenReturn(204);

		final Client client = new Client(transport);
		final PaymentsOrdersApi api = client.newPaymentsOrdersApi();
		api.cancelAuthorization("auth-token");

		verify(transport.conn, times(1)).setRequestMethod("DELETE");
		assertEquals("/payments/v1/authorizations/auth-token", transport.requestPath);
	}
}
