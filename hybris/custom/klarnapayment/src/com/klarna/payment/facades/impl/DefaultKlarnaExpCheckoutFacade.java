package com.klarna.payment.facades.impl;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.commercefacades.address.AddressVerificationFacade;
import de.hybris.platform.commercefacades.address.data.AddressVerificationErrorField;
import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.klarna.api.expcheckout.model.KlarnaExpCheckoutAuthorizationResponse;
import com.klarna.api.model.ApiException;
import com.klarna.api.payments.model.PaymentsOrder;
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.integration.dto.KlarnaAddressDTO;
import com.klarna.payment.constants.KlarnapaymentConstants;
import com.klarna.payment.data.KlarnaAddressData;
import com.klarna.payment.data.KlarnaPaymentRequestData;
import com.klarna.payment.data.KlarnaRequestData;
import com.klarna.payment.data.KlarnaShippingChangeResponseData;
import com.klarna.payment.data.KlarnaShippingOptionData;
import com.klarna.payment.data.KlarnaWebhookData;
import com.klarna.payment.facades.KPPaymentCheckoutFacade;
import com.klarna.payment.facades.KPPaymentFacade;
import com.klarna.payment.facades.KlarnaExpCheckoutFacade;
import com.klarna.payment.facades.KlarnaNetworkSessionFacade;
import com.klarna.payment.model.KPPaymentInfoModel;
import com.klarna.payment.util.LogHelper;


public class DefaultKlarnaExpCheckoutFacade implements KlarnaExpCheckoutFacade
{

	private static final Logger LOG = Logger.getLogger(DefaultKlarnaExpCheckoutFacade.class);

	private static final String KLARNA_PREFIX = "KLARNA_";

	@Resource(name = "klarnaExpCheckoutAuthPayloadConverter")
	private Converter klarnaExpCheckoutAuthPayloadConverter;

	@Resource(name = "klarnaPaymentsAddressReverseConverter")
	private Converter klarnaPaymentsAddressReverseConverter;

	@Resource(name = "kpAddressReverseConverter")
	private Converter<AddressData, AddressModel> kpAddressReverseConverter;

	@Resource(name = "cartFactory")
	private CartFactory cartFactory;

