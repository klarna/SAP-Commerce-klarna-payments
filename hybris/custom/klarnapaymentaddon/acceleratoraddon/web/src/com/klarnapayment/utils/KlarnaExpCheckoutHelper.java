package com.klarnapayment.utils;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klarna.api.expcheckout.model.KlarnaExpCheckoutAuthorizationResponse;
import com.klarna.payment.data.KlarnaPaymentRequestData;
import com.klarna.payment.data.KlarnaShippingOptionData;
import com.klarna.payment.data.KlarnaWebhookData;
import com.klarna.payment.facades.KlarnaExpCheckoutFacade;
import com.klarnapayment.constants.KlarnapaymentaddonWebConstants;


public class KlarnaExpCheckoutHelper
{
	protected static final Logger LOG = LoggerFactory.getLogger(KlarnaExpCheckoutHelper.class);

	@Resource
	private SessionService sessionService;

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "klarnaExpCheckoutFacade")
	private KlarnaExpCheckoutFacade klarnaExpCheckoutFacade;

	@Resource(name = "klarnaPaymentHelper")
	private KlarnaPaymentHelper klarnaPaymentHelper;

	@Resource(name = "cartService")
	private CartService cartService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "i18nService")
	private I18NService i18nService;

	@Resource(name = "messageSource")
	private MessageSource messageSource;

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
		if (StringUtils.isEmpty(expCheckoutCartId) || cartFacade.getSessionCart() == null
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
		LOG.error("Customer Email Id not available in Payment Request object");
		return null;
	}

	public boolean prepareCheckoutUser(final String emailId, final HttpServletRequest request, final HttpServletResponse response)
	{
		// Prepare checkout user
		final CartModel cartModel = cartService.getSessionCart();
		if (userService.isAnonymousUser(cartModel.getUser()))
		{
			if (userFacade.isAnonymousUser())
			{
				try
				{
					// Create guest user for checkout. Use dummy email id if user email id is not available
					customerFacade.createGuestUserForAnonymousCheckout(
							(StringUtils.isNotEmpty(emailId) ? emailId : KlarnapaymentaddonWebConstants.KLARNA_GUEST_TEMP_EMAIL_ID),
							messageSource.getMessage("text.guest.customer", null, i18nService.getCurrentLocale()));
					final String anonymousUserGuid = StringUtils.substringBefore(cartModel.getUser().getUid(), "|");
					klarnaPaymentHelper.updateAnonymousCookie(anonymousUserGuid, request, response);
					return true;
				}
				catch (final DuplicateUidException e)
				{
					LOG.error("Guest user creation failed. Cannot proceed with express checkout!");
					return false;
				}
			}
			else
			{
				cartService.changeCurrentCartUser(userService.getCurrentUser());
				return true;
			}
		}
		else if (userFacade.isAnonymousUser())
		{
			if (StringUtils.contains(cartModel.getUser().getUid(), KlarnapaymentaddonWebConstants.KLARNA_GUEST_TEMP_EMAIL_ID)
					&& StringUtils.isNotEmpty(emailId))
			{
				// Replace the dummy email id with actual email id of the user
				cartModel.getUser().setUid(StringUtils.replace(cartModel.getUser().getUid(),
						KlarnapaymentaddonWebConstants.KLARNA_GUEST_TEMP_EMAIL_ID, emailId));
				modelService.save(cartModel.getUser());
			}
			return true;
		}
		else
		{
			// Registered customer. Check if the cart belongs to the same customer.
			return klarnaExpCheckoutFacade.isValidSessionUserCart();
		}
	}

	public String getEmailIdFromWebhookData(final KlarnaWebhookData webhookData)
	{
		if (webhookData.getPayload() != null && webhookData.getPayload().getKlarnaCustomer() != null
				&& webhookData.getPayload().getKlarnaCustomer().getCustomerProfile() != null)
		{
			return webhookData.getPayload().getKlarnaCustomer().getCustomerProfile().getEmail();
		}
		LOG.error("Customer Email Id not available in Webhook Payload");
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
}