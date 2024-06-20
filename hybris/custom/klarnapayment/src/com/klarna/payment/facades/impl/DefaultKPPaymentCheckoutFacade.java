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

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.PaymentModeService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.klarna.api.merchant_card_service.model.CardServiceCard;
import com.klarna.api.merchant_card_service.model.CardServiceSettlementRequest;
import com.klarna.api.merchant_card_service.model.CardServiceSettlementResponse;
import com.klarna.api.model.ApiException;
import com.klarna.api.order_management.OrderManagementCapturesApi;
import com.klarna.api.order_management.model.OrderManagementAddress;
import com.klarna.api.order_management.model.OrderManagementCaptureObject;
import com.klarna.api.order_management.model.OrderManagementOrder;
import com.klarna.api.order_management.model.OrderManagementOrderLine;
import com.klarna.api.payments.model.PaymentsOrder;
import com.klarna.data.KlarnaConfigData;
import com.klarna.model.KlarnaConfigModel;
import com.klarna.payment.data.KPPaymentInfoData;
import com.klarna.payment.enums.KlarnaFraudStatusEnum;
import com.klarna.payment.enums.KlarnaOrderTypeEnum;
import com.klarna.payment.facades.KPConfigFacade;
import com.klarna.payment.facades.KPOrderFacade;
import com.klarna.payment.facades.KPPaymentCheckoutFacade;
import com.klarna.payment.facades.KPPaymentFacade;
import com.klarna.payment.model.KPPaymentInfoModel;
import com.klarna.payment.model.OrderFailedEmailProcessModel;
import com.klarna.payment.services.KPCurrencyConversionService;
import com.klarna.payment.services.KPOrderService;
import com.klarna.payment.services.KPPaymentInfoService;
import com.klarna.payment.services.KPTitleService;
import com.klarna.payment.util.KlarnaConversionUtils;
import com.klarna.payment.util.LogHelper;


public class DefaultKPPaymentCheckoutFacade implements KPPaymentCheckoutFacade
{
	protected static final Logger LOG = Logger.getLogger(DefaultKPPaymentCheckoutFacade.class);
	private static final String KLARNA_PAYMENT_CODE = "klarnapayment";
	private static final String KLARNA = "KLARNA_";

	private CartService cartService;
	private ModelService modelService;
	private KPConfigFacade kpConfigFacade;
	private CommerceCheckoutService commerceCheckoutService;

	private UserService userService;
	private Converter<AddressData, AddressModel> kpAddressReverseConverter;
	private PaymentModeService paymentModeService;
	private KPCurrencyConversionService kpCurrencyConversionService;
	private KPPaymentInfoService kpPaymentInfoService;
	private CommonI18NService commonI18NService;
	private DeliveryService deliveryService;
	private KPTitleService kpTitleService;
	private Converter<KPPaymentInfoModel, KPPaymentInfoData> kpPaymentInfoConverter;
	private BusinessProcessService businessProcessService;
	private BaseSiteService baseSiteService;
	private KPOrderFacade kpOrderFacade;
	private KPOrderService kpOrderService;



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
	 * @return the kpOrderFacade
	 */
	public KPOrderFacade getKpOrderFacade()
	{
		return kpOrderFacade;
	}

	/**
	 * @param kpOrderFacade
	 *           the kpOrderFacade to set
	 */
	public void setKpOrderFacade(final KPOrderFacade kpOrderFacade)
	{
		this.kpOrderFacade = kpOrderFacade;
	}

	/**
	 * @return the kpConfigFacade
	 */
	public KPConfigFacade getKpConfigFacade()
	{
		return kpConfigFacade;
	}

