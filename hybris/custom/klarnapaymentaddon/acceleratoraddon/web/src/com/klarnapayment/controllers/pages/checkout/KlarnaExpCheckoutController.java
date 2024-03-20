package com.klarnapayment.controllers.pages.checkout;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.commercefacades.address.AddressVerificationFacade;
import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.order.CartService;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.klarna.api.expcheckout.model.KlarnaExpCheckoutAuthCallbackRequest;
import com.klarna.api.expcheckout.model.KlarnaExpCheckoutAuthorizationResponse;
import com.klarna.api.model.ApiException;
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.payment.facades.KPPaymentCheckoutFacade;
import com.klarna.payment.facades.KPPaymentFacade;
import com.klarna.payment.facades.KlarnaExpCheckoutFacade;
import com.klarna.payment.util.LogHelper;
import com.klarnapayment.utils.KlarnaExpCheckoutHelper;
import com.klarnapayment.utils.KlarnaPaymentHelper;


/**
 * @author hybris
 *
 */
@Controller
@RequestMapping(value = "/klarna/express-checkout")
public class KlarnaExpCheckoutController extends AbstractPageController
{
	private static final Logger LOG = Logger.getLogger(KlarnaExpCheckoutController.class);

	private static final String KLARNA_EXP_CHECKOUT_CART_ID = "klarnaExpCheckoutCartId";
	private static final String IS_KLARNA_EXP_CHECKOUT_SESSION = "isKlarnaExpCheckoutSession";
	private static final String CLIENT_TOKEN = "clientToken";

	@Resource(name = "REDIRECT_TO_SUMMARY")
	private String redirectToSummary;

	@Resource(name = "klarnaExpCheckoutFacade")
	private KlarnaExpCheckoutFacade klarnaExpCheckoutFacade;

	@Resource(name = "klarnaExpCheckoutHelper")
	private KlarnaExpCheckoutHelper klarnaExpCheckoutHelper;

	@Resource(name = "klarnaPaymentHelper")
	private KlarnaPaymentHelper klarnaPaymentHelper;

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	@Resource(name = "checkoutFacade")
	private CheckoutFacade checkoutFacade;

	@Resource(name = "cartService")
	private CartService cartService;

	@Resource(name = "addressVerificationFacade")
	private AddressVerificationFacade addressVerificationFacade;

	@Resource(name = "kpPaymentCheckoutFacade")
	private KPPaymentCheckoutFacade kpPaymentCheckoutFacade;

	@Resource(name = "kpPaymentFacade")
	private KPPaymentFacade kpPaymentFacade;


