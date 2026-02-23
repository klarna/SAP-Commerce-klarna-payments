package com.klarna.payment.facades;

import de.hybris.platform.commercefacades.user.data.AddressData;

import com.klarna.api.expcheckout.model.KlarnaExpCheckoutAuthorizationResponse;
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.payment.data.KlarnaAddressData;
import com.klarna.payment.data.KlarnaRequestData;
import com.klarna.payment.data.KlarnaShippingChangeResponseData;
import com.klarna.payment.data.KlarnaShippingOptionData;
import com.klarna.payment.data.KlarnaWebhookData;


public interface KlarnaExpCheckoutFacade
{
	PaymentsSession getAuthorizePayload();

	void createNewSessionCart();

	boolean isValidSessionUserCart();

	AddressData getShippingAddress(final KlarnaExpCheckoutAuthorizationResponse klarnaExpCheckoutAuthorizationResponse);

	boolean isValidAddress(final AddressData addressData);

	boolean addPaymentInfo(final KlarnaExpCheckoutAuthorizationResponse klarnaExpCheckoutAuthorizationResponse);

	boolean addBillingAddress(final AddressData addressData);

	AddressData getAddressData(KlarnaAddressData klarnaAddressData);

	KlarnaShippingChangeResponseData getShippingChangeResponse();

	KlarnaShippingChangeResponseData setDeliveryMode(final KlarnaShippingOptionData shippingOptionData);

	boolean updateCartForCheckout(final KlarnaRequestData requestData);

	boolean setShippingAddress(final KlarnaRequestData requestData);

	boolean setShippingOption(final KlarnaRequestData requestData);

	boolean setPaymentInfo(final KlarnaRequestData requestData);

	boolean setBillingAddress(final KlarnaRequestData requestData);

	boolean updateCartWithWebhookData(final KlarnaWebhookData webhookData);

}
