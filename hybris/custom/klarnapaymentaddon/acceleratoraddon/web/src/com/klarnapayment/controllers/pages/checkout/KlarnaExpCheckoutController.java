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
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
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
import com.klarna.data.KlarnaConfigData;
import com.klarna.integration.dto.KlarnaPaymentResponsePayloadDTO;
import com.klarna.payment.data.KlarnaPaymentRequestData;
import com.klarna.payment.data.KlarnaRejectionResponseData;
import com.klarna.payment.data.KlarnaShippingChangeResponseData;
import com.klarna.payment.facades.KPPaymentCheckoutFacade;
import com.klarna.payment.facades.KPPaymentFacade;
import com.klarna.payment.facades.KlarnaConfigFacade;
import com.klarna.payment.facades.KlarnaExpCheckoutFacade;
import com.klarna.payment.facades.KlarnaPaymentRequestFacade;
import com.klarna.payment.util.LogHelper;
import com.klarnapayment.constants.KlarnapaymentaddonWebConstants;
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

	@Resource(name = "klarnaConfigFacade")
	private KlarnaConfigFacade klarnaConfigFacade;

	@Resource
	private KlarnaPaymentRequestFacade klarnaPaymentRequestFacade;


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
			getSessionService().setAttribute(KlarnapaymentaddonWebConstants.KLARNA_EXP_CHECKOUT_CART_ID,
					cartFacade.getSessionCartGuid());
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
				final String expCheckoutCartId = getSessionService()
						.getAttribute(KlarnapaymentaddonWebConstants.KLARNA_EXP_CHECKOUT_CART_ID);
				if (cartFacade.getSessionCart() == null
						|| !StringUtils.equalsIgnoreCase(expCheckoutCartId, cartFacade.getSessionCart().getGuid()))
				{
					LOG.error("Session Cart not matching Express Checkout Cart.");
				}
				else
				{
					boolean isValidCheckoutUser = prepareCheckoutUser(authorizationResponse.getCollectedShippingAddress().getEmail(),
							request, response);
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
							storeSelectedPaymentMethodInSession(authorizationResponse);
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

	@RequestMapping(value = "/create-payment-request", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public KlarnaPaymentResponsePayloadDTO createPaymentRequest(@RequestParam("productCode")
	final String productCode, @RequestParam("productQty")
	final String productQty) throws ApiException, IOException
	{
		LogHelper.debugLog(LOG, "Entering getAuthorizePayload method");
		try
		{
			if (StringUtils.isNotEmpty(productCode))
			{
				klarnaExpCheckoutFacade.createNewSessionCart();
				CartModificationData cartModificationData = cartFacade.addToCart(productCode, Long.parseLong(productQty));
				if (!CommerceCartModificationStatus.SUCCESS.equalsIgnoreCase(cartModificationData.getStatusCode()))
				{
					LOG.error("Add to cart failed for product code " + productCode + " due to error :::"
							+ cartModificationData.getStatusCode());
					return null;
				}
			}
			getSessionService().setAttribute(KlarnapaymentaddonWebConstants.KLARNA_EXP_CHECKOUT_CART_ID,
					cartFacade.getSessionCartGuid());
			return klarnaPaymentRequestFacade.createPaymentRequest();
		}
		catch (Exception e)
		{
			LOG.error("Error getting authorization payload :: ", e);
		}
		return null;
	}

	@RequestMapping(value = "/update-shipping-address", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateShippingAddress(@RequestBody
	final Map<String, Object> requestMap, final HttpServletRequest request, final HttpServletResponse response)
	{
		try
		{
			// Check if the response object is valid
			if (!klarnaExpCheckoutHelper.validateShippingAddressChangeRequest(requestMap))
			{
				LOG.error("Invalid address change request.");
				return getErrorResponseForShippingAddressChangeUpdate();
			}
			if (!klarnaExpCheckoutHelper.validateExpressCheckoutCart())
			{
				return getErrorResponseForShippingAddressChangeUpdate();
			}
			if (!prepareCheckoutUser(klarnaExpCheckoutHelper.getEmailIdFromPaymentRequest(requestMap), request, response))
			{
				LOG.error("Invalid checkout user.");
				return getErrorResponseForShippingAddressChangeUpdate();
			}
			AddressData addressData = klarnaExpCheckoutFacade.getShippingAddressFromRequestMap(requestMap);
			if (!isValidAddress(addressData))
			{
				LOG.error("Invalid shipping address.");
				return getErrorResponseForShippingAddressChangeUpdate();
			}
			if (setDeliveryDetails(addressData))
			{
				final KlarnaShippingChangeResponseData shippingAddressChangeResponse = klarnaExpCheckoutFacade
						.getShippingAddressChangeResponse();
				Map<String, Object> successResponse = new HashMap<>();
				successResponse.put("status", "success");
				successResponse.put("successResponse", shippingAddressChangeResponse);
				return successResponse;
			}
			else
			{
				LOG.error("Shipping details could not be set.");
			}
		}
		catch (Exception e)
		{
			LOG.error("Exception occured during shipping address update :: ", e);
		}
		return getErrorResponseForShippingAddressChangeUpdate();
	}

	@RequestMapping(value = "/update-shipping-method", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateShippingMethod(@RequestBody
	final Map<String, Object> requestMap)
	{
		try
		{
			// Check if the response object is valid
			if (!klarnaExpCheckoutHelper.validateShippingOptionSelectRequest(requestMap))
			{
				LOG.error("Invalid shipping optoin change request.");
				return getErrorResponseForShippingOptionUpdate();
			}
			if (!klarnaExpCheckoutHelper.validateExpressCheckoutCart())
			{
				return getErrorResponseForShippingOptionUpdate();
			}
			if (getUserFacade().isAnonymousUser())
			{
				LOG.error("Anonymous User.");
				return getErrorResponseForShippingOptionUpdate();
			}
			if (cartFacade.getSessionCart().getDeliveryAddress() == null)
			{
				LOG.error("Delivery Address is not available.");
				return getErrorResponseForShippingOptionUpdate();
			}
			final KlarnaShippingChangeResponseData shippingOptionChangeResponse = klarnaExpCheckoutFacade
					.setDeliveryMode(requestMap);
			shippingOptionChangeResponse.setSelectedShippingOptionReference(null);
			shippingOptionChangeResponse.setShippingOptions(null);
			Map<String, Object> successResponse = new HashMap<>();
			successResponse.put("status", "success");
			successResponse.put("successResponse", shippingOptionChangeResponse);
			return successResponse;
		}
		catch (Exception e)
		{
			LOG.error("Exception occured during shipping option update :: ", e);
		}
		return getErrorResponseForShippingOptionUpdate();
	}

	@RequestMapping(value = "/on-payment-complete", method = RequestMethod.POST)
	@ResponseBody
	public boolean onPaymentComplete(@RequestBody
	final Map<String, Object> requestMap, final HttpServletRequest request, final HttpServletResponse response)
	{
		try
		{
			// Check if the response object is valid
			if (!klarnaExpCheckoutHelper.validatePaymentCompleteRequest(requestMap))
			{
				LOG.error("Invalid payment request.");
				return false;
			}

			if (!klarnaExpCheckoutHelper.validateExpressCheckoutCart())
			{
				return false;
			}
			final KlarnaPaymentRequestData paymentRequestData = (KlarnaPaymentRequestData) requestMap.get("paymentRequest");
			final KlarnaConfigData klarnaConfig = klarnaConfigFacade.getKlarnaConfig();
			if (Boolean.TRUE.equals(klarnaConfig.getIntegratedViaPSP()))
			{
				if (StringUtils.isNotEmpty(paymentRequestData.getStateContext().getInteroperabilityToken()))
				{
					getSessionService().setAttribute(KlarnapaymentaddonWebConstants.KLARNA_INTEROPERABILITY_TOKEN,
							paymentRequestData.getStateContext().getInteroperabilityToken());
					LogHelper.debugLog(LOG, "Interoperability Token for Cart Id " + cartFacade.getSessionCart().getCode()
							+ " saved to session:: " + paymentRequestData.getStateContext().getInteroperabilityToken());
				}
				else if (StringUtils.isNotEmpty(paymentRequestData.getStateContext().getKlarnaNetworkSessionToken()))
				{
					getSessionService().setAttribute(KlarnapaymentaddonWebConstants.KLARNA_NETWORK_SESSION_TOKEN,
							paymentRequestData.getStateContext().getKlarnaNetworkSessionToken());
					LogHelper.debugLog(LOG, "Klarna Network Session Token for Cart Id " + cartFacade.getSessionCart().getCode()
							+ " saved to session:: " + paymentRequestData.getStateContext().getKlarnaNetworkSessionToken());
				}
				return true;
			}
			if (!prepareCheckoutUser(klarnaExpCheckoutHelper.getEmailIdFromPaymentRequest(requestMap), request, response))
			{
				LOG.error("Invalid checkout user. Cannot proceed with order placement");
				return false;
			}
			AddressData addressData = klarnaExpCheckoutFacade.getShippingAddressFromRequestMap(requestMap);
			if (!isValidAddress(addressData))
			{
				LOG.error("Invalid shipping address. Cannot proceed with order placement");
				return false;
			}
			if (!setDeliveryDetails(addressData))
			{
				LOG.error("Delivery details couldnot be set. Cannot proceed with order placement");
				return false;
			}
			if (!klarnaExpCheckoutFacade.setPaymentDetailsForOneStepKEC(addressData))
			{
				LOG.error("Delivery Address is not available.");
				return false;
			}
			if (cartFacade.getSessionCart().getDeliveryMode() == null)
			{
				LOG.error("Delivery Mode is not available.");
				return false;
			}

			// TODO Place order
			return true;
		}
		catch (Exception e)
		{
			LOG.error("Exception occured during shipping address update :: ", e);
		}
		return false;
	}

	private boolean prepareCheckoutUser(final String emailId,
			final HttpServletRequest request, final HttpServletResponse response)
	{
		// Prepare checkout user
		if (getUserFacade().isAnonymousUser())
		{
			if (StringUtils.isEmpty(emailId))
			{
				LOG.error("Customer email id not availalbe. Guest chekcout user will be created with dummy email id :: "
						+ KlarnapaymentaddonWebConstants.KLARNA_GUEST_TEMP_EMAIL_ID);
			}
			try
			{
				getCustomerFacade().createGuestUserForAnonymousCheckout(
						(StringUtils.isNotEmpty(emailId) ? emailId : KlarnapaymentaddonWebConstants.KLARNA_GUEST_TEMP_EMAIL_ID),
						getMessageSource().getMessage("text.guest.customer", null, getI18nService().getCurrentLocale()));

				// TODO set the real email id later
				//customer.setContactEmail(realEmail);
				//customer.setUid(customer.getUid().split("\\|")[0] + "|" + realEmail);
				//modelService.save(customer);

				final String anonymousUserGuid = StringUtils.substringBefore(cartService.getSessionCart().getUser().getUid(), "|");
				klarnaPaymentHelper.updateAnonymousCookie(anonymousUserGuid, request, response);
				return true;
			}
			catch (final DuplicateUidException e)
			{
				LOG.error("Guest user creation failed. Cannot proceed with express checkout!");
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

	private void storeSelectedPaymentMethodInSession(final
			KlarnaExpCheckoutAuthorizationResponse authorizationResponse) {
		if(CollectionUtils.isNotEmpty(authorizationResponse.getPaymentMethodCategories())) {
			if(StringUtils.isNotEmpty(authorizationResponse.getPaymentMethodCategories().get(0).getIdentifier())) {
				getSessionService().setAttribute(KlarnapaymentaddonWebConstants.KLARNA_SELECTED_PAYMENT_METHOD,
						authorizationResponse.getPaymentMethodCategories().get(0).getIdentifier());
			}
		}
	}

	private Map<String, Object> getErrorResponseForShippingAddressChangeUpdate()
	{
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("status", "error");
		final KlarnaRejectionResponseData rejectionResponse = new KlarnaRejectionResponseData();
		rejectionResponse.setRejectionReason(KlarnapaymentaddonWebConstants.KLARNA_ADDRESS_NOT_SUPPORTED_ERROR);
		errorResponse.put("rejectionResponse", rejectionResponse);
		return errorResponse;
	}

	private Map<String, Object> getErrorResponseForShippingOptionUpdate()
	{
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("status", "error");
		final KlarnaRejectionResponseData rejectionResponse = new KlarnaRejectionResponseData();
		rejectionResponse.setRejectionReason(KlarnapaymentaddonWebConstants.KLARNA_INVALID_OPTION_ERROR);
		errorResponse.put("rejectionResponse", rejectionResponse);
		return errorResponse;
	}

}
