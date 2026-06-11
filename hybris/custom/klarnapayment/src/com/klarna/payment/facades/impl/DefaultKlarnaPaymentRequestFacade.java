package com.klarna.payment.facades.impl;

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.session.SessionService;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.klarna.data.KlarnaConfigData;
import com.klarna.integration.dto.KlarnaCreatePaymentResponseDTO;
import com.klarna.integration.dto.KlarnaInteroperabilityDataDTO;
import com.klarna.integration.dto.KlarnaPaymentResponsePayloadDTO;
import com.klarna.payment.constants.KlarnapaymentConstants;
import com.klarna.payment.data.KlarnaEventData;
import com.klarna.payment.event.KlarnaEventPublisher;
import com.klarna.payment.facades.KlarnaConfigFacade;
import com.klarna.payment.facades.KlarnaPaymentRequestFacade;
import com.klarna.payment.services.KlarnaPaymentRequestService;
import com.klarna.payment.util.KlarnaServicesUtil;
import com.klarna.payment.util.LogHelper;


public class DefaultKlarnaPaymentRequestFacade implements KlarnaPaymentRequestFacade
{

	private static final Logger LOG = Logger.getLogger(DefaultKlarnaPaymentRequestFacade.class);


	@Resource(name = "cartService")
	private CartService cartService;

	@Resource(name = "commerceCheckoutService")
	private CommerceCheckoutService commerceCheckoutService;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource
	private KlarnaPaymentRequestService klarnaPaymentRequestService;

	@Resource
	private KlarnaConfigFacade klarnaConfigFacade;

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	@Resource
	private KlarnaEventPublisher klarnaEventPublisher;

	@Resource
	private KlarnaServicesUtil klarnaServicesUtil;

	@Override
	public KlarnaPaymentResponsePayloadDTO createPaymentRequest()
	{
		final CartModel cartModel = cartService.getSessionCart();
		if (cartModel != null)
		{
			calculateCart(cartModel);
			final KlarnaConfigData klarnaConfig = klarnaConfigFacade.getKlarnaConfig();
			final KlarnaCreatePaymentResponseDTO createPaymentResponse = klarnaPaymentRequestService.createPaymentRequest(cartModel,
					klarnaConfig);
			if (createPaymentResponse != null && createPaymentResponse.getPaymentResponsePayload() != null)
			{
				if (Boolean.TRUE.equals(klarnaConfig.getIntegratedViaPSP())
						&& Boolean.TRUE.equals(klarnaConfig.getShareShoppingData()))
				{
					createKlarnaInteroperabilityData();
				}
				return createPaymentResponse.getPaymentResponsePayload();
			}
			else
			{
				LOG.error("Payment request creation failed for Cart Id :: " + cartModel.getCode());
				return null;
			}
		}
		else
		{
			LOG.error("Session Cart is not available. Cannot create payment request for express checkout!");
		}
		return null;
	}

	@Override
	public void createKlarnaInteroperabilityData()
	{
		final CartModel cartModel = cartService.getSessionCart();
		final KlarnaInteroperabilityDataDTO interoperabilityData = klarnaPaymentRequestService
				.createKlarnaInteroperabilityData(cartModel);
		sessionService.setAttribute(KlarnapaymentConstants.KLARNA_INTEROPERABILITY_DATA, interoperabilityData);
	}

	@Override
	public boolean handlePaymentUpdateForPSPIntegration(final String networkSessionToken, final String paymentState) {
		if (StringUtils.isNotEmpty(networkSessionToken))
		{
			sessionService.setAttribute(KlarnapaymentConstants.KLARNA_NETWORK_SESSION_TOKEN, networkSessionToken);
			LogHelper.debugLog(LOG, "Klarna Network Session Token for Cart Id " + cartFacade.getSessionCart().getCode()
					+ " saved to session:: " + networkSessionToken);
		}
		else
		{
			LOG.error("Klarna Network Session Token is not available in the request.");
			return false;
		}
		if (StringUtils.isNotEmpty(paymentState))
		{
			sessionService.setAttribute(KlarnapaymentConstants.KLARNA_PAYMENT_STATE, paymentState);
			LogHelper.debugLog(LOG, "Klarna Payment State for Cart Id " + cartFacade.getSessionCart().getCode()
					+ " saved to session:: " + paymentState);
		}
		else
		{
			LOG.error("Klarna Payment State is not available in the request.");
			return false;
		}
		final KlarnaEventData klarnaEventData = new KlarnaEventData();
		klarnaEventData.setKlarnaNetworkSessionToken(networkSessionToken);
		klarnaEventData.setKlarnaPaymentState(paymentState);
		klarnaEventPublisher.publishProperyChangeEvent(KlarnapaymentConstants.KLARNA_EVENT_DATA, null,
				klarnaServicesUtil.convertRequestDtoToString(klarnaEventData));
		return true;
	}

	private void calculateCart(final CartModel cartModel)
	{
		commerceCheckoutService.calculateCart(createCommerceCheckoutParameter(cartModel, true));
	}

	private CommerceCheckoutParameter createCommerceCheckoutParameter(final CartModel cart, final boolean enableHooks)
	{
		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setEnableHooks(enableHooks);
		parameter.setCart(cart);
		return parameter;
	}
}
