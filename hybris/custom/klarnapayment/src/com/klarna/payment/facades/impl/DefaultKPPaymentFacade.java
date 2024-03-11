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
package com.klarna.payment.facades.impl;

import static java.lang.System.getProperty;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.klarna.api.Client;
import com.klarna.api.HttpTransport;
import com.klarna.api.merchant_card_service.VirtualCreditCardSettlementsApi;
import com.klarna.api.merchant_card_service.model.CardServiceSettlementRequest;
import com.klarna.api.merchant_card_service.model.CardServiceSettlementResponse;
import com.klarna.api.model.ApiException;
import com.klarna.api.order_management.OrderManagementOrdersApi;
import com.klarna.api.order_management.model.OrderManagementUpdateMerchantReferences;
import com.klarna.api.payments.PaymentsOrdersApi;
import com.klarna.api.payments.PaymentsSessionsApi;
import com.klarna.api.payments.model.PaymentsCreateOrderRequest;
import com.klarna.api.payments.model.PaymentsMerchantSession;
import com.klarna.api.payments.model.PaymentsOrder;
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.payment.data.KlarnaConfigData;
import com.klarna.payment.enums.KPEndpointMode;
import com.klarna.payment.enums.KPEndpointType;
import com.klarna.payment.facades.KPConfigFacade;
import com.klarna.payment.facades.KPPaymentFacade;
import com.klarna.payment.model.KPPaymentInfoModel;
import com.klarna.payment.services.KPOrderService;
import com.klarna.payment.util.LogHelper;


