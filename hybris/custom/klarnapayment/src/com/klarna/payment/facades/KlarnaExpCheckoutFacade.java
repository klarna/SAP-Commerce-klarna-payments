package com.klarna.payment.facades;

import de.hybris.platform.commercefacades.user.data.AddressData;

import com.klarna.api.expcheckout.model.KlarnaExpCheckoutAuthorizationResponse;
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.payment.data.KlarnaExpCheckoutConfigData;


public interface KlarnaExpCheckoutFacade
{

	KlarnaExpCheckoutConfigData getKlarnaExpCheckoutConfigData();

	PaymentsSession getAuthorizePayload();

	void createNewSessionCart();

	boolean isValidSessionUserCart();

	AddressData getShippingAddress(final KlarnaExpCheckoutAuthorizationResponse klarnaExpCheckoutAuthorizationResponse);

	boolean addPaymentInfo(final KlarnaExpCheckoutAuthorizationResponse klarnaExpCheckoutAuthorizationResponse);

	boolean addBillingAddress(final AddressData addressData);

}