	/**
	 * @return the commerceCheckoutService
	 */
	public CommerceCheckoutService getCommerceCheckoutService()
	{
		return commerceCheckoutService;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @return the kpAddressReverseConverter
	 */
	public Converter<AddressData, AddressModel> getKpAddressReverseConverter()
	{
		return kpAddressReverseConverter;
	}

	/**
	 * @return the kpPaymentInfoService
	 */
	public KPPaymentInfoService getKpPaymentInfoService()
	{
		return kpPaymentInfoService;
	}

	/**
	 * @return the deliveryService
	 */
	public DeliveryService getDeliveryService()
	{
		return deliveryService;
	}

	/**
	 * @return the kpTitleService
	 */
	public KPTitleService getKpTitleService()
	{
		return kpTitleService;
	}

	/**
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @return the baseSiteService
	 */
	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * @param baseSiteService
	 *           the baseSiteService to set
	 */
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	/**
	 * @return the businessProcessService
	 */
	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	/**
	 * @param businessProcessService
	 *           the businessProcessService to set
	 */
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	/**
	 * @return the kpPaymentInfoConverter
	 */
	public Converter<KPPaymentInfoModel, KPPaymentInfoData> getKpPaymentInfoConverter()
	{
		return kpPaymentInfoConverter;
	}

	/**
	 * @param kpPaymentInfoConverter
	 *           the kpPaymentInfoConverter to set
	 */
	public void setKpPaymentInfoConverter(final Converter<KPPaymentInfoModel, KPPaymentInfoData> kpPaymentInfoConverter)
	{
		this.kpPaymentInfoConverter = kpPaymentInfoConverter;
	}

	/**
	 * @param kpTitleService
	 *           the kpTitleService to set
	 */
	public void setKpTitleService(final KPTitleService kpTitleService)
	{
		this.kpTitleService = kpTitleService;
	}

	/**
	 * @param deliveryService
	 *           the deliveryService to set
	 */
	public void setDeliveryService(final DeliveryService deliveryService)
	{
		this.deliveryService = deliveryService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @param kpPaymentInfoService
	 *           the kpPaymentInfoService to set
	 */
	public void setKpPaymentInfoService(final KPPaymentInfoService kpPaymentInfoService)
	{
		this.kpPaymentInfoService = kpPaymentInfoService;
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
	 * @param commerceCheckoutService
	 *           the commerceCheckoutService to set
	 */
	public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService)
	{
		this.commerceCheckoutService = commerceCheckoutService;
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
	 * @param kpConfigFacade
	 *           the kpConfigFacade to set
	 */
	public void setKpConfigFacade(final KPConfigFacade kpConfigFacade)
	{
		this.kpConfigFacade = kpConfigFacade;
	}

	/**
	 * @return the kpCurrencyConversionService
	 */
	public KPCurrencyConversionService getKpCurrencyConversionService()
	{
		return kpCurrencyConversionService;
	}

	/**
	 * @param kpCurrencyConversionService
	 *           the kpCurrencyConversionService to set
	 */
	public void setKpCurrencyConversionService(final KPCurrencyConversionService kpCurrencyConversionService)
	{
		this.kpCurrencyConversionService = kpCurrencyConversionService;
	}

	/**
	 * @return the paymentModeService
	 */
	public PaymentModeService getPaymentModeService()
	{
		return paymentModeService;
	}

	/**
	 * @param paymentModeService
	 *           the paymentModeService to set
	 */
	public void setPaymentModeService(final PaymentModeService paymentModeService)
	{
		this.paymentModeService = paymentModeService;
	}

	/**
	 * @return the kpPaymentFacade
	 */
	public KPPaymentFacade getKpPaymentFacade()
	{
		return kpPaymentFacade;
	}

	/**
	 * @param kpPaymentFacade
	 *           the kpPaymentFacade to set
	 */
	public void setKpPaymentFacade(final KPPaymentFacade kpPaymentFacade)
	{
		this.kpPaymentFacade = kpPaymentFacade;
	}

	KPPaymentFacade kpPaymentFacade;

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

	@Override
	public boolean isKlarnaPayment()
	{
		final CartModel cartModel = getCartService().getSessionCart();
		return (cartModel != null && cartModel.getPaymentInfo() != null
				&& cartModel.getPaymentInfo() instanceof KPPaymentInfoModel);
	}

	@Override
	public void saveKlarnaOrderId(final PaymentsOrder authorizationResponse) throws ApiException, IOException
	{
		LogHelper.debugLog(LOG, "Entering saveKlarnaOderID");
		final CartModel cartModel = getCartService().getSessionCart();
		if (cartModel != null)
		{
			cartModel.setKpOrderId(authorizationResponse.getOrderId());
			cartModel.setPaymentMode(getPaymentModeService().getPaymentModeForCode(KLARNA_PAYMENT_CODE));
			final String fraudStatus = authorizationResponse.getFraudStatus();
			cartModel.setKpFraudStatus(fraudStatus);
			final KPPaymentInfoModel paymentInfo = (KPPaymentInfoModel) cartModel.getPaymentInfo();
			final PaymentTransactionModel transaction = getKpPaymentInfoService().findKpPaymentTransaction(cartModel.getCode());

			if (KlarnaFraudStatusEnum.PENDING.getValue().equalsIgnoreCase(fraudStatus))
			{
				cartModel.setIsKpPendingOrder(Boolean.TRUE);

			}
			/************************ KLARNAPII-952 **************************/
			getKpPaymentFacade().createTransactionEntry(paymentInfo.getAuthToken(), paymentInfo, transaction,
					PaymentTransactionType.AUTHORIZATION, TransactionStatus.ACCEPTED.name(),
					TransactionStatusDetails.SUCCESFULL.name());
			//if vcn enabled, ignore autocapture mode, go with handle settlement

			//else if autocapture, do catpure

			final KlarnaConfigData klarnaConfig = getKpConfigFacade().getKlarnaConfig();
			/* Creating settlement request only when fraud status is accepted or FRAUD_RISK_ACCEPTED */
			if (fraudStatus.equals(KlarnaFraudStatusEnum.ACCEPTED.getValue())
					|| fraudStatus.equals(KlarnaFraudStatusEnum.FRAUD_RISK_ACCEPTED.getValue()))
			{

				if (klarnaConfig.getCredential() != null && BooleanUtils.isTrue(klarnaConfig.getCredential().getVcnEnabled()))
				{
					final String vcnKey = klarnaConfig.getCredential().getVcnKey();
					LogHelper.debugLog(LOG, "Going for handle settlement");
					handleSettlement(cartModel, vcnKey);

				}
				getKpPaymentFacade().createTransactionEntry(authorizationResponse.getOrderId(),
						(KPPaymentInfoModel) cartModel.getPaymentInfo(), transaction, PaymentTransactionType.KLARNA_ORDER_PLACED,
						TransactionStatus.ACCEPTED.name(), fraudStatus);
			}
			getModelService().save(cartModel);
			getCartService().setSessionCart(cartModel);
			LogHelper.debugLog(LOG, "Saved Klarna Order ID");

		}
	}

	@Override
	public void doAutoCapture(final String kpOrderId) throws ApiException, IOException
	{
		final KlarnaConfigData klarnaConfig = getKpConfigFacade().getKlarnaConfig();
		if (klarnaConfig.getCredential() != null && BooleanUtils.isFalse(klarnaConfig.getCredential().getVcnEnabled())
				&& (klarnaConfig.getKpConfig() != null && BooleanUtils.isTrue(klarnaConfig.getKpConfig().getAutoCapture())))
		{
			final OrderModel orderModel = kpOrderService.getOderForKlarnaOrderId(kpOrderId);
			final String fraudStatus = orderModel.getKpFraudStatus();
			if (fraudStatus.equals(KlarnaFraudStatusEnum.ACCEPTED.getValue())
					|| fraudStatus.equals(KlarnaFraudStatusEnum.FRAUD_RISK_ACCEPTED.getValue()))
			{
				final OrderManagementCapturesApi captureApi = getKpOrderFacade().captureKlarnaOrder(orderModel.getKpOrderId(),
						orderModel.getStore());
				final OrderManagementCaptureObject captureRequest = new OrderManagementCaptureObject();
				captureRequest.setCapturedAmount(KlarnaConversionUtils.getKlarnaLongValue(kpCurrencyConversionService
						.convertToPurchaseCurrencyPrice(Double.valueOf(orderModel.getTotalPrice().doubleValue()))));
				final String captureId = captureApi.create(captureRequest);
				final PaymentTransactionModel transaction = orderModel.getPaymentTransactions().get(0);

				getKpPaymentFacade().createTransactionEntry(captureId, (KPPaymentInfoModel) orderModel.getPaymentInfo(), transaction,
						PaymentTransactionType.CAPTURE, TransactionStatus.ACCEPTED.name(), TransactionStatusDetails.SUCCESFULL.name());

			}
		}
	}

	public void handleSettlement(final CartModel cartModel, final String vcnKey) throws ApiException, IOException
	{
		LogHelper.debugLog(LOG, "Entering handleSettlement");
		final KPPaymentInfoModel klarnaPaymentInfo = (KPPaymentInfoModel) cartModel.getPaymentInfo();


		LogHelper.debugLog(LOG, "VCN Eanabled");
		klarnaPaymentInfo.setIsVCNUsed(Boolean.TRUE);
		final CardServiceSettlementRequest settlementData = new CardServiceSettlementRequest();
		settlementData.setOrderId(cartModel.getKpOrderId());
		settlementData.setKeyId(vcnKey);
		final CardServiceSettlementResponse sdata = getKpPaymentFacade().createSettlement(settlementData);
		final CardServiceCard cardData = sdata.getCards().get(0);

		try
		{
			klarnaPaymentInfo.setVcnBrand(cardData.getBrand());
			klarnaPaymentInfo.setVcnHolder(cardData.getHolder());

			klarnaPaymentInfo.setKpVCNPCIData(cardData.getPciData());
			klarnaPaymentInfo.setKpVCNIV(cardData.getIv());
			klarnaPaymentInfo.setKpVCNAESKey(cardData.getAesKey());
			klarnaPaymentInfo.setKpVCNCardID(cardData.getCardId());


			getModelService().saveAll();
			LogHelper.debugLog(LOG, "VCN data Saved");
		}
		catch (final Exception e)
		{
			LOG.error("Encryption Error occured :: " + e.getMessage());
		}

	}

	/**
	 *
	 */
	private JsonObject parsePCI(final String parsePCI)
	{
		final JsonParser parser = new JsonParser();
		final JsonElement elements = parser.parse(parsePCI);
		return elements.getAsJsonObject();
	}

	@Override
	public boolean isCartSynchronization(final CartData cartData, final OrderManagementOrder orderData)
	{
		LogHelper.debugLog(LOG, "Entering isCartSynchronization");
		final BigDecimal totalPrice = cartData.isNet() ? cartData.getTotalPriceWithTax().getValue()
				: cartData.getTotalPrice().getValue();
		if (!KlarnaConversionUtils
				.getKlarnaLongValue(
						kpCurrencyConversionService.convertToPurchaseCurrencyPrice(Double.valueOf(totalPrice.doubleValue())))
				.equals(orderData.getOrderAmount()))
		{
			LogHelper.debugLog(LOG, "Cart is not In Synch");
			return false;
		}
		final HashMap<String, Long> productMap = new HashMap<String, Long>();
		for (final OrderManagementOrderLine orderLine : orderData.getOrderLines())
		{
			if (KlarnaOrderTypeEnum.PHYSICAL.getValue().equals(orderLine.getType()))
			{
				productMap.put(orderLine.getReference(), orderLine.getQuantity());
			}
		}
		for (final OrderEntryData orderEntry : cartData.getEntries())
		{
			if (!(productMap.containsKey(orderEntry.getProduct().getCode())
					&& productMap.get(orderEntry.getProduct().getCode()).equals(orderEntry.getQuantity())))
			{
				LogHelper.debugLog(LOG, "Cart is not In Synch");
				return false;
			}
		}
		LogHelper.debugLog(LOG, "Cart is Synch");
		return true;
	}

	@Override
	public void saveAuthorization(final String authorizationToken, final String paymentOption, final Boolean finalizeRequired)
	{
		final CartModel cart = getCartService().getSessionCart();
		KPPaymentInfoModel kpPaymentInfo = null;
		if (!(cart.getPaymentInfo() instanceof KPPaymentInfoModel))
		{
			kpPaymentInfo = getModelService().create(KPPaymentInfoModel.class);
			final String klarnaPaymentCode = KLARNA + cart.getCode();
			kpPaymentInfo.setCode(klarnaPaymentCode);
			kpPaymentInfo.setUser(userService.getCurrentUser());
			kpPaymentInfo.setPaymentOption(paymentOption);
			kpPaymentInfo.setFinalizeRequired(finalizeRequired);
		}
		else
		{
			kpPaymentInfo = (KPPaymentInfoModel) cart.getPaymentInfo();
			kpPaymentInfo.setPaymentOption(paymentOption);
			kpPaymentInfo.setFinalizeRequired(finalizeRequired);
		}

		kpPaymentInfo.setAuthToken(authorizationToken);

		cart.setPaymentInfo(kpPaymentInfo);
		if (getUserService().isAnonymousUser(getUserService().getCurrentUser()))
		{
			cart.setKpAnonymousGUID(cart.getUser().getUid());
		}
		getModelService().saveAll();
	}

	@Override
	public void processPayment(final AddressData addressData, final String sessionId)
	{

		final AddressModel addressModel = getKpAddressReverseConverter().convert(addressData);
		final CartModel cartModel = getCartService().getSessionCart();
		cartModel.setKpIdentifier(KLARNA + cartModel.getCode());
		final KPPaymentInfoModel kpPaymentInfoModel = (KPPaymentInfoModel) cartModel.getPaymentInfo();
		addressModel.setOwner(kpPaymentInfoModel);
		if (kpPaymentInfoModel != null)
		{
			kpPaymentInfoModel.setBillingAddress(addressModel);
		}
		cartModel.setPaymentInfo(kpPaymentInfoModel);
		cartModel.setPaymentAddress(addressModel);
		getCartService().setSessionCart(cartModel);
		final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true);
		parameter.setPaymentInfo(kpPaymentInfoModel);
		getCommerceCheckoutService().setPaymentInfo(parameter);
	}

	protected CommerceCheckoutParameter createCommerceCheckoutParameter(final CartModel cart, final boolean enableHooks)
	{
		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setEnableHooks(enableHooks);
		parameter.setCart(cart);
		return parameter;
	}

	@Override
	public boolean hasNoPaymentInfo()
	{
		final CartModel cartModel = cartService.getSessionCart();
		return cartModel == null || cartModel.getPaymentInfo() == null;
	}

	@Override
	public String getUserGUID()
	{
		final CartModel cartModel = cartService.getSessionCart();
		return StringUtils.substringBefore(cartModel.getKpAnonymousGUID(), "|");

	}

	@Override
	public void removePaymentInfo()
	{
		final CartModel cartModel = cartService.getSessionCart();
		cartModel.setPaymentInfo(null);
	}

	@Override
	public void updateCart(final OrderManagementOrder klarnaOrderData)
	{
		final CartModel cartModel = getCartService().getSessionCart();
		final UserModel customer = getUserService().getUserForUID(cartModel.getKpAnonymousGUID());
		if (customer != null)
		{
			cartModel.setUser(customer);

			final KPPaymentInfoModel kpPaymentInfoModel = getKpPaymentInfoService().getKpPaymentInfo(cartModel.getCode());
			kpPaymentInfoModel.setBillingAddress(cartModel.getPaymentAddress());
			cartModel.setPaymentInfo(kpPaymentInfoModel);
			final AddressModel deliveryAddress = getAddressModel(klarnaOrderData);
			deliveryAddress.setShippingAddress(Boolean.TRUE);
			deliveryAddress.setOwner(customer);
			cartModel.setDeliveryAddress(deliveryAddress);
			cartModel.setDeliveryMode(getDeliveryMode(klarnaOrderData));
			cartModel.setPaymentMode(getPaymentModeService().getPaymentModeForCode(KLARNA_PAYMENT_CODE));
			getModelService().saveAll();

		}
	}

	/**
	 *
	 */
	private DeliveryModeModel getDeliveryMode(final OrderManagementOrder klarnaOrderData)
	{
		final List<OrderManagementOrderLine> orderLines = klarnaOrderData.getOrderLines();
		for (final OrderManagementOrderLine orderLine : orderLines)
		{
			if (orderLine.getType().equals(KlarnaOrderTypeEnum.SHIPPING_FEE.getValue()))
			{
				return getDeliveryService().getDeliveryModeForCode(orderLine.getReference());

			}
		}
		return null;
	}

	private AddressModel getAddressModel(final OrderManagementOrder klarnaOrderData)
	{
		final AddressModel shipAddress = getModelService().create(AddressModel.class);
		final OrderManagementAddress shippingAddress = klarnaOrderData.getShippingAddress();
		shipAddress.setFirstname(shippingAddress.getGivenName());
		shipAddress.setLastname(shippingAddress.getFamilyName());
		shipAddress.setLine1(shippingAddress.getStreetAddress());
		shipAddress.setLine2(shippingAddress.getStreetAddress2());
		shipAddress.setStreetname(shippingAddress.getStreetAddress());
		shipAddress.setStreetnumber(shippingAddress.getStreetAddress2());
		shipAddress.setTown(shippingAddress.getCity());
		if (StringUtils.isNotEmpty(shippingAddress.getTitle()))
		{
			shipAddress.setTitle(getKpTitleService().getTitleByName(shippingAddress.getTitle()));
		}
		shipAddress.setEmail(shippingAddress.getEmail());
		shipAddress.setPostalcode(shippingAddress.getPostalCode());
		shipAddress.setPhone1(shippingAddress.getPhone());

		setAddressCountryNRegion(shipAddress, shippingAddress);
		return shipAddress;
	}

	/**
	 *
	 */
	private void setAddressCountryNRegion(final AddressModel shipAddress, final OrderManagementAddress shippingAddress)
	{
		final CountryModel countryModel = getCommonI18NService().getCountry(shippingAddress.getCountry().toUpperCase());
		shipAddress.setCountry(countryModel);
		String regionIso = null;
		if (shippingAddress.getRegion() != null)
		{
			regionIso = shippingAddress.getCountry() + "-" + shippingAddress.getRegion();
		}

		try
		{
			if (countryModel != null && countryModel.getRegions() != null && !countryModel.getRegions().isEmpty()
					&& regionIso != null)
			{
				shipAddress.setRegion(commonI18NService.getRegion(countryModel, StringUtils.upperCase(regionIso)));
			}
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
		}

	}

	@Override
	public KPPaymentInfoData getKPPaymentInfo()
	{
		final CartModel cartModel = cartService.getSessionCart();
		if (cartModel != null && cartModel.getPaymentInfo() != null)
		{
			if (cartModel.getPaymentInfo() instanceof KPPaymentInfoModel)
			{
				return getKpPaymentInfoConverter().convert((KPPaymentInfoModel) cartModel.getPaymentInfo());

			}

		}
		return null;

	}

	@Override
	public void updatePaymentInfo(final OrderManagementOrder klarnaOrderData)
	{
		final String pay_separator = "#";
		final CartModel cartModel = getCartService().getSessionCart();

		final PaymentTransactionModel transaction = getKpPaymentInfoService().findKpPaymentTransaction(cartModel.getCode());
		final KPPaymentInfoModel kpPaymentInfoModel = (KPPaymentInfoModel) transaction.getInfo();
		final String currPaymentOption = kpPaymentInfoModel.getPaymentOption();

		String init_pay_method = klarnaOrderData.getInitialPaymentMethod().getType().name();
		String description = klarnaOrderData.getInitialPaymentMethod().getDescription();
		String installments = String.valueOf(klarnaOrderData.getInitialPaymentMethod().getNumberOfInstallments());

		//String finalPaymentOption = currPaymentOption.concat(pay_separator+init_pay_method);
		init_pay_method = init_pay_method == null || init_pay_method.equals("null") ? "" : init_pay_method;
		description = description == null || description.equals("null") ? "" : description;
		installments = installments == null || installments.equals("null") ? "" : installments;
		final String finalPaymentOption = currPaymentOption
				.concat(pay_separator + init_pay_method + pay_separator + description + pay_separator + installments);
		kpPaymentInfoModel.setPaymentOption(finalPaymentOption);
		cartModel.setPaymentInfo(kpPaymentInfoModel);
		getModelService().saveAll();
	}

	@Override
	public void sendFailedNotification(final String errorMessage)
	{

		final CartModel cart = cartService.getSessionCart();
		final KlarnaConfigModel config = cart.getStore().getConfig();
		if (config != null && config.getKpConfig() != null && StringUtils.isNotEmpty(config.getKpConfig().getMerchantEmail()))
		{
			final OrderFailedEmailProcessModel orderFailedEmailProcessModel = (OrderFailedEmailProcessModel) getBusinessProcessService()
					.createProcess("OrderFailedEmailProcess-" + cart.getCode() + "-" + System.currentTimeMillis(),
							"orderFailedEmailProcess");
			orderFailedEmailProcessModel.setSite(getBaseSiteService().getCurrentBaseSite());
			orderFailedEmailProcessModel.setStore(cart.getStore());
			orderFailedEmailProcessModel.setCustomer((CustomerModel) cart.getUser());
			orderFailedEmailProcessModel.setLanguage(getCommonI18NService().getCurrentLanguage());
			//orderFailedEmailProcessModel.setCart(cart);
			orderFailedEmailProcessModel.setKpOrderId(cart.getKpOrderId() == null ? "" : cart.getKpOrderId());
			orderFailedEmailProcessModel.setCartId(cart.getCode());
			orderFailedEmailProcessModel.setKperrorMessage(errorMessage);
			final String storeName = cart.getStore().getName() == null ? cart.getStore().getUid() : cart.getStore().getName();
			orderFailedEmailProcessModel.setCountryName(config.getCode() + " / " + storeName);
			getModelService().save(orderFailedEmailProcessModel);
			getBusinessProcessService().startProcess(orderFailedEmailProcessModel);
		}
	}
}
