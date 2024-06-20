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
import de.hybris.platform.core.model.user.CustomerModel;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.klarna.api.Client;
import com.klarna.api.HttpTransport;
import com.klarna.api.login.KlarnaLoginApi;
import com.klarna.api.merchant_card_service.VirtualCreditCardSettlementsApi;
import com.klarna.api.merchant_card_service.model.CardServiceSettlementRequest;
import com.klarna.api.merchant_card_service.model.CardServiceSettlementResponse;
import com.klarna.api.model.ApiException;
import com.klarna.api.order_management.OrderManagementOrdersApi;
import com.klarna.api.order_management.model.OrderManagementUpdateMerchantReferences;
import com.klarna.api.payments.PaymentsOrdersApi;
import com.klarna.api.payments.PaymentsSessionsApi;
import com.klarna.api.payments.model.PaymentsCreateOrderRequest;
import com.klarna.api.payments.model.PaymentsCustomer;
import com.klarna.api.payments.model.PaymentsMerchantSession;
import com.klarna.api.payments.model.PaymentsOrder;
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.api.signin.model.KlarnaSigninTokenRequest;
import com.klarna.api.signin.model.KlarnaSigninTokenResponse;
import com.klarna.data.KlarnaConfigData;
import com.klarna.data.KlarnaCredentialData;
import com.klarna.data.KlarnaKPConfigData;
import com.klarna.payment.constants.KlarnapaymentConstants;
import com.klarna.payment.enums.KlarnaEnv;
import com.klarna.payment.facades.KPConfigFacade;
import com.klarna.payment.facades.KPPaymentFacade;
import com.klarna.payment.facades.KlarnaSignInFacade;
import com.klarna.payment.model.KPPaymentInfoModel;
import com.klarna.payment.services.KPOrderService;
import com.klarna.payment.util.LogHelper;


