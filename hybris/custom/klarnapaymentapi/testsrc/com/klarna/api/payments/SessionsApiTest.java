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
import com.klarna.api.payments.model.PaymentsAddress;
import com.klarna.api.payments.model.PaymentsMerchantSession;
import com.klarna.api.payments.model.PaymentsMerchantUrls;
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.api.payments.model.StatusEnum;


@RunWith(MockitoJUnitRunner.class)
public class SessionsApiTest extends TestCase
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
		final String payload = "{ \"expired_at\": \"2011-08-12T20:17:46.384Z\", \"session_id\": \"0b1d9815-165e-42e2\", \"client_token\": \"eyJhb.ewogI\", \"payment_method_categories\": [ { \"identifier\": \"pay_later\", \"name\": \"Pay Later\" } ] }";
		when(transport.conn.getInputStream()).thenReturn(this.makeInputStream(payload));

		final PaymentsSession data = new PaymentsSession();
		data.setOrderAmount(100L);
		final PaymentsMerchantUrls urls = new PaymentsMerchantUrls();
		urls.setConfirmation("https://example.com/confirm");
		data.setMerchantUrls(urls);

		final Client client = new Client(transport);
		final PaymentsSessionsApi api = client.newPaymentsSessionsApi();
		final PaymentsMerchantSession session = api.create(data);

		verify(transport.conn, times(1)).setRequestMethod("POST");
		assertEquals("/payments/v1/sessions", transport.requestPath);
		assertEquals("0b1d9815-165e-42e2", session.getSessionId());
		assertEquals("eyJhb.ewogI", session.getClientToken());
		assertEquals("pay_later", session.getPaymentMethodCategories().get(0).getIdentifier());

		final String requestPayout = transport.requestPayout.toString();
		assertTrue(requestPayout.contains("\"order_amount\":100"));
		assertTrue(requestPayout.contains("\"confirmation\":\"https://example.com/confirm\""));
	}

	@Test
	public void testCreatePaymentsSessionModel() throws IOException
	{
		final ObjectMapper mapper = new DefaultMapper();
		final String data = "{\"acquiring_channel\": \"test\", \"order_amount\": 12345, \"expires_at\": \"2019-11-07T10:10:54.991Z\"}";
		final PaymentsSession request = mapper.readValue(data, PaymentsSession.class);
		assertEquals("test", request.getAcquiringChannel());
		assertEquals(12345, request.getOrderAmount().longValue());
		assertEquals("2019-11-07T10:10:54.991Z", request.getExpiresAt().toString());
	}

	@Test
	public void testFetch() throws IOException
	{
		when(transport.conn.getResponseCode()).thenReturn(200);
		when(transport.conn.getHeaderFields()).thenReturn(new HashMap<String, List<String>>()
		{
			{
				put("Content-Type", Arrays.asList(MediaType.APPLICATION_JSON));
			}
		});
		final String payload = "{ \"purchase_country\": \"US\", \"order_amount\": 100, \"status\": \"incomplete\", \"client_token\": \"eyJhbGciOi.ewogIC\" }";
		when(transport.conn.getInputStream()).thenReturn(this.makeInputStream(payload));

		final Client client = new Client(transport);
		final PaymentsSessionsApi api = client.newPaymentsSessionsApi();
		final PaymentsSession session = api.fetch("my-session-id");

		verify(transport.conn, times(1)).setRequestMethod("GET");
		assertEquals("/payments/v1/sessions/my-session-id", transport.requestPath);
		assertEquals("US", session.getPurchaseCountry());
		final Long amount = 100L;
		assertEquals(amount, session.getOrderAmount());
		assertEquals(StatusEnum.INCOMPLETE, session.getStatus());
	}

	@Test
	public void testUpdate() throws IOException
	{
		when(transport.conn.getResponseCode()).thenReturn(204);

		final PaymentsSession data = new PaymentsSession();
		data.setOrderAmount(100L);
		final PaymentsMerchantUrls urls = new PaymentsMerchantUrls();
		urls.setConfirmation("https://example.com/confirm");
		data.setMerchantUrls(urls);

		final Client client = new Client(transport);
		final PaymentsSessionsApi api = client.newPaymentsSessionsApi();
		api.update("my-session-id", data);

		verify(transport.conn, times(1)).setRequestMethod("POST");
		assertEquals("/payments/v1/sessions/my-session-id", transport.requestPath);

		final String requestPayout = transport.requestPayout.toString();
		assertTrue(requestPayout.contains("\"order_amount\":100"));
		assertTrue(requestPayout.contains("\"confirmation\":\"https://example.com/confirm\""));
	}

	@Test
	public void testShippingBillingAddressses() throws IOException
	{
		PaymentsSession data = new PaymentsSession();
		final PaymentsAddress shipAddress = new PaymentsAddress();
		shipAddress.setCountry("DE");
		shipAddress.setCity("Berlin");
		shipAddress.setFamilyName("Test");
		shipAddress.setGivenName("Name");
		final PaymentsAddress billAddress = new PaymentsAddress();
		billAddress.setCountry("SE");
		billAddress.setCity("Stockholm");
		billAddress.setFamilyName("John");
		billAddress.setGivenName("Doe");
		data.setShippingAddress(shipAddress);
		data.setBillingAddress(billAddress);

		assertEquals("DE", data.getShippingAddress().getCountry());
		assertEquals("Test", data.getShippingAddress().getFamilyName());
		assertEquals("SE", data.getBillingAddress().getCountry());
		assertEquals("Stockholm", data.getBillingAddress().getCity());

		data = new PaymentsSession();
		final PaymentsAddress shipAddress1 = new PaymentsAddress();
		shipAddress1.setCountry("AA");
		shipAddress1.setCity("BB");
		shipAddress1.setFamilyName("CC");
		shipAddress1.setGivenName("DD");
		data.setShippingAddress(shipAddress1);
		assertEquals("AA", data.getShippingAddress().getCountry());
		assertEquals("BB", data.getShippingAddress().getCity());
	}
}
