package com.klarnapayment.utils;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;

import com.klarna.api.expcheckout.model.KlarnaExpCheckoutAuthorizationResponse;


public class KlarnaExpCheckoutHelper
{
	protected static final Logger LOG = Logger.getLogger(KlarnaExpCheckoutHelper.class);

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
}