	@Resource(name = "cartService")
	private CartService cartService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "commerceCheckoutService")
	private CommerceCheckoutService commerceCheckoutService;

	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "checkoutFacade")
	private CheckoutFacade checkoutFacade;

	@Resource(name = "addressVerificationFacade")
	private AddressVerificationFacade addressVerificationFacade;

	@Resource(name = "kpPaymentFacade")
	private KPPaymentFacade kpPaymentFacade;

	@Resource(name = "kpPaymentCheckoutFacade")
	private KPPaymentCheckoutFacade kpPaymentCheckoutFacade;

	@Resource(name = "siteConfigService")
	private SiteConfigService siteConfigService;

	@Resource
	private Converter<KlarnaAddressData, AddressData> klarnaAddressReverseConverter;

	@Resource
	private Converter<AbstractOrderModel, KlarnaShippingChangeResponseData> klarnaShippingChangeResponseConverter;

	@Resource
	private Converter<KlarnaAddressDTO, AddressData> klarnaAddressDTOReverseConverter;

	@Resource
	private KlarnaNetworkSessionFacade klarnaNetworkSessionFacade;


	@Override
	public PaymentsSession getAuthorizePayload()
	{
		final CartModel cartModel = cartService.getSessionCart();
		if (cartModel != null)
		{
			calculateCart(cartModel);
			// Create Payload
			final PaymentsSession authPayload = (PaymentsSession) klarnaExpCheckoutAuthPayloadConverter.convert(cartModel);
			if (authPayload != null)
			{
				return authPayload;
			}
			else
			{
				// Add to cart failed
				LOG.error("Auth Payload creation failed for Cart Id :: " + cartModel.getCode());
				return null;
			}
		}
		else
		{
			// Add to cart failed
			LOG.error("Session Cart not available. Cannot create payload for express checkout authorization!");
		}
		return null;
	}

	@Override
	public void createNewSessionCart()
	{
		// Create new cart and set it in session
		final CartModel cartModel = cartFactory.createCart();
		if (!userService.isAnonymousUser(userService.getCurrentUser()))
		{
			cartModel.setUser(userService.getCurrentUser());
		}
		modelService.save(cartModel);
		cartService.setSessionCart(cartModel);
	}

	@Override
	public boolean isValidSessionUserCart()
	{
		// Session cart should belong to the session user
		final CartModel cartModel = cartService.getSessionCart();
		if (cartModel != null && cartModel.getUser() != null)
		{
			if (userService.getCurrentUser() != null
					&& (StringUtils.equalsIgnoreCase(userService.getCurrentUser().getUid(), cartModel.getUser().getUid())))
			{
				return true;
			}
		}
		LOG.error("Express Checkout Cart doesn't belong to the logged in user!");
		return false;
	}

	@Override
	public boolean isValidAddress(final AddressData addressData)
	{
		if (addressData != null)
		{
			final AddressVerificationResult<AddressVerificationDecision> verificationResult = addressVerificationFacade
					.verifyAddressData(addressData);
			if(verificationResult != null) {
				if(AddressVerificationDecision.ACCEPT.equals(verificationResult.getDecision())) {
					return true;
				}
				else {
					LOG.error("Address verification decision :: "+verificationResult.getDecision());
					if (MapUtils.isNotEmpty(verificationResult.getErrors()))
					{
						LOG.error("Address verification errors ::");
						verificationResult.getErrors().entrySet().stream().forEach(entry -> {
							LOG.error("Address verification error: Field " + entry.getKey());
							final AddressVerificationErrorField errorField = entry.getValue();
							LOG.error("Address verification error: Field " + errorField.getName() + " is "
									+ (errorField.isMissing() ? "missing" : "invalid"));
						});
					}
				}
			}
		}
		return false;
	}

	@Override
	public AddressData getShippingAddress(final KlarnaExpCheckoutAuthorizationResponse klarnaExpCheckoutAuthorizationResponse)
	{
		try
		{
			if (klarnaExpCheckoutAuthorizationResponse.getCollectedShippingAddress() != null)
			{
				return (AddressData) klarnaPaymentsAddressReverseConverter
						.convert(klarnaExpCheckoutAuthorizationResponse.getCollectedShippingAddress());
			}
		}
		catch (final Exception e)
		{
			// Add to cart failed
			LOG.error("Error getting shipping address from express checkout authorization resposne! ", e);
		}
		return null;
	}

	@Override
	public boolean addPaymentInfo(final KlarnaExpCheckoutAuthorizationResponse klarnaExpCheckoutAuthorizationResponse)
	{
		final CartModel cartModel = cartService.getSessionCart();
		KPPaymentInfoModel kpPaymentInfoModel = null;
		if ((cartModel.getPaymentInfo() == null) || !(cartModel.getPaymentInfo() instanceof KPPaymentInfoModel))
		{
			kpPaymentInfoModel = modelService.create(KPPaymentInfoModel.class);
			kpPaymentInfoModel.setCode(KLARNA_PREFIX + cartModel.getCode());
			kpPaymentInfoModel.setUser(cartModel.getUser());
			kpPaymentInfoModel.setOwner(cartModel);
			kpPaymentInfoModel.setFinalizeRequired(Boolean.TRUE);
		}
		else
		{
			kpPaymentInfoModel = (KPPaymentInfoModel) cartModel.getPaymentInfo();
		}
		kpPaymentInfoModel.setFinalizeRequired(klarnaExpCheckoutAuthorizationResponse.getFinalizeRequired());
		//kpPaymentInfo.setPaymentOption(paymentOption);
		//kpPaymentInfo.setAuthToken(authorizationToken);

		// Update cart with payment info
		return setPaymentInfoInCart(cartModel, kpPaymentInfoModel);

		//if (getUserService().isAnonymousUser(getUserService().getCurrentUser()))
		//{
		//	cart.setKpAnonymousGUID(cart.getUser().getUid());
		//}
	}

	@Override
	public boolean addBillingAddress(final AddressData addressData)
	{
		if (addressData == null)
		{
			return false;
		}
		final AddressModel addressModel = kpAddressReverseConverter.convert(addressData);
		addressModel.setBillingAddress(Boolean.TRUE);
		final CartModel cartModel = cartService.getSessionCart();
		cartModel.setKpIdentifier(KLARNA_PREFIX + cartModel.getCode());
		final KPPaymentInfoModel kpPaymentInfoModel = (KPPaymentInfoModel) cartModel.getPaymentInfo();
		addressModel.setOwner(kpPaymentInfoModel);
		if (kpPaymentInfoModel != null)
		{
			kpPaymentInfoModel.setBillingAddress(addressModel);
		}
		cartModel.setPaymentAddress(addressModel);
		return setPaymentInfoInCart(cartModel, kpPaymentInfoModel);
	}

	@Override
	public AddressData getAddressData(final KlarnaAddressData klarnaAddressData)
	{
		if (klarnaAddressData != null)
		{
			return klarnaAddressReverseConverter.convert(klarnaAddressData);
		}
		return null;
	}

	@Override
	public KlarnaShippingChangeResponseData getShippingChangeResponse()
	{
		return klarnaShippingChangeResponseConverter.convert(cartService.getSessionCart());
	}

	@Override
	public boolean setDeliveryMode(final KlarnaShippingOptionData shippingOptionData)
	{
		return checkoutFacade.setDeliveryMode(shippingOptionData.getShippingOptionReference());
	}

	@Override
	public boolean updateCartForCheckout(final KlarnaRequestData requestData)
	{
		// Set Shipping Address and Shipping Option
		if (!setShippingAddress(requestData))
		{
			return false;
		}
		if (!setShippingOption(requestData))
		{
			return false;
		}
		// Set Payment Info and Billing Address
		if (!setPaymentInfo(requestData))
		{
			return false;
		}
		return setBillingAddress(requestData);
	}

	@Override
	public boolean setShippingAddress(final KlarnaRequestData requestData)
	{
		KlarnaAddressData klarnaShippingAddressData = requestData.getShippingAddress();
		if (klarnaShippingAddressData == null && requestData.getPaymentRequest().getStateContext().getShipping() != null)
		{
			klarnaShippingAddressData = requestData.getPaymentRequest().getStateContext().getShipping().getAddress();
		}
		if (klarnaShippingAddressData != null)
		{
			final AddressData shippingAddress = getAddressData(klarnaShippingAddressData);
			addRecipientNameToAddress(shippingAddress, requestData.getPaymentRequest());
			if (!isValidAddress(shippingAddress))
			{
				LOG.error("Invalid shipping address. Cannot proceed with order placement");
				return false;
			}
			userFacade.addAddress(shippingAddress);
			if (!checkoutFacade.setDeliveryAddress(shippingAddress))
			{
				LOG.error("Shipping address couldnot be set. Cannot proceed with order placement");
				return false;
			}
			LogHelper.debugLog(LOG, "Shipping Address updated.");
			return true;
		}
		else if (cartService.getSessionCart().getDeliveryAddress() != null)
		{
			return true;
		}
		LOG.error("Shipping address not available. Cannot proceed with order placement");
		return false;
	}

	@Override
	public boolean setShippingOption(final KlarnaRequestData requestData)
	{
		final String currentShippingOption = (cartService.getSessionCart().getDeliveryMode() != null)
				? cartService.getSessionCart().getDeliveryMode().getCode()
				: null;
		if (requestData.getShippingOption() != null
				&& StringUtils.isNotEmpty(requestData.getShippingOption().getShippingOptionReference()))
		{
			if (!StringUtils.equalsIgnoreCase(currentShippingOption, requestData.getShippingOption().getShippingOptionReference()))
			{
				if (!checkoutFacade.setDeliveryMode(requestData.getShippingOption().getShippingOptionReference()))
				{
					LOG.error("Error! Could not set delivery mode :: " + requestData.getShippingOption().getShippingOptionReference());
					return false;
				}
			}
		}
		if (StringUtils.isEmpty(currentShippingOption))
		{
			LogHelper.debugLog(LOG, "Shipping option not available in the request. Setting cheapest delivery mode..");
			if (!checkoutFacade.setCheapestDeliveryModeForCheckout())
			{
				LOG.error("Delivery mode could not be set. Cannot proceed with order placement");
				return false;
			}
			else
			{
				LogHelper.debugLog(LOG, "Shipping Option updated.");
			}
		}
		return true;
	}

	@Override
	public boolean setPaymentInfo(final KlarnaRequestData requestData)
	{
		final CartModel cartModel = cartService.getSessionCart();
		KPPaymentInfoModel kpPaymentInfoModel = null;
		if (!(cartModel.getPaymentInfo() instanceof KPPaymentInfoModel))
		{
			kpPaymentInfoModel = modelService.create(KPPaymentInfoModel.class);
			kpPaymentInfoModel.setCode(KLARNA_PREFIX + cartModel.getCode());
			kpPaymentInfoModel.setUser(cartModel.getUser());
			kpPaymentInfoModel.setOwner(cartModel);
		}
		else
		{
			kpPaymentInfoModel = (KPPaymentInfoModel) cartModel.getPaymentInfo();
		}
		// KLARNAPII-2754: Use Klarna Network Session Token instead of Payment Token - reverted
		kpPaymentInfoModel.setAuthToken(requestData.getPaymentRequest().getStateContext().getPaymentToken());
		//kpPaymentInfoModel.setAuthToken(requestData.getPaymentRequest().getStateContext().getKlarnaNetworkSessionToken());
		if(setPaymentInfoInCart(cartModel, kpPaymentInfoModel)) {
			kpPaymentFacade.createPaymentTransaction();
			LogHelper.debugLog(LOG, "Payment Info updated.");
			return true;
		}
		LOG.error("Payment Info could not be set. Cannot proceed with order placement");
		return false;
	}

	@Override
	public boolean setBillingAddress(final KlarnaRequestData requestData)
	{
		final KlarnaAddressData klarnaBillingAddressData = getBillingAddressFromPaymentRequest(requestData.getPaymentRequest());
		AddressData billingAddress = getAddressData(klarnaBillingAddressData);
		if (billingAddress != null)
		{
			billingAddress
					.setEmail(requestData.getPaymentRequest().getStateContext().getKlarnaCustomer().getCustomerProfile().getEmail());
			if (StringUtils.isNotEmpty(
					requestData.getPaymentRequest().getStateContext().getKlarnaCustomer().getCustomerProfile().getFamilyName()))
			{
				billingAddress.setFirstName(
						requestData.getPaymentRequest().getStateContext().getKlarnaCustomer().getCustomerProfile().getGivenName());
				billingAddress.setLastName(
						requestData.getPaymentRequest().getStateContext().getKlarnaCustomer().getCustomerProfile().getFamilyName());
			}
			else
			{
				addRecipientNameToAddress(billingAddress, requestData.getPaymentRequest());
			}
		}
		else
		{
			billingAddress = checkoutFacade.getCheckoutCart().getDeliveryAddress();
		}
		final AddressModel addressModel = kpAddressReverseConverter.convert(billingAddress);
		addressModel.setBillingAddress(Boolean.TRUE);
		final CartModel cartModel = cartService.getSessionCart();
		final KPPaymentInfoModel kpPaymentInfoModel = (KPPaymentInfoModel) cartModel.getPaymentInfo();
		addressModel.setOwner(kpPaymentInfoModel);
		kpPaymentInfoModel.setBillingAddress(addressModel);
		cartModel.setPaymentAddress(addressModel);
		cartModel.setKpIdentifier(KLARNA_PREFIX + cartModel.getCode());
		if (userService.isAnonymousUser(userService.getCurrentUser()))
		{
			cartModel.setKpAnonymousGUID(cartModel.getUser().getUid());
		}
		calculateCart(cartModel);
		modelService.saveAll(addressModel, kpPaymentInfoModel, cartModel);
		modelService.refresh(kpPaymentInfoModel);
		modelService.refresh(cartModel);
		LogHelper.debugLog(LOG, "Billing Address updated.");
		return true;
	}

	@Override
	public boolean updateCartWithWebhookData(final KlarnaWebhookData webhookData)
	{
		// Set Shipping Address and Shipping Option
		if (!setShippingAddressWithWebhookData(webhookData))
		{
			return false;
		}
		if (!setShippingOptionWithWebhookData(webhookData))
		{
			return false;
		}
		// Set Payment Info and Billing Address
		if (!setPaymentInfoWithWebhookData(webhookData))
		{
			return false;
		}
		return setBillingAddressWithWebhookData(webhookData);
	}

	@Override
	public boolean createKlarnaOrder() throws ApiException, IOException
	{
		final PaymentsOrder authorizationResponse = kpPaymentFacade.getPaymentAuthorization(null);
		if (authorizationResponse == null)
		{
			return false;
		}
		kpPaymentCheckoutFacade.saveKlarnaOrderId(authorizationResponse);
		return true;
	}

	@Override
	public String getPlaceOrderURL()
	{
		final StringBuilder placeOrderUrl = new StringBuilder(
				siteConfigService.getProperty(KlarnapaymentConstants.KP_MERCHANT_URL_CONFIRMATION));
		placeOrderUrl.append("/?kid=KLARNA_").append(cartService.getSessionCart().getCode());
		return placeOrderUrl.toString();
	}

	@Override
	public boolean handlePaymentUpdateForPSPIntegration(final String networkSessionToken, final String paymentState)
	{
		return klarnaNetworkSessionFacade.updateNetworkSession(networkSessionToken, paymentState);
	}

	protected boolean setShippingAddressWithWebhookData(final KlarnaWebhookData webhookData)
	{
		if (webhookData.getPayload() == null || webhookData.getPayload().getShipping() == null
				|| webhookData.getPayload().getShipping().getAddress() == null)
		{
			if (cartService.getSessionCart().getDeliveryAddress() != null)
			{
				return true;
			}
			else
			{
				LOG.error("Shipping address not available in webhook data");
				return false;
			}
		}
		final KlarnaAddressDTO shippingAddressDTO = webhookData.getPayload().getShipping().getAddress();
		final AddressData shippingAddress = klarnaAddressDTOReverseConverter.convert(shippingAddressDTO);
		if (webhookData.getPayload().getShipping().getRecipient() != null)
		{
			shippingAddress.setFirstName(webhookData.getPayload().getShipping().getRecipient().getGivenName());
			shippingAddress.setLastName(webhookData.getPayload().getShipping().getRecipient().getFamilyName());
		}
		if (!isValidAddress(shippingAddress))
		{
			LOG.error("Invalid shipping address. Cannot proceed with checkout");
			return false;
		}
		userFacade.addAddress(shippingAddress);
		if (!checkoutFacade.setDeliveryAddress(shippingAddress))
		{
			LOG.error("Shipping address couldnot be set. Cannot proceed with order placement");
			return false;
		}
		LogHelper.debugLog(LOG, "Shipping address updated with webhook data");
		return true;
	}

	protected boolean setShippingOptionWithWebhookData(final KlarnaWebhookData webhookData)
	{
		final String currentShippingOption = (cartService.getSessionCart().getDeliveryMode() != null)
				? cartService.getSessionCart().getDeliveryMode().getCode()
				: null;
		if (StringUtils.isNotEmpty(webhookData.getPayload().getShipping().getShippingReference()) && !StringUtils
				.equalsIgnoreCase(currentShippingOption, webhookData.getPayload().getShipping().getShippingReference()))
		{
			if (checkoutFacade.setDeliveryMode(webhookData.getPayload().getShipping().getShippingReference()))
			{
				LogHelper.debugLog(LOG, "Shipping option updated with webhook data");
				return true;
			}
			else
			{
				LOG.error("Shipping option couldnot be set. Cannot proceed with order placement");
				return false;
			}
		}
		else if (StringUtils.isEmpty(currentShippingOption))
		{
			LOG.error("Shipping option not available. Cannot proceed with order placement");
			return false;
		}
		return true;
	}

	protected boolean setPaymentInfoWithWebhookData(final KlarnaWebhookData webhookData)
	{
		final CartModel cartModel = cartService.getSessionCart();
		KPPaymentInfoModel kpPaymentInfoModel = null;
		if (!(cartModel.getPaymentInfo() instanceof KPPaymentInfoModel))
		{
			kpPaymentInfoModel = modelService.create(KPPaymentInfoModel.class);
			kpPaymentInfoModel.setCode(KLARNA_PREFIX + cartModel.getCode());
			kpPaymentInfoModel.setUser(cartModel.getUser());
			kpPaymentInfoModel.setOwner(cartModel);
		}
		else
		{
			kpPaymentInfoModel = (KPPaymentInfoModel) cartModel.getPaymentInfo();
		}
		// KLARNAPII-2754: Use Klarna Network Session Token instead of Payment Token - reverted
		kpPaymentInfoModel.setAuthToken(webhookData.getPayload().getPaymentToken());
		//kpPaymentInfoModel.setAuthToken(webhookData.getPayload().getKlarnaNetworkSessionToken());
		if (setPaymentInfoInCart(cartModel, kpPaymentInfoModel))
		{
			kpPaymentFacade.createPaymentTransaction();
			LogHelper.debugLog(LOG, "Payment Info updated with webhook data");
			return true;
		}
		LOG.error("Payment Info couldnot be set. Cannot proceed with order placement");
		return false;
	}

	protected boolean setBillingAddressWithWebhookData(final KlarnaWebhookData webhookData)
	{
		AddressData billingAddress;
		if (webhookData.getPayload().getKlarnaCustomer() != null
				&& webhookData.getPayload().getKlarnaCustomer().getCustomerProfile() != null
				&& webhookData.getPayload().getKlarnaCustomer().getCustomerProfile().getAddress() != null)
		{
			final KlarnaAddressDTO billingAddressDTO = webhookData.getPayload().getKlarnaCustomer().getCustomerProfile()
					.getAddress();
			billingAddress = klarnaAddressDTOReverseConverter.convert(billingAddressDTO);
			billingAddress.setEmail(webhookData.getPayload().getKlarnaCustomer().getCustomerProfile().getEmail());
			billingAddress.setFirstName(webhookData.getPayload().getKlarnaCustomer().getCustomerProfile().getGivenName());
			billingAddress.setLastName(webhookData.getPayload().getKlarnaCustomer().getCustomerProfile().getFamilyName());
		}
		else
		{
			billingAddress = checkoutFacade.getCheckoutCart().getDeliveryAddress();
		}
		final AddressModel addressModel = kpAddressReverseConverter.convert(billingAddress);
		addressModel.setBillingAddress(Boolean.TRUE);
		final CartModel cartModel = cartService.getSessionCart();
		final KPPaymentInfoModel kpPaymentInfoModel = (KPPaymentInfoModel) cartModel.getPaymentInfo();
		addressModel.setOwner(kpPaymentInfoModel);
		kpPaymentInfoModel.setBillingAddress(addressModel);
		cartModel.setPaymentAddress(addressModel);
		cartModel.setKpIdentifier(KLARNA_PREFIX + cartModel.getCode());
		if (userService.isAnonymousUser(userService.getCurrentUser()))
		{
			cartModel.setKpAnonymousGUID(cartModel.getUser().getUid());
		}
		calculateCart(cartModel);
		modelService.saveAll(addressModel, kpPaymentInfoModel, cartModel);
		modelService.refresh(kpPaymentInfoModel);
		modelService.refresh(cartModel);
		LogHelper.debugLog(LOG, "Billing Address updated successfully.");
		return true;
	}


	private void addRecipientNameToAddress(final AddressData address, final KlarnaPaymentRequestData paymentRequestData)
	{
		if (address != null && paymentRequestData != null && paymentRequestData.getStateContext().getShipping() != null
				&& paymentRequestData.getStateContext().getShipping().getRecipient() != null)
		{
			address.setFirstName(paymentRequestData.getStateContext().getShipping().getRecipient().getGivenName());
			address.setLastName(paymentRequestData.getStateContext().getShipping().getRecipient().getFamilyName());
		}
	}

	private KlarnaAddressData getBillingAddressFromPaymentRequest(final KlarnaPaymentRequestData paymentRequestData)
	{
		if (paymentRequestData.getStateContext() != null && paymentRequestData.getStateContext().getKlarnaCustomer() != null
				&& paymentRequestData.getStateContext().getKlarnaCustomer().getCustomerProfile() != null)
		{
			return paymentRequestData.getStateContext().getKlarnaCustomer().getCustomerProfile().getAddress();
		}
		return null;
	}

	private void calculateCart(final CartModel cartModel)
	{
		commerceCheckoutService.calculateCart(createCommerceCheckoutParameter(cartModel, true));
	}

	private boolean setPaymentInfoInCart(final CartModel cartModel, final KPPaymentInfoModel kpPaymentInfoModel)
	{
		final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true);
		parameter.setPaymentInfo(kpPaymentInfoModel);
		return commerceCheckoutService.setPaymentInfo(parameter);
	}

	private CommerceCheckoutParameter createCommerceCheckoutParameter(final CartModel cart, final boolean enableHooks)
	{
		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setEnableHooks(enableHooks);
		parameter.setCart(cart);
		return parameter;
	}
}
