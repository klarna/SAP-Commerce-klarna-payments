package com.klarna.payment.facades.impl;

import de.hybris.platform.commercefacades.address.AddressVerificationFacade;
import de.hybris.platform.commercefacades.address.data.AddressVerificationErrorField;
import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commercefacades.order.CartFacade;
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

import javax.annotation.Resource;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.klarna.api.expcheckout.model.KlarnaExpCheckoutAuthorizationResponse;
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.payment.data.KlarnaAddressData;
import com.klarna.payment.data.KlarnaPaymentRequestData;
import com.klarna.payment.data.KlarnaRequestData;
import com.klarna.payment.data.KlarnaShippingChangeResponseData;
import com.klarna.payment.data.KlarnaShippingOptionData;
import com.klarna.payment.facades.KlarnaExpCheckoutFacade;
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

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	@Resource
	private Converter<KlarnaAddressData, AddressData> klarnaAddressReverseConverter;

	@Resource
	private Converter<AbstractOrderModel, KlarnaShippingChangeResponseData> klarnaShippingChangeResponseConverter;


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
	public KlarnaShippingChangeResponseData setDeliveryMode(final KlarnaShippingOptionData shippingOptionData)
	{
		checkoutFacade.setDeliveryMode(shippingOptionData.getShippingOptionReference());
		return klarnaShippingChangeResponseConverter.convert(cartService.getSessionCart());
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
		if (!setBillingAddress(requestData))
		{
			return false;
		}
		return true;
	}

	@Override
	public boolean setShippingAddress(final KlarnaRequestData requestData)
	{
		KlarnaAddressData klarnaShippingAddressData = requestData.getShippingAddress();
		if (klarnaShippingAddressData == null && requestData.getPaymentRequest().getStateContext().getShipping() != null)
		{
			klarnaShippingAddressData = requestData.getPaymentRequest().getStateContext().getShipping().getAddress();
		}
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
		return true;
	}

	@Override
	public boolean setShippingOption(final KlarnaRequestData requestData)
	{
		boolean isShippingOptionSet = false;
		if (requestData.getShippingOption() != null
				&& StringUtils.isNotEmpty(requestData.getShippingOption().getShippingOptionReference()))
		{
			if (cartFacade.getSessionCart().getDeliveryMode() != null
					&& StringUtils.equalsIgnoreCase(cartFacade.getSessionCart().getDeliveryMode().getCode(),
							requestData.getShippingOption().getShippingOptionReference()))
			{
				isShippingOptionSet = true;
			}
			else
			{
				isShippingOptionSet = checkoutFacade.setDeliveryMode(requestData.getShippingOption().getShippingOptionReference());
			}
		}
		if (!isShippingOptionSet)
		{
			LogHelper.debugLog(LOG, "Shipping option not available in the request. Setting cheapest delivery mode..");
			if (!checkoutFacade.setCheapestDeliveryModeForCheckout())
			{
				LOG.error("Shipping option couldnot be set. Cannot proceed with order placement");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean setPaymentInfo(final KlarnaRequestData requestData)
	{
		final CartModel cartModel = cartService.getSessionCart();
		KPPaymentInfoModel kpPaymentInfoModel = null;
		if ((cartModel.getPaymentInfo() == null) || !(cartModel.getPaymentInfo() instanceof KPPaymentInfoModel))
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
		kpPaymentInfoModel.setAuthToken(requestData.getPaymentRequest().getStateContext().getPaymentToken());
		return setPaymentInfoInCart(cartModel, kpPaymentInfoModel);
	}

	@Override
	public boolean setBillingAddress(final KlarnaRequestData requestData)
	{
		// Set Payment Info and Billing Address
		final KlarnaAddressData klarnaBillingAddressData = getBillingAddressFromPaymentRequest(requestData.getPaymentRequest());
		AddressData billingAddress = getAddressData(klarnaBillingAddressData);
		if (billingAddress != null)
		{
			addRecipientNameToAddress(billingAddress, requestData.getPaymentRequest());
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
		modelService.saveAll(addressModel, kpPaymentInfoModel, cartModel);
		modelService.refresh(kpPaymentInfoModel);
		modelService.refresh(cartModel);
		return true;
	}

	private void addRecipientNameToAddress(final AddressData address, final KlarnaPaymentRequestData paymentRequestData)
	{
		if (address != null && paymentRequestData.getStateContext().getShipping() != null
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