public class DefaultKPPaymentFacade implements KPPaymentFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultKPPaymentFacade.class);

	private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
	public static final String KLARNA_LOGIN_BASE_URL = "https://login.klarna.com";
	public static final String KLARNA_LOGIN_TEST_BASE_URL = "https://login.playground.klarna.com";
	public static final String KLARNA_MARKET_EU = "/eu";
	public static final String KLARNA_MARKET_NA = "/na";

	@Resource(name = "klarnaSignInFacade")
	private KlarnaSignInFacade klarnaSignInFacade;

	@Resource(name = "klarnaCustomerProfileReverseConverter")
	private Converter klarnaCustomerProfileReverseConverter;

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
		final KlarnaConfigData klarnConfigData = kpConfigFacade.getKlarnaConfig();

		if (klarnConfigData != null)
		{
			final KlarnaCredentialData credentialData = klarnConfigData.getCredential();
			if (credentialData != null)
			{
				final String merchanId = credentialData.getApiUserName();
				final String sharedSecret = credentialData.getApiPassword();
				final URI endpoint = getKlarnaEnpoint(klarnConfigData, credentialData);

				final String shoporplatform = Config.getParameter("shoporplatform") != null ? Config.getParameter("shoporplatform")
						: "Hybris_SAPCom";
				final String platformversion = Config.getParameter("platformversion") != null ? Config.getParameter("platformversion")
						: "1905";
				final String modulename = Config.getParameter("modulename") != null ? Config.getParameter("modulename") : "KP";
				final String moduleversion = Config.getParameter("moduleversion") != null ? Config.getParameter("moduleversion")
						: "6.0";

				final String USER_AGENT = String.format(
						"Language/Java_%s (Vendor/%s; VM/%s) Module-name-and-version/%s OS/%s Shop-name-and-version/%s",
						getProperty("java.version"), getProperty("java.vendor"), getProperty("java.vm.name"),
						modulename + "_" + moduleversion, getProperty("os.name") + "_" + getProperty("os.version"),
						shoporplatform + "_" + platformversion);

				LOG.warn(USER_AGENT.toString());
				return (new Client(merchanId, sharedSecret, endpoint, USER_AGENT.toString()));
			}
		}
		return null;
	}

	private URI getKlarnaEnpoint(final KlarnaConfigData klarnConfigData, final KlarnaCredentialData credentialData)
	{
		LogHelper.debugLog(LOG, "Entering getKlarnaEnpoint.. ");
		final String marketRegion = credentialData.getMarketRegion();
		if (klarnConfigData.getEnvironment().equals(KlarnaEnv.PRODUCTION))
		{
			if (marketRegion.equals(KlarnapaymentConstants.KLARNA_MARKET_REGION_EUROPE))
			{
				return HttpTransport.EU_BASE_URL;
			}
			if (marketRegion.equals(KlarnapaymentConstants.KLARNA_MARKET_REGION_AMERICAS))
			{
				return HttpTransport.NA_BASE_URL;
			}
			if (marketRegion.equals(KlarnapaymentConstants.KLARNA_MARKET_REGION_ASIA_AND_OCEANIA))
			{
				return URI.create(OC_LIVE_ENDPOINT);
			}
		}
		else
		{
			if (marketRegion.equals(KlarnapaymentConstants.KLARNA_MARKET_REGION_EUROPE))
			{
				return HttpTransport.EU_TEST_BASE_URL;
			}
			if (marketRegion.equals(KlarnapaymentConstants.KLARNA_MARKET_REGION_AMERICAS))
			{
				return HttpTransport.NA_TEST_BASE_URL;
			}
			if (marketRegion.equals(KlarnapaymentConstants.KLARNA_MARKET_REGION_ASIA_AND_OCEANIA))
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
			// Set Access token for Auto login for Klarna Sign In customers
			setAccessTokenForAutoLogin(klarnaCreditSessionData);

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
		final KlarnaKPConfigData kpConfig = klarnConfig.getKpConfig();
		if (kpConfig.getMerchantReference2() != null)
		{
			final Object attrval = getModelService().getAttributeValue(orderModel, kpConfig.getMerchantReference2());
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

	// Klarna Sign In
	private void setAccessTokenForAutoLogin(final PaymentsSession paymentsSession)
	{
		final KlarnaConfigData klarnConfigData = kpConfigFacade.getKlarnaConfig();

		if (getUserService().getCurrentUser() != null && getUserService().getCurrentUser() instanceof CustomerModel)
		{
			final CustomerModel customerModel = (CustomerModel) getUserService().getCurrentUser();
			if (customerModel.getKlarnaCustomerProfile() != null
					&& customerModel.getKlarnaCustomerProfile().getRefreshToken() != null)
			{
				// Klarna Sign In Customer. Get Access Token
				try
				{
					final KlarnaSigninTokenResponse klarnaSigninTokenResponse = getKlarnaSignInTokens(klarnConfigData,
							customerModel.getKlarnaCustomerProfile().getRefreshToken());
					customerModel.getKlarnaCustomerProfile().setRefreshToken(klarnaSigninTokenResponse.getRefreshToken());
					getModelService().save(customerModel.getKlarnaCustomerProfile());
					if (paymentsSession.getCustomer() == null)
					{
						paymentsSession.setCustomer(new PaymentsCustomer());
					}
					paymentsSession.getCustomer().setKlarnaAccessToken(klarnaSigninTokenResponse.getAccessToken());
				}
				catch (final Exception e)
				{
					LOG.error("Exception getting access token for customer uid " + customerModel.getUid() + "... ", e);
				}
			}
		}
	}

	private KlarnaSigninTokenResponse getKlarnaSignInTokens(final KlarnaConfigData klarnaConfigData, final String refreshToken)
			throws ApiException, IOException
	{
		LogHelper.debugLog(LOG, "getKlarnaSignInTokens .. . ");
		final KlarnaSigninTokenRequest klarnaSigninTokenRequest = new KlarnaSigninTokenRequest();

		klarnaSigninTokenRequest.setRefreshToken(refreshToken);
		klarnaSigninTokenRequest
				.setClientId(klarnaConfigData.getCredential() != null ? klarnaConfigData.getCredential().getClientId() : null);
		klarnaSigninTokenRequest.setGrantType(GRANT_TYPE_REFRESH_TOKEN);
		final KlarnaLoginApi klarnaLoginAPI = getKlarnaLoginApi(klarnaConfigData);
		return klarnaLoginAPI.getTokens(klarnaSigninTokenRequest);
	}

	private KlarnaLoginApi getKlarnaLoginApi(final KlarnaConfigData klarnaConfigData)
	{
		LogHelper.debugLog(LOG, "getting new KlarnaLoginApi object.. . ");
		return getKlarnaLoginClient(klarnaConfigData).newKlarnaLoginApi();
	}

	private Client getKlarnaLoginClient(final KlarnaConfigData klarnaConfigData)
	{
		LogHelper.debugLog(LOG, "Getting klarna login client.. ");

		//final String merchanId = klarnConfig.getMerchantID();
		//final String sharedSecret = klarnConfig.getSharedSecret();
		final URI endpoint = getKlarnaLoginEndpoint(klarnaConfigData);

		//final String shoporplatform = Config.getParameter("shoporplatform") != null ? Config.getParameter("shoporplatform")
		//		: "Hybris_SAPCom";
		//final String platformversion = Config.getParameter("platformversion") != null ? Config.getParameter("platformversion")
		//		: "2211";
		//final String modulename = Config.getParameter("modulename") != null ? Config.getParameter("modulename") : "KP";
		//final String moduleversion = Config.getParameter("moduleversion") != null ? Config.getParameter("moduleversion") : "6.0";

		//final String USER_AGENT = String.format(
		//		"Language/Java_%s (Vendor/%s; VM/%s) Module-name-and-version/%s OS/%s Shop-name-and-version/%s",
		//		getProperty("java.version"), getProperty("java.vendor"), getProperty("java.vm.name"),
		//		modulename + "_" + moduleversion, getProperty("os.name") + "_" + getProperty("os.version"),
		//		shoporplatform + "_" + platformversion);

		//LOG.warn(USER_AGENT.toString());
		return (new Client(endpoint));
		//return (new Client(merchanId, sharedSecret, endpoint));
	}

	private URI getKlarnaLoginEndpoint(final KlarnaConfigData klarnaConfigData)
	{
		LogHelper.debugLog(LOG, "Entering getKlarnaLoginEndpoint.. ");
		final StringBuilder uriBuilder = new StringBuilder(StringUtils.EMPTY);
		if (StringUtils.equalsIgnoreCase(klarnaConfigData.getEnvironment(), KlarnaEnv.PRODUCTION.getCode()))
		{
			uriBuilder.append(KLARNA_LOGIN_BASE_URL);
		}
		else
		{
			uriBuilder.append(KLARNA_LOGIN_TEST_BASE_URL);
		}
		// TODO set region
		final KlarnaConfigData klarnConfigData = kpConfigFacade.getKlarnaConfig();
		if (klarnConfigData.getCredential() != null && StringUtils.isNotBlank(klarnConfigData.getCredential().getMarketRegion()))
		{
			uriBuilder.append("/" + klarnConfigData.getCredential().getMarketRegion().toLowerCase());
		}
		return URI.create(uriBuilder.toString());
	}

}
