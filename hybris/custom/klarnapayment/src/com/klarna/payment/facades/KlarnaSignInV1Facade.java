
package com.klarna.payment.facades;

import de.hybris.platform.core.model.user.CustomerModel;

import com.klarna.api.signin.model.KlarnaSigninResponse;
import com.klarna.api.signin.model.KlarnaSigninUserAccountLinking;
import com.klarna.api.signin.model.KlarnaSigninUserAccountProfile;
import com.klarna.payment.enums.KlarnaSigninProfileStatus;


/**
 *
 */
public interface KlarnaSignInV1Facade
{
	KlarnaSigninProfileStatus checkAndUpdateProfile(final KlarnaSigninResponse klarnaSigninResponse);

	boolean createNewCustomer(final KlarnaSigninResponse klarnaSigninResponse);

	void updateCustomer(CustomerModel customer, final KlarnaSigninUserAccountProfile klarnaSigninUserAccountProfile,
			final KlarnaSigninUserAccountLinking klarnaSigninUserAccountLinking);

	boolean validateSigninToken(KlarnaSigninResponse klarnaSigninResponse, String environment);
}
