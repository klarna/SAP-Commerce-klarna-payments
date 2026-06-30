package com.klarna.payment.facades.impl;

import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.klarna.data.KlarnaConfigData;
import com.klarna.integration.dto.KlarnaCreatePaymentResponseDTO;
import com.klarna.integration.dto.KlarnaPaymentResponsePayloadDTO;
import com.klarna.payment.facades.KlarnaConfigFacade;
import com.klarna.payment.facades.KlarnaNetworkSessionFacade;
import com.klarna.payment.facades.KlarnaPaymentRequestFacade;
import com.klarna.payment.services.KlarnaPaymentRequestService;


public class DefaultKlarnaPaymentRequestFacade implements KlarnaPaymentRequestFacade
{

	private static final Logger LOG = Logger.getLogger(DefaultKlarnaPaymentRequestFacade.class);

	@Resource(name = "cartService")
	private CartService cartService;

	@Resource(name = "commerceCheckoutService")
	private CommerceCheckoutService commerceCheckoutService;

	@Resource
	private KlarnaPaymentRequestService klarnaPaymentRequestService;

	@Resource
	private KlarnaConfigFacade klarnaConfigFacade;

	@Resource
	private KlarnaNetworkSessionFacade klarnaNetworkSessionFacade;

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
				klarnaNetworkSessionFacade.storeNetworkSessionData();
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
