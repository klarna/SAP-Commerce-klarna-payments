package com.klarna.payment.facades.impl;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.klarna.api.expcheckout.model.KlarnaExpCheckoutAuthorizationResponse;
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.payment.data.KlarnaExpCheckoutConfigData;
import com.klarna.payment.facades.KlarnaExpCheckoutFacade;
import com.klarna.payment.model.KPPaymentInfoModel;
import com.klarna.payment.model.KlarnaExpCheckoutConfigModel;


public class DefaultKlarnaExpCheckoutFacade implements KlarnaExpCheckoutFacade
{

	private static final Logger LOG = Logger.getLogger(DefaultKlarnaExpCheckoutFacade.class);

	private static final String KLARNA_PREFIX = "KLARNA_";

	@Resource(name = "klarnaExpCheckoutConfigConverter")
	private Converter klarnaExpCheckoutConfigConverter;

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

	@Override
	public KlarnaExpCheckoutConfigData getKlarnaExpCheckoutConfigData()
	{
		final BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
		final KlarnaExpCheckoutConfigModel model = baseStore.getKlarnaExpCheckoutConfig();
		if (model != null)
		{
			return (KlarnaExpCheckoutConfigData) klarnaExpCheckoutConfigConverter.convert(model);
		}
		return null;
	}

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
