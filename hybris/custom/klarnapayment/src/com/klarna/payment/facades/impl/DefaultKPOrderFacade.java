package com.klarna.payment.facades.impl;

import static java.lang.System.getProperty;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.strategies.SubmitOrderStrategy;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.klarna.api.Client;
import com.klarna.api.HttpTransport;
import com.klarna.api.merchant_card_service.model.CardServiceCard;
import com.klarna.api.merchant_card_service.model.CardServiceSettlementRequest;
import com.klarna.api.merchant_card_service.model.CardServiceSettlementResponse;
import com.klarna.api.model.ApiException;
import com.klarna.api.model.ErrorMessage;
import com.klarna.api.order_management.OrderManagementCapturesApi;
import com.klarna.api.order_management.OrderManagementOrdersApi;
import com.klarna.api.order_management.OrderManagementRefundsApi;
import com.klarna.api.order_management.model.OrderManagementCaptureObject;
import com.klarna.api.order_management.model.OrderManagementOrder;
import com.klarna.api.order_management.model.OrderManagementRefundObject;
import com.klarna.api.shoppingdata.KlarnaShoppingDataAPI;
import com.klarna.data.KlarnaConfigData;
import com.klarna.data.KlarnaCredentialData;
import com.klarna.payment.constants.GeneratedKlarnapaymentConstants.Enumerations.KlarnaEnv;
import com.klarna.payment.constants.KlarnapaymentConstants;
import com.klarna.payment.enums.KlarnaFraudStatusEnum;
import com.klarna.payment.facades.KPOrderFacade;
import com.klarna.payment.facades.KPPaymentFacade;
import com.klarna.payment.facades.KlarnaConfigFacade;
import com.klarna.payment.model.KPPaymentInfoModel;
import com.klarna.payment.services.KPCurrencyConversionService;
import com.klarna.payment.services.KPOrderService;
import com.klarna.payment.services.KPPaymentInfoService;
import com.klarna.payment.util.KlarnaConversionUtils;
import com.klarna.payment.util.LogHelper;



public class DefaultKPOrderFacade implements KPOrderFacade
{
	protected static final Logger LOG = Logger.getLogger(DefaultKPOrderFacade.class);
	private static final String ENDMODE_TEST = "TEST";
	private static final String EVENT = "_waitKlarnaFraudRiskEvent";
	private static final String ALLOWED_ORDER_STATUS = "orderstatus_suspension_allowed";
	private ModelService modelService;
	private SubmitOrderStrategy eventPublishingSubmitOrderStrategy;
	private KPOrderService kpOrderService;
	private BusinessProcessService businessProcessService;
	private KlarnaConfigFacade klarnaConfigFacade;
	private KPPaymentFacade kpPaymentFacade;
	private UserService userService;
	private CartService cartService;
	private CustomerAccountService customerAccountService;
	private BaseStoreService baseStoreService;
	private KPCurrencyConversionService kpCurrencyConversionService;
	private KPPaymentInfoService kpPaymentInfoService;


	private final String OC_LIVE_ENDPOINT = "https://api-oc.klarna.com";
	private final String OC_TEST_ENDPOINT = "https://api-oc.playground.klarna.com";

	private final String KLARNA_ORDER_REVIEW_ACCEPTED = "KLARNA_ORDER_REVIEW_ACCEPTED";
	private final String KLARNA_ORDER_REVIEW_REJECTED = "KLARNA_ORDER_REVIEW_REJECTED";

	private static final String ORDER_NOT_FOUND_FOR_USER_AND_BASE_STORE = "Order with guid %s not found for current user in current BaseStore";


