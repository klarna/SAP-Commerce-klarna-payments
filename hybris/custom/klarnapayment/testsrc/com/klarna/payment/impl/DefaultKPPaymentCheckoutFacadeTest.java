package com.klarna.payment.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.PaymentModeService;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.klarna.api.merchant_card_service.model.CardServiceCard;
import com.klarna.api.merchant_card_service.model.CardServiceSettlementRequest;
import com.klarna.api.merchant_card_service.model.CardServiceSettlementResponse;
import com.klarna.api.model.ApiException;
import com.klarna.api.order_management.model.OrderManagementAddress;
import com.klarna.api.order_management.model.OrderManagementInitialPaymentMethodDto;
import com.klarna.api.order_management.model.OrderManagementOrder;
import com.klarna.api.order_management.model.OrderManagementOrderLine;
import com.klarna.api.payments.model.PaymentsOrder;
import com.klarna.data.KlarnaConfigData;
import com.klarna.data.KlarnaCredentialData;
import com.klarna.data.KlarnaKPConfigData;
import com.klarna.payment.enums.KlarnaFraudStatusEnum;
import com.klarna.payment.enums.KlarnaOrderTypeEnum;
import com.klarna.payment.facades.KPPaymentFacade;
import com.klarna.payment.facades.KlarnaConfigFacade;
import com.klarna.payment.facades.impl.DefaultKPPaymentCheckoutFacade;
import com.klarna.payment.model.KPPaymentInfoModel;
import com.klarna.payment.services.KPCurrencyConversionService;
import com.klarna.payment.services.KPPaymentInfoService;
import com.klarna.payment.services.KPTitleService;


@UnitTest
public class DefaultKPPaymentCheckoutFacadeTest
{

