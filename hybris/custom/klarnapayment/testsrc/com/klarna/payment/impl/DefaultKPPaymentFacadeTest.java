/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2021 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.klarna.payment.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.klarna.api.model.ApiException;
import com.klarna.api.order_management.OrderManagementOrdersApi;
import com.klarna.api.order_management.model.OrderManagementUpdateMerchantReferences;
import com.klarna.api.payments.PaymentsSessionsApi;
import com.klarna.api.payments.model.PaymentsCreateOrderRequest;
import com.klarna.api.payments.model.PaymentsMerchantSession;
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.payment.data.KlarnaConfigData;
import com.klarna.payment.facades.KPConfigFacade;
import com.klarna.payment.facades.impl.DefaultKPPaymentFacade;
import com.klarna.payment.model.KPPaymentInfoModel;
import com.klarna.payment.services.KPOrderService;


@UnitTest
public class DefaultKPPaymentFacadeTest
{
	@Mock
	private KPConfigFacade kpConfigFacade;
	@Mock
	private CartService cartService;
	@Mock
	private ModelService modelService;
	@Mock
	private Converter<AbstractOrderModel, PaymentsSession> klarnaCreditSessionConverter;
	@Mock
	private Converter<AbstractOrderModel, PaymentsSession> klarnaCreditSessionInitialConverter;
	@Mock
	private Converter<AddressData, AddressModel> kpAddressReverseConverter;
	@Mock
	private Converter<PaymentsSession, PaymentsCreateOrderRequest> klarnaPaymentOrderConverter;
	@Mock
	private KPOrderService kpOrderService;
	@Mock
	private UserService userService;
	@Mock
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock
	private PaymentsSessionsApi session;
	@Mock
	private PaymentsSession klarnaCreditSessionData;
	@Mock
	private PaymentsMerchantSession sessionResponse;
	@Mock
	private OrderManagementOrdersApi klarnaOrder;