	@RequestMapping(value = "/create-authorize-payload", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public PaymentsSession createAuthorizePayload(@RequestParam("productCode")
	final String productCode, @RequestParam("productQty")
	final String productQty, final HttpSession httpSession) throws ApiException, IOException
	{
		LogHelper.debugLog(LOG, "Entering getAuthorizePayload method");
		try
		{
			if (StringUtils.isNotEmpty(productCode))
			{
				// Express Checkout initiated from product page. Create new cart and set in session
				klarnaExpCheckoutFacade.createNewSessionCart();
				CartModificationData cartModificationData = cartFacade.addToCart(productCode, Long.parseLong(productQty));
				if (!CommerceCartModificationStatus.SUCCESS.equalsIgnoreCase(cartModificationData.getStatusCode()))
				{
					// Add to cart failed
					LOG.error("Add to cart failed for product code " + productCode + " due to error :::"
							+ cartModificationData.getStatusCode());
					return null;
				}
			}
			getSessionService().setAttribute(KLARNA_EXP_CHECKOUT_CART_ID, cartFacade.getSessionCartGuid());
			// Get order payload for authorize call
			return klarnaExpCheckoutFacade.getAuthorizePayload();
		}
		catch (Exception e)
		{
			LOG.error("Error getting authorization payload :: ", e);
		}
		return null;
		//httpSession.setAttribute("clientToken", creditSessionData.getClientToken());
	}

	@RequestMapping(value = "/process-authorize-response", method = RequestMethod.POST)
	@ResponseBody
	public String processAuthorizeResponse(@RequestBody final
	KlarnaExpCheckoutAuthorizationResponse authorizationResponse, final HttpSession httpSession, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		LogHelper.debugLog(LOG, "Entering processAuthorizeResponse method");
		try
		{
			// Check if the response object is valid
			if (klarnaExpCheckoutHelper.validateAuthorizationResponse(authorizationResponse))
			{
				// Check if the session cart is same as the express checkout cart
				final String expCheckoutCartId = getSessionService().getAttribute(KLARNA_EXP_CHECKOUT_CART_ID);
				if (cartFacade.getSessionCart() == null
						|| !StringUtils.equalsIgnoreCase(expCheckoutCartId, cartFacade.getSessionCart().getGuid()))
				{
					LOG.error("Session Cart not matching Express Checkout Cart.");
				}
				else
				{
					boolean isValidCheckoutUser = prepareCheckoutUser(authorizationResponse, request, response);
					// Prepare checkout user
					if (isValidCheckoutUser)
					{
						LogHelper.debugLog(LOG, "User set for express checkout");
						// Get shipping address from authorization response
						AddressData addressData = klarnaExpCheckoutFacade.getShippingAddress(authorizationResponse);
						boolean isValidAddress = isValidAddress(addressData);
						// Set delivery details if the shipping address is available and is valid
						boolean isDeliveryDetailsSet = (isValidAddress) && setDeliveryDetails(addressData);
						// Set payment info and billing address
						boolean isPaymentDetailsSet = setPaymentDetails(authorizationResponse,
								(isValidAddress) ? addressData : null);
						if (isDeliveryDetailsSet && isPaymentDetailsSet)
						{
							LogHelper.debugLog(LOG, "Cart set for express checkout! Cart Id : " + cartFacade.getSessionCart().getCode());
							LogHelper.debugLog(LOG, "Redirecting to checkout summary page");
							return StringUtils.substringAfter(redirectToSummary, REDIRECT_PREFIX);
						}
						else
						{
							LOG.error("Cannot set checkout properties to cart for express checkout!");
						}
					}
					else
					{
						LOG.error("Cannot set user for express checkout!");
					}
				}
			}
			else
			{
				LOG.error("Invalid Authorization Response.");
			}
		}
		catch (Exception e)
		{
			LOG.error("Exception occured during express checkout :: ", e);
		}
		LOG.error("Cart not ready for Express Checkout! Redirecting to Multistep Checkout...");
		return StringUtils.substringAfter(getCheckoutRedirectUrl(), REDIRECT_PREFIX);
	}

	@RequestMapping(value = "/authorize-callback", method = RequestMethod.POST)
	@RequireHardLogIn
	public String authorizeCallback(@RequestBody
	final KlarnaExpCheckoutAuthCallbackRequest klarnaExpCheckoutAuthCallbackRequest, final HttpSession httpSession)
	{
		if (klarnaExpCheckoutAuthCallbackRequest == null
				|| StringUtils.isEmpty(klarnaExpCheckoutAuthCallbackRequest.getAuthorizationToken()))
		{
			LOG.error("Invalid Authorization Callback");
			return null;
		}
		kpPaymentCheckoutFacade.saveAuthorization(klarnaExpCheckoutAuthCallbackRequest.getAuthorizationToken(), null, null);
		LogHelper.debugLog(LOG,
				"Saved Authorization Token for Session Id :: " + klarnaExpCheckoutAuthCallbackRequest.getSessionId());
		kpPaymentFacade.createPaymentTransaction();
		LogHelper.debugLog(LOG,
				"Created Payment Transaction for Session Id :: " + klarnaExpCheckoutAuthCallbackRequest.getSessionId());
		return "success";
	}

	private boolean prepareCheckoutUser(final KlarnaExpCheckoutAuthorizationResponse authorizationResponse,
			final HttpServletRequest request, final HttpServletResponse response)
	{
		// Prepare checkout user
		if (getUserFacade().isAnonymousUser())
		{
			if (authorizationResponse.getCollectedShippingAddress() == null
					|| StringUtils.isEmpty(authorizationResponse.getCollectedShippingAddress().getEmail()))
			{
				LOG.error("Cannot create guest user as shipping address email id is not available. Redirecting to checkout login...");
			}
			else
			{
				LogHelper.debugLog(LOG, "Creating guest user for checkout ");
				try
				{
					getCustomerFacade().createGuestUserForAnonymousCheckout(
							authorizationResponse.getCollectedShippingAddress().getEmail(),
							getMessageSource().getMessage("text.guest.customer", null, getI18nService().getCurrentLocale()));

					final String anonymousUserGuid = StringUtils.substringBefore(cartService.getSessionCart().getUser().getUid(),
							"|");
					klarnaPaymentHelper.updateAnonymousCookie(anonymousUserGuid, request, response);
					return true;
				}
				catch (final DuplicateUidException e)
				{
					LOG.error("Guest user creation failed. Cannot proceed with express checkout!");
				}
			}
		}
		else
		{
			return klarnaExpCheckoutFacade.isValidSessionUserCart();
		}
		return false;
	}

	private boolean isValidAddress(final AddressData addressData)
	{
		if (addressData != null)
		{
			final AddressVerificationResult<AddressVerificationDecision> verificationResult = addressVerificationFacade
					.verifyAddressData(addressData);
			if (verificationResult != null && ((verificationResult.getErrors() != null && !verificationResult.getErrors().isEmpty())
					|| (verificationResult.getSuggestedAddresses() != null && !verificationResult.getSuggestedAddresses().isEmpty())))
			{
				return false;
			}
		}
		return true;
	}

	private boolean setDeliveryDetails(final AddressData addressData)
	{
		boolean isSuccess = false;
		if (addressData != null)
		{
			// Set delivery address using collected shipping address in authorization response
			getUserFacade().addAddress(addressData);
			isSuccess = checkoutFacade.setDeliveryAddress(addressData);
			if (isSuccess)
			{
				LogHelper.debugLog(LOG, "Delivery address set successfully!");
				// Set cheapest delivery method available
				isSuccess = checkoutFacade.setCheapestDeliveryModeForCheckout();
				if (isSuccess)
				{
					LogHelper.debugLog(LOG, "Cheapest delivery mode set by default!");
				}
				else
				{
					LOG.error("Delivery mode could not be set for user email id: " + addressData.getEmail());
				}
			}
			else
			{
				LOG.error("Delivery address could not be set for user email id: "
						+ addressData.getEmail());
			}
		}
		else
		{
			LOG.error("No valid delivery address! ");
		}
		return isSuccess;
	}

	private boolean setPaymentDetails(final KlarnaExpCheckoutAuthorizationResponse authorizationResponse,
			final AddressData addressData)
	{
		// Create new payment info
		boolean isSuccess = klarnaExpCheckoutFacade.addPaymentInfo(authorizationResponse);
		if (isSuccess)
		{
			LogHelper.debugLog(LOG, "Klarna payment info set successfully!");
			getSessionService().setAttribute(CLIENT_TOKEN, authorizationResponse.getClientToken());
			getSessionService().setAttribute(IS_KLARNA_EXP_CHECKOUT_SESSION, Boolean.TRUE);
			// Use delivery address as billing address
			isSuccess = klarnaExpCheckoutFacade.addBillingAddress(addressData);
			if (isSuccess)
			{
				LogHelper.debugLog(LOG, "Billing address set successfully!");
			}
			else
			{
				LOG.error("Billing addess could not be set.");
			}
		}
		else
		{
			LOG.error("Payment info could not be set.");
		}
		return isSuccess;
	}

}