	@Mock
	private CartService cartService;
	@Mock
	private ModelService modelService;
	@Mock
	private KlarnaConfigFacade klarnaConfigFacade;
	@Mock
	private CommerceCheckoutService commerceCheckoutService;
	@Mock
	private UserService userService;
	@Mock
	private Converter<AddressData, AddressModel> kpAddressReverseConverter;
	@Mock
	private PaymentModeService paymentModeService;
	@Mock
	private KPCurrencyConversionService kpCurrencyConversionService;
	@Mock
	private KPPaymentInfoService kpPaymentInfoService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private DeliveryService deliveryService;
	@Mock
	private KPTitleService kpTitleService;
	@Mock
	private KPPaymentFacade kpPaymentFacade;
	DefaultKPPaymentCheckoutFacade defaultKPPaymentCheckoutFacade;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultKPPaymentCheckoutFacade = Mockito.spy(new DefaultKPPaymentCheckoutFacade());
		Mockito.doReturn(cartService).when(defaultKPPaymentCheckoutFacade).getCartService();
		Mockito.doReturn(commerceCheckoutService).when(defaultKPPaymentCheckoutFacade).getCommerceCheckoutService();
		Mockito.doReturn(commonI18NService).when(defaultKPPaymentCheckoutFacade).getCommonI18NService();
		Mockito.doReturn(deliveryService).when(defaultKPPaymentCheckoutFacade).getDeliveryService();
		Mockito.doReturn(kpAddressReverseConverter).when(defaultKPPaymentCheckoutFacade).getKpAddressReverseConverter();
		//Mockito.doReturn(klarnaConfigFacade).when(defaultKPPaymentCheckoutFacade).getKlarnaConfigFacade();
		Mockito.doReturn(kpCurrencyConversionService).when(defaultKPPaymentCheckoutFacade).getKpCurrencyConversionService();
		Mockito.doReturn(kpPaymentFacade).when(defaultKPPaymentCheckoutFacade).getKpPaymentFacade();
		Mockito.doReturn(kpPaymentInfoService).when(defaultKPPaymentCheckoutFacade).getKpPaymentInfoService();
		Mockito.doReturn(kpTitleService).when(defaultKPPaymentCheckoutFacade).getKpTitleService();
		Mockito.doReturn(modelService).when(defaultKPPaymentCheckoutFacade).getModelService();
		Mockito.doReturn(paymentModeService).when(defaultKPPaymentCheckoutFacade).getPaymentModeService();
		Mockito.doReturn(userService).when(defaultKPPaymentCheckoutFacade).getUserService();


	}

	@Test
	public void testIsKlarnaPayment()
	{
		final CartModel cartmodel = new CartModel();
		final KPPaymentInfoModel paymentInfoModel = new KPPaymentInfoModel();
		cartmodel.setPaymentInfo(paymentInfoModel);
		Mockito.doReturn(cartmodel).when(cartService).getSessionCart();
		Assert.assertTrue(defaultKPPaymentCheckoutFacade.isKlarnaPayment());
	}

	@Test
	public void testIsNotKlarnaPayment()
	{
		final CartModel cartmodel = new CartModel();
		final CreditCardPaymentInfoModel paymentInfoModel = new CreditCardPaymentInfoModel();
		cartmodel.setPaymentInfo(paymentInfoModel);
		Mockito.doReturn(cartmodel).when(cartService).getSessionCart();
		Assert.assertFalse(defaultKPPaymentCheckoutFacade.isKlarnaPayment());

	}

	@Test
	public void testSaveKlarnaOrderId()
	{
		final KlarnaConfigData klarnaConfig = new KlarnaConfigData();
		final KlarnaKPConfigData kpConfig = new KlarnaKPConfigData();
		final KlarnaCredentialData cred = new KlarnaCredentialData();
		cred.setVcnEnabled(true);
		cred.setVcnKey("1234");
		kpConfig.setAutoCapture(Boolean.FALSE);
		klarnaConfig.setCredential(cred);
		klarnaConfig.setKpConfig(kpConfig);
		Mockito.doReturn(klarnaConfig).when(klarnaConfigFacade).getKlarnaConfig();

		final CartModel cartmodel = new CartModel();
		final KPPaymentInfoModel paymentInfoModel = new KPPaymentInfoModel();
		cartmodel.setPaymentInfo(paymentInfoModel);
		Mockito.doReturn(cartmodel).when(cartService).getSessionCart();
		final PaymentsOrder authorizationResponse = new PaymentsOrder();
		authorizationResponse.setOrderId("12345");
		authorizationResponse.setFraudStatus(KlarnaFraudStatusEnum.ACCEPTED.getValue());

		try
		{
			Mockito.doNothing().when(defaultKPPaymentCheckoutFacade).handleSettlement(Mockito.any(CartModel.class),
					Mockito.anyString());
			Mockito.doNothing().when(modelService).save(Mockito.any(CartModel.class));
			Mockito.doReturn(new PaymentTransactionModel()).when(kpPaymentInfoService).findKpPaymentTransaction(Mockito.anyString());
			Mockito.doNothing().when(kpPaymentFacade).createTransactionEntry(Mockito.anyString(),
					Mockito.any(KPPaymentInfoModel.class), Mockito.any(PaymentTransactionModel.class),
					Mockito.any(PaymentTransactionType.class), Mockito.anyString(), Mockito.anyString());
			defaultKPPaymentCheckoutFacade.saveKlarnaOrderId(authorizationResponse);
			System.out.println(cartmodel.getKpOrderId());
			Assert.assertEquals("12345", cartmodel.getKpOrderId());
		}
		catch (ApiException | IOException e)
		{
			// YTODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testHandleSettlement()
	{
		final CartModel cartmodel = new CartModel();
		final KPPaymentInfoModel paymentInfoModel = new KPPaymentInfoModel();
		cartmodel.setPaymentInfo(paymentInfoModel);

		final KlarnaConfigData klarnaConfig = new KlarnaConfigData();
		klarnaConfig.getCredential().setVcnEnabled(true);
		klarnaConfig.getCredential().setVcnKey("1234");

		Mockito.doReturn(klarnaConfig).when(klarnaConfigFacade).getKlarnaConfig();

		try
		{
			final CardServiceSettlementResponse sdata = new CardServiceSettlementResponse();
			final CardServiceCard cardData = new CardServiceCard();
			final List<CardServiceCard> cards = new ArrayList<CardServiceCard>();
			cardData.setAmount(Long.getLong("1000"));
			cardData.setBrand("ABC");
			cardData.setHolder("TEST");

			cardData.setPciData("TEST");
			cardData.setIv("TEST");
			cardData.setAesKey("TEST");
			cardData.setCardId("TEST");
			cards.add(cardData);
			sdata.setCards(cards);
			Mockito.doReturn(sdata).when(kpPaymentFacade).createSettlement(Mockito.any(CardServiceSettlementRequest.class));
			Mockito.doNothing().when(modelService).saveAll();
			defaultKPPaymentCheckoutFacade.handleSettlement(cartmodel, "1234");
		}
		catch (ApiException | IOException e)
		{
			// YTODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testSaveAuthorization()
	{
		final CartModel cartmodel = new CartModel();
		final KPPaymentInfoModel paymentInfoModel = new KPPaymentInfoModel();
		cartmodel.setPaymentInfo(paymentInfoModel);
		final UserModel user = new UserModel();
		user.setUid("UIDIDID");
		cartmodel.setUser(user);
		Mockito.doReturn(cartmodel).when(cartService).getSessionCart();
		Mockito.doReturn(new KPPaymentInfoModel()).when(modelService).create(KPPaymentInfoModel.class);
		Mockito.doReturn(user).when(userService).getCurrentUser();
		Mockito.doReturn(true).when(userService).isAnonymousUser(Mockito.any(UserModel.class));
		Mockito.doNothing().when(modelService).saveAll();

		defaultKPPaymentCheckoutFacade.saveAuthorization("ASDF", "SALE", Boolean.TRUE);
	}

	@Test
	public void testProcessPayment()
	{
		final CartModel cartmodel = new CartModel();
		final KPPaymentInfoModel paymentInfoModel = new KPPaymentInfoModel();
		cartmodel.setPaymentInfo(paymentInfoModel);

		final AddressModel addressModel = new AddressModel();
		Mockito.doReturn(cartmodel).when(cartService).getSessionCart();
		Mockito.doReturn(addressModel).when(kpAddressReverseConverter).convert(Mockito.any(AddressData.class));
		Mockito.doReturn(true).when(commerceCheckoutService).setPaymentInfo(Mockito.any(CommerceCheckoutParameter.class));
		defaultKPPaymentCheckoutFacade.processPayment(new AddressData(), "ASDF");
	}

	@Test
	public void testUpdateCart()
	{
		final CartModel cartmodel = new CartModel();
		final KPPaymentInfoModel paymentInfoModel = new KPPaymentInfoModel();
		//cartmodel.setPaymentInfo(paymentInfoModel);

		final AddressModel paymentAddress = new AddressModel();
		cartmodel.setPaymentAddress(paymentAddress);

		final UserModel user = new UserModel();
		user.setUid("UIDIDID");
		cartmodel.setUser(user);

		final OrderManagementAddress shippingAddress = new OrderManagementAddress();
		shippingAddress.setGivenName("ABS");
		shippingAddress.setFamilyName("ABS");
		shippingAddress.setStreetAddress("ABS");
		shippingAddress.setStreetAddress2("ABS");
		shippingAddress.setCity("ABS");
		shippingAddress.setEmail("ABS@ABS.com");
		shippingAddress.setPostalCode("TW31NN");
		shippingAddress.setPhone("1234567890");
		shippingAddress.setCountry("GB");

		final PaymentModeModel paymentModeModel = new PaymentModeModel();
		paymentModeModel.setCode("klarna");

		final OrderManagementOrder orderManagementOrder = new OrderManagementOrder();
		orderManagementOrder.setShippingAddress(shippingAddress);
		final List<OrderManagementOrderLine> orderLines = new ArrayList();
		final OrderManagementOrderLine orderManagementOrderLine = new OrderManagementOrderLine();
		orderManagementOrderLine.setType(KlarnaOrderTypeEnum.SHIPPING_FEE.getValue());
		orderLines.add(orderManagementOrderLine);
		orderManagementOrder.setOrderLines(orderLines);

		Mockito.doReturn(cartmodel).when(cartService).getSessionCart();
		Mockito.doReturn(user).when(userService).getUserForUID(Mockito.anyString());
		Mockito.doReturn(new AddressModel()).when(modelService).create(AddressModel.class);

		Mockito.doReturn(paymentInfoModel).when(kpPaymentInfoService).getKpPaymentInfo(Mockito.anyString());
		Mockito.doReturn(paymentModeModel).when(paymentModeService).getPaymentModeForCode(Mockito.anyString());
		Mockito.doNothing().when(modelService).saveAll();
		defaultKPPaymentCheckoutFacade.updateCart(orderManagementOrder);
	}

	@Test
	public void testUpdatePaymentInfo()
	{
		final CartModel cartmodel = new CartModel();

		final PaymentTransactionModel transaction = new PaymentTransactionModel();
		final KPPaymentInfoModel paymentInfoModel = new KPPaymentInfoModel();
		transaction.setInfo(paymentInfoModel);
		paymentInfoModel.setPaymentOption("klarna");

		final OrderManagementOrder orderManagementOrder = new OrderManagementOrder();
		final OrderManagementInitialPaymentMethodDto dto = new OrderManagementInitialPaymentMethodDto();
		dto.setType("CARD");
		dto.setDescription("Test");
		dto.setNumberOfInstallments(7);
		orderManagementOrder.setInitialPaymentMethod(dto);

		Mockito.doReturn(cartmodel).when(cartService).getSessionCart();
		Mockito.doReturn(transaction).when(kpPaymentInfoService).findKpPaymentTransaction(Mockito.anyString());
		Mockito.doNothing().when(modelService).saveAll();
		defaultKPPaymentCheckoutFacade.updatePaymentInfo(orderManagementOrder);
	}




}