	DefaultKPPaymentFacade defaultKPPaymentFacade;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultKPPaymentFacade = Mockito.spy(new DefaultKPPaymentFacade());
		Mockito.doReturn(cartService).when(defaultKPPaymentFacade).getCartService();
		Mockito.doReturn(klarnaCreditSessionConverter).when(defaultKPPaymentFacade).getKlarnaCreditSessionConverter();
		Mockito.doReturn(klarnaCreditSessionInitialConverter).when(defaultKPPaymentFacade).getKlarnaCreditSessionInitialConverter();
		Mockito.doReturn(kpOrderService).when(defaultKPPaymentFacade).getKpOrderService();
		Mockito.doReturn(kpAddressReverseConverter).when(defaultKPPaymentFacade).getKpAddressReverseConverter();
		Mockito.doReturn(kpConfigFacade).when(defaultKPPaymentFacade).getKpConfigFacade();
		Mockito.doReturn(kpAddressReverseConverter).when(defaultKPPaymentFacade).getKpAddressReverseConverter();
		Mockito.doReturn(klarnaPaymentOrderConverter).when(defaultKPPaymentFacade).getKlarnaPaymentOrderConverter();
		Mockito.doReturn(commerceCartCalculationStrategy).when(defaultKPPaymentFacade).getCommerceCartCalculationStrategy();
		Mockito.doReturn(commerceCartService).when(defaultKPPaymentFacade).getCommerceCartService();
		Mockito.doReturn(modelService).when(defaultKPPaymentFacade).getModelService();
		Mockito.doReturn(userService).when(defaultKPPaymentFacade).getUserService();


	}

	@Test
	public void testCreateNA() throws ApiException, IOException
	{
		final CartModel cartmodel = new CartModel();
		final KPPaymentInfoModel paymentInfoModel = new KPPaymentInfoModel();
		cartmodel.setPaymentInfo(paymentInfoModel);

		final KlarnaConfigData klarnConfig = new KlarnaConfigData();
		klarnConfig.setMerchantID("ABCD");
		klarnConfig.setSharedSecret("1234");
		klarnConfig.setEndpointMode("LIVE");
		klarnConfig.setEndpointType("KPEndpointType.NORTH_AMERICA");

		final PaymentsSession result = new PaymentsSession();
		result.setClientToken("1234");

		Mockito.doReturn(session).when(defaultKPPaymentFacade).getKlarnaCreditSession(Mockito.anyString());
		Mockito.doReturn(cartmodel).when(cartService).getSessionCart();
		Mockito.doReturn(klarnConfig).when(kpConfigFacade).getKlarnaConfig();
		Mockito.doReturn(new AddressModel()).when(kpAddressReverseConverter).convert(Mockito.any(AddressData.class));
		Mockito.doReturn(klarnaCreditSessionData).when(klarnaCreditSessionConverter).convert(Mockito.any(CartModel.class));
		Mockito.doReturn(sessionResponse).when(session).create(Mockito.any(PaymentsSession.class));

		Mockito.when(sessionResponse.getSessionId()).thenReturn("ASDF");
		Mockito.doReturn(result).when(session).fetch(Mockito.anyString());

		Mockito.doNothing().when(modelService).refresh(Mockito.any(CartModel.class));
		Mockito.doNothing().when(modelService).saveAll();
		final HttpSession session = Mockito.mock(HttpSession.class);
		Mockito.doReturn(null).when(session).getAttribute(Mockito.anyString());
		defaultKPPaymentFacade.getORcreateORUpdateSession(session, new AddressData(), false, false);

	}

	@Test
	public void testUpdateSessionNA() throws ApiException, IOException
	{
		final CartModel cartmodel = new CartModel();
		final KPPaymentInfoModel paymentInfoModel = new KPPaymentInfoModel();
		cartmodel.setPaymentInfo(paymentInfoModel);

		final KlarnaConfigData klarnConfig = new KlarnaConfigData();
		klarnConfig.setMerchantID("ABCD");
		klarnConfig.setSharedSecret("1234");
		klarnConfig.setEndpointMode("LIVE");
		klarnConfig.setEndpointType("KPEndpointType.NORTH_AMERICA");

		final PaymentsSession result = new PaymentsSession();
		result.setClientToken("1234");

		Mockito.doReturn(session).when(defaultKPPaymentFacade).getKlarnaCreditSession(Mockito.anyString());
		Mockito.doReturn(cartmodel).when(cartService).getSessionCart();
		Mockito.doReturn(klarnConfig).when(kpConfigFacade).getKlarnaConfig();
		Mockito.doReturn(new AddressModel()).when(kpAddressReverseConverter).convert(Mockito.any(AddressData.class));
		Mockito.doReturn(klarnaCreditSessionData).when(klarnaCreditSessionConverter).convert(Mockito.any(CartModel.class));
		Mockito.doReturn(sessionResponse).when(session).create(Mockito.any(PaymentsSession.class));

		Mockito.when(sessionResponse.getSessionId()).thenReturn("ASDF");
		Mockito.doReturn(result).when(session).fetch(Mockito.anyString());

		Mockito.doNothing().when(modelService).refresh(Mockito.any(CartModel.class));
		Mockito.doNothing().when(modelService).saveAll();
		final HttpSession session = Mockito.mock(HttpSession.class);
		Mockito.doReturn("ASDF").when(session).getAttribute(Mockito.anyString());
		defaultKPPaymentFacade.getORcreateORUpdateSession(session, new AddressData(), false, false);

	}

	@Test
	public void testUpdateSessionEU() throws ApiException, IOException
	{
		final CartModel cartmodel = new CartModel();
		final KPPaymentInfoModel paymentInfoModel = new KPPaymentInfoModel();
		cartmodel.setPaymentInfo(paymentInfoModel);

		final KlarnaConfigData klarnConfig = new KlarnaConfigData();
		klarnConfig.setMerchantID("ABCD");
		klarnConfig.setSharedSecret("1234");
		klarnConfig.setEndpointMode("LIVE");
		klarnConfig.setEndpointType("KPEndpointType.EUROPE");

		final PaymentsSession result = new PaymentsSession();
		result.setClientToken("1234");

		Mockito.doReturn(session).when(defaultKPPaymentFacade).getKlarnaCreditSession(Mockito.anyString());
		Mockito.doReturn(cartmodel).when(cartService).getSessionCart();
		Mockito.doReturn(klarnConfig).when(kpConfigFacade).getKlarnaConfig();
		Mockito.doReturn(new AddressModel()).when(kpAddressReverseConverter).convert(Mockito.any(AddressData.class));
		Mockito.doReturn(klarnaCreditSessionData).when(klarnaCreditSessionConverter).convert(Mockito.any(CartModel.class));
		Mockito.doReturn(sessionResponse).when(session).create(Mockito.any(PaymentsSession.class));

		Mockito.when(sessionResponse.getSessionId()).thenReturn("ASDF");
		Mockito.doReturn(result).when(session).fetch(Mockito.anyString());

		Mockito.doNothing().when(modelService).refresh(Mockito.any(CartModel.class));
		Mockito.doNothing().when(modelService).saveAll();
		final HttpSession session = Mockito.mock(HttpSession.class);
		Mockito.doReturn("ASDDF").when(session).getAttribute(Mockito.anyString());
		defaultKPPaymentFacade.getORcreateORUpdateSession(session, new AddressData(), false, false);

	}

	@Test
	public void testAcknowledgeOrderNotify() throws ApiException, IOException
	{

		final OrderModel cartmodel = new OrderModel();
		final KPPaymentInfoModel paymentInfoModel = new KPPaymentInfoModel();
		cartmodel.setPaymentInfo(paymentInfoModel);

		final KlarnaConfigData klarnConfig = new KlarnaConfigData();
		klarnConfig.setMerchantID("ABCD");
		klarnConfig.setSharedSecret("1234");
		klarnConfig.setEndpointMode("LIVE");
		klarnConfig.setEndpointType("KPEndpointType.EUROPE");
		klarnConfig.setMerchantReference2("guid");

		Mockito.doReturn(klarnConfig).when(kpConfigFacade).getKlarnaConfig();
		Mockito.doReturn(cartmodel).when(kpOrderService).getOderForKlarnaOrderId(Mockito.anyString());
		Mockito.doReturn(cartmodel).when(modelService).getAttributeValue(Mockito.any(), Mockito.anyString());
		Mockito.doNothing().when(klarnaOrder).updateMerchantReferences(Mockito.anyString(),
				Mockito.any(OrderManagementUpdateMerchantReferences.class));
		Mockito.doNothing().when(klarnaOrder).acknowledgeOrder(Mockito.anyString());
		final OrderData hybrisOrder = new OrderData();
		hybrisOrder.setCode("!234");
		defaultKPPaymentFacade.acknowledgeOrderNotify("asdff", klarnaOrder, hybrisOrder);
	}
}

