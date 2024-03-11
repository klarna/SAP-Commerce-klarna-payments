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

package com.klarna.api;

import java.net.URI;

import com.klarna.api.merchant_card_service.VirtualCreditCardSettlementsApi;
import com.klarna.api.order_management.OrderManagementCapturesApi;
import com.klarna.api.order_management.OrderManagementOrdersApi;
import com.klarna.api.order_management.OrderManagementRefundsApi;
import com.klarna.api.payments.PaymentsOrdersApi;
import com.klarna.api.payments.PaymentsSessionsApi;


/**
 * SDK client builder.
 *
 * This client simplifies a way of how to create a new API Resource. Also this client contains all known Klarna API
 * resources.
 */
public class Client
{

	/**
	 * HTTP Transport.
	 */
	protected final HttpTransport transport;

	/**
	 * Configures a default HttpUrlConnectionTransport with passed credentials.
	 *
	 * @param merchantId
	 *           Merchant ID/Username (UID)
	 * @param sharedSecret
	 *           Merchant shared secret/password
	 * @param baseUri
	 *           Klarna API URLs.
	 *
	 * @see HttpTransport Base URLs
	 */
	public Client(final String merchantId, final String sharedSecret, final URI baseUri)
	{

		this.transport = new HttpUrlConnectionTransport(merchantId, sharedSecret, baseUri);
	}

	public Client(final String merchantId, final String sharedSecret, final URI baseUri, final String userAgent)
	{

		this.transport = new HttpUrlConnectionTransport(merchantId, sharedSecret, baseUri, userAgent);
	}

	/**
	 * Uses custom transport to send HTTP requests.
	 *
	 * @param transport
	 *           HTTP transport instance
	 */
	public Client(final HttpTransport transport)
	{
		this.transport = transport;
	}

	/**
	 * Creates a new CheckoutOrdersApi resource instance.
	 *
	 * @see examples.CheckoutExample
	 *
	 * @return new instance
	 */
	/*
	 * public CheckoutOrdersApi newCheckoutOrdersApi() { return new CheckoutOrdersApi(transport); }
	 *
	 *//**
		 * Creates a new TokensApi resource instance.
		 *
		 * @see examples.CustomerTokenExample
		 *
		 * @param customerToken
		 *           Customer token
		 * @return new instance
		 */
	/*
	 * public TokensApi newTokensApi(final String customerToken) { return new TokensApi(transport, customerToken); }
	 *
	 *//**
		 * Creates a new HPPSessionsApi resource instance.
		 *
		 * @see examples.HostedPaymentPageExample
		 *
		 * @return new instance
		 */
	/*
	 * public HPPSessionsApi newHPPSessionsApi() { return new HPPSessionsApi(transport); }
	 *
	 *//**
		 * Creates a new VirtualCreditCardApi resource instance.
		 *
		 * @deprecated As of 3.1.0, renaming in Merchant Card Service API. Use {@link #newVirtualCreditCardSettlementsApi}
		 *             instead.
		 *
		 * @return new instance
		 */

	/*
	 * @Deprecated public VirtualCreditCardApi newVirtualCreditCardApi() { return new VirtualCreditCardApi(transport); }
	 *
	 *//**
		 * Creates a new VirtualCreditCardSettlementsApi resource instance.
		 *
		 * @see examples.MerchantCardServiceExample
		 *
		 * @return new instance
		 */

	public VirtualCreditCardSettlementsApi newVirtualCreditCardSettlementsApi()
	{
		return new VirtualCreditCardSettlementsApi(transport);
	}

	/**
	 * Creates a new VirtualCreditCardPromisesApi resource instance.
	 *
	 * @see examples.MerchantCardServiceExample
	 *
	 * @return new instance
	 */
	/*
	 * public VirtualCreditCardPromisesApi newVirtualCreditCardPromisesApi() { return new
	 * VirtualCreditCardPromisesApi(transport); }
	 *
	 *//**
		 * Creates a new OrderManagementCapturesApi resource instance.
		 *
		 * @see examples.OrderManagementExample
		 *
		 * @param orderId
		 *           Order ID
		 * @return new instance
		 */


	public OrderManagementCapturesApi newOrderManagementCapturesApi(final String orderId)
	{
		return new OrderManagementCapturesApi(transport, orderId);
	}

	/**
	 * Creates a new OrderManagementOrdersApi resource instance.
	 *
	 * @see examples.OrderManagementExample
	 *
	 * @return new instance
	 */

	public OrderManagementOrdersApi newOrderManagementOrdersApi()
	{
		return new OrderManagementOrdersApi(transport);
	}

	/**
	 * Creates a new OrderManagementRefundsApi resource instance.
	 *
	 * @see examples.OrderManagementExample
	 *
	 * @param orderId
	 *           Order ID
	 * @return new instance
	 */


	public OrderManagementRefundsApi newOrderManagementRefundsApi(final String orderId)
	{
		return new OrderManagementRefundsApi(transport, orderId);
	}

	/**
	 * Creates a new PaymentsOrdersApi resource instance.
	 *
	 * @see examples.PaymentsExample
	 *
	 * @return new instance
	 */


	public PaymentsOrdersApi newPaymentsOrdersApi()
	{
		return new PaymentsOrdersApi(transport);
	}



	/**
	 * Creates a new PaymentsSessionsApi resource instance.
	 *
	 * @see examples.PaymentsExample
	 *
	 * @return new instance
	 */

	public PaymentsSessionsApi newPaymentsSessionsApi()
	{
		return new PaymentsSessionsApi(transport);
	}


	/**
	 * Creates a new SettlementsPayoutsApi resource instance.
	 *
	 * @see examples.SettlementsExample
	 *
	 * @return new instance
	 */
	/*
	 * public SettlementsPayoutsApi newSettlementsPayoutsApi() { return new SettlementsPayoutsApi(transport); }
	 *
	 *//**
		 * Creates a new SettlementsTransactionsApi resource instance.
		 *
		 * @see examples.SettlementsExample
		 *
		 * @return new instance
		 */
	/*
	 * public SettlementsTransactionsApi newSettlementsTransactionsApi() { return new
	 * SettlementsTransactionsApi(transport); }
	 *
	 *//**
		 * Creates a new SettlementsReportsApi resource instance.
		 *
		 * @see examples.SettlementsExample
		 *
		 * @return new instance
		 */
	/*
	 * public SettlementsReportsApi newSettlementsReportsApi() { return new SettlementsReportsApi(transport); }
	 *
	 *//**
		 * Creates a new InstantShoppingOrdersApi resource instance.
		 *
		 * @param authorizationToken
		 *           authorization token
		 * @see examples.InstantShoppingExample
		 *
		 * @return new instance
		 */
	/*
	 * public InstantShoppingOrdersApi newInstantShoppingOrdersApi(final String authorizationToken) { return new
	 * InstantShoppingOrdersApi(transport, authorizationToken); }
	 *
	 *//**
		 * Creates a new InstantShoppingButtonKeysApi resource instance.
		 *
		 * @see examples.InstantShoppingExample
		 *
		 * @return new instance
		 *//*
			 * public InstantShoppingButtonKeysApi newInstantShoppingButtonKeysApi() { return new
			 * InstantShoppingButtonKeysApi(transport); }
			 */
}
