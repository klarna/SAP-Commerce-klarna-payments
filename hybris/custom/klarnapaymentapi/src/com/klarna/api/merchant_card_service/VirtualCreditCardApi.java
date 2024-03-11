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

import com.klarna.api.HttpTransport;


/**
 * Merchant Card Service API: Virtual credit card resource.
 *
 * @deprecated As of 3.1.0, renaming in Merchant Card Service API. Use VirtualCreditCardSettlementsApi instead.
 * @see VirtualCreditCardSettlementsApi
 *
 *      The Merchant Card Service (MCS) API is used to settle orders with virtual credit cards.
 */
@Deprecated
public class VirtualCreditCardApi extends VirtualCreditCardSettlementsApi
{
	public VirtualCreditCardApi(final HttpTransport transport)
	{
		super(transport);
	}
}