public class DefaultKPPaymentFacade implements KPPaymentFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultKPPaymentFacade.class);
	private KPConfigFacade kpConfigFacade;
	private CartService cartService;
	private ModelService modelService;
	private Converter<AbstractOrderModel, PaymentsSession> klarnaCreditSessionConverter;
	private Converter<AbstractOrderModel, PaymentsSession> klarnaCreditSessionAttConverter;

	/**
	 * @return the klarnaCreditSessionAttConverter
	 */
	public Converter<AbstractOrderModel, PaymentsSession> getKlarnaCreditSessionAttConverter()
	{
		return klarnaCreditSessionAttConverter;
	}

	/**
	 * @param klarnaCreditSessionAttConverter
	 *           the klarnaCreditSessionAttConverter to set
	 */
	public void setKlarnaCreditSessionAttConverter(
			final Converter<AbstractOrderModel, PaymentsSession> klarnaCreditSessionAttConverter)
	{
		this.klarnaCreditSessionAttConverter = klarnaCreditSessionAttConverter;
	}

	/**
	 * @return the kpOrderService
	 */
	public KPOrderService getKpOrderService()
	{
		return kpOrderService;
	}

	/**
	 * @param kpOrderService
	 *           the kpOrderService to set
	 */
	public void setKpOrderService(final KPOrderService kpOrderService)
	{
		this.kpOrderService = kpOrderService;
	}

	/**
	 * @return the kpConfigFacade
	 */
	public KPConfigFacade getKpConfigFacade()
	{
		return kpConfigFacade;
	}

	/**
	 * @return the klarnaCreditSessionInitialConverter
	 */
	public Converter<AbstractOrderModel, PaymentsSession> getKlarnaCreditSessionInitialConverter()
	{
		return klarnaCreditSessionInitialConverter;
	}

	private Converter<AbstractOrderModel, PaymentsSession> klarnaCreditSessionInitialConverter;
	private Converter<AddressData, AddressModel> kpAddressReverseConverter;
	private Converter<PaymentsSession, PaymentsCreateOrderRequest> klarnaPaymentOrderConverter;
	@Resource(name = "kpOrderService")
	private KPOrderService kpOrderService;

	private final String OC_TEST_ENDPOINT = "https://api-oc.playground.klarna.com";
	private final String OC_LIVE_ENDPOINT = "https://api-oc.klarna.com";

	private static final String KP_PAYMENT_PROVIDER = "KlarnaPaymentProvider";
	private UserService userService;
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;
	private CommerceCartService commerceCartService;

	/**
	 * @return the commerceCartCalculationStrategy
	 */
	public CommerceCartCalculationStrategy getCommerceCartCalculationStrategy()
	{
		return commerceCartCalculationStrategy;
	}

	/**
	 * @param commerceCartCalculationStrategy
	 *           the commerceCartCalculationStrategy to set
	 */
	public void setCommerceCartCalculationStrategy(final CommerceCartCalculationStrategy commerceCartCalculationStrategy)
	{
		this.commerceCartCalculationStrategy = commerceCartCalculationStrategy;
	}

	/**
	 * @return the commerceCartService
	 */
	public CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	/**
	 * @param commerceCartService
	 *           the commerceCartService to set
	 */
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the kpAddressReverseConverter
	 */
	public Converter<AddressData, AddressModel> getKpAddressReverseConverter()
	{
		return kpAddressReverseConverter;
	}

	/**
	 * @param kpAddressReverseConverter
	 *           the kpAddressReverseConverter to set
	 */
	public void setKpAddressReverseConverter(final Converter<AddressData, AddressModel> kpAddressReverseConverter)
	{
		this.kpAddressReverseConverter = kpAddressReverseConverter;
	}

	/**
	 * @param klarnaCreditSessionInitialConverter
	 *           the klarnaCreditSessionInitialConverter to set
	 */
	public void setKlarnaCreditSessionInitialConverter(
			final Converter<AbstractOrderModel, PaymentsSession> klarnaCreditSessionInitialConverter)
	{
		this.klarnaCreditSessionInitialConverter = klarnaCreditSessionInitialConverter;
	}

	/**
	 * @param kpConfigFacade
	 *           the kpConfigFacade to set
	 */
	public void setKpConfigFacade(final KPConfigFacade kpConfigFacade)
	{
		this.kpConfigFacade = kpConfigFacade;
	}

	/**
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the klarnaCreditSessionConverter
	 */
	public Converter<AbstractOrderModel, PaymentsSession> getKlarnaCreditSessionConverter()
	{
		return klarnaCreditSessionConverter;
	}

	/**
	 * @param klarnaCreditSessionConverter
	 *           the klarnaCreditSessionConverter to set
	 */
	public void setKlarnaCreditSessionConverter(final Converter<AbstractOrderModel, PaymentsSession> klarnaCreditSessionConverter)
	{
		this.klarnaCreditSessionConverter = klarnaCreditSessionConverter;
	}

	@Override
	public PaymentsSessionsApi getKlarnaCreditSession(final String sessionId)
	{
		return getKlarnaClient().newPaymentsSessionsApi();
	}



	/**
	 * @return the klarnaPaymentOrderConverter
	 */
	public Converter<PaymentsSession, PaymentsCreateOrderRequest> getKlarnaPaymentOrderConverter()
	{
		return klarnaPaymentOrderConverter;
	}

	/**
	 * @param klarnaPaymentOrderConverter
	 *           the klarnaPaymentOrderConverter to set
	 */
	public void setKlarnaPaymentOrderConverter(
			final Converter<PaymentsSession, PaymentsCreateOrderRequest> klarnaPaymentOrderConverter)
	{
		this.klarnaPaymentOrderConverter = klarnaPaymentOrderConverter;
	}



	@Override
	public Client getKlarnaClient()
	{
		LogHelper.debugLog(LOG, "Getting klarna client.. ");
		final KlarnaConfigData klarnConfig = kpConfigFacade.getKlarnaConfig();
		final String merchanId = klarnConfig.getMerchantID();
		final String sharedSecret = klarnConfig.getSharedSecret();
		final URI endpoint = getKlarnaEnpoint(klarnConfig);

		final String shoporplatform = Config.getParameter("shoporplatform") != null ? Config.getParameter("shoporplatform")
				: "Hybris_SAPCom";
		final String platformversion = Config.getParameter("platformversion") != null ? Config.getParameter("platformversion")
				: "1905";
		final String modulename = Config.getParameter("modulename") != null ? Config.getParameter("modulename") : "KP";
		final String moduleversion = Config.getParameter("moduleversion") != null ? Config.getParameter("moduleversion") : "6.0";

		final String USER_AGENT = String.format(
				"Language/Java_%s (Vendor/%s; VM/%s) Module-name-and-version/%s OS/%s Shop-name-and-version/%s",
				getProperty("java.version"), getProperty("java.vendor"), getProperty("java.vm.name"),
				modulename + "_" + moduleversion, getProperty("os.name") + "_" + getProperty("os.version"),
				shoporplatform + "_" + platformversion);

		LOG.warn(USER_AGENT.toString());
		return (new Client(merchanId, sharedSecret, endpoint, USER_AGENT.toString()));
		//return (new Client(merchanId, sharedSecret, endpoint));
	}

	private URI getKlarnaEnpoint(final KlarnaConfigData klarnConfig)
	{
		LogHelper.debugLog(LOG, "Entering getKlarnaEnpoint.. ");
		if (klarnConfig.getEndpointMode().equals(KPEndpointMode.LIVE.toString()))
		{
			if (klarnConfig.getEndpointType().equals(KPEndpointType.EUROPE.toString()))
			{
				return HttpTransport.EU_BASE_URL;
			}
			if (klarnConfig.getEndpointType().equals(KPEndpointType.NORTH_AMERICA.toString()))
			{
				return HttpTransport.NA_BASE_URL;
			}
			if (klarnConfig.getEndpointType().equals(KPEndpointType.OCEANIA.toString()))
			{
				return URI.create(OC_LIVE_ENDPOINT);
			}
		}
		else
		{
			if (klarnConfig.getEndpointType().equals(KPEndpointType.EUROPE.toString()))
			{
				return HttpTransport.EU_TEST_BASE_URL;
			}
			if (klarnConfig.getEndpointType().equals(KPEndpointType.NORTH_AMERICA.toString()))
			{
				return HttpTransport.NA_TEST_BASE_URL;
			}
			if (klarnConfig.getEndpointType().equals(KPEndpointType.OCEANIA.toString()))
			{
				return URI.create(OC_TEST_ENDPOINT);
			}
		}
		return null;
	}


	@Override
	public PaymentsSession getORcreateORUpdateSession(final HttpSession httpSession, final AddressData addressData,
			final boolean isPaymentSelected, final boolean isFinal) throws ApiException, IOException
	{
		LogHelper.debugLog(LOG, "Entering getORcreateORUpdateSession.. ");
		final String sessionId = (String) httpSession.getAttribute("sessionId");
		final PaymentsSessionsApi creditSession = getKlarnaCreditSession(sessionId);
		final CartModel cart = getCartService().getSessionCart();
		getModelService().refresh(cart);
		if (addressData != null)
		{
			final AddressModel addressModel = getKpAddressReverseConverter().convert(addressData);
			addressModel.setOwner(cart);
			cart.setPaymentAddress(addressModel);
			getModelService().saveAll();
		}
		else
		{
			cart.setPaymentAddress(null);
			getModelService().saveAll();
		}
		PaymentsSession klarnaCreditSessionData;
		if (sessionId == null)
		{

			LogHelper.debugLog(LOG, "Session id not Present.. ");
			if (getKpConfigFacade().isNorthAmerianKlarnaPayment())
			{
				klarnaCreditSessionData = getKlarnaCreditSessionConverter().convert(cart);
			}
			else
			{
				klarnaCreditSessionData = getKlarnaCreditSessionInitialConverter().convert(cart);
			}
			LogHelper.debugLog(LOG, "Going to create Session ... . ");
			final PaymentsMerchantSession sessionResponse = creditSession.create(klarnaCreditSessionData);
			httpSession.setAttribute("sessionId", sessionResponse.getSessionId());
			klarnaCreditSessionData = creditSession.fetch(sessionResponse.getSessionId());

		}
		else
		{
			LogHelper.debugLog(LOG, "Session id  Present.. ");

			klarnaCreditSessionData = updateSession(isFinal, cart, isPaymentSelected, creditSession, sessionId);
		}
		return klarnaCreditSessionData;

	}


	private PaymentsSession updateSession(final boolean isFinal, final CartModel cart, final boolean isPaymentSelected,
			final PaymentsSessionsApi creditSession, final String sessionId) throws ApiException, IOException
	{
		PaymentsSession klarnaCreditSessionData;
		if (isFinal)
		{
			klarnaCreditSessionData = klarnaCreditSessionAttConverter.convert(cart);

		}
		else
		{
			if (getKpConfigFacade().isNorthAmerianKlarnaPayment() || isPaymentSelected)
			{
				klarnaCreditSessionData = getKlarnaCreditSessionConverter().convert(cart);
			}
			else
			{
				klarnaCreditSessionData = getKlarnaCreditSessionInitialConverter().convert(cart);
			}
		}
		LogHelper.debugLog(LOG, "Going to update Session ... . ");
		creditSession.update(sessionId, klarnaCreditSessionData);
		klarnaCreditSessionData = creditSession.fetch(sessionId);
		return klarnaCreditSessionData;

	}

	@Override
	public PaymentsOrdersApi getKlarnaAuthorization(final String authorizationToken)
	{
		LogHelper.debugLog(LOG, "getting new Authorization object.. . ");
		return getKlarnaClient().newPaymentsOrdersApi();
	}

	@Override
	public PaymentsOrdersApi getKlarnaDeleteAuthorization(final String authorizationToken) throws ApiException, IOException
	{
		final CartModel cart = cartService.getSessionCart();
		final KPPaymentInfoModel kpPaymentInfo = (KPPaymentInfoModel) cart.getPaymentInfo();

		//Delete Authorization if different payment method
		if (kpPaymentInfo == null)
		{
			LogHelper.debugLog(LOG, "deleting Authorization.. . ");
			final PaymentsOrdersApi paymentsOrdersApi = getKlarnaClient().newPaymentsOrdersApi();
			paymentsOrdersApi.cancelAuthorization(authorizationToken);
			return paymentsOrdersApi;
		}
		return null;
	}


	@Override
	public PaymentsOrder getPaymentAuthorization(final String sessionId) throws ApiException, IOException
	{
		LogHelper.debugLog(LOG, "Going to Authorize Payment .. . ");
		final CartModel cart = cartService.getSessionCart();
		modelService.refresh(cart);

		//	final PaymentsSessionsApi creditSession = this.getKlarnaCreditSession(sessionId);
		final PaymentsSession klarnaCreditSessionData = klarnaCreditSessionAttConverter.convert(cart);


		//creditSession.update(sessionId, klarnaCreditSessionData);

		final PaymentsCreateOrderRequest request = klarnaPaymentOrderConverter.convert(klarnaCreditSessionData);
		final KPPaymentInfoModel kpPaymentInfoModel = (KPPaymentInfoModel) cart.getPaymentInfo();
		final PaymentsOrdersApi klarnaAuthorize = getKlarnaAuthorization(kpPaymentInfoModel.getAuthToken());
		return klarnaAuthorize.create(kpPaymentInfoModel.getAuthToken(), request);
	}

	@Override
	public String getAuthToken()
	{
		LogHelper.debugLog(LOG, "Going to get token Authorize Payment .. . ");
		final CartModel cart = cartService.getSessionCart();
		modelService.refresh(cart);
		final KPPaymentInfoModel kpPaymentInfoModel = (KPPaymentInfoModel) cart.getPaymentInfo();
		return kpPaymentInfoModel.getAuthToken();
	}

	@Override
	public CardServiceSettlementResponse createSettlement(final CardServiceSettlementRequest settlementData)
			throws ApiException, IOException
	{
		LogHelper.debugLog(LOG, "Going Create Settlement .. . ");
		final VirtualCreditCardSettlementsApi settlement = getKlarnaClient().newVirtualCreditCardSettlementsApi();
		return (settlement.createSettlement(settlementData));
	}

	@Override
	public OrderManagementOrdersApi getKlarnaOrderById()
	{
		return getKlarnaClient().newOrderManagementOrdersApi();
	}

	@Override
	public void acknowledgeOrderNotify(final String kpOrderId, final OrderManagementOrdersApi klarnaOrder,
			final de.hybris.platform.commercefacades.order.data.OrderData hybrisOrder) throws ApiException, IOException
	{
		LogHelper.debugLog(LOG, "Acknowledge Order creation  .. . ");
		final OrderManagementUpdateMerchantReferences paramUpdateMerchantReferences = new OrderManagementUpdateMerchantReferences();
		paramUpdateMerchantReferences.setMerchantReference1(hybrisOrder.getCode());
		/* Set MerchantReference2 */
		final KlarnaConfigData klarnConfig = getKpConfigFacade().getKlarnaConfig();
		final OrderModel orderModel = getKpOrderService().getOderForKlarnaOrderId(kpOrderId);
		if (klarnConfig.getMerchantReference2() != null)
		{
			final Object attrval = getModelService().getAttributeValue(orderModel, klarnConfig.getMerchantReference2());
			if (attrval != null)
			{
				final String reference2 = attrval.toString();
				paramUpdateMerchantReferences.setMerchantReference2(reference2);
				LOG.warn("Setting MerchantReference2 :" + paramUpdateMerchantReferences.getMerchantReference2());
			}
		}

		klarnaOrder.updateMerchantReferences(kpOrderId, paramUpdateMerchantReferences);
		klarnaOrder.acknowledgeOrder(kpOrderId);
	}

	@Override
	public void createPaymentTransaction()
	{
		final CartModel cart = getCartService().getSessionCart();
		final KPPaymentInfoModel kpPaymentInfo = (KPPaymentInfoModel) cart.getPaymentInfo();
		final PaymentTransactionModel transaction = modelService.create(PaymentTransactionModel.class);
		//final PaymentTransactionType paymentTransactionType = PaymentTransactionType.CREATE_SUBSCRIPTION;
		transaction.setCode(cart.getCode());
		transaction.setRequestId(kpPaymentInfo.getCode());
		transaction.setRequestToken(kpPaymentInfo.getAuthToken());
		transaction.setPaymentProvider(KP_PAYMENT_PROVIDER);
		transaction.setInfo(kpPaymentInfo);
		modelService.save(transaction);

		//create transactionentry for transaction
		//createTransactionEntry(kpPaymentInfo, transaction, paymentTransactionType);
		//create transaction for cart
		createTransactionForCart(cart.getUser().getUid(), cart.getCode(), transaction);

	}

	@Override
	public void createTransactionEntry(final String token, final KPPaymentInfoModel kpPaymentInfo,
			final PaymentTransactionModel transaction, final PaymentTransactionType paymentTransactionType,
			final String transactionStatus, final String transactionStatusDetail)
	{
		final String newEntryCode = this.getNewPaymentTransactionEntryCode(transaction, paymentTransactionType);
		final CartModel cart = getCartService().getSessionCart();
		final PaymentTransactionEntryModel entry = modelService.create(PaymentTransactionEntryModel.class);
		entry.setAmount(BigDecimal.valueOf(cart.getTotalPrice()));
		entry.setCurrency(cart.getCurrency());
		entry.setType(paymentTransactionType);
		entry.setPaymentTransaction(transaction);
		entry.setRequestId(kpPaymentInfo.getCode());
		entry.setRequestToken(token);
		entry.setTransactionStatus(transactionStatus);
		entry.setTransactionStatusDetails(transactionStatusDetail);

		entry.setCode(newEntryCode);
		modelService.save(entry);
	}

	private void createTransactionForCart(final String uid, final String code, final PaymentTransactionModel transaction)
	{
		final UserModel user = getUserService().getUserForUID(uid);
		final CartModel cartModel = getCommerceCartService().getCartForCodeAndUser(code, user);
		final List<PaymentTransactionModel> transactions = new ArrayList();
		transactions.add(transaction);
		cartModel.setPaymentTransactions(transactions);
		modelService.save(cartModel);

		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
		commerceCartParameter.setEnableHooks(true);
		commerceCartParameter.setCart(cartModel);
		getCommerceCartCalculationStrategy().calculateCart(commerceCartParameter);
	}

	private String getNewPaymentTransactionEntryCode(final PaymentTransactionModel transaction,
			final PaymentTransactionType paymentTransactionType)
	{
		return transaction.getEntries() == null ? transaction.getCode() + "-" + paymentTransactionType.getCode() + "-1"
				: transaction.getCode() + "-" + paymentTransactionType.getCode() + "-" + (transaction.getEntries().size() + 1);
	}

	@Override
	public void deletePaymentTransaction()
	{
		final CartModel cart = getCartService().getSessionCart();
		for (final PaymentTransactionModel txn : cart.getPaymentTransactions())
		{
			if (txn.getInfo() instanceof KPPaymentInfoModel)
			{
				modelService.remove(txn);
			}
		}

	}

}