	/**
	 * @return the kpPaymentInfoService
	 */
	public KPPaymentInfoService getKpPaymentInfoService()
	{
		return kpPaymentInfoService;
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
	 * @return the customerAccountService
	 */
	public CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	/**
	 * @param customerAccountService
	 *           the customerAccountService to set
	 */
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
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

	/**
	 * @return the klarnaConfigFacade
	 */
	public KlarnaConfigFacade getKlarnaConfigFacade()
	{
		return klarnaConfigFacade;
	}

	/**
	 * @param klarnaConfigFacade
	 *           the klarnaConfigFacade to set
	 */
	public void setKlarnaConfigFacade(final KlarnaConfigFacade klarnaConfigFacade)
	{
		this.klarnaConfigFacade = klarnaConfigFacade;
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
	 * @return the eventPublishingSubmitOrderStrategy
	 */
	public SubmitOrderStrategy getEventPublishingSubmitOrderStrategy()
	{
		return eventPublishingSubmitOrderStrategy;


	}

	/**
	 * @param eventPublishingSubmitOrderStrategy
	 *           the eventPublishingSubmitOrderStrategy to set
	 */
	public void setEventPublishingSubmitOrderStrategy(final SubmitOrderStrategy eventPublishingSubmitOrderStrategy)
	{
		this.eventPublishingSubmitOrderStrategy = eventPublishingSubmitOrderStrategy;
	}

	@Override
	public void updateOrderForPending(final String klarnaOrderId, final String status) throws ApiException, IOException
	{
		LogHelper.debugLog(LOG, "Entering updateOrderForPending");
		LogHelper.debugLog(LOG, "Order PENDING Notification status : " + status);
		final OrderModel orderModel = kpOrderService.getOderForKlarnaOrderId(klarnaOrderId);
		if (orderModel != null && orderModel.getStatus() != OrderStatus.COMPLETED)
		{
			orderModel.setKpFraudStatus(status);
			final PaymentTransactionModel transaction = orderModel.getPaymentTransactions().get(0);


			if (status.equals(KlarnaFraudStatusEnum.REJECTED.getValue())
					|| status.equals(KlarnaFraudStatusEnum.FRAUD_RISK_REJECTED.getValue()))
			{
				orderModel.setStatus(OrderStatus.SUSPENDED);
				final KPPaymentInfoModel paymentInfo = (KPPaymentInfoModel) orderModel.getPaymentInfo();
				getKpPaymentFacade().createTransactionEntry(klarnaOrderId, paymentInfo, transaction,
						PaymentTransactionType.KLARNA_ORDER_PLACED, status, KLARNA_ORDER_REVIEW_REJECTED);
			}

			handleFraudriskStopped(status, orderModel);
			modelService.save(orderModel);
			LogHelper.debugLog(LOG, "Order Updated on PENDING Notification");

			/**************** KLARNAPII-952 *******************/
			final KlarnaConfigData klarnaConfig = getKlarnaConfigFacade().getKlarnaConfig();
			final KlarnaCredentialData credential = klarnaConfig.getCredential();
			if (credential != null)
			{
				if (status.equals(KlarnaFraudStatusEnum.ACCEPTED.getValue())
						|| status.equals(KlarnaFraudStatusEnum.FRAUD_RISK_ACCEPTED.getValue()))
				{
					String captureId = null;
					boolean capture = false;
					final KPPaymentInfoModel paymentInfo = (KPPaymentInfoModel) orderModel.getPaymentInfo();

					if (BooleanUtils.isTrue(credential.getVcnEnabled()))
					{
						handleSettlement(orderModel);
					}
					else if (klarnaConfig.getKpConfig() != null && BooleanUtils.isTrue(klarnaConfig.getKpConfig().getAutoCapture())
							&& orderModel.getStatus() != OrderStatus.PAYMENT_CAPTURED)
					{
						final OrderManagementCapturesApi captureApi = captureKlarnaOrder(orderModel.getKpOrderId(),
								orderModel.getStore());
						final OrderManagementCaptureObject captureRequest = new OrderManagementCaptureObject();
						captureRequest.setCapturedAmount(KlarnaConversionUtils.getKlarnaLongValue(kpCurrencyConversionService
								.convertToPurchaseCurrencyPrice(Double.valueOf(orderModel.getTotalPrice().doubleValue()))));
						captureId = captureApi.create(captureRequest);
						capture = true;

					}
					LogHelper.debugLog(LOG, " Updated transaction entry to Authorization for transaction " + transaction.getCode());

					kpPaymentFacade.createTransactionEntry(klarnaOrderId, (KPPaymentInfoModel) orderModel.getPaymentInfo(),
							transaction, PaymentTransactionType.KLARNA_ORDER_PLACED, status, KLARNA_ORDER_REVIEW_ACCEPTED);
					if (capture)
					{
						kpPaymentFacade.createTransactionEntry(captureId, (KPPaymentInfoModel) orderModel.getPaymentInfo(), transaction,
								PaymentTransactionType.CAPTURE, TransactionStatus.ACCEPTED.name(),
								TransactionStatusDetails.SUCCESFULL.name());
					}
					eventPublishingSubmitOrderStrategy.submitOrder(orderModel);

				}
			}

		}

	}

	private void handleFraudriskStopped(final String status, final OrderModel orderModel)
	{
		if (status.equals(KlarnaFraudStatusEnum.FRAUD_RISK_STOPPED.getValue()))
		{
			if (isOrderSuspensionAllowed(orderModel.getStatus()))
			{
				orderModel.setIsKpFraudRiskStopped(Boolean.TRUE);
				orderModel.setStatus(OrderStatus.SUSPENDED);
				final String env = klarnaConfigFacade.getKlarnaConfig().getEnvironment();
				if (StringUtils.isNotBlank(env) && env.equalsIgnoreCase(KlarnaEnv.PLAYGROUND))
				{
					getBusinessProcessService().triggerEvent(orderModel.getCode() + EVENT); //NOSONAR
				}

				LogHelper.debugLog(LOG, "Order is supended");
			}
			else
			{
				orderModel.setIsKpFraudRiskStopped(Boolean.FALSE);
			}
		}
	}

	/**
	 * @throws IOException
	 * @throws ApiException
	 *
	 */
	private void handleSettlement(final OrderModel orderModel) throws ApiException, IOException
	{
		LogHelper.debugLog(LOG, "Entering handleSettlement");
		final KPPaymentInfoModel klarnaPaymentInfo = (KPPaymentInfoModel) orderModel.getPaymentInfo();
		final KlarnaConfigData klarnaConfig = klarnaConfigFacade.getKlarnaConfig();
		final KlarnaCredentialData credentialData = klarnaConfig.getCredential();
		if (credentialData != null)
		{
			if (BooleanUtils.isTrue(credentialData.getVcnEnabled()))
			{
				LogHelper.debugLog(LOG, "VCN Eanabled");
				klarnaPaymentInfo.setIsVCNUsed(Boolean.TRUE);
				final CardServiceSettlementRequest settlementData = new CardServiceSettlementRequest();
				settlementData.setOrderId(orderModel.getKpOrderId());
				settlementData.setKeyId(credentialData.getVcnKey());
				final CardServiceSettlementResponse sdata = kpPaymentFacade.createSettlement(settlementData);
				final CardServiceCard cardData = sdata.getCards().get(0);
				try
				{
					klarnaPaymentInfo.setVcnBrand(cardData.getBrand());
					klarnaPaymentInfo.setVcnHolder(cardData.getHolder());
					klarnaPaymentInfo.setKpVCNPCIData(cardData.getPciData());
					klarnaPaymentInfo.setKpVCNIV(cardData.getIv());
					klarnaPaymentInfo.setKpVCNAESKey(cardData.getAesKey());
					klarnaPaymentInfo.setKpVCNCardID(cardData.getCardId());
					modelService.saveAll();
					LogHelper.debugLog(LOG, "VCN data Saved");
				}
				catch (final Exception e)
				{
					LOG.error("Encryption Error occured :: " + e.getMessage());
				}
			}
		}
	}

	private JsonObject parsePCI(final String parsePCI)
	{
		final JsonParser parser = new JsonParser();
		final JsonElement elements = parser.parse(parsePCI);
		return elements.getAsJsonObject();
	}

	private boolean isOrderSuspensionAllowed(final OrderStatus orderStatus)
	{

		final String allowed = Config.getParameter(ALLOWED_ORDER_STATUS);
		if (orderStatus == null || StringUtils.isEmpty(allowed))
		{
			return true;
		}
		else
		{
			final String[] allowedStatus = allowed.split(",");
			for (final String stat : allowedStatus)
			{
				if (orderStatus.getCode().equals(stat))
				{
					return true;
				}
			}

		}
		return false;
	}



	@Override
	public String getKlarnaIdByOrderByKid(final String kid)
	{

		final AbstractOrderModel orderModel = kpOrderService.getOrderForKId(kid);
		if (orderModel == null)
		{
			return null;
		}
		return orderModel.getKpOrderId();

	}

	@Override
	public OrderManagementCapturesApi captureKlarnaOrder(final String orderId, final BaseStoreModel store)
	{
		return getKlarnaClient(store).newOrderManagementCapturesApi(orderId);
	}

	@Override
	public Client getKlarnaClient(final BaseStoreModel store)
	{
		LogHelper.debugLog(LOG, "Getting klarna client.. ");
		final KlarnaConfigData klarnConfigData = klarnaConfigFacade.getKlarnaConfig();

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
				//final String sdkVesrion = Config.getParameter("sdkversion") != null ? Config.getParameter("sdkversion") : "4.0.1";

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
	public String cancelKlarnaOrder(final String order_Id)
	{
		String cancelStatus = null;
		OrderModel orderModel = null;
		try
		{

			final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();

			orderModel = getCustomerAccountService().getOrderForCode((CustomerModel) getUserService().getCurrentUser(), order_Id,
					baseStoreModel);

			final OrderManagementOrdersApi klarnaOrder = kpPaymentFacade.getKlarnaOrderById();
			klarnaOrder.cancelOrder(orderModel.getKpOrderId());
			cancelStatus = "success";
		}
		catch (final ApiException e)
		{
			final ErrorMessage error = e.getErrorMessage();
			return error.getErrorCode();
		}
		catch (final IOException e)
		{
			LOG.error(e.getMessage());
			return e.getMessage();
		}
		catch (final ModelNotFoundException e)
		{
			throw new UnknownIdentifierException(String.format(ORDER_NOT_FOUND_FOR_USER_AND_BASE_STORE, order_Id));
		}
		return cancelStatus;
	}

	@Override
	public String refundKlarnaOrder(final String orderId)
	{
		String refundStatus = null;
		OrderModel orderModel = null;
		try
		{

			final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();

			orderModel = getCustomerAccountService().getOrderForCode((CustomerModel) getUserService().getCurrentUser(), orderId,
					baseStoreModel);

			final OrderManagementOrdersApi klarnaOrder = kpPaymentFacade.getKlarnaOrderById();
			final OrderManagementOrder fetchOrder = klarnaOrder.fetch(orderModel.getKpOrderId());

			final Long refundedAmount = fetchOrder.getCapturedAmount();

			final OrderManagementRefundObject refundObject = new OrderManagementRefundObject();
			refundObject.setRefundedAmount(refundedAmount);

			final OrderManagementRefundsApi refundOrder = getRefundKlarnaOrder(orderModel.getKpOrderId());
			refundStatus = refundOrder.create(refundObject);

		}
		catch (final ApiException e)
		{
			final ErrorMessage error = e.getErrorMessage();
			return error.getErrorCode();
		}
		catch (final IOException e)
		{
			LOG.error(e.getMessage());
			return e.getMessage();
		}
		catch (final ModelNotFoundException e)
		{
			throw new UnknownIdentifierException(String.format(ORDER_NOT_FOUND_FOR_USER_AND_BASE_STORE, orderId));
		}
		return refundStatus;
	}

	@Override
	public OrderManagementRefundsApi getRefundKlarnaOrder(final String orderId)
	{
		return kpPaymentFacade.getKlarnaClient().newOrderManagementRefundsApi(orderId);
	}
	
	@Override
	public KlarnaShoppingDataAPI getKlarnaShoppingDataAPI(String sessionId)
	{
		return kpPaymentFacade.getKlarnaClient().newKlarnaShoppingDataAPI(sessionId);
	}

}
