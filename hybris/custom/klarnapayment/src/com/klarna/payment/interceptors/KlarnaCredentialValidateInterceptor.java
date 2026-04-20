package com.klarna.payment.interceptors;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import org.apache.commons.lang3.StringUtils;

import com.klarna.model.KlarnaCredentialModel;

/**
 *
 */
public class KlarnaCredentialValidateInterceptor implements ValidateInterceptor<KlarnaCredentialModel>
{

	@Override
	public void onValidate(final KlarnaCredentialModel klarnaCredentialModel, final InterceptorContext ctx)
			throws InterceptorException
	{
		if (!StringUtils.startsWithIgnoreCase(klarnaCredentialModel.getClientId(), "klarna_test_client")
				&& !StringUtils.startsWithIgnoreCase(klarnaCredentialModel.getClientId(), "klarna_live_client"))
		{
			throw new InterceptorException("The Client ID format is invalid. Please verify it and try again.");
		}
	}

}
