package com.klarnapayment.utils;

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.servicelayer.session.SessionService;

import javax.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klarna.api.expcheckout.model.KlarnaExpCheckoutAuthorizationResponse;
import com.klarna.payment.data.KlarnaAddressData;
import com.klarna.payment.data.KlarnaPaymentRequestData;
import com.klarna.payment.data.KlarnaShippingOptionData;
import com.klarnapayment.constants.KlarnapaymentaddonWebConstants;


public class KlarnaExpCheckoutHelper
{
	protected static final Logger LOG = Logger.getLogger(KlarnaExpCheckoutHelper.class);

	@Resource
	private SessionService sessionService;

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	@Resource(name = "klarnaObjectMapper")
	protected ObjectMapper objectMapper;


	public <T> T convertResponseStringToDto(final String response, final Class<T> dtoClass)
	{
		try
		{
			if (StringUtils.isNotEmpty(response))
			{
				return objectMapper.readValue(response, dtoClass);
			}
		}
		catch (final Exception e)
		{
			LOG.error("Exception in parsing response string to DTO class " + dtoClass.getName() + " ::", e);
		}
		return null;
	}

	public boolean validateAuthorizationResponse(
			final KlarnaExpCheckoutAuthorizationResponse klarnaExpCheckoutAuthorizationResponse)
	{
		if (klarnaExpCheckoutAuthorizationResponse == null)
		{
			LOG.error("Authorization response is null!");
			return false;
		}
		else if (StringUtils.isEmpty(klarnaExpCheckoutAuthorizationResponse.getClientToken()))
		{
			LOG.error("Authorization response doesnt contain client token!");
			return false;
		}
		else if (klarnaExpCheckoutAuthorizationResponse.getCollectedShippingAddress() == null)
		{
			LOG.warn("Authorization response doesnt contain shipping address!");
		}
		else if (StringUtils.isEmpty(klarnaExpCheckoutAuthorizationResponse.getCollectedShippingAddress().getEmail()))
		{
			LOG.warn("Authorization response shipping address doesn't contain email id! Email id is mandatory for guest checkout");
		}
		else if (BooleanUtils.isFalse(klarnaExpCheckoutAuthorizationResponse.getFinalizeRequired()))
		{
			LOG.warn("Finalize required is FALSE! It should always be TRUE!");
		}
		return true;
	}

	public boolean validateExpressCheckoutCart()
	{
		// Check if the session cart is same as the express checkout cart
		final String expCheckoutCartId = sessionService.getAttribute(KlarnapaymentaddonWebConstants.KLARNA_EXP_CHECKOUT_CART_ID);
		if (cartFacade.getSessionCart() == null
				|| !StringUtils.equalsIgnoreCase(expCheckoutCartId, cartFacade.getSessionCart().getGuid()))
		{
			LOG.error("Session Cart not matching Express Checkout Cart.");
			return false;
		}
		return true;
	}

	public String getEmailIdFromPaymentRequest(final KlarnaPaymentRequestData paymentRequestData)
	{
		if (paymentRequestData != null && paymentRequestData.getStateContext() != null
				&& paymentRequestData.getStateContext().getKlarnaCustomer() != null
				&& paymentRequestData.getStateContext().getKlarnaCustomer().getCustomerProfile() != null)
		{
			return paymentRequestData.getStateContext().getKlarnaCustomer().getCustomerProfile().getEmail();
		}
		return null;
	}

	public KlarnaAddressData getBillingAddressFromPaymentRequest(final KlarnaPaymentRequestData paymentRequestData)
	{
		if (paymentRequestData.getStateContext() != null && paymentRequestData.getStateContext().getKlarnaCustomer() != null
				&& paymentRequestData.getStateContext().getKlarnaCustomer().getCustomerProfile() != null)
		{
			return paymentRequestData.getStateContext().getKlarnaCustomer().getCustomerProfile().getAddress();
		}
		return null;
	}

	public KlarnaShippingOptionData getShippingOptionFromPaymentRequest(final KlarnaPaymentRequestData paymentRequestData)
	{
		if (paymentRequestData != null && paymentRequestData.getStateContext() != null
				&& paymentRequestData.getStateContext().getShipping() != null)
		{
			return paymentRequestData.getStateContext().getShipping().getShippingOption();
		}
		return null;
	}

	public void addRecipientNameToAddress(final AddressData address, final KlarnaPaymentRequestData paymentRequestData)
	{
		if (address != null && paymentRequestData.getStateContext().getShipping() != null
				&& paymentRequestData.getStateContext().getShipping().getRecipient() != null)
		{
			address.setFirstName(paymentRequestData.getStateContext().getShipping().getRecipient().getGivenName());
			address.setLastName(paymentRequestData.getStateContext().getShipping().getRecipient().getFamilyName());
		}
	}

